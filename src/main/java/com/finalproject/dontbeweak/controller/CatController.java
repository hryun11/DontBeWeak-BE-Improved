package com.finalproject.dontbeweak.controller;

import com.finalproject.dontbeweak.dto.CatResponseDto;
import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.service.CatService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Api(value = "ApiController v1")
public class CatController {
    private final CatService catService;

    // 내 고양이 조회
    @GetMapping("/cat")
    @ApiOperation(value = "내 고양이 조회")
    public ResponseEntity<CatResponseDto> getMyCatStatus(@AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        Cat cat = catService.getMyCat(username);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CatResponseDto(cat));
    }


    // 친구 고양이 조회
    @GetMapping("/cat/{username}")
    @ApiOperation(value = "친구 고양이 조회")
    public ResponseEntity<CatResponseDto> getFriendsCat
    (@PathVariable("username") String username) {

        Cat friendsCat = catService.getFriendsCat(username);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new CatResponseDto(friendsCat));
    }
}
