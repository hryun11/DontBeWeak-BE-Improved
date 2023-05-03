package com.finalproject.dontbeweak.auth.interceptor;

import com.finalproject.dontbeweak.auth.JwtTokenProvider;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.jwtwithredis.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.RequestDispatcher;
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
        RequestDispatcher rd = request.getRequestDispatcher("/" + requestURI);

        log.info("==== 1. 액세스 토큰 추출 완료 : {} ====", accessToken);

        if (accessToken == null) {
            log.info("액세스 토큰 없음");
            response.sendRedirect("/login?redirectURL="+requestURI); // 로그인 페이지로
            return false;
        }

        // Access Token 유효성 검증
        if (!jwtTokenProvider.validateAccessToken(accessToken)) {
            log.info("유효하지 않은 액세스 토큰");
            response.sendRedirect("/login?redirectURL="+requestURI); // 로그인 페이지로
            return false;
        }

        // 토큰이 만료되었을경우
        if (!jwtTokenProvider.checkExpiredToken(accessToken)) {

            log.info("==== 만료된 토큰입니다 ====");

            // Redis에서 이전에 만료되어 재발급에 사용된 적 있는 토큰인지 찾기
            String isExpired = (String) redisTemplate.opsForValue().get(accessToken);
            if (!ObjectUtils.isEmpty(isExpired)) {
                log.info("이미 재발급에 사용된 토큰.  폐기 상태");
                log.error(ErrorCode.USED_EXPIRED_TOKEN.getMessage(), ErrorCode.USED_EXPIRED_TOKEN.getStatus());
                customResponse.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);

                response.sendRedirect("/login?redirectURL="+requestURI); // 로그인 페이지로
                return false;

                //TODO
                //여기서 메서드 끝나야함.
            }

            // blacklist에 없지만 사용 기한이 지났을 경우
            if (!jwtTokenProvider.getExpiredAccessTokenlifeSpan(accessToken)) {
                log.info("재발급에 사용될 수 있는 시간 지남");
                log.warn(ErrorCode.INVALIED_EXPIRED_TOKEN.getMessage(), ErrorCode.INVALIED_EXPIRED_TOKEN.getStatus());

                response.sendRedirect("/login?redirectURL="+requestURI); // 로그인 페이지로
                return false;
            }

            // 액세스토큰 재발급
            jwtTokenProvider.regenerateAccessTokenProcess(response, accessToken, customResponse);
            log.info("액세스토큰 재발급 완료");
            rd.include(request, response);  // 작업하던 페이지로 리다이렉트
            return true;
        }

        log.info("==== 3. [PASS] 만료되지 않은 토큰입니다. ====");

        // Redis 에 해당 accessToken logout 여부 확인 (로그아웃 시 RT가 삭제되고 AT가 Redis에 저장됨)
        String isLoggedOut = (String) redisTemplate.opsForValue().get(accessToken);
        log.info("==== BLACKLIST : Redis에서 토큰 찾기 ====");

        //로그아웃된 토큰일 경우
        if (!ObjectUtils.isEmpty(isLoggedOut)) {
            log.info("로그아웃된 유저");
            log.error(ErrorCode.LOGGED_OUT_TOKEN.getMessage(), ErrorCode.LOGGED_OUT_TOKEN.getStatus());
            customResponse.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
            //로그인 페이지로 리다이렉트
            response.sendRedirect("/login?redirectURL="+requestURI);
            return false;
        }

        log.info("==== 4. [PASS] 로그인 상태의 토큰입니다. ====");

        // 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        log.info("==== 5. [PASS] SecurityContext 저장 ====");

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

