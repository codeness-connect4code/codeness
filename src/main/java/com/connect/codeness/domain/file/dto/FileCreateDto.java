package com.connect.codeness.domain.file.dto;

import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Builder
public class FileCreateDto {

	private MultipartFile file;

}
