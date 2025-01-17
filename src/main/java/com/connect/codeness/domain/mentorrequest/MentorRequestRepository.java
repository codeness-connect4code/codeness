package com.connect.codeness.domain.mentorrequest;

import com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto;
import com.connect.codeness.global.enums.MentorRequestStatus;
import com.connect.codeness.global.exception.BusinessException;
import com.connect.codeness.global.exception.ExceptionType;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MentorRequestRepository extends JpaRepository<MentorRequest, Long> {
	boolean existsByUserId(Long userId);

	@Query("""
			SELECT new com.connect.codeness.domain.mentorrequest.dto.MentorRequestResponseDto(mr)
			FROM MentorRequest mr WHERE mr.isAccepted = :status
			ORDER BY mr.createdAt ASC
		""")
	List<MentorRequestResponseDto> findByIsAccepted(MentorRequestStatus status);

	default MentorRequest findByIdOrElseThrow(Long mentoringRequestId){
		return findById(mentoringRequestId).orElseThrow(
			()->new BusinessException(ExceptionType.NOT_FOUND_MENTOR_REQUEST)
		);
	}
}
