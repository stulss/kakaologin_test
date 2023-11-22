package com.example.login_test.Kakao;

import com.example.login_test.core.security.JwtTokenProvider;
import com.example.login_test.core.utils.ApiUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class KakaoController {
    private final KakaoService kakaoService;

    @GetMapping("/oauth2/kakao/callback")
    public ResponseEntity<?> callback(String code) {
        String jwt = kakaoService.login(code);
        return ResponseEntity.ok().header(JwtTokenProvider.HEADER, jwt)
                .body(ApiUtils.success(null));
    }
}