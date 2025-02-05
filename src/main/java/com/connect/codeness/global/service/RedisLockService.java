package com.connect.codeness.global.service;

import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisLockService {
		private final RedissonClient redissonClient;

		public RedisLockService(RedissonClient redissonClient){
			this.redissonClient = redissonClient;
		}

		//특정 key에 대한 락 획득
		public boolean acquireLock(String key, long waitTime, long leaseTime){
			RLock redisLock = redissonClient.getLock(key);

			try{
				boolean acquired = redisLock.tryLock(waitTime, leaseTime, TimeUnit.MILLISECONDS);

				if (acquired) {
					log.info("락 획득 성공: " + key);
				} else {
					log.error("락 획득 실패: " + key);
				}
				return acquired;
			} catch (InterruptedException  exception){
				log.error("락 획득 중 오류 발생: " + key);
				Thread.currentThread().interrupt();
				return false;
			}
		}
		
		//현재 쓰레드가 가진 락만 해제
		public void releaseLock(String key){
			RLock redisLock = redissonClient.getLock(key);

			if(redisLock.isHeldByCurrentThread()){
				try {
					redisLock.unlock();
					log.info("락 해제: " + key);
				} catch (IllegalMonitorStateException exception){
					log.error("락 해제 실패 - 이미 해제되었거나 보유한 락이 아님: " + key);
				}
			} else {
				log.info("현재 쓰레드가 보유한 락이 아님: " + key);
			}
		}

}
