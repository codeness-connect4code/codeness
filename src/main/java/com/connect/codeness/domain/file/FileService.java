package com.connect.codeness.domain.file;

import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import java.io.IOException;
import org.springframework.web.multipart.MultipartFile;

public interface FileService {

	// 파일 업로드 메서드
	CommonResponseDto createFile(MultipartFile inputFile, Long userId, FileCategory category) throws IOException;

	// 파일 삭제 메서드
	CommonResponseDto deleteFile(Long userId, FileCategory category) throws IOException;
}
