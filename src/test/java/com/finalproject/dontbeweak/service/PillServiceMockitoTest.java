package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.repository.pill.PillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PillServiceMockitoTest {

    @DisplayName("내 영양제 추가")
    @Nested
    class addMyPill {

        @Mock
        private MemberRepository memberRepository;
        @Mock
        private PillRepository pillRepository;
        @Spy
        @InjectMocks
        private PillService pillService;

        @DisplayName("성공")
        @Test
        void success() {
            //given
            Member member = Member.builder()
                    .username("test")
                    .nickname("test")
                    .password("1234")
                    .build();

            when(memberRepository.save(member)).thenReturn(member);
            memberRepository.save(member);

            //when
            when(memberRepository.findByUsername(member.getUsername())).thenReturn(Optional.of(member));

            ArrayList<Pill> pillList = new ArrayList<>();
            int j=0;
            for (int i = 1; i <= 5; i++) {
                String productName = "productname0"+i;

                if (i > 1) {
                    if (pillList.get(j).getProductName().equals(productName)) {
                        when(pillRepository.existsByMemberAndProductName(member, productName))
                                .thenReturn(true);
                        System.out.println("==================true : 중복 존재 ===");
                    } else {
                        when(pillRepository.existsByMemberAndProductName(member, productName))
                                .thenReturn(false);
                        System.out.println("===================false1 : 통과 ======");
                    }
                }
                if (i == 1){
                    when(pillRepository.existsByMemberAndProductName(member, productName))
                            .thenReturn(false);
                    System.out.println("=================false2: 통과 =======");
                }
                if (i != 1) {
                    j++;
                }
                Pill pill = this.pillService.addMyPill(member.getUsername(), productName, "white");
                pillList.add(pill);
            }

            for (Pill pill : pillList) {
                System.out.println("pill.getProductName() = " + pill.getProductName());
            }

            //then
            given(pillRepository.findAll()).willReturn(pillList);

            assertThat(pillRepository.findAll().size()).as("pillRepository.findAll()").isEqualTo(5);
            assertThat(pillList.get(0).getProductName()).as("pill1 productname").isEqualTo("productname01");
            assertThat(pillList.get(1).getProductName()).as("pill2 productname").isEqualTo("productname02");
        }

        @DisplayName("중복 영양제 추가")
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
}
