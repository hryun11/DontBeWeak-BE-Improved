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
import com.finalproject.dontbeweak.model.oauth.KakaoProfile;
import com.finalproject.dontbeweak.model.Member;
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
public class KakaoOAuthServiceImpl implements OAuthService {
    private final MemberRepository memberRepository;
    private final CatRepository catRepository;
    private final PasswordEncoder passwordEncoder;
    private final RedisTemplate<String, String> redisTokenStore;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String REFRESH_TOKEN_KEY = "RT:";
    private static final String BEARER_TYPE = "Bearer ";

    @Override
    public SocialLoginInfoDto requestOAuthLogin(String code, HttpServletResponse response) {
        // kakao에 인증용 토큰 요청
        OAuthToken oAuthToken = requestOAuthToken(code);
        // 받은 인증용 토큰과 함께 카카오 유저 정보 요청
        KakaoProfile kakaoProfile = requestOAuthProfile(oAuthToken);

        Member kakaoMember = Member.builder()
                .nickname(kakaoProfile.getKakao_account().getProfile().getNickname())
                .username("Kakao_" + kakaoProfile.getId())
                .password(kakaoProfile.getId().toString()) //임시 비밀번호
                .oauth("kakao")
                .build();

        //가입자 혹은 비가입자 체크해서 처리
        String username = kakaoMember.getUsername();

        if (memberRepository.findMemberByUsername(username) == null) {
            log.info("가입된 적 없는 신규 회원입니다.");
            signUpOAuthMember(kakaoMember); // <-- 이 로직이 자동 로그인 입니다. 지우시면 회원가입 따로 하시면 됩니다.
        }

        // kakao 로그인 처리
        log.info("kakao 로그인 진행 중");
        if (username == null) {
            throw new CustomException(ErrorCode.LOGIN_ERROR_CODE);
        }

        Member memberEntity = memberRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));

        UserDetailsImpl userDetails = new UserDetailsImpl(memberEntity);
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        //홀더에 검증이 완료된 정보 값 넣어준다. -> 이제 controller 에서 @AuthenticationPrincipal UserDetailsImpl userDetails 로 정보를 꺼낼 수 있다.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. 인증 정보를 기반으로 JWT 토큰 생성
        generateToken(response, authentication);

        /*
         *   return "로그인 한 회원의 유저네임: "+kakaoUser.getUsername()+", 닉네임: "+kakaoUser.getNickname();
         * */

        String nickname = kakaoMember.getNickname();

        return new SocialLoginInfoDto(username, nickname);
    }

    private OAuthToken requestOAuthToken(String code) {
        //POST방식으로 key=value 데이터를 요청(카카오쪽으로)
        RestTemplate rt = new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");

        //HttpBody 오브젝트 생성
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", "599bca646044fc4147f7f8f4c461f9ca");
        params.add("redirect_uri", "http://dontbeweak.kr/auth/kakao/callback");
//        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers);

        //Http 요청하기 - POST방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> responseEntity = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );
        //Gson, Json Simple, ObjectMapper 중 하나로 json 데이터를 담는다.
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;

        try {
            oauthToken = objectMapper.readValue(responseEntity.getBody(), OAuthToken.class);
            System.out.println(oauthToken); //oauthToken 값 확인해 보기
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //엑세스 토큰만 뽑아서 확인
        System.out.println("oauthToken.getAccess_token() = " + oauthToken.getAccess_token());

        return oauthToken;
    }

    private KakaoProfile requestOAuthProfile(OAuthToken oAuthToken) {
        RestTemplate rt2 = new RestTemplate();

        //HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add(HttpHeaders.AUTHORIZATION, BEARER_TYPE + oAuthToken.getAccess_token());
        headers2.add(HttpHeaders.CONTENT_TYPE, "application/x-www-form-urlencoded;charset=utf-8");


        //HttpHeader와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest2 = new HttpEntity<>(headers2);

        //Http 요청하기 - POST방식으로 - 그리고 response 변수의 응답 받음.
        ResponseEntity<String> response2 = rt2.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoProfileRequest2,
                String.class
        );

        //KakaoProfile오브젝트를 ObjectMapper로 담는다.
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;

        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        //User 오브젝트: username, password, nickname
        System.out.println("카카오 아이디(번호): " + kakaoProfile.getId());
        System.out.println("카카오 닉네임: " + kakaoProfile.getKakao_account().getProfile().getNickname());
        System.out.println("클라이언트 서버 유저네임 : " + "Kakao_" + kakaoProfile.getId());

        return kakaoProfile;
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

        Cat cat = new Cat(newOAuthMember);
        catRepository.save(cat);
    }

    private void generateToken(HttpServletResponse response, Authentication authentication) {
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        System.out.println("access token : " + tokenInfo.getAccessToken());
        System.out.println("refresh token : " + tokenInfo.getRefreshToken());
        System.out.println("access token, refresh token 생성 완료");

        // 4. RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
        redisTokenStore.opsForValue().set(
                REFRESH_TOKEN_KEY + authentication.getName(),
                tokenInfo.getRefreshToken(),
                tokenInfo.getRefreshTokenExpirationTime(),
                TimeUnit.MILLISECONDS
        );

        log.info("Redis 저장소에 Refresh Token 저장 완료");

        response.addHeader(HttpHeaders.AUTHORIZATION, BEARER_TYPE + tokenInfo.getAccessToken());

        log.info("Access Token : {}", BEARER_TYPE + tokenInfo.getAccessToken());
    }

}
