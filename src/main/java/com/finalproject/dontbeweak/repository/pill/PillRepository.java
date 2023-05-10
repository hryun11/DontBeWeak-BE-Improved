package com.finalproject.dontbeweak.repository.pill;

import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.pill.Pill;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PillRepository extends JpaRepository<Pill, Long> {
    List<Pill> findByMember_Username(String username);

    boolean existsByMemberAndProductName(Member member, String productName);

    Pill findByMemberAndProductName(Member member, String productName);

    void deleteByProductNameAndMember_Username(String productName, String Username);

    void deletePillByProductNameAndMember(String productName, Member member);

    void deletePillsByMember(Member member);
}