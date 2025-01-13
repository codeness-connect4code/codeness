package com.connect.codeness.domain.file;

import com.connect.codeness.domain.file.dto.FileCreateDto;
import com.connect.codeness.domain.file.dto.FileDeleteDto;
import com.connect.codeness.global.dto.CommonResponseDto;
import com.connect.codeness.global.enums.FileCategory;
import java.io.IOException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/files")
public class FileController {

	private final FileService fileService;

	public FileController(FileService fileService) {
		this.fileService = fileService;
	}

	@PostMapping
	public ResponseEntity<CommonResponseDto> createFile(@ModelAttribute FileCreateDto file)
		throws IOException {

		CommonResponseDto result = fileService.createFile(file.getFile(), 1l, FileCategory.PROFILE);

		return new ResponseEntity<>(result, HttpStatus.CREATED);
	}

	@DeleteMapping
	public ResponseEntity<CommonResponseDto> deleteFile(@RequestBody FileDeleteDto dto)throws IOException{

		CommonResponseDto result = fileService.deleteFile(dto.getUserId(), dto.getFileCategory());

		return new ResponseEntity<>(result, HttpStatus.OK);
	}
}
