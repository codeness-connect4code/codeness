package com.connect.codeness.domain.user;

import com.connect.codeness.global.enums.UserStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	boolean existsByEmail(String email);
	Optional<User> findByEmail(String email);
	Optional<User> findById(Long id);

	default User findByIdOrElseThrow(Long id) {
		User user = findById(id).orElseThrow(
			() -> new BusinessException(ExceptionType.USER_ALREADY_DELETED)
		);

		if (user.getUserStatus() == UserStatus.LEAVE){
			throw new BusinessException(ExceptionType.USER_ALREADY_DELETED);
		}
		return user;
	}

	default User findByEmailOrElseThrow(String email) {
		User user = findByEmail(email).orElseThrow(
			() -> new BusinessException(ExceptionType.USER_ALREADY_DELETED)
		);

		if (user.getUserStatus() == UserStatus.LEAVE){
			throw new BusinessException(ExceptionType.USER_ALREADY_DELETED);
		}
		return user;
	}
}
