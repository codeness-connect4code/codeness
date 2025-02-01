package com.connect.codeness.global.config;


import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

	@Value("${spring.data.redis.host}")
	private String host;
	
	@Value("${spring.data.redis.port}")
	private int port;
	
	@Value("${spring.data.redis.password}")
	private String password;

	@Value("${spring.data.redis.timeout}")
	private long timeout; //ms 단위
	
	//redis 서버 주소 연결
	private static final String REDISSON_PREFIX = "redis://";

	@Bean
	public RedissonClient redissonClient() {
		Config config = new Config();
		config.useSingleServer()
			.setAddress(REDISSON_PREFIX + host + ":" + port)
			.setTimeout((int) timeout)
			.setPassword(password.isEmpty() ? null : password);

		return Redisson.create(config);
	}

	//데이터 저장, 조회하기 위해 필요(직렬화 설정 가능, 다양한 데이터 타입 지원)
	@Bean
	public RedisTemplate<String, Long> redisTemplate(RedisConnectionFactory redisConnectionFactory){
		RedisTemplate<String, Long> redisTemplate = new RedisTemplate<>();
		redisTemplate.setConnectionFactory(redisConnectionFactory);
		redisTemplate.setKeySerializer(new StringRedisSerializer());//문자열 key 직렬화
		redisTemplate.setValueSerializer(new GenericToStringSerializer<>(Long.class));//Long 값 직렬화

		return  redisTemplate;
	}
}
