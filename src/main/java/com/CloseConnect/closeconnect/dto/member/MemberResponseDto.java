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

}
