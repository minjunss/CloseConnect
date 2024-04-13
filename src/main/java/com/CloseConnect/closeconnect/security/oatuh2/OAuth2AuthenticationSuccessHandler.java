package com.CloseConnect.closeconnect.security.oatuh2;

import com.CloseConnect.closeconnect.dto.member.MemberResponseDto;
import com.CloseConnect.closeconnect.security.oatuh2.cookie.CookieAuthorizationRequestRepository;
import com.CloseConnect.closeconnect.security.oatuh2.cookie.CookieUtils;
import com.CloseConnect.closeconnect.security.oatuh2.jwt.JwtTokenProvider;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Optional;

import static com.CloseConnect.closeconnect.security.oatuh2.cookie.CookieAuthorizationRequestRepository.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Value("${oauth.authorized-redirect-uri}")
    private String redirectUri;
    private final JwtTokenProvider jwtTokenProvider;
    private final CookieAuthorizationRequestRepository cookieAuthorizationRequestRepository;

    // 사용자가 OAuth 2.0 인증에 성공했을 때 호출되는 메서드
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 인증 성공 후 리다이렉트할 대상 URL 결정
        String targetUrl = determineTargetUrl(request, response, authentication);
        //targetUrl = "http://" + cookieAuthorizationRequestRepository.extractRedirectUri(targetUrl);

        // 이미 응답이 전송되었는지 확인하고 처리
        if (response.isCommitted()) {
            log.debug("Response has already been committed.");
            return;
        }

        // 인증 속성을 지우고 리다이렉트 전략에 따라 리다이렉트 수행
        clearAuthenticationAttributes(request, response);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    // 리다이렉트할 대상 URL을 결정하는 메서드
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // 리다이렉트 URI 파라미터가 쿠키에 있는지 확인
        Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);

        // 리다이렉트 URI가 존재하고 허용된 리다이렉트 URI인지 확인
        if (redirectUri.isPresent() && !isAuthorizedRedirectUri(redirectUri.get())) {
            throw new RuntimeException("Redirect URIs do not match");
        }

        // 리다이렉트 URI가 존재하지 않으면 기본 대상 URL 사용
        String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

        // JWT 생성
        MemberResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // 토큰을 쿼리 파라미터로 추가하여 URL 생성
        String token = UriComponentsBuilder.fromUriString(targetUrl)
                .queryParam("token", tokenInfo.getAccessToken())
                .build().toUriString();

        return token;
    }

    // 주어진 URI가 허용된 리다이렉트 URI인지 확인하는 메서드
    private boolean isAuthorizedRedirectUri(String uri) {
        URI clientRedirectUri = URI.create(uri);
        URI authorizedUri = URI.create(redirectUri);

        return authorizedUri.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                && authorizedUri.getPort() == clientRedirectUri.getPort();
    }

    // 인증 속성을 지우는 메서드
    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        cookieAuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }
}
