package com.finalproject.dontbeweak.publicdata;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ApiRepository extends JpaRepository<Api,Long> {
    @Query(value = "SELECT * FROM api WHERE product LIKE %:product%", nativeQuery = true)
    Page<Api> selectProduct(@Param("product") String product,
                          Pageable pageNo);
}