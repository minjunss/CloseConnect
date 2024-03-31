package com.CloseConnect.closeconnect.controller.login;

import com.CloseConnect.closeconnect.service.member.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
@Tag(name = "Login API", description = "OAuth2 Login API 입니다.")
public class LoginController {

    private final MemberService memberService;

    // OAuth2 소셜로그인 콜백 API
    //http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:8080/oauth2/callback/{provider}
    @GetMapping("/callback/{provider}")
    @Tag(name = "Login API")
    @Operation(summary = "소셜 로그인 콜백 API", description = "예시: http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:8080/oauth2/callback/google")
    public void oauth2Login(@RequestParam OAuth2UserRequest oAuth2UserRequest,
                            @PathVariable String provider) {
        //memberService.loadUser(oAuth2UserRequest); // OAuth2 로그인 시도 시 OAuth2UserService 구현체 자동 호출
    }
}
