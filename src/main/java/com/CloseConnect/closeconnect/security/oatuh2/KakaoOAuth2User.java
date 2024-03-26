package com.CloseConnect.closeconnect.security.oatuh2;

import java.util.Map;

public class KakaoOAuth2User extends OAuth2UserInfo{
    public KakaoOAuth2User(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getOAuth2Id() {
        return (String) attributes.get("kakao_account");
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map<String, Object>)attributes.get("profile")).get("nickname");
    }
}
