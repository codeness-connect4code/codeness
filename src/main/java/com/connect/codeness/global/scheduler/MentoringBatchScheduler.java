package com.connect.codeness.global.scheduler;

import java.time.Year;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@Slf4j
@Configuration
@EnableScheduling
public class MentoringBatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job deleteMentoringPostJob;
	private final Job updateExpiredMentoringScheduleJob;

	@Autowired
	public MentoringBatchScheduler(JobLauncher jobLauncher, Job deleteMentoringPostJob, Job updateExpiredMentoringScheduleJob) {
		this.jobLauncher = jobLauncher;
		this.deleteMentoringPostJob = deleteMentoringPostJob;
		this.updateExpiredMentoringScheduleJob = updateExpiredMentoringScheduleJob;
	}

	private void runJob(Job job, String jobName) {
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())//실행 시점 파라미터 추가
				.toJobParameters();

			//배치 실행
			JobExecution jobExecution = jobLauncher.run(job, jobParameters);

			//배치 실행 후 상태 확인해서 로그 기록
			if (jobExecution.getStatus().isUnsuccessful()) {
				log.error("{} 배치가 실행되었지만, 진행을 실패하였습니다. - ExitStatus : {}", jobName, jobExecution.getExitStatus().getExitCode());
			} else {
				log.info("{} 배치 실행 완료 - ExitStatus : {}", jobName, jobExecution.getExitStatus().getExitCode());
			}

		} catch (Exception exception) {
			log.error("{} 배치 실행 중 오류 발생 - {} ", jobName, exception.getMessage());
		}
	}

	/**
	 * - 상태가 DELETED인 멘토링 공고 삭제
	 * - 3년 주기로 실행 : 매년 1월 1일 오전 3시
	 */
//	@Scheduled(cron = "0 0/1 * * * ?")//1분마다 실행 - TODO : Schedule TEST
	@Scheduled(cron = "0 0 3 1 1 ?")
	public void runDeleteMentoringPostJob() {
		//현재 연도
		int currentYear = Year.now().getValue();
		//주기 - 3년
		int cycle = 3;

		if(currentYear % cycle == 0){
			log.info("{}년 주기로 배치 실행 : 현재 연도는 {}년 입니다. 배치를 실행합니다.", cycle, currentYear);
			//배치 실행
			runJob(deleteMentoringPostJob, "deleteMentoringPostJob");
		} else {
			log.info("{}년 주기로 배치 실행 : 현재 연도는 {}년이므로, 배치를 실행하지 않습니다. ", cycle, currentYear);
		}
	}

	/**
	 * 현재 날짜보다 과거인 멘토링 스케쥴 상태 EXPIRED로 변경
	 * - 매일 오전 3시에 실행
	 */
//	@Scheduled(cron = "0 0/2 * * * ?") // TODO : Schedule TEST
	@Scheduled(cron = "0 0 3 * * ?")
	public void runUpdateExpiredMentoringScheduleJob() {
		runJob(updateExpiredMentoringScheduleJob, "updateExpiredMentoringScheduleJob");
	}


}
