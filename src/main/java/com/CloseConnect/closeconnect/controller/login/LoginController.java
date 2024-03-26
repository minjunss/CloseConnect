package com.CloseConnect.closeconnect.controller.login;

import com.CloseConnect.closeconnect.service.member.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/oauth2")
@RequiredArgsConstructor
public class LoginController {

    private final MemberService memberService;

    // OAuth2 소셜로그인 콜백 API
    //http://localhost:8080/oauth2/authorize/google?redirect_uri=http://localhost:8080/oauth2/callback/{provider}
    @GetMapping("/callback/{provider}")
    public void oauth2Login(@RequestParam OAuth2UserRequest oAuth2UserRequest,
                            @PathVariable String provider) throws Exception {
        System.out.println("==================================1212121212");
        memberService.loadUser(oAuth2UserRequest);

    }
}
