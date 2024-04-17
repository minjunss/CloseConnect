package com.CloseConnect.closeconnect.security.handler;

import com.CloseConnect.closeconnect.entity.member.Member;
import com.CloseConnect.closeconnect.entity.token.Token;
import com.CloseConnect.closeconnect.repository.member.MemberRepository;
import com.CloseConnect.closeconnect.repository.token.TokenRepository;
import com.CloseConnect.closeconnect.security.oatuh2.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@RequiredArgsConstructor
@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final TokenRepository tokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = jwtTokenProvider.resolveToken(request);
        if (token != null) {
            Token foundToken = tokenRepository.findByToken(token);
            foundToken.registerBlacklist();
            Member member = memberRepository.findByEmail(foundToken.getEmail()).orElse(null);
            member.logout();
            memberRepository.save(member);
            tokenRepository.save(foundToken);
        }
    }
}
