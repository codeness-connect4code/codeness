package com.connect.codeness.global.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
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
public class BatchScheduler {

	private final JobLauncher jobLauncher;
	private final Job deleteMentoringScheduleJob;

	@Autowired
	public BatchScheduler(JobLauncher jobLauncher, Job deleteMentoringScheduleJob) {
		this.jobLauncher = jobLauncher;
		this.deleteMentoringScheduleJob = deleteMentoringScheduleJob;
	}

	@Scheduled(cron = "0 0 4 1 * ?")//매월 1일 오전 4시
//	@Scheduled(cron = "0 0/1 * * * ?")//1분마다 실행 - TODO :  테스트
	public void runDeleteMentoringScheduleJob(){
		try {
			JobParameters jobParameters = new JobParametersBuilder()
				.addLong("timestamp", System.currentTimeMillis())//실행 시점 파라미터 추가
				.toJobParameters();

			jobLauncher.run(deleteMentoringScheduleJob, jobParameters);
			log.info("MentoringSchedule 삭제 배치 실행 완료");
		} catch (Exception exception) {
			log.error("MentoringSchedule 삭제 배치 진행 중 오류 발생 - " + exception.getMessage());
		}


	}


}
