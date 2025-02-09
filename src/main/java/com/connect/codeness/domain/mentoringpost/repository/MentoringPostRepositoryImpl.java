package com.connect.codeness.domain.mentoringpost.repository;


import static com.connect.codeness.domain.mentoringpost.entity.QMentoringPost.mentoringPost;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostSearchResponseDto;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.MentoringPostStatus;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MentoringPostRepositoryImpl implements MentoringPostRepositoryCustom {

	private final JPAQueryFactory jpaQueryFactory;

	@PersistenceContext
	private EntityManager entityManager;

	public MentoringPostRepositoryImpl(JPAQueryFactory jpaQueryFactory, EntityManager entityManager) {
		this.jpaQueryFactory = jpaQueryFactory;
		this.entityManager = entityManager;
	}

	@Override
	public Page<MentoringPostSearchResponseDto> findAllBySearchParameters(String title, String field, String nickname, Pageable pageable) {
		BooleanExpression condition = Expressions.asBoolean(true).isTrue();

		if (title != null && !title.isEmpty()) {
			condition = condition.and(filterByTitle(title));
		}
		if (field != null && !field.isEmpty()) {
			condition = condition.and(filterByField(field));
		}
		if (nickname != null && !nickname.isEmpty()) {
			condition = condition.and(filterByNickname(nickname));
		}

		condition = condition.and(filterByMentoringPostStatus(MentoringPostStatus.DISPLAYED));

		// ✅ Native Query 사용 - FULLTEXT INDEX 검색 최적화
		String sql = """
			    SELECT m.id AS mentoringPostId, 
			           u.user_nickname AS userNickname, 
			           m.title AS title, 
			           m.field AS field, 
			           m.career AS career, 
			           COALESCE(AVG(r.star_rating), 0.0) AS starRating, 
			           (MATCH(m.title) AGAINST(:title IN BOOLEAN MODE) + 
			            MATCH(u.user_nickname) AGAINST(:nickname IN BOOLEAN MODE)) AS relevanceScore
			    FROM mentoring_post m
			    LEFT JOIN review r ON r.mentoring_post_id = m.id
			    INNER JOIN user u ON m.mentor_id = u.id
			    WHERE m.mentoring_post_status = 'DISPLAYED'
			    GROUP BY m.id, u.user_nickname, m.title, m.field, m.career
			    ORDER BY relevanceScore DESC, m.created_at DESC
			    LIMIT :limit OFFSET :offset
			""";

		Query query = entityManager.createNativeQuery(sql)
			.setParameter("title", title)
			.setParameter("nickname", nickname)
			.setParameter("limit", pageable.getPageSize())
			.setParameter("offset", pageable.getOffset());

		List<Object[]> resultList = query.getResultList();

		// ✅ DTO 변환 (Native Query 결과를 MentoringPostSearchResponseDto로 매핑)
		List<MentoringPostSearchResponseDto> results = resultList.stream()
			.map(row -> new MentoringPostSearchResponseDto(
				((Number) row[0]).longValue(),  // mentoringPostId
				(String) row[1],  // userNickname
				(String) row[2],  // title
				(String) row[3],  // field
				((Number) row[4]).intValue(),  // career
				row[5] != null ? ((Number) row[5]).doubleValue() : 0.0  // starRating
			))
			.toList();

		// ✅ QueryDSL을 활용한 COUNT 최적화
		long total = jpaQueryFactory.select(mentoringPost.count())
			.from(mentoringPost)
			.where(condition)
			.fetchOne();

		return new PageImpl<>(results, pageable, total);
	}

	// ✅ 닉네임 검색 (LIKE & FULLTEXT 혼합)
	private BooleanExpression filterByNickname(String nickname) {
		if (nickname == null || nickname.isEmpty()) {
			return Expressions.asBoolean(true).isTrue();
		}

		// 2글자 이하이면 LIKE 검색, 3글자 이상이면 FULLTEXT 검색
		return nickname.length() <= 2
			? mentoringPost.user.userNickname.contains(nickname)
			: Expressions.booleanTemplate("MATCH({0}) AGAINST ({1} IN BOOLEAN MODE)",
				mentoringPost.user.userNickname, nickname);
	}

	// ✅ 분야 필터링
	private BooleanExpression filterByField(String field) {
		try {
			return field != null && !field.isEmpty()
				? mentoringPost.field.eq(FieldType.valueOf(field.toUpperCase()))
				: Expressions.asBoolean(true).isTrue();
		} catch (IllegalArgumentException e) {
			return Expressions.asBoolean(false).isFalse(); // 매칭 실패 시 조건 제거
		}
	}

	// ✅ 제목 검색 (LIKE & FULLTEXT 혼합)
	private BooleanExpression filterByTitle(String title) {
		if (title == null || title.isEmpty()) {
			return Expressions.asBoolean(true).isTrue();
		}

		return title.length() <= 2
			? mentoringPost.title.contains(title)
			: Expressions.booleanTemplate("MATCH({0}) AGAINST ({1} IN BOOLEAN MODE)",
				mentoringPost.title, title);
	}

	// ✅ 멘토링 상태 필터
	private BooleanExpression filterByMentoringPostStatus(MentoringPostStatus status) {
		return mentoringPost.mentoringPostStatus.eq(status);
	}
}


