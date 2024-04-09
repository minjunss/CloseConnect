package com.CloseConnect.closeconnect.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

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
        private boolean isLoggedIn;
        private Double latitude;
        private Double longitude;
    }


}
