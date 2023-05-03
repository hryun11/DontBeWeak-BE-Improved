package com.finalproject.dontbeweak.repository;

import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CatRepository extends JpaRepository<Cat, Long> {
    Optional<Cat> findByMember_Username(String username);

//    Cat findByMember_Username(String username);

    Cat findCatByMember(Member member);



//    Cat findByUser

}
