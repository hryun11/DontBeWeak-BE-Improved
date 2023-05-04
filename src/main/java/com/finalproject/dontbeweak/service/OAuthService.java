package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.dto.SocialLoginInfoDto;

import javax.servlet.http.HttpServletResponse;

public interface OAuthService {
    SocialLoginInfoDto requestOAuthLogin(String code, HttpServletResponse response);

}

