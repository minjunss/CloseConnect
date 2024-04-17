package com.CloseConnect.closeconnect.security.oatuh2;

import java.util.LinkedHashMap;
import java.util.Map;

public class NaverOAuth2User extends OAuth2UserInfo {
    private LinkedHashMap<String, Object> response;

    public NaverOAuth2User(Map<String, Object> attributes) {
        super(attributes);
        response = (LinkedHashMap<String, Object>) attributes.get("response");
    }

    @Override
    public String getOAuth2Id() {
        return (String) response.get("id");
    }

    @Override
    public String getEmail() {
        return (String) response.get("email");
    }

    @Override
    public String getName() {
        return (String) response.get("nickname");
    }
}
