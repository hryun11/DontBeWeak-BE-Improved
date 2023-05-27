package com.finalproject.dontbeweak.auth.interceptor;

import com.finalproject.dontbeweak.auth.JwtTokenProvider;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.jwtwithredis.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationInterceptor implements HandlerInterceptor {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final Response customResponse;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Request Header 에서 JWT 토큰 추출
        String accessToken = resolveToken(request);
        String requestURI = request.getRequestURI();

        log.info("==== 1. 액세스 토큰 추출 완료 : {} ====", accessToken);

        if (accessToken == null) {
            log.error("액세스 토큰 없음");
            response.sendRedirect("/login?redirectURL="+requestURI); // 로그인 페이지로
            return false;
        }

        // Access Token 유효성 검증
        if (!jwtTokenProvider.validateAccessToken(accessToken)) {
            log.error("유효하지 않은 액세스 토큰");
            response.sendRedirect("/login?redirectURL="+requestURI); // 로그인 페이지로
            return false;
        }

        // 토큰이 만료되지 않았을 때
        if (jwtTokenProvider.checkExpiredToken(accessToken)) {
            log.info("==== [PASS] 만료되지 않은 토큰입니다. ====");

            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return true;
        }

        log.warn("==== 만료된 토큰입니다 ====");

        // Redis(blacklist)에 존재하는 액세스 토큰인지 확인(이전에 만료되어 재발급에 사용된 적 있는 토큰인지,
        String isExpired = (String) redisTemplate.opsForValue().get(accessToken);

        if (!ObjectUtils.isEmpty(isExpired)) {
            log.error("This access token exists in Redis store");

            response.sendRedirect("/login?redirectURL="+requestURI); // 로그인 페이지로
            return false;
        }

        // blacklist에서도 만료되어 삭제된 토큰인지
        if (!jwtTokenProvider.checkDeletedToken(accessToken)) {
            log.error("재발급에 사용될 수 있는 시간 지남");

            response.sendRedirect("/login?redirectURL="+requestURI); // 로그인 페이지로
            return false;
        }

        // 액세스토큰 재발급
        jwtTokenProvider.regenerateAccessToken(response, accessToken, customResponse);
        log.info("액세스토큰 재발급 완료");
        return true;



    }

    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

