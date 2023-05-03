package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.dto.FriendRequestDto;
import com.finalproject.dontbeweak.dto.FriendResponseDto;
import com.finalproject.dontbeweak.exception.CustomException;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.model.Friend;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.repository.FriendRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.finalproject.dontbeweak.exception.ErrorCode.FRIEND_ADD_CODE;
import static com.finalproject.dontbeweak.exception.ErrorCode.FRIEND_CHECK_CODE;


@Service
@RequiredArgsConstructor
@Slf4j
public class FriendService {
    private final FriendRepository friendRepository;
    private final MemberRepository memberRepository;

    //친구 추가
    @Transactional
    public void addFriend(FriendRequestDto friendRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //유저에 없는 사람을 추가 못하도록 함
        log.info("가입된 유저가 맞는지 확인");
        Member friend = memberRepository.findByUsername(friendRequestDto.getFriendname())
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을수 없습니다"));

        String nickname = friend.getNickname();

        //자기 자신을 추가 못하게 함
        log.info("추가하려는 친구가 본인인지 확인");
        Member memberTemp = memberRepository.findById(userDetails.getUser().getId())
                .orElseThrow();
        if (memberTemp.getUsername().equals(friendRequestDto.getFriendname()))
            throw new CustomException(FRIEND_ADD_CODE);

        //이미 등록된 친구를 친구추가 할 수 없도록 함

        log.info("친구 중복 체크");

//        List<Friend> friends =userTemp.getFriends();
//        for(Friend overlapUser : friends) {
//            if (overlapUser.getFriendname().equals(friendRequestDto.getFriendname()))
//                throw new CustomException(FRIEND_CHECK_CODE);
//        }

        log.info("친구 저장");
        Friend newFriend = Friend.builder()
                .member(userDetails.getUser())
                .friendname(friendRequestDto.getFriendname())
                .nickname(nickname)
                .build();
        friendRepository.save(newFriend);
    }

    @Transactional
    public FriendResponseDto addFriendV2(FriendRequestDto friendRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {
        //유저에 없는 사람을 추가 못하도록 함
        log.info("가입된 유저가 맞는지 확인");
        String friendname = friendRequestDto.getFriendname();
        Member friend = memberRepository.findByUsername(friendname)
                .orElseThrow(() -> new IllegalArgumentException("유저를 찾을수 없습니다"));

        //자기 자신을 추가 못하게 함
        log.info("추가하려는 친구가 본인인지 확인");
        if (friend.getUsername().equals(userDetails.getUsername())) {
            throw new CustomException(FRIEND_ADD_CODE);
        }

        //이미 등록된 친구를 친구추가 할 수 없도록 함
        log.info("친구 중복 체크");
        if (friendRepository.existsByFriendname(friendname)) {
            throw new CustomException(FRIEND_CHECK_CODE);
        }

        log.info("친구 저장");
        Friend newFriend = Friend.builder()
                .member(userDetails.getUser())
                .friendname(friendname) // 친구 아이디(username)
                .nickname(friend.getNickname())
                .build();
        friendRepository.save(newFriend);

        return new FriendResponseDto(newFriend);
    }

    //친구 목록 조회
    public List<FriendResponseDto> listfriend(@AuthenticationPrincipal UserDetailsImpl userDetails) {

        Member memberTemp = memberRepository.findById(userDetails.getUser().getId())
                .orElseThrow(()->new CustomException(ErrorCode.FRIEND_ADD_CODE));
//        List<Friend> friends = userTemp.getFriends();
        List<FriendResponseDto> responseDtos = new ArrayList<>();
//        for(Friend friend: friends){
//            FriendResponseDto friendResponseDto = new FriendResponseDto(friend.getNickname(), friend.getFriendname());
//            responseDtos.add(friendResponseDto);
//        }
        return responseDtos;
    }

    public List<FriendResponseDto> getFriendList(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        log.info("전체 친구 불러오기");
        List<Friend> friendList = friendRepository.findAllByMember_Username(userDetails.getUsername());

        return friendList.stream()
                .map(FriendResponseDto::new)
                .collect(Collectors.toList());

//        return friendList.stream()
//                .map(friend -> new FriendResponseDto(friend))
//                .collect(Collectors.toList());
    }
}