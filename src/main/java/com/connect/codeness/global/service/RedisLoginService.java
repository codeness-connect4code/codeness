package com.connect.codeness.global.service;

import com.connect.codeness.domain.user.dto.UserLoginDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisLoginService {
	private final StringRedisTemplate redisTemplate;
	private final ObjectMapper objectMapper;

	public RedisLoginService(StringRedisTemplate redisTemplate, ObjectMapper objectMapper) {
		this.redisTemplate = redisTemplate;
		this.objectMapper = objectMapper;
	}

	public void saveLoginInfo(UserLoginDto loginDto){
		try {
			String key = UserLoginDto.getKey(loginDto.getId());
			String value = objectMapper.writeValueAsString(loginDto);

			String oldValue = redisTemplate.opsForValue().get(key);
			if(oldValue != null){
				UserLoginDto oldLoginDto = objectMapper.readValue(oldValue, UserLoginDto.class);
			}

			redisTemplate.opsForValue().set(key,value,24, TimeUnit.HOURS);
		}catch (JsonProcessingException e){
			log.error("로그인 정보 저장 실패",e);
			throw new RuntimeException("로그인 정보를 저장하는데 실패했습니다.");
		}
	}

	public Optional<UserLoginDto> getLoginInfo(Long userId){
		try {
			String key = UserLoginDto.getKey(userId);
			String value = redisTemplate.opsForValue().get(key);

			if (value == null){
				return Optional.empty();
			}

			UserLoginDto loginDto = objectMapper.readValue(value, UserLoginDto.class);
			return Optional.of(loginDto);
		}catch (JsonProcessingException e){
			log.error("로그인 정보 조회 실패");
			return Optional.empty();
		}
	}

	public void removeLoginInfo(Long userId){
		String key = UserLoginDto.getKey(userId);
		redisTemplate.delete(key);
		log.info("로그인 정보 삭제 : ", userId);
	}

	public boolean validateToken(Long userId, String accessToken){
		return getLoginInfo(userId)
			.map(loginDto -> loginDto.getAccessToken().equals(accessToken))
			.orElse(false);
	}

}
