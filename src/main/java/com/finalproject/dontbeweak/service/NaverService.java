package com.finalproject.dontbeweak.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.finalproject.dontbeweak.dto.SocialLoginInfoDto;
import com.finalproject.dontbeweak.auth.JwtTokenProvider;
import com.finalproject.dontbeweak.jwtwithredis.Response;
import com.finalproject.dontbeweak.jwtwithredis.UserResponseDto;
import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.NaverProfile;
import com.finalproject.dontbeweak.model.OAuthToken;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NaverService {
    private final MemberRepository memberRepository;
    private final CatRepository catRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate redisTemplate;
    private final Response response;
    private static final String BEARER_TYPE = "Bearer";


    public SocialLoginInfoDto requestNaver(String code, HttpServletResponse response){

        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type","authorization_code");
        params.add("client_id","k7spc3MRJ9Ut2UUxudqp");
        params.add("client_secret","1feqER4xKL");
        params.add("code",code);
        params.add("client_secret","1feqER4xKL");
        params.add("state", "dontbeweak");

        HttpEntity<MultiValueMap<String, String>> naverTokenRequest = //바디와 헤더값을 넣어준다
                new HttpEntity<>(params, headers); //아래의 exchange가 HttpEntity 오브젝트를 받게 되어있다.

        //Http요청하기 - Post방식으로 - 그리고 responseEntity 변수의 응답 받음.
        ResponseEntity<String> responseEntity = rt.exchange(
                "https://nid.naver.com/oauth2.0/token",
                HttpMethod.POST,
                naverTokenRequest,
                String.class
        );

        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(responseEntity.getBody(), OAuthToken.class);
            System.out.println(oauthToken); //oauthToken 값 확인해 보기
        } catch (
                JsonProcessingException e) {
            e.printStackTrace();
        }
        //엑세스 토큰만 뽑아서 확인
        System.out.println("네이버 엑세스 토큰 : " + oauthToken.getAccess_token());


        RestTemplate rt2 = new RestTemplate();

        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization","Bearer "+oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        HttpEntity<MultiValueMap<String, String>> naverProfileRequest2 = //바디와 헤더값을 넣어준다
                new HttpEntity<>(headers2); //아래의 exchange가 HttpEntity 오브젝트를 받게 되어있다.

        //Http요청하기 - Post방식으로 - 그리고 responseEntity 변수의 응답 받음.
        ResponseEntity<String> response2 = rt2.exchange(
                "https://openapi.naver.com/v1/nid/me",
                HttpMethod.POST,
                naverProfileRequest2,
                String.class
        );

        ObjectMapper objectMapper2 = new ObjectMapper();
        NaverProfile naverProfile = null;
        try {
            naverProfile = objectMapper2.readValue(response2.getBody(), NaverProfile.class);
        } catch (
                JsonProcessingException e) {
            e.printStackTrace();
        }

        System.out.println("네이버 아이디: "+naverProfile.getResponse().getId());
        System.out.println("네이버 닉네임: "+naverProfile.getResponse().getName());
        System.out.println("클라이언트 서버 유저네임 : " + "Naver_" + naverProfile.getResponse().getId());

        Member naverMember = Member.builder()
                .nickname(naverProfile.getResponse().getName())
                .username("Naver_"+naverProfile.getResponse().getId())
                .password(naverProfile.getResponse().getId())
                .oauth("naver")
                .build();

        Member originMember = findByUser(naverMember.getUsername());

        if(originMember.getUsername() == null){
            System.out.println("신규 회원입니다.");
            SignupNaverUser(naverMember);
        }

        // naver 로그인 처리
        System.out.println("naver 로그인 진행중");
        if (naverMember.getUsername() != null) {
            Member memberEntity = memberRepository.findByUsername(naverMember.getUsername()).orElseThrow(
                    () -> new IllegalArgumentException("naver username이 없습니다.")
            );
            UserDetailsImpl userDetails = new UserDetailsImpl(memberEntity);
            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            //홀더에 검증이 완료된 정보 값 넣어준다. -> 이제 controller 에서 @AuthenticationPrincipal UserDetailsImpl userDetails 로 정보를 꺼낼 수 있다.
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 인증 정보를 기반으로 JWT 토큰 생성
            UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

            System.out.println("access token : " + tokenInfo.getAccessToken());
            System.out.println("refresh token : " + tokenInfo.getRefreshToken());
            System.out.println("access token, refresh token 생성 완료");

            // RefreshToken Redis 저장 (expirationTime 설정을 통해 자동 삭제 처리)
            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
            System.out.println("refresh token redis 저장 완료");

            response.addHeader("Authorization", BEARER_TYPE + " " + tokenInfo.getAccessToken());
            System.out.println("JWT토큰 : " + BEARER_TYPE + " " + tokenInfo.getAccessToken());
        }

        String username = naverMember.getUsername();
        String nickname = naverMember.getNickname();

        SocialLoginInfoDto socialLoginInfoDto = new SocialLoginInfoDto(username, nickname);
        return socialLoginInfoDto;

    }


    //신규 네이버 회원 강제 가입
    public String SignupNaverUser(Member naverMember) {
        String error = "";
        String username = naverMember.getUsername();
        String password = naverMember.getPassword();
        String nickname = naverMember.getNickname();
        String oauth = naverMember.getOauth();

        // 패스워드 인코딩
        password = passwordEncoder.encode(password);
        naverMember.setPassword(password);

        Member member = Member.builder()
                .username(username)
                .nickname(nickname)
                .password(password)
                .oauth(oauth)
                .role("ROLE_USER")
                .build();

        memberRepository.save(member);

        Cat cat = new Cat(member, "firstCatImage");
        catRepository.save(cat);

        return error;
    }

    @Transactional(readOnly = true)
    public Member findByUser(String username) {
        Member member = memberRepository.findByUsername(username).orElseGet(
                ()-> {return new Member();}
        );
        return member;
    }

}
