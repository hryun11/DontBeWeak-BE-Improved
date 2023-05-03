package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback(value = true)
class CatServiceTest {

    @Autowired
    CatService catService;
    @Autowired
    CatRepository catRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("내 고양이 조회 테스트")
    @Test
    public void getMyCat() {
        //given
        String password = passwordEncoder.encode("1234");

        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password(password)
                .build();

        Cat cat = new Cat(member, "catImage01");

        member.setCat(cat);

        memberRepository.save(member);
        catRepository.save(cat);

        //when
        Cat myCat = catService.getMyCat(member.getUsername());

        //then
        assertThat(cat).isEqualTo(myCat);
    }
}