-- 제목 검색 & 닉네임 검색 최적화
ALTER TABLE mentoring_post ADD FULLTEXT INDEX idx_fulltext_title (title);
ALTER TABLE user ADD FULLTEXT INDEX idx_fulltext_user_nickname (user_nickname);
