package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.CatImageEnum;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.item.Item;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.repository.item.ItemRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
@Nested
class ItemServiceTest {
    @Autowired ItemService itemService;
    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CatRepository catRepository;


    @DisplayName("아이템 등록")
    @Test
    public void InputItem() {
        System.out.println("====================================");
        System.out.println("ItemServiceTest.InputItem");
        itemService.inputItem("item01", "itemImg01", 5);

        Item byId = itemRepository.findById(1L).orElseThrow();
        assertThat(itemRepository.count()).isEqualTo(1);
        assertThat(byId.getItemName()).isEqualTo("item01");
    }


    @DisplayName("아이템 구입 및 적용")
    @Test
    void patchItem() {
        System.out.println("====================================");
        System.out.println("ItemServiceTest.patchItem");

        //given
        Member member = Member.builder()
                .username("test")
                .nickname("test")
                .password("password")
                .point(40)
                .build();

        Cat cat = new Cat(member);

        member.setCat(cat);

        memberRepository.save(member);
        catRepository.save(cat);

        cat.setLevel(9);
        cat.setExp(15);

        itemService.inputItem("item01", "itemImg01", 5);

        Item item01 = itemRepository.findByItemName("item01");

        UserDetailsImpl userDetails = new UserDetailsImpl(member);

        //when
        itemService.patchItem(item01.getId(), userDetails);

        //then
        assertThat(member.getPoint()).isEqualTo(35);
        assertThat(cat.getExp()).isEqualTo(0);
        assertThat(cat.getLevel()).isEqualTo(10);
        assertThat(cat.getCatImage()).isEqualTo(CatImageEnum.LEVEL10.getImageUrl());
    }
}