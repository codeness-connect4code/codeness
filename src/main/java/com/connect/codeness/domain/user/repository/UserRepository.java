package com.connect.codeness.domain.user.repository;

import com.connect.codeness.domain.admin.dto.AdminMentorListResponseDto;
import com.connect.codeness.domain.user.entity.User;
import com.connect.codeness.global.enums.UserRole;
import com.connect.codeness.global.enums.UserStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

	//이메일 중복여부 확인
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findById(Long id);

	//유저 고유 식별자 조회 예외처리
	default User findByIdOrElseThrow(Long id) {
		User user = findById(id).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_USER)
		);

		if (user.getUserStatus() == UserStatus.LEAVE){
			throw new BusinessException(ExceptionType.USER_ALREADY_DELETED);
		}
		return user;
	}

	//유저 이메일 조회 예외처리
	default User findByEmailOrElseThrow(String email) {
		User user = findByEmail(email).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_USER)
		);

		if (user.getUserStatus() == UserStatus.LEAVE){
			throw new BusinessException(ExceptionType.USER_ALREADY_DELETED);
		}
		return user;
	}

	//유저 role 조회, 리스트 반환
	@Query("SELECT new com.connect.codeness.domain.admin.dto.AdminMentorListResponseDto(u) FROM user u WHERE u.role = :userRole")
	Page<AdminMentorListResponseDto> findByRole(UserRole userRole, Pageable pageable);
}
