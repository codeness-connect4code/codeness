package com.connect.codeness.global.config;

import com.connect.codeness.domain.mentoringpost.entity.MentoringPost;
import com.connect.codeness.domain.mentoringschedule.entity.MentoringSchedule;
import com.connect.codeness.global.enums.MentoringPostStatus;
import com.connect.codeness.global.enums.MentoringScheduleStatus;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;

@Slf4j
@Configuration
public class MentoringBatchConfig {

	private final EntityManagerFactory entityManagerFactory;

	public MentoringBatchConfig(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * JpaTransactionManager Bean 등록
	 */
	@Bean(name = "transactionManager")
	public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory) {
		return new JpaTransactionManager(entityManagerFactory);
	}

	/**
	 * Job : MentoringPost 삭제
	 * - 배치 작업 전체 실행 단위
	 * - 멘토링 포스트를 삭제하기 전에 payment 테이블의 멘토링 스케쥴 id를 null로 변경해야한다 - 연관관계 해제
	 */
	@Bean
	public Job deleteMentoringPostJob(JobRepository jobRepository, Step deleteMentoringPostStep) {
		return new JobBuilder("deleteMentoringPostStep", jobRepository)
			.start(deleteMentoringPostStep)//step 실행
			.build();
	}

	/**
	 * Step : MentoringPost 삭제
	 * - 배치 개별 실행 단계
	 */
	@Bean
	public Step deleteMentoringPostStep(JobRepository jobRepository, JpaTransactionManager jpaTransactionManager) {
		return new StepBuilder("deleteMentoringPostStep", jobRepository)
			.<MentoringPost, MentoringPost>chunk(10, jpaTransactionManager)
			.reader(mentoringPostItemReader())
			.writer(mentoringPostItemWriter())
			.build();
	}

	/**
	 * ItemReader : 삭제할 MentoringPost 조회
	 * - 상태가 DELETED인 MentoringPost 조회
	 */
	@Bean
	public JpaPagingItemReader<MentoringPost> mentoringPostItemReader() {
		return new JpaPagingItemReaderBuilder<MentoringPost>()
			.name("mentoringPostItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT mp "
				+ "FROM MentoringPost mp "
				+ "WHERE mp.mentoringPostStatus = :mentoringPostStatus ")
			.parameterValues(Collections.singletonMap("mentoringPostStatus", MentoringPostStatus.DELETED))
			.pageSize(10)//한번에 10개씩 조회 - chunk 사이즈랑 동일하게 설정 : 성능향상
			.build();
	}

	/**
	 * ItemWriter : 조회된 MentoringPost 삭제
	 * - Payment 테이블의 mentoring_schedule_id를 null로 변경 - 연관관계 해제
	 * - MentoringSchedule 삭제
	 */
	@Bean
	public ItemWriter<MentoringPost> mentoringPostItemWriter() {
		return items -> {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();//트랜잭션 단위로 시작

			for (MentoringPost mentoringPost : items) {
				//Payment - MentoringScheduleId null 변경
				entityManager.createQuery(
						" UPDATE Payment p " +
							"SET p.mentoringSchedule = NULL " +
							"WHERE p.mentoringSchedule " +
							"IN (" +
							"SELECT ms " +
							"FROM MentoringSchedule ms " +
							"WHERE ms.mentoringPost = :mentoringPost) "
					).setParameter("mentoringPost", mentoringPost)
					.executeUpdate();

				//MentoringSchedule 삭제
				entityManager.createQuery(
						"DELETE " +
							"FROM MentoringSchedule ms " +
							"WHERE ms.mentoringPost = :mentoringPost"
					).setParameter("mentoringPost", mentoringPost)
					.executeUpdate();

				//MentoringPost 삭제
				entityManager.remove(entityManager.merge(mentoringPost));
				log.info("{}개의 MentoringPost가 삭제되었습니다.", items.size());
			}

			//DB에 반영
			entityTransaction.commit();
			//자원 해제
			entityManager.close();
		};
	}

	/**
	 * Job : 만료된 MentoringSchedule 상태 업데이트
	 */
	@Bean
	public Job updateExpiredMentoringScheduleJob(JobRepository jobRepository, Step updateExpiredMentoringScheduleStep) {
		return new JobBuilder("updateExpiredMentoringScheduleJob", jobRepository)
			.start(updateExpiredMentoringScheduleStep)
			.build();
	}

	/**
	 * Step : 만료된 MentoringSchedule 상태 업데이트
	 */
	@Bean
	public Step updateExpiredMentoringScheduleStep(JobRepository jobRepository, JpaTransactionManager jpaTransactionManager) {
		return new StepBuilder("updateExpiredMentoringScheduleStep", jobRepository)
			.<MentoringSchedule, MentoringSchedule>chunk(10, jpaTransactionManager)
			.reader(expiredMentoringScheduleItemReader())
			.writer(expiredMentoringScheduleItemWriter())
			.build();
	}

	/**
	 * ItemReader : 만료된 MentoringSchedule 조회
	 * - 현재 날짜보다 과거이거나, 현재 날짜이면서 현재 시간보다 과거인 MentoringSchedule 조회
	 */
	@Bean
	public JpaPagingItemReader<MentoringSchedule> expiredMentoringScheduleItemReader() {
		return new JpaPagingItemReaderBuilder<MentoringSchedule>()
			.name("expiredMentoringScheduleItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString(
				"SELECT ms "
					+ "FROM MentoringSchedule ms "
					+ "WHERE ms.mentoringScheduleStatus != :expiredMentoringScheduleStatus "
					+ "AND (ms.mentoringDate < CURRENT_DATE OR (ms.mentoringDate = CURRENT_DATE AND ms.mentoringTime < CURRENT_TIME))"
			)
			.parameterValues(Collections.singletonMap("expiredMentoringScheduleStatus", MentoringScheduleStatus.EXPIRED))
			.pageSize(10)
			.build();
	}

	/**
	 * ItemWriter : 조회한 MentoringSchedule 상태 변경
	 * - EXPIRED로 변경
	 */
	@Bean
	public ItemWriter<MentoringSchedule> expiredMentoringScheduleItemWriter() {
		return items -> {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();//트랜잭션 단위로 시작

			for (MentoringSchedule mentoringSchedule : items) {
				mentoringSchedule.updateStatus(MentoringScheduleStatus.EXPIRED);
				//상태 변경 후 저장
				entityManager.merge(mentoringSchedule);
			}

			//DB 반영
			entityTransaction.commit();
			//자원 해제
			entityManager.close();

			log.info("{}개의 MentoringSchedule이 EXPIRED(만료) 상태로 변경되었습니다.", items.size());
		};
	}

}
