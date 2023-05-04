package com.finalproject.dontbeweak.repository;

import com.finalproject.dontbeweak.model.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    @EntityGraph(attributePaths = {"cat"})
    Optional<Member> findByUsername(String username);

    @Override
    @EntityGraph(attributePaths = {"cat"})
    List<Member> findAll();

    @EntityGraph(attributePaths = {"cat"})
    Member findMemberByUsername(String username);

//    @Query("select distinct u from User u join u.cat c")
//    Optional<User> findUserByUsername(String username);

//    void deleteUserByUsername(String username);

    void deleteUserByUsername(String username);

    Member findUserById(Long id);

}