package com.finalproject.dontbeweak.repository.pill;

import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class PillRepositoryTest {

    @Autowired PillRepository pillRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CatRepository catRepository;
    @Autowired
    PasswordEncoder passwordEncoder;

    @DisplayName("내 영양제 삭제 테스트")
    @Test
    public void deleteMyPill() {
        //given
        String password = passwordEncoder.encode("1234");

        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password(password)
                .build();

        memberRepository.save(member);

        Pill pill = new Pill(member, "pill01", "color01");
        pillRepository.save(pill);
        System.out.println("========================= 영양제 저장 ===");

        //when
        pillRepository.deleteByProductNameAndMember_Username(pill.getProductName(), member.getUsername());
        System.out.println("============================= 삭제 완료");

        //then
        List<Pill> byUser_username = pillRepository.findByMember_Username(member.getUsername());

        assertThat(byUser_username.size()).isEqualTo(0);
    }

    @DisplayName("내 영양제 전체 삭제")
    @Test
    public void deleteMyPills() {
        //given
        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password("1234")
                .build();

        memberRepository.save(member);

        for (int i = 1; i <=3; i++) {
            String name = "pill"+i;
            Pill pill = new Pill(member, name, "color");
            pillRepository.save(pill);
        }
        System.out.println("========================= 영양제 저장 ===");

        long count = pillRepository.count();
        assertThat(count).isEqualTo(3);

        //when
        pillRepository.deletePillsByMember(member);
        System.out.println("============================ 내 영양제 전체 삭제");

        //then
        long count1 = pillRepository.count();
        assertThat(count1).isEqualTo(0);
    }
}