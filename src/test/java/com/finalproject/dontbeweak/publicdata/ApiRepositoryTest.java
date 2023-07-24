package com.finalproject.dontbeweak.publicdata;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;

@SpringBootTest
@Transactional
@Rollback(value = false)
class ApiRepositoryTest {

    @Autowired ApiRepository apiRepository;

    @Autowired
    EntityManager entityManager;

    @DisplayName("검색 기능 테스트")
    @Test
    void findByProductNameContainingTest() {

        String productname;

        for (int i = 1; i < 16; i++) {
            productname = "product" + i;

            Api api = Api.builder()
                    .PRODUCT(productname)
                    .build();

            entityManager.persist(api);
        }

        for (int i = 1; i < 11; i++) {
            productname = "비타민" + i;

            Api api = Api.builder().PRODUCT(productname).build();
            entityManager.persist(api);
        }

        System.out.println("======================= Paging 시작");

        Pageable pageable = PageRequest.of(1, 3, Sort.Direction.DESC, "id");

        Page<Api> pages = apiRepository.findByProductNameContaining("비타민", pageable);

        System.out.println("pages.getTotalPages() = " + pages.getTotalPages());
        System.out.println("pages.getTotalElements() = " + pages.getTotalElements());
        System.out.println("================== Paging 끝");

    }
}