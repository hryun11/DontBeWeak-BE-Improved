package com.finalproject.dontbeweak.auth.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.dontbeweak.auth.JwtTokenProvider;
import com.finalproject.dontbeweak.jwtwithredis.UserResponseDto;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configurers.PortMapperConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RequiredArgsConstructor
public class FormLoginFilter extends UsernamePasswordAuthenticationFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTokenStore;

    public FormLoginFilter(
            AuthenticationManager authenticationManager,
            JwtTokenProvider jwtTokenProvider,
            RedisTemplate redisTemplate) {
        super(authenticationManager);

        this.jwtTokenProvider = jwtTokenProvider;
        this.redisTokenStore = redisTemplate;
    }


    //login 요청하면 로그인 시도를 위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        log.info("JwtAuthenticationFilter: 로그인 시도 중");

        /*
        * 테스트 thymeleaf html form에서 json으로 못 보낼 때
        * */
        log.info("request.getContentType() = {}", request.getContentType());

        if (request.getContentType().equals("application/x-www-form-urlencoded")) {

            String username = request.getParameter("username");
            String password = request.getParameter("password");

            System.out.println("username = " + username);
            System.out.println("password = " + password);

            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, password);

            Authentication authentication = getAuthenticationManager().authenticate(authenticationToken);

            return authentication;
        }

        try {
            ObjectMapper om = new ObjectMapper();
            Member member = om.readValue(request.getInputStream(), Member.class);
            log.info("사용자 : " + member.getUsername());
            log.info("=============================================");

            UsernamePasswordAuthenticationToken authenticationToken =
                    new UsernamePasswordAuthenticationToken(member.getUsername(), member.getPassword());

            Authentication authentication =
                    getAuthenticationManager().authenticate(authenticationToken);

            return authentication;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //attemptAuthentication실행 후 인증이 정상적으로 되었으면 successfulAuthentication 함수가 실행됨.
    //JWT 토큰을 만들어서 request요청한 사용자에게 JWT토큰을 response해주면 됨.
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        log.info("successfulAuthentication 실행됨: 인증이 완료되었다는 뜻.");
        UserDetailsImpl userDetails = (UserDetailsImpl) authResult.getPrincipal();

        //RSA방식은 아니고 Hash암호 방식
//        String jwtToken = JWT.create()
//                .withSubject("cos토큰")
//                .withExpiresAt(new Date(System.currentTimeMillis()+(60000*60)))
//                .withClaim("username",userDetails.getUser().getUsername())
//                .sign(Algorithm.HMAC512("thwjd2"));

        // 3. 인증 정보를 기반으로 JWT AccessToken, RefreshToken 생성
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authResult);

        log.info("ACCESS TOKEN : " + tokenInfo.getAccessToken());
        log.info("REFRESH TOKEN : " + tokenInfo.getRefreshToken());
        log.info("==== access token, refresh token 생성 완료 ====");


        // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
        redisTokenStore.opsForValue()
                .set("RT:" + authResult.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        log.info("==== refresh token redis 저장 완료 ====");

        response.addHeader("Authorization", "Bearer "+tokenInfo.getAccessToken());

        chain.doFilter(request, response);
    }


    //로그인 예외처리
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        log.info(failed.getMessage());
        response.setStatus(400);
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(failed.getMessage());

    }
}