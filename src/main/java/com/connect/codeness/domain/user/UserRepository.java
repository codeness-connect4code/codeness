package com.connect.codeness.domain.user;

import com.connect.codeness.domain.user.dto.UserResponseDto;
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


	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findById(Long id);

	default User findByIdOrElseThrow(Long id) {
		User user = findById(id).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_USER)
		);

		if (user.getUserStatus() == UserStatus.LEAVE){
			throw new BusinessException(ExceptionType.USER_ALREADY_DELETED);
		}
		return user;
	}

	default User findByEmailOrElseThrow(String email) {
		User user = findByEmail(email).orElseThrow(
			() -> new BusinessException(ExceptionType.NOT_FOUND_USER)
		);

		if (user.getUserStatus() == UserStatus.LEAVE){
			throw new BusinessException(ExceptionType.USER_ALREADY_DELETED);
		}
		return user;
	}

	@Query("SELECT new com.connect.codeness.domain.user.dto.UserResponseDto(u) FROM user u WHERE u.role = :userRole")
	Page<UserResponseDto> findByRole(UserRole userRole, Pageable pageable);
}
