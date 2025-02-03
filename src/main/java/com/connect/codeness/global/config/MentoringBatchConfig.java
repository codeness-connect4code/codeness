package com.connect.codeness.global.config;

import com.connect.codeness.domain.mentoringschedule.entity.MentoringSchedule;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
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

@Configuration
public class MentoringBatchConfig {

	private final EntityManagerFactory entityManagerFactory;

	public MentoringBatchConfig(EntityManagerFactory entityManagerFactory) {
		this.entityManagerFactory = entityManagerFactory;
	}

	/**
	 * JobRepository Bean 등록
	 * - 배치 실행 정보 저장
	 */
//	@Bean
//	public JobRepository jobRepository() throws Exception {
//		JobRepositoryFactoryBean jobRepositoryFactoryBean = new JobRepositoryFactoryBean();
//		jobRepositoryFactoryBean.setDataSource(dataSource);
//		jobRepositoryFactoryBean.setTransactionManager(platformTransactionManager);
//		jobRepositoryFactoryBean.setDatabaseType("MYSQL");
//
//		return jobRepositoryFactoryBean.getObject();
//	}
	/**
	 * JpaTransactionManager Bean 등록
	 */
	@Bean
	public JpaTransactionManager jpaTransactionManager(EntityManagerFactory entityManagerFactory){
		return new JpaTransactionManager(entityManagerFactory);
	}


	/**
	 * JobLauncher Bean 등록
	 * - Job을 원하는 시점에서 실행하는 역할
	 * - 배치를 실행할 때 특정 파라미터 전달 가능
	 * - 실행중인 Job 상태 확인 가능
	 */
//	@Bean
//	public JobLauncher jobLauncher(JobRepository jobRepository) throws Exception {
//		TaskExecutorJobLauncher jobLauncher = new TaskExecutorJobLauncher();
//		jobLauncher.setJobRepository(jobRepository);
//		jobLauncher.setTaskExecutor(new SimpleAsyncTaskExecutor());//비동기 실행 지원
//		jobLauncher.afterPropertiesSet();//빈 초기화
//
//		return jobLauncher;
//	}

	/**
	 * Job 등록
	 * - 배치 작업 전체 실행 단위
	 */
	@Bean
	public Job deleteMentoringScheduleJob(JobRepository jobRepository, Step deleteMentoringScheduleStep) {
		return new JobBuilder("deleteMentoringScheduleJob", jobRepository)
			.start(deleteMentoringScheduleStep)//step 실행
			.build();
	}

	/**
	 * Step 등록
	 * - 배치 개별 실행 단계
	 */
	@Bean
	public Step deleteMentoringScheduleStep(JobRepository jobRepository, JpaTransactionManager jpaTransactionManager) {
		return new StepBuilder("deleteMentoringScheduleStep", jobRepository)
			.<MentoringSchedule, MentoringSchedule>chunk(10, jpaTransactionManager)
			.reader(mentoringScheduleItemReader())//데이터 조회
			.writer(mentoringScheduleItemWriter())//데이터 삭제
			.build();
	}

	/**
	 * ItemReader - 삭제 대상 데이터 조회
	 */
	@Bean
	public JpaPagingItemReader<MentoringSchedule> mentoringScheduleItemReader() {
		return new JpaPagingItemReaderBuilder<MentoringSchedule>()
			.name("mentoringScheduleItemReader")
			.entityManagerFactory(entityManagerFactory)
			.queryString("SELECT ms "
						+ "FROM MentoringSchedule ms "
						+ "WHERE ms.MentoringScheduleStatus = 'DELETED' ")
			.pageSize(10)//한번에 10개씩 조회
			.build();
	}

	/**
	 * ItemWriter - 조회 데이터 삭제
	 */
	@Bean
	public ItemWriter<MentoringSchedule> mentoringScheduleItemWriter(){
		return items -> {
			EntityManager entityManager = entityManagerFactory.createEntityManager();
			EntityTransaction entityTransaction = entityManager.getTransaction();
			entityTransaction.begin();//트랜잭션 단위로 시작

			//mentoringSchedule 영속 상태로 변환하고 -> 영속 상태의 엔티티를 DB에서 삭제
			for(MentoringSchedule mentoringSchedule : items){
				entityManager.remove(entityManager.merge(mentoringSchedule));
			}

			//삭제 작업 DB에 반영
			entityTransaction.commit();
			//자원 해제
			entityManager.close();
		};
	}

}
