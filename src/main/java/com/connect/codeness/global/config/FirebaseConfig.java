package com.connect.codeness.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.database-url}")
	private String databaseUrl;

	@Value("${firebase.config-path:}")  // 환경 변수에 설정된 경우 사용
	private String configPath;

	@Value("${firebase.config-base64:}")  // Base64 인코딩된 JSON을 받을 경우 사용
	private String configBase64;

	@Bean
	public FirebaseDatabase firebaseDatabase() throws IOException {
		InputStream serviceAccount;

		if (!configBase64.isEmpty()) {
			// 🔹 Base64 환경 변수 기반 로드 (CICD에 적합)
			byte[] decodedBytes = Base64.getDecoder().decode(configBase64);
			serviceAccount = new ByteArrayInputStream(decodedBytes);
		} else {
			// 🔹 파일 경로 기반 로드 (로컬 & 서버)
			serviceAccount = new FileInputStream(configPath);
		}

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(serviceAccount))
			.setDatabaseUrl(databaseUrl)
			.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}

		return FirebaseDatabase.getInstance();
	}
}
