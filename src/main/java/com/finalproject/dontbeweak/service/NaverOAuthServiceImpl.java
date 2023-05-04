package com.finalproject.dontbeweak.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.dontbeweak.auth.JwtTokenProvider;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.dto.SocialLoginInfoDto;
import com.finalproject.dontbeweak.exception.CustomException;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.jwtwithredis.UserResponseDto;
import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.oauth.NaverProfile;
import com.finalproject.dontbeweak.model.oauth.OAuthToken;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class NaverOAuthServiceImpl implements OAuthService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final CatRepository catRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTokenStore;

    private static final String REFRESH_TOKEN_KEY = "RT:";
    private static final String BEARER_TYPE = "Bearer ";

    @Override
    public SocialLoginInfoDto requestOAuthLogin(String code, HttpServletResponse response) {

        OAuthToken oAuthToken = requestOAuthToken(code);

        NaverProfile naverProfile = requestOAuthProfile(oAuthToken);

        Member naverMember = Member.builder()
                .username("Naver_"+naverProfile.getResponse().getId())
                .nickname(naverProfile.getResponse().getName())
                .password(naverProfile.getResponse().getId())
                .oauth("naver")
                .build();

        //가입되지 않은 새 소셜로그인 사용자 회원가입
        String username = naverMember.getUsername();

        if (memberRepository.findMemberByUsername(username) == null) {
            log.info("가입되지 않은 사용자 : 회원가입");
            signUpOAuthMember(naverMember);
        }

        log.info("Naver 로그인 처리 중");
        Member memberEntity = memberRepository.findByUsername(username).orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        UserDetailsImpl userDetails = new UserDetailsImpl(memberEntity);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        /*
        *  컨텍스트홀더에 검증이 완료된 정보 값을 넣어준다.
        *  -> 이제 controller에서 @AuthenticationPrincipal UserDetailsImpl로 유저 정보를 꺼낼 수 있음.
        * */
        SecurityContextHolder.getContext().setAuthentication(authentication);

        generateToken(response, authentication);

        String nickname = naverMember.getNickname();
        return new SocialLoginInfoDto(username, nickname);
    }

    private OAuthToken requestOAuthToken(String code) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        LinkedMultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "k7spc3MRJ9Ut2UUxudqp");
        params.add("client_secret","1feqER4xKL");
        params.add("code", code);
        params.add("client_secret","1feqER4xKL");
        params.add("state", "dontbeweak");

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest
                = new HttpEntity<>(params, headers);    // body, header value

        ResponseEntity<String> responseEntity = restTemplate.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oAuthToken = null;
        try {
            oAuthToken = objectMapper.readValue(responseEntity.getBody(), OAuthToken.class);
            System.out.println("oAuthToken = " + oAuthToken);   //oauthToken 값 확인인
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        //액세스 토큰 확인
        System.out.println("oAuthToken.getAccess_token() = " + oAuthToken.getAccess_token());

        return oAuthToken;
    }

    private NaverProfile requestOAuthProfile(OAuthToken oAuthToken) {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + oAuthToken.getAccess_token());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> profileRequest
                = new HttpEntity<>(headers);

        ResponseEntity<String> profileResponse = restTemplate.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                profileRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        NaverProfile naverProfile = null;
        try {
            naverProfile = objectMapper.readValue(profileResponse.getBody(), NaverProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return naverProfile;
    }

    private void signUpOAuthMember(Member member) {
        String encodePassword = passwordEncoder.encode(member.getPassword());

        Member newOAuthMember = Member.builder()
                .username(member.getUsername())
                .nickname(member.getNickname())
                .password(encodePassword)
                .oauth(member.getOauth())
                .role("ROLE_USER")
                .build();

        memberRepository.save(newOAuthMember);

        Cat cat = new Cat(newOAuthMember, "firstCatImage");
        catRepository.save(cat);
    }

    private void generateToken(HttpServletResponse response, Authentication authentication) {
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        System.out.println("getAccessToken() = " + tokenInfo.getAccessToken());
        System.out.println("getRefreshToken() = " + tokenInfo.getRefreshToken());
        log.info("Access Token, Refresh Token 생성 완료");

        // redis에 refresh token 저장(
        redisTokenStore.opsForValue().set(
                REFRESH_TOKEN_KEY + authentication.getName(),
                tokenInfo.getRefreshToken(),
                tokenInfo.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );
        log.info("Redis 저장소에 Refresh Token 저장 완료");

        response.addHeader(HttpHeaders.AUTHORIZATION, BEARER_TYPE+tokenInfo.getAccessToken());
        log.info("Access Token : {}", BEARER_TYPE + tokenInfo.getAccessToken());
    }
}
