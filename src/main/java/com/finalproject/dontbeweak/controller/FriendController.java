package com.finalproject.dontbeweak.controller;

import com.finalproject.dontbeweak.dto.FriendRequestDto;
import com.finalproject.dontbeweak.dto.FriendResponseDto;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.service.FriendService;
import io.swagger.annotations.ApiOperation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/friend")
public class FriendController {
    private FriendService friendService;


    //친구 추가
    @PostMapping
    @ApiOperation(value = "친구 추가")
    public ResponseEntity<FriendRequestDto> addFriend(@RequestBody FriendRequestDto friendRequestDto,
                                                      @AuthenticationPrincipal UserDetailsImpl userDetails){
        friendService.addFriend(friendRequestDto,userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(null);
    }

    //친구 추가
    @PostMapping("/v2")
    @ApiOperation(value = "친구 추가")
    public ResponseEntity<FriendResponseDto> addFriendV2(@RequestBody FriendRequestDto friendRequestDto,
                                                         @AuthenticationPrincipal UserDetailsImpl userDetails){
        FriendResponseDto friendResponseDto = friendService.addFriendV2(friendRequestDto, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(friendResponseDto);
    }

    //친구 목록 조회
    @GetMapping
    @ApiOperation(value = "내 친구 목록 조회")
    public  ResponseEntity<List<FriendResponseDto>> listfriend(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<FriendResponseDto> friendResponseDtoList = friendService.listfriend(userDetails);
        return ResponseEntity.status(HttpStatus.OK)
                .body(friendResponseDtoList);
    }

    @GetMapping("/v2")
    @ApiOperation(value = "내 친구 목록 조회")
    public  ResponseEntity<List<FriendResponseDto>> getFriendList(@AuthenticationPrincipal UserDetailsImpl userDetails){
        List<FriendResponseDto> friendList = friendService.getFriendList(userDetails);
        return ResponseEntity.status(HttpStatus.OK)
                .body(friendList);
    }
}
