package com.connect.codeness.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
public class SchedulerConfig {

	/**
	 * payment 도메인에서 사용하는 스케줄러
	 */
	@Bean(name = "paymentTaskScheduler")
	public ThreadPoolTaskScheduler paymentTaskScheduler(){
		ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
		//최대 동시 실행 가능 개수
		threadPoolTaskScheduler.setPoolSize(3);
		//스레드 이름 지정
		threadPoolTaskScheduler.setThreadNamePrefix("Payment_Scheduler_Task_");
		return threadPoolTaskScheduler;
	}

}
