package com.connect.codeness.domain.mentorrequest.repository;

import com.connect.codeness.domain.mentorrequest.dto.MentorRequestGetResponseDto;
import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.domain.mentorrequest.entity.MentorRequest;
import com.connect.codeness.global.enums.MentorRequestStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorRequestRepository extends JpaRepository<MentorRequest, Long> {
	boolean existsByUserIdAndIsAccepted(Long userId, MentorRequestStatus status);

	//멘토 신청을 status로 조회
	@Query("""
			SELECT mr
			FROM MentorRequest mr WHERE mr.isAccepted = :status
			ORDER BY mr.createdAt ASC
		""")
	List<MentorRequestResponseDto> findByIsAccepted(MentorRequestStatus status);

	default MentorRequest findByIdOrElseThrow(Long mentoringRequestId){
		return findById(mentoringRequestId).orElseThrow(
			()->new BusinessException(ExceptionType.NOT_FOUND_MENTOR_REQUEST)
		);
	}

	//멘토 신청을 유저 고유 식별자로 조회
	@Query("""
			SELECT mr
			FROM MentorRequest mr WHERE mr.user.id = :userId
			ORDER BY mr.createdAt ASC
		""")
	List<MentorRequestGetResponseDto> findAllByUserId(Long userId);
}
