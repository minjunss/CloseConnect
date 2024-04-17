package com.CloseConnect.closeconnect.security.oatuh2.cookie;

import io.micrometer.common.util.StringUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class CookieAuthorizationRequestRepository implements AuthorizationRequestRepository {

    public static final String OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME = "oauth2_auth_request";
    public static final String REDIRECT_URI_PARAM_COOKIE_NAME = "redirect_uri";
    private static final int COOKIE_EXPIRE_SECONDS = 180;
    @Override
    public OAuth2AuthorizationRequest loadAuthorizationRequest(HttpServletRequest request) {
        return CookieUtils.getCookie(request, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME)
                .map(cookie -> CookieUtils.deserialize(cookie, OAuth2AuthorizationRequest.class))
                .orElse(null);
    }

    @Override
    public void saveAuthorizationRequest(OAuth2AuthorizationRequest authorizationRequest, HttpServletRequest request, HttpServletResponse response) {
        if (authorizationRequest == null) {
            removeAuthorizationRequestCookies(request, response);
            return;
        }

        CookieUtils.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, CookieUtils.serialize(authorizationRequest), COOKIE_EXPIRE_SECONDS);
        //String redirectUrlAfterLogin = request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME);
        String urlBeforeLogin = extractRedirectUri(request.getParameter(REDIRECT_URI_PARAM_COOKIE_NAME));
        //String redirectUrlAfterLogin = "http://localhost:8080/test1"; //임시
        String redirectUrlAfterLogin = "http://localhost:8080/view/loginRedirect?redirect_uri=" + urlBeforeLogin;
        if (StringUtils.isNotBlank(redirectUrlAfterLogin)) {
            CookieUtils.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, redirectUrlAfterLogin, COOKIE_EXPIRE_SECONDS);
        }
    }

    @Override
    public OAuth2AuthorizationRequest removeAuthorizationRequest(HttpServletRequest request, HttpServletResponse response) {
        return this.loadAuthorizationRequest(request);
    }

    public void removeAuthorizationRequestCookies(HttpServletRequest request, HttpServletResponse response) {
        CookieUtils.deleteCookie(request, response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
        CookieUtils.deleteCookie(request, response, REDIRECT_URI_PARAM_COOKIE_NAME);
    }

    public String extractRedirectUri(String url) {
        String[] parts = url.split("\\?");
        if (parts.length > 1) {
            String paramsPart = parts[1];
            String[] params = paramsPart.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                String paramName = keyValue[0];
                if ("redirect_uri".equals(paramName)) {
                    return keyValue.length > 1 ? keyValue[1] : "";
                }
            }
        }
        return null;
    }
}
