package com.finalproject.dontbeweak.model;

import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.service.CatService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static com.finalproject.dontbeweak.model.CatImageEnum.LEVEL01;
import static com.finalproject.dontbeweak.model.CatImageEnum.LEVEL20;
import static org.assertj.core.api.Assertions.*;


@SpringBootTest
@Transactional
@Rollback
class CatImageEnumTest {
    @Autowired
    CatRepository catRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    CatService catService;

    @Test
    void EnumTest() {
        System.out.println(LEVEL01.getImageUrl());

        //given
        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password("1234")
                .build();

        Cat cat = new Cat(member);

        member.setCat(cat);

        memberRepository.save(member);
        catRepository.save(cat);

        //when
        cat.setLevel(20);
        System.out.println("================ cat.getLevel() = " + cat.getLevel());

        CatImageEnum[] catImageEnums = CatImageEnum.values();
        for (CatImageEnum catImageEnum : catImageEnums) {
            if (catImageEnum.getLevel() == cat.getLevel()) {
                cat.setImage(catImageEnum.getImageUrl());
            }
        }

        //then
        assertThat(cat.getCatImage()).isEqualTo(LEVEL20.getImageUrl());
    }

}