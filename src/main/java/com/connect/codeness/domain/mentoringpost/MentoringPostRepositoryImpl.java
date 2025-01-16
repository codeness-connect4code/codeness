package com.connect.codeness.domain.mentoringpost;


import static com.connect.codeness.domain.mentoringpost.QMentoringPost.mentoringPost;
import com.connect.codeness.global.enums.FieldType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class MentoringPostRepositoryImpl implements MentoringPostRepositoryCustom {


	private final JPAQueryFactory jpaQueryFactory;

	public MentoringPostRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	//QueryDSL 사용
	@Override
	public Page<MentoringPost> findAllBySearchParameters(String title, String field, String nickname, Pageable pageable) {

		//쿼리 생성
		JPQLQuery<MentoringPost> jpqlQuery = jpaQueryFactory.selectFrom(mentoringPost)
			.distinct()
			.where(
				filterByTitle(title),
				filterByField(field),
				filterByNickname(nickname)
			)
			.orderBy(mentoringPost.createdAt.desc());

		//페이징
		long total = jpqlQuery.fetchCount();//전체 데이터 개수

		List<MentoringPost> content = jpqlQuery
			.offset(pageable.getOffset()) //현재 페이지 첫번째 데이터 위치 설정
			.limit(pageable.getPageSize()) //한 페이지에서 가져올 최대 데이터 수
			.fetch();

		return new PageImpl<>(content, pageable, total);
	}

	//BooleanExpression 사용 - 닉네임
	private BooleanExpression filterByNickname(String nickname) {
		return nickname != null && !nickname.isEmpty() ? mentoringPost.user.userNickname.containsIgnoreCase(nickname) : null;
	}

	//분야
	private BooleanExpression filterByField(String field) {
		//문자열 입력 받아서 매칭되는 FieldType Enum으로 변환
		return field != null && !field.isEmpty() ? mentoringPost.field.eq(FieldType.fromString(field)) : null;
	}

	//제목
	private BooleanExpression filterByTitle(String title) {
		return title != null && !title.isEmpty() ? mentoringPost.title.containsIgnoreCase(title) : null;
	}

}

