package com.finalproject.dontbeweak.controller;


import com.finalproject.dontbeweak.dto.*;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.jwtwithredis.Helper;
import com.finalproject.dontbeweak.jwtwithredis.Response;
import com.finalproject.dontbeweak.jwtwithredis.UserResponseDto;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.service.*;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;


@Slf4j
@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;
    private final KakaoOAuthServiceImpl kakaoOAuthService;
    private final NaverOAuthServiceImpl naverOAuthService;



    //회원가입 요청 처리
//    @PostMapping("/user/signup")
    @ApiOperation(value = "회원가입 요청 처리")
    public String registerUser(@Valid @RequestBody SignupRequestDto requestDto){
        String res = memberService.registerUser(requestDto);
        if(res.equals("")){
            return "회원가입 성공";
        }else{
            return res;
        }
    }
    //회원가입 요청 처리
    @PostMapping("/user/signup")
    @ApiOperation(value = "회원가입 요청 처리")
    public String registerUserV2(@Valid @RequestBody SignupRequestDtoV2 requestDto, BindingResult bindingResult){
        if (bindingResult.hasErrors()) {
            return "user/signup";
        }
        String res = memberService.registerUserV2(requestDto);
        if(res.equals("")){
            return "회원가입 성공";
        }else{
            return res;
        }
    }

//    @PostMapping("/login")
//    public ResponseEntity<?> login(@Validated UserRequestDto.Login login, Errors errors) {
//        // validation check
//        if (errors.hasErrors()) {
//            return response.invalidFields(Helper.refineErrors(errors));
//        }
//        return userService.login(login);
//    }

    //카카오 소셜 로그인
    @GetMapping("/auth/kakao/callback")
    @ApiOperation(value = "카카오 소셜 로그인")
    public @ResponseBody SocialLoginInfoDto kakaoCallback(String code, HttpServletResponse response) {      //ResponseBody -> Data를 리턴해주는 컨트롤러 함수
        return kakaoOAuthService.requestOAuthLogin(code, response);
    }

    //네이버 소셜 로그인
    @GetMapping("/auth/naver/callback")
    @ApiOperation(value = "네이버 소셜 로그인")
    public @ResponseBody SocialLoginInfoDto naverCallback(String code, HttpServletResponse response){
        return naverOAuthService.requestOAuthLogin(code, response);
    }

    //로그인 유저 정보
    @GetMapping("/user")
    @ApiOperation(value = "로그인 유저 정보", notes = "로그인 한 사용자 정보를 조회한다.")
    public LoginIdCheckDto userDetails(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        return memberService.userInfo(userDetails);
    }


    // 로그아웃
    @PostMapping("/user/logout")
    @ApiOperation(value = "로그아웃")
    public ResponseEntity<?> logout(HttpServletRequest httpServletRequest, Response response, Errors errors) {
        // validation check
        if (errors.hasErrors()) {
            return response.invalidFields(Helper.refineErrors(errors));
        }
        return memberService.logout(httpServletRequest);
    }

    //회원 탈퇴
    @DeleteMapping("/delete")
    public ResponseEntity<UserResponseDto.deletedUserInfo> deleteAccount(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {
        UserResponseDto.deletedUserInfo deleteUser = memberService.deleteAccount(userDetails);

        return ResponseEntity.status(HttpStatus.OK).body(deleteUser);
    }

    @PostMapping("/user/reissue")
    public String reissue(@RequestParam(defaultValue = "/") String redirectURL, HttpServletRequest request, HttpServletResponse response) {
        log.info("reissue API");
        memberService.reissue(request, response);

        return "redirect:" + redirectURL;
    }
}