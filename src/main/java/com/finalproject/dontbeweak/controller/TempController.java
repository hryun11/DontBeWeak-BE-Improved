package com.finalproject.dontbeweak.controller;

import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/v1")
public class TempController {

    private final MemberRepository memberRepository;

    @GetMapping("/test")
    public String testApi() {
        return "test";
    }

    @GetMapping("/findAuth/{username}")
    public Optional<Member> findAuthByUsername(@PathVariable("username") String username) {
        log.info("유저 정보 조회 시작");
        return memberRepository.findByUsername(username);
    }

    @GetMapping("/findAll")
    public List<Member> findAll() {
        return memberRepository.findAll();
    }
}