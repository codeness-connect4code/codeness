package com.connect.codeness.global.constants;

public interface Constants {
    // 페이징 상수
    String PAGE_SIZE = "10";
    String PAGE_NUMBER = "0";


    //헤더 상수
    String AUTHORIZATION = "Authorization";
    String BEARER = "Bearer ";
    int ACCESS_TOKEN_EXPIRATION = (int) (15 * 60 * 1000L); // 15분
    int REFRESH_TOKEN_EXPIRATION = (int) (7 * 24 * 60 * 60 * 1000L); // 7일
    String FRONTEND_URL = "http://localhost:3000";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";


    //멘토링 스케줄 상수
    Long SCHEDULE_TIME = 1L;
    Long RETENTION_DAYS = 30L;

    //채팅 관련 상수
    long AUTO_DELETE_WAITING = 100;
    long CHATROOMS_LOAD_TIME = 10;
    long CHATS_LOAD_TIME = 10;

    //뉴스 관련 상수
    long NEWS_TO_KST_HOURS = 32400;
}
