package com.example.login_test.Kakao;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Getter
@RequiredArgsConstructor
public class KakaoUri {

    private final String KAKAO_TOKEN_API_URL = "https://kauth.kakao.com/oauth/token";
    private final String KAKAO_USER_ME_API_URL = "https://kapi.kakao.com/v2/user/me";
    private final String KAKAO_ACCESS_TOKEN_URL = "https://kapi.kakao.com/v1/user/access_token_info";
    private final String KAKAO_MESSAGE_URL = "https://kapi.kakao.com/v2/api/talk/memo/default/send";
    private final String CLIENT_ID = "a37ca1b91468aaf37e93f7d3f11b5860";
    private final String REDIRECT_URI = "http://localhost:8080/oauth/kakao";
    private final String KAKAO_LOGOUT_URL = "https://kauth.kakao.com/oauth/logout";
}