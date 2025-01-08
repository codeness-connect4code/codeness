package com.connect.codeness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CodenessApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodenessApplication.class, args);
	}
}
