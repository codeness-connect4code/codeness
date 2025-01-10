package com.connect.codeness.domain.file;

import com.connect.codeness.global.enums.FileCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDeleteDto {

	private Long userId;

	private FileCategory fileCategory;

}
