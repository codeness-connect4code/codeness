package com.connect.codeness.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.database-url}")
	private String databaseUrl;

	@Value("${firebase.config-path}")
	private String configPath;

	@Bean
	public FirebaseDatabase firebaseDatabase() throws IOException {
		// 🔹 `ClassPathResource` 대신 직접 파일 경로 지정
		FileInputStream serviceAccount = new FileInputStream(configPath);

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.setDatabaseUrl(databaseUrl)
			.build();

		// Firebase 앱이 이미 초기화되었는지 확인
		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}

		return FirebaseDatabase.getInstance();
	}
}
