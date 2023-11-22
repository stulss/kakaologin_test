package com.example.login_test.Kakao;

import com.example.login_test.core.security.JwtTokenProvider;
import com.example.login_test.user.User;
import com.example.login_test.user.UserRepository;
import com.example.login_test.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class KakaoService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public String login(String code) {
        KakaoTokenResponse tokens = getAccessToken(code);
        String accessToken = tokens.getAccess_token();
        String refreshToken = tokens.getRefresh_token();

        kakaouser kakaoProfile = getKakaoProfile(accessToken, refreshToken);
        kakaouser.KakaoAccount kakaoAccount = kakaoProfile.getKakao_account();

        // 이메일을 기준으로 사용자를 찾습니다. 없다면 새로운 사용자를 만듭니다.
        User user = userRepository.findByEmail(kakaoAccount.getEmail())
                .orElseGet(() -> userRepository.save(User.builder()
                        .email(kakaoAccount.getEmail())
                        .password(passwordEncoder.encode(accessToken)) // 임시 비밀번호 설정
                        .roles(Collections.singletonList("ROLE_USER"))
                        .username(kakaoAccount.getName())
                        .phoneNumber(kakaoAccount.getPhone_number())
                        .build()));

        return JwtTokenProvider.create(user);
    }

    private KakaoTokenResponse getAccessToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://kauth.kakao.com/oauth/token";

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("grant_type", "authorization_code");
        parameters.add("client_id", "36c959a2f5c7884b843040de20e1b165");
        parameters.add("redirect_uri", "http://localhost:8080/oauth2/kakao/callback");
        parameters.add("code", code);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(parameters, headers);

        ResponseEntity<KakaoTokenResponse> responseEntity = restTemplate.postForEntity(url, request, KakaoTokenResponse.class);

        return responseEntity.getBody();
    }

    private kakaouser getKakaoProfile(String accessToken, String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();

        String url = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);

        HttpEntity<kakaouser> entity = new HttpEntity<>(headers);

        ResponseEntity<kakaouser> responseEntity;
        try {
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, kakaouser.class);
        } catch (HttpClientErrorException.Unauthorized e) {
            // 액세스 토큰이 만료되었을 경우
            accessToken = refreshAccessToken(refreshToken);
            headers.set("Authorization", "Bearer " + accessToken);
            entity = new HttpEntity<>(headers);
            responseEntity = restTemplate.exchange(url, HttpMethod.GET, entity, kakaouser.class);
        }

        return responseEntity.getBody();
    }

    private String refreshAccessToken(String refreshToken) {
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://kauth.kakao.com/oauth/token";
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "refresh_token");
        params.put("client_id", "36c959a2f5c7884b843040de20e1b165");
        params.put("refresh_token", refreshToken);

        return restTemplate.postForObject(url, params, Map.class).get("access_token").toString();
    }
}