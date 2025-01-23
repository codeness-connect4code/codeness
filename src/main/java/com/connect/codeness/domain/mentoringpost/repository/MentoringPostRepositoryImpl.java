package com.connect.codeness.domain.mentoringpost.repository;


import static com.connect.codeness.domain.mentoringpost.entity.QMentoringPost.mentoringPost;
import static com.connect.codeness.domain.review.entity.QReview.review;

import com.connect.codeness.domain.mentoringpost.dto.MentoringPostSearchResponseDto;
import com.connect.codeness.global.enums.FieldType;
import com.connect.codeness.global.enums.MentoringPostStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Slf4j
@Repository
public class MentoringPostRepositoryImpl implements MentoringPostRepositoryCustom {


	private final JPAQueryFactory jpaQueryFactory;

	public MentoringPostRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	//QueryDSL 사용
	@Override
	public Page<MentoringPostSearchResponseDto> findAllBySearchParameters(String title, String field, String nickname, Pageable pageable) {
		
		//조건을 동적으로 추가
		BooleanExpression condition = Expressions.asBoolean(true).isTrue(); // condition 초기화

		//개별 조건 추가
		if (!(title == null || title.isEmpty())) {
			condition = condition.and(filterByTitle(title)); //제목
		}
		if  (!(field == null || field.isEmpty())) {
			condition = condition.and(filterByField(field)); //분야
		}
		if (!(nickname == null || nickname.isEmpty())) {
			condition = condition.and(filterByNickname(nickname)); //닉네임
		}
		//상태 조건은 항상 추가
		condition = condition.and(filterByMentoringPostStatus(MentoringPostStatus.DISPLAYED));

		log.debug("Final Query Condition: {}", condition);

		//쿼리 생성 & Projections 사용 &  평균 별점 계산 & 상태가 존재인 것만
		JPQLQuery<MentoringPostSearchResponseDto> jpqlQuery = jpaQueryFactory.select(
			//Projections.fields - 필드 이름으로 DTO 매핑
			Projections.fields(MentoringPostSearchResponseDto.class,
				mentoringPost.id.as("mentoringPostId"),
				mentoringPost.user.userNickname.as("userNickname"),
				mentoringPost.title.as("title"),
				mentoringPost.field.stringValue().as("field"), //enum에서 string으로 변환
				mentoringPost.career.as("career"),
				review.starRating.avg().coalesce(0.0).as("starRating")//null 처리하기
			)
		).from(mentoringPost)
			.leftJoin(review).on(review.mentoringPost.eq(mentoringPost))
			.join(mentoringPost.user)
			.where(condition) //필터
			.groupBy(
				mentoringPost.id,
				mentoringPost.user.userNickname,
				mentoringPost.title,
				mentoringPost.field,
				mentoringPost.career
			)
			.orderBy(mentoringPost.createdAt.desc());//최신순 정렬

		//페이징
		long total = jpqlQuery.fetchCount();//전체 데이터 개수

		List<MentoringPostSearchResponseDto> content = jpqlQuery
			.offset(pageable.getOffset()) //현재 페이지 첫번째 데이터 위치 설정
			.limit(pageable.getPageSize()) //한 페이지에서 가져올 최대 데이터 수
			.fetch();

		return new PageImpl<>(content, pageable, total);
	}

	//BooleanExpression 사용 - 닉네임
	private BooleanExpression filterByNickname(String nickname) {
		return !(nickname == null || nickname.isEmpty()) ? mentoringPost.user.userNickname.containsIgnoreCase(nickname)
			: Expressions.asBoolean(true).isTrue(); //기본 조건
	}

	//분야
	private BooleanExpression filterByField(String field) {
		try {
			return !(field == null || field.isEmpty())
				? mentoringPost.field.eq(FieldType.valueOf(field.toUpperCase()))//대문자 변환 후, enum으로 변환
				: Expressions.asBoolean(true).isTrue(); //조건 없음
		} catch (IllegalArgumentException e) {
			return Expressions.asBoolean(false).isFalse();//값이 잘못 되었거나 매칭이 안될 경우, 결과 반환 x
		}
	}

	//제목
	private BooleanExpression filterByTitle(String title) {
		return !(title == null || title.isEmpty()) ? mentoringPost.title.containsIgnoreCase(title)
			: Expressions.asBoolean(true).isTrue();
	}

	//멘토링 상태
	private BooleanExpression filterByMentoringPostStatus(MentoringPostStatus mentoringPostStatus){
		return mentoringPost.mentoringPostStatus.eq(mentoringPostStatus);
	}

}

