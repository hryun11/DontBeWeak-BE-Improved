package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.auth.JwtTokenProvider;
import com.finalproject.dontbeweak.dto.SignupRequestDtoV2;
import com.finalproject.dontbeweak.jwtwithredis.*;
import com.finalproject.dontbeweak.dto.LoginIdCheckDto;
import com.finalproject.dontbeweak.dto.SignupRequestDto;
import com.finalproject.dontbeweak.exception.CustomException;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.repository.pill.PillHistoryRepository;
import com.finalproject.dontbeweak.repository.pill.PillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final CatRepository catRepository;
    private final PillRepository pillRepository;
    private final PillHistoryRepository pillHistoryRepository;
    private final RedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final Response response;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final HttpServletResponse httpServletResponse;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_TYPE = "Bearer";


    //일반 회원가입
    @Transactional
    public String registerUser(SignupRequestDto requestDto){
        String error = "";
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();
        String nickname = requestDto.getNickname();
//        String pattern = "^[a-zA-Z0-9]*$";
        String pattern = "^[a-zA-Z]{1}[a-zA-Z0-9_]{4,11}$";

        //회원 username 중복 확인
        Optional<Member> found = memberRepository.findByUsername(username);
        if(found.isPresent()){
            throw new CustomException(ErrorCode.USERNAME_DUPLICATION_CODE);
        }

        //회원가입 조건
        if(!Pattern.matches(pattern, username)){
            throw new CustomException(ErrorCode.USERNAME_FORM_CODE);
        }
        if (!password.equals(passwordCheck)){
            throw new CustomException(ErrorCode.PASSWORD_CHECK_CODE);
        } else if (password.length() < 4) {
            throw new CustomException(ErrorCode.PASSWORD_LENGTH_CODE);
        }

        //패스워드 인코딩
        password = passwordEncoder.encode(password);
        requestDto.setPassword(password);

        //유저 정보 저장
        Member member = Member.builder()
                .username(username)
                .nickname(nickname)
                .password(password)
                .role("ROLE_USER")
                .build();

        memberRepository.save(member);

        // 회원가입 후 사용자의 새 고양이 자동 생성
        Cat cat = new Cat(member);
        catRepository.save(cat);

        return error;
    }

    @Transactional
    public String registerUserV2(SignupRequestDtoV2 requestDto){
        String error = "";
        String username = requestDto.getUsername();
        String password = requestDto.getPassword();
        String passwordCheck = requestDto.getPasswordCheck();
        String nickname = requestDto.getNickname();

        //회원 username 중복 확인
        Optional<Member> found = memberRepository.findByUsername(username);
        if(found.isPresent()){
            throw new CustomException(ErrorCode.USERNAME_DUPLICATION_CODE);
        }

        //회원가입 조건
        if (!password.equals(passwordCheck)){
            throw new CustomException(ErrorCode.PASSWORD_CHECK_CODE);
        }

        //패스워드 인코딩
        String encode = passwordEncoder.encode(password);

        //유저 정보 저장
        Member member = Member.builder()
                .username(username)
                .nickname(nickname)
                .password(encode)
                .role("ROLE_USER")
                .build();

        memberRepository.save(member);

        // 회원가입 후 사용자의 새 고양이 자동 생성
        Cat cat = new Cat(member);
        catRepository.save(cat);

        return error;
    }

    //로그인 유저 정보 반환
    public LoginIdCheckDto userInfo(UserDetailsImpl userDetails) {

        String username = userDetails.getUser().getUsername();
        String nickname = userDetails.getUser().getNickname();
        int point = userDetails.getUser().getPoint();

        Optional<Cat> catTemp = catRepository.findByMember_Username(username);
        int level = catTemp.get().getLevel();
        int exp = catTemp.get().getExp();

        LoginIdCheckDto userInfo = new LoginIdCheckDto(username, nickname, point, level, exp);
        return userInfo;
    }


    // 로그아웃
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest) {

        // 1. Request Header에서 토큰 정보 추출
        String accessToken = resolveToken(httpServletRequest);

        // 2. Access Token 검증
        if (!jwtTokenProvider.validateToken(accessToken)) {
            return response.fail("잘못된 요청입니다.", HttpStatus.BAD_REQUEST);
        }

        // 3. Access Token 복호화로 추출한 username으로 authentication 객체 만들기
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

        // 3. Redis 에서 해당 Username으로 저장된 Refresh Token 이 있는지 여부를 확인 후, 있을 경우 삭제.
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            // Refresh Token 삭제
            redisTemplate.delete("RT:" + authentication.getName());
            System.out.println("=== 리프레시 토큰 삭제 완료 ===");
        }

        // 4. 해당 Access Token 유효시간 가지고 와서 BlackList 로 저장하기
        Long expiration = jwtTokenProvider.getExpiration(accessToken);
        redisTemplate.opsForValue()
                .set(accessToken, "logout", expiration, TimeUnit.MILLISECONDS);

        return response.success("로그아웃 되었습니다.");
    }


    // Request Header에서 토큰 정보 추출
    private String resolveToken(HttpServletRequest httpServletRequest) {

        String bearerToken = httpServletRequest.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public Member login(String username, String password) {
        return memberRepository.findByUsername(username)
                .filter(u -> u.getPassword().equals(passwordEncoder.encode(password)))
                .orElse(null);
    }

    //사용자 삭제
    @Transactional
    public UserResponseDto.deletedUserInfo deleteAccount(UserDetailsImpl userDetails) {
        Member member = memberRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_USER));
        log.info("사용자 찾음");

        pillHistoryRepository.deletePillHistoriesByMember(member);
        log.info("사용자 복용 기록 전체 삭제");

        pillRepository.deletePillsByMember(member);
        log.info("사용자 영양제 전체 삭제");

        memberRepository.delete(member);
        log.info("사용자 및 고양이 삭제 완료");

        return UserResponseDto.deletedUserInfo.builder()
                .userId(member.getId())
                .username(member.getUsername())
                .nickname(member.getNickname())
                .build();
    }

//    public ResponseEntity<?> authority() {
//        // SecurityContext에 담겨 있는 authentication userEamil 정보
//        String user = ;
//
//        User user = userRepository.findByUsername(username)
//                .orElseThrow(() -> new UsernameNotFoundException("No authentication information."));
//
//        // add ROLE_ADMIN
//        user.getRole().add(Authority.ROLE_ADMIN.name());
//        userRepository.save(user);
//
//        return response.success();
//    }
}
