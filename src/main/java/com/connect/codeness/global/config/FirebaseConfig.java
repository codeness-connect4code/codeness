package com.connect.codeness.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.database-url}")
	private String databaseUrl;

	@Value("${firebase.config-path}") // 🔹 파일 경로 직접 사용!
	private String configPath;

	@Bean
	public FirebaseDatabase firebaseDatabase() throws IOException {
//		// 🔹 환경 변수에서 직접 파일 경로를 가져와서 사용!
//		FileInputStream serviceAccount = new FileInputStream(configPath);
		Resource resource = new ClassPathResource(configPath);
		InputStream serviceAccount = resource.getInputStream();

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