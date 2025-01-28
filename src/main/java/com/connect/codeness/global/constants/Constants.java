package com.connect.codeness.global.constants;

public interface Constants {
    String PAGE_SIZE = "10";
    String PAGE_NUMBER = "0";
    String AUTHORIZATION = "Authorization";
    String BEARER = "Bearer ";
    int ACCESS_TOKEN_EXPIRATION = (int) (15 * 60 * 1000L); // 15분
    int REFRESH_TOKEN_EXPIRATION = (int) (7 * 24 * 60 * 60 * 1000L); // 7일
    String FRONTEND_URL = "http://localhost:3000";
    String ACCESS_TOKEN = "access_token";
    String REFRESH_TOKEN = "refresh_token";
}
