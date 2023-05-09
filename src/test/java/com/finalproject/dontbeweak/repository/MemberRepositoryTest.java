package com.finalproject.dontbeweak.repository;

import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class MemberRepositoryTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired CatRepository catRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    EntityManager entityManager;

    @DisplayName("사용자 삭제")
    @Test
    public void deleteUser() {
        //given
        String password = passwordEncoder.encode("1234");

        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password(password)
                .build();

        Cat cat = new Cat(member);

        member.setCat(cat);

        memberRepository.save(member);
        catRepository.save(cat);

        Member foundMember = memberRepository.findUserById(member.getId());
        Cat foundCat = catRepository.findByMember_Username(member.getUsername()).orElseThrow();

        assertThat(foundMember).isEqualTo(member);
        assertThat(foundCat).isEqualTo(cat);

        System.out.println("================================================ 유저 저장");

        //when
        memberRepository.delete(member);

        System.out.println("============================= 유저 삭제");

        // then
        long countUser = memberRepository.count();
        long countCat = catRepository.count();

        assertThat(countUser).isEqualTo(0);
        assertThat(countCat).isEqualTo(0);
    }
}