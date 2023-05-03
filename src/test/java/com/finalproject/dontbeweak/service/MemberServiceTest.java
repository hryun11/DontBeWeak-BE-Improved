package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.dto.SignupRequestDtoV2;
import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.repository.pill.PillHistoryRepository;
import com.finalproject.dontbeweak.repository.pill.PillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;


import static org.assertj.core.api.Assertions.*;

@TestPropertySource("classpath:application.properties")
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Transactional
@Rollback
class MemberServiceTest {

    @Autowired
    MemberService memberService;
    @Autowired
    MemberRepository memberRepository;
    @Autowired CatRepository catRepository;
    @Autowired
    PillRepository pillRepository;
    @Autowired
    PillHistoryRepository pillHistoryRepository;


    @DisplayName("회원가입 테스트")
    @Test
    public void register() {
        //given
        SignupRequestDtoV2 requestDtoV2 = new SignupRequestDtoV2("test1", "test1", "1234", "1234");

        //when
        memberService.registerUserV2(requestDtoV2);

        //then
        System.out.println("=============== userRepository.findUserByUsername 쿼리 시작=========");
        Member member = memberRepository.findByUsername("test1").orElseThrow(() -> new IllegalArgumentException("존재하지 않는 아이디입니다."));
        System.out.println("====================================쿼리 끝=========");
        Cat cat = catRepository.findCatByMember(member);

        assertThat(member.getUsername()).isEqualTo("test1");
        assertThat(member.getNickname()).isEqualTo("test1");
        assertThat(cat.getMember().getUsername()).isEqualTo("test1");
    }

    @DisplayName("회원 탈퇴 테스트")
    @Test
    public void deleteAccount() {
        //given
        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password("1234")
                .build();

        Cat cat = new Cat(member, "catImage01");
        member.setCat(cat);

        memberRepository.save(member);
        catRepository.save(cat);

        for (int i = 1; i <=3; i++) {
            String name = "pill"+i;
            Pill pill = new Pill(member, name, "color");
            pillRepository.save(pill);
        }
        System.out.println("pill count = " + pillRepository.count());
        UserDetailsImpl userDetails = new UserDetailsImpl(member);

        //when
        memberService.deleteAccount(userDetails);

        //then
        assertThat(memberRepository.count()).isEqualTo(0);
        assertThat(catRepository.count()).isEqualTo(0);
        assertThat(pillRepository.count()).isEqualTo(0);
        assertThat(pillHistoryRepository.count()).isEqualTo(0);

    }
}