package com.connect.codeness.global.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.FirebaseDatabase;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Base64;

@Configuration
public class FirebaseConfig {

	@Value("${firebase.database-url}")
	private String databaseUrl;

	@Value("${firebase.config-base64}")
	private String configBase64;

	@Bean
	public FirebaseDatabase firebaseDatabase() throws IOException {
		// Base64 환경변수에서 Firebase Key 복원
		byte[] decodedKey = Base64.getDecoder().decode(configBase64);
		File tempFile = File.createTempFile("firebase", ".json");
		try (FileOutputStream fos = new FileOutputStream(tempFile)) {
			fos.write(decodedKey);
		}

		FirebaseOptions options = FirebaseOptions.builder()
			.setCredentials(GoogleCredentials.fromStream(new FileInputStream(tempFile)))
			.setDatabaseUrl(databaseUrl)
			.build();

		if (FirebaseApp.getApps().isEmpty()) {
			FirebaseApp.initializeApp(options);
		}

		return FirebaseDatabase.getInstance();
	}
}
