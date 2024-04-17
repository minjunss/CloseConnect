package com.CloseConnect.closeconnect.security.oatuh2;

import java.util.Map;

public class KakaoOAuth2User extends OAuth2UserInfo{
    public KakaoOAuth2User(Map<String, Object> attributes) {
        super(attributes);
    }

    @Override
    public String getOAuth2Id() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getEmail() {
        return (String) ((Map<String, Object>)attributes.get("kakao_account")).get("email");
    }

    @Override
    public String getName() {
        return (String) ((Map<String, Object>)attributes.get("properties")).get("nickname");
    }
}
