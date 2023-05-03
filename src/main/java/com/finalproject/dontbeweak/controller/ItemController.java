package com.finalproject.dontbeweak.controller;

import com.finalproject.dontbeweak.dto.item.BuyItemResponseDto;
import com.finalproject.dontbeweak.dto.item.ItemRequestDto;
import com.finalproject.dontbeweak.dto.item.ItemResponseDto;
import com.finalproject.dontbeweak.exception.CustomException;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.model.item.Item;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.service.ItemService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    //아이템 목록 조회
    @GetMapping("/items")
    @ApiOperation(value = "아이템 목록 조회")
    public ResponseEntity<List<ItemResponseDto>> getItem(@AuthenticationPrincipal UserDetailsImpl userDetails) {
        if (userDetails == null) {
            // 유저가 없다는 의미이므로 비정상 페이지 리턴
            throw new CustomException(ErrorCode.LOGIN_CHECK_CODE);
        } else {
            List<ItemResponseDto> itemResponseDtoList = itemService.getItem();
            return ResponseEntity.ok().body(itemResponseDtoList);
        }
    }

   // 아이템 구입 및 적용
    @PatchMapping("/items/{itemId}")
    @ApiOperation(value = "아이템 구입 및 적용")
    public ResponseEntity<BuyItemResponseDto> patchItem(@PathVariable Long itemId, @AuthenticationPrincipal UserDetailsImpl userDetails){
        if (userDetails == null) {
            // 유저가 없다는 의미이므로 비정상 페이지 리턴
            throw new CustomException(ErrorCode.LOGIN_CHECK_CODE);
        } else {
            BuyItemResponseDto buyItemResponseDto = itemService.patchItem(itemId, userDetails);
            return ResponseEntity.status(HttpStatus.CREATED).body(buyItemResponseDto);
        }
    }

    //아이템 등록
    @PostMapping("/items")
    public ResponseEntity<Item> inputItem(@RequestBody ItemRequestDto itemRequestDto) throws IOException {
        itemService.inputItem(itemRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(null);
    }
}
