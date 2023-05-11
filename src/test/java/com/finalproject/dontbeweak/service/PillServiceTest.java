package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.model.pill.PillHistory;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.repository.pill.PillHistoryRepository;
import com.finalproject.dontbeweak.repository.pill.PillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class PillServiceTest {

    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PillRepository pillRepository;
    @Autowired
    PillService pillService;
    @Autowired
    PillHistoryRepository pillHistoryRepository;


    @DisplayName("내 영양제 추가")
    @Nested
    class addMyPill {

        @DisplayName("성공")
        @Test
        void success() {
            //given
            Member member = Member.builder()
                    .username("test")
                    .password("1234")
                    .build();

            memberRepository.save(member);

            //when
            Pill pill = pillService.addMyPill(member.getUsername(), "pill01", "white");
            Pill pill1 = pillService.addMyPill(member.getUsername(), "pill02", "red");

            //then
            long count = pillRepository.count();
            Pill pill01 = pillRepository.findByMemberAndProductName(member, "pill01");
            Pill pill02 = pillRepository.findByMemberAndProductName(member, "pill02");

            assertThat(count).as("pill count").isEqualTo(2);
            assertThat(pill01).isEqualTo(pill);
            assertThat(pill02).isEqualTo(pill1);
        }

        @DisplayName("중복 영양제 추가 실패")
        @Test
        void addDuplicatedPill_fail() {
            //given
            Member member = Member.builder()
                    .username("test")
                    .nickname("test")
                    .password("1234")
                    .build();
            memberRepository.save(member);

            pillService.addMyPill(member.getUsername(), "productname01", "white");

            //when
            Throwable thrown = catchThrowable(() -> pillService.addMyPill("test", "productname01", "white"));

            //then
            assertThat(thrown).as("중복 영양제 추가")
                    .isInstanceOf(Exception.class)
                    .hasMessageContaining("이미 등록된 영양제입니다.");

            /*
            * thrown, assertThat 더 가독성있게 -> assertThatThrownBy 사용
            * */
//            assertThatThrownBy(() -> pillService.addMyPill("test", "productname02", "white")).isInstanceOf(CustomException.class);

//            fail("이미 등록된 영양제입니다.");
        }
    }

    @DisplayName("영양제 복용 완료")
    @Test
    void checkMyPill() {
        //given
        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password("password")
                .build();

        memberRepository.save(member);

        Pill pill = new Pill(member, "pill01", "color01");
        pillRepository.save(pill);

        System.out.println("============= pill.getDone() = " + pill.getDone());
        //when
        pillService.checkMyPill(pill.getProductName(), LocalDateTime.now(), member);

        //then
        PillHistory pillHistory = pillHistoryRepository.findById(1L).orElseThrow();

        assertThat(pillHistoryRepository.count()).isEqualTo(1);
        assertThat(pill.getDone()).isTrue();
        assertThat(member.getPoint()).isEqualTo(10);
        assertThat(pillHistory.getPillId()).isEqualTo(pill.getId());
    }

    @DisplayName("내 영양제 삭제 api")
    @Test
    void deleteMyPill() {
        //given
        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password("password")
                .build();

        memberRepository.save(member);

        Pill pill = new Pill(member, "pill01", "color01");
        pillRepository.save(pill);

        LocalDateTime localDateTime = LocalDateTime.of(2023,5,10,6,6);

        PillHistory pillHistory = new PillHistory(member, pill.getProductName(), pill.getCustomColor(), localDateTime, pill.getId());

        pillHistoryRepository.save(pillHistory);

        //when
        pillService.deleteMyPill(pill.getProductName(), member);

        //then
        PillHistory foundHistory = pillHistoryRepository.findById(1L).orElseThrow();
        System.out.println("========== foundHistory.getPillNum() = " + foundHistory.getPillId());

        assertThat(pillRepository.count()).isEqualTo(0);
        assertThat(pillHistoryRepository.count()).isEqualTo(1);
        assertThat(foundHistory.getPillId()).isEqualTo(1L);
    }
}