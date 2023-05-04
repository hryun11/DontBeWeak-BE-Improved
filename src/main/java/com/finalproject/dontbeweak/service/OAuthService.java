package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.dto.SocialLoginInfoDto;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.OAuthToken;

import javax.servlet.http.HttpServletResponse;

public interface OAuthService {
    SocialLoginInfoDto requestOAuth(String code, HttpServletResponse response);

    OAuthToken requestOAuthToken(String code);

    void signUpOAuthMember(Member member);
}

