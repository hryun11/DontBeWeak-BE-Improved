package com.finalproject.dontbeweak;

import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.repository.pill.PillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;

//@Component
@RequiredArgsConstructor
public class TestDataInit {

    private final MemberRepository memberRepository;
    private final CatRepository catRepository;
    private final PasswordEncoder passwordEncoder;
    private final PillRepository pillRepository;
    /*
    *  테스트용 유저 데이터
    * */
    @PostConstruct
    public void init() {
        String password = passwordEncoder.encode("1234");
        String name = "test";

        Member member = Member.builder()
                .username(name)
                .nickname(name)
                .password(password)
                .role("ROLE_USER")
                .build();

        Cat cat = new Cat(member, "catImage01");
        catRepository.save(cat);
        memberRepository.save(member);

        for (int i = 1; i <= 100000; i++) {
            String productname = "pill"+i;
            Pill pill = new Pill(member, productname, "color");
            pillRepository.save(pill);
        }
    }
}
