package com.connect.codeness.domain.mentoringpost.repository;


import static com.connect.codeness.domain.mentoringpost.entity.QMentoringPost.mentoringPost;
import static com.connect.codeness.domain.review.entity.QReview.review;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostSearchResponseDto;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.MentoringPostStatus;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
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
		//queryDSL 필터링
		BooleanExpression condition = Expressions.asBoolean(true).isTrue();

		condition = Stream.of(
				filterByTitleWithoutFullText(title),
				filterByField(field),
				filterByNicknameWithoutFullText(nickname),
				filterByMentoringPostStatus(MentoringPostStatus.DISPLAYED)
			)
			.filter(Objects::nonNull)
			.reduce(BooleanExpression::and)// AND 조건 결합
			.orElse(Expressions.asBoolean(true).isTrue());

		//검색어 길이에 따른 쿼리 분기
		boolean useNativeQuery = (title != null && title.length() >= 3) || (nickname != null && nickname.length() >= 3);

		if (!useNativeQuery) {
			return executeQueryDsl(condition, pageable);
		}
		//Native Query 사용 - FULLTEXT INDEX 검색 최적화
		//3글자 미만 검색어는 MATCH() 수행 안함 - like 사용 & 3글자 이상일때 중간 텍스트 검색시 like 반영
		String sql = """
			SELECT m.id AS mentoringPostId,
			 u.user_nickname AS userNickname,
			 m.title AS title,
			 m.field AS field,
			 m.career AS career,
			 COALESCE(AVG(r.star_rating), 0.0) AS starRating,
			 (IF(:title IS NOT NULL AND LENGTH(:title) >= 3, 
			  			MATCH(m.title) AGAINST(CONCAT(:title, '*') IN BOOLEAN MODE), 0) +
			  IF(:nickname IS NOT NULL AND LENGTH(:nickname) >= 3, 
			   			MATCH(u.user_nickname) AGAINST(CONCAT(:nickname, '*') IN BOOLEAN MODE), 0)
			  			) AS relevanceScore
			FROM mentoring_post m
			LEFT JOIN review r ON r.mentoring_post_id = m.id
			INNER JOIN user u ON m.mentor_id = u.id
			WHERE m.mentoring_post_status = 'DISPLAYED'
			AND (
			 (:title IS NULL) OR
			 (LENGTH(:title) < 3 AND m.title LIKE CONCAT('%', :title, '%')) OR
			 (LENGTH(:title) >= 3 AND 
  				MATCH(m.title) AGAINST(CONCAT(:title, '*') IN BOOLEAN MODE) OR
				m.title LIKE CONCAT('%', :title, '%')
			 ))
			AND (
			 (:nickname IS NULL) OR
			 (LENGTH(:nickname) < 3 AND u.user_nickname LIKE CONCAT('%', :nickname, '%')) OR
			 (LENGTH(:nickname) >= 3 AND 
  				MATCH(u.user_nickname) AGAINST(CONCAT(:nickname, '*') IN BOOLEAN MODE) OR
 				u.user_nickname LIKE CONCAT('%', :nickname, '%')
			 ))
			GROUP BY m.id, u.user_nickname, m.title, m.field, m.career
			ORDER BY relevanceScore DESC, m.created_at DESC
			LIMIT :limit OFFSET :offset
			""";

		//쿼리 실행 및 결과 반환
		Query query = entityManager.createNativeQuery(sql)
			.setParameter("title", (title != null && !title.isEmpty()) ? title : null) //빈 문자열일 경우 null
			.setParameter("nickname", (nickname != null && !nickname.isEmpty()) ? nickname : null)
			.setParameter("limit", pageable.getPageSize())
			.setParameter("offset", pageable.getOffset());

		//Native Query 결과 리스트로 변환
		List<Object[]> resultList = query.getResultList();

		//DTO 변환 (Native Query 결과를 MentoringPostSearchResponseDto로 매핑)
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

		//QueryDSL을 활용한 COUNT 최적화
		long total = jpaQueryFactory.select(mentoringPost.count())
			.from(mentoringPost)
			.where(condition) // QueryDSL 필터만 적용
			.fetchOne();

		return new PageImpl<>(results, pageable, total);
	}

	private Page<MentoringPostSearchResponseDto> executeQueryDsl(BooleanExpression condition, Pageable pageable) {
		List<MentoringPostSearchResponseDto> result = jpaQueryFactory
			.select(Projections.constructor(MentoringPostSearchResponseDto.class,
				mentoringPost.id,
				mentoringPost.user.userNickname,
				mentoringPost.title,
				mentoringPost.field.stringValue(), //string 변환
				mentoringPost.career,
				ExpressionUtils.as(
					JPAExpressions.select(
							Expressions.numberTemplate(
								Double.class,
								"COALESCE(AVG({0}), 0.0)",
								review.starRating
							))
						.from(review)
						.where(review.mentoringPost.eq(mentoringPost)),
					"starRating"
				)
			))
			.from(mentoringPost)
			.where(condition)
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetch();

		long total = countTotalResults(condition);
		return new PageImpl<>(result, pageable, total);
	}

	private long countTotalResults(BooleanExpression condition) {
		return jpaQueryFactory
			.select(mentoringPost.count())
			.from(mentoringPost)
			.where(condition)
			.fetchOne();
	}

	/**
	 * BooleanExpression 필터 메서드 (QueryDSL)
	 * - 제목 검색 (LIKE & FULLTEXT 같이 사용)
	 */
	private BooleanExpression filterByTitleWithoutFullText(String title) {
		//검색어가 없으면 전체 조회(조건 적용 x)
		if (title == null || title.isEmpty()) {
			//필터 적용 x QueryDSL where()절에서 무시됨
			return null;
		}

		//3글자 이상이면 QueryDSL에서 필터 적용 x -> Native Query에서 처리
		return title.length() <= 2 ? mentoringPost.title.contains(title) : null;
	}

	/**
	 * - 닉네임 검색 (LIKE & FULLTEXT 같이 사용)
	 */
	private BooleanExpression filterByNicknameWithoutFullText(String nickname) {
		//검색어가 없으면 전체 조회(조건 적용 x)
		if (nickname == null || nickname.isEmpty()) {
			return Expressions.asBoolean(true).isTrue();
		}

		//3글자 이상이면 QueryDSL에서 필터 적용 x -> Native Query에서 처리
		return nickname.length() <= 2 ? mentoringPost.user.userNickname.contains(nickname) : null;
	}

	/**
	 * - 분야 검색 (LIKE & FULLTEXT 같이 사용)
	 */
	private BooleanExpression filterByField(String field) {
		if (field == null || field.isEmpty()) {
			return null;
		}
		try {
			//올바른 값이면 필터링
			return mentoringPost.field.eq(FieldType.valueOf(field.toUpperCase()));
		} catch (IllegalArgumentException e) {
			log.warn("잘못된 field 값 입력: {}", field);
			//필터 적용 x QueryDSL where()절에서 무시됨
			return null;
		}
	}


	/**
	 * - 멘토링 상태 필터링
	 */
	private BooleanExpression filterByMentoringPostStatus(MentoringPostStatus status) {
		return mentoringPost.mentoringPostStatus.eq(status);
	}
}


