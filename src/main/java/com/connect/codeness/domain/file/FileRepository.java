package com.connect.codeness.domain.file;

import com.connect.codeness.global.enums.FileCategory;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileRepository extends JpaRepository<ImageFile, Long> {

	Optional<ImageFile> findByUserId(Long userId);

	// fileCategory와 userId로 파일 목록 검색
	Optional<ImageFile> findByUserIdAndFileCategory(Long userId, FileCategory fileCategory);

	// fileCategory와 userId로 파일 찾기 (없으면 예외 던짐)
	default ImageFile findByUserIdAndFileCategoryOrElseThrow(Long userId, FileCategory fileCategory) {
		return findByUserIdAndFileCategory(userId, fileCategory)
			.orElseThrow(() -> new BusinessException(ExceptionType.NOT_FOUND_FILE));
	}


}
