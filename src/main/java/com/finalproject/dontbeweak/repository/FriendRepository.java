package com.finalproject.dontbeweak.repository;

import com.finalproject.dontbeweak.model.Friend;
import com.finalproject.dontbeweak.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FriendRepository extends JpaRepository<Friend,Long> {

    List<Friend> findAllByMember_Username(String username);
    boolean existsByFriendname(String friendname);

}
