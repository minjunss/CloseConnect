package com.CloseConnect.closeconnect.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class MemberResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private Long accessTokenExpirationTime;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class ResponseDto {
        private Long id;
        private String name;
        private String email;
        private boolean isLoggedIn;
        private Object latitude;
        private Object longitude;
    }


}
