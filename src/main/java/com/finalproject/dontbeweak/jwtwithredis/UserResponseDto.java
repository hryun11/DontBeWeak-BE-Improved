package com.finalproject.dontbeweak.jwtwithredis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class UserResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo {
        private String grantType;
        private String accessToken;
        private String refreshToken;
        private Long refreshTokenExpirationTime;
        private Long accessTokenExpirationTime;
    }

    @Builder
    @Getter
    @AllArgsConstructor
    public static class deletedUserInfo {
        private Long userId;
        private String username;
        private String nickname;
    }

}
