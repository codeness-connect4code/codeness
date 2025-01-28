package com.connect.codeness.domain.settlement.repository;

import com.connect.codeness.domain.admin.dto.AdminSettlementListResponseDto;
import com.connect.codeness.domain.admin.dto.AdminSettlementResponseDto;
import com.connect.codeness.domain.settlement.entity.Settlement;
import com.connect.codeness.global.enums.SettlementStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SettlementRepository extends JpaRepository<Settlement, Long> {

	@Query("""
			SELECT s
			FROM Settlement s
			WHERE s.settlementStatus = :settlementStatus
			AND s.user.id = :mentorId
			""")
	List<Settlement> findAllByUserIdAndSettleStatus(Long mentorId, SettlementStatus settlementStatus);

	@Query("""
			SELECT new com.connect.codeness.domain.admin.dto.AdminSettlementListResponseDto(
			s.user.id, s.user.name, COUNT(s), SUM(s.paymentHistory.paymentCost), MAX(s.settlementRequestAt), s.user.account)
			FROM Settlement s
			WHERE s.settlementStatus = :settlementStatus
			GROUP BY s.user.id, s.user.name
			ORDER BY MAX(s.settlementRequestAt) DESC
			""")
	List<AdminSettlementListResponseDto> findBySettleStatusMentorGroupList(
		SettlementStatus settlementStatus);

	@Query("""
			SELECT new com.connect.codeness.domain.admin.dto.AdminSettlementResponseDto(s)
			FROM Settlement s
			WHERE s.settlementStatus = :settlementStatus
			AND s.user.id = :mentorId
			""")
	List<AdminSettlementResponseDto> findByUserIdAndSettleStatus(Long mentorId, SettlementStatus settlementStatus);
}
