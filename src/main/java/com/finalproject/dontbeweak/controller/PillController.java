package com.finalproject.dontbeweak.controller;


import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.dto.pill.*;
import com.finalproject.dontbeweak.exception.CustomException;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.service.PillService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PillController {
    private final PillService pillService;

    //영양제 등록
    @PostMapping("/schedule")
    @ApiOperation(value = "내 영양제 등록")
    public ResponseEntity<PillResponseDto> addMyPill(
            @RequestBody PillRequestDto pillRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails) {

        String username = userDetails.getUsername();
        String productName = pillRequestDto.getProductName();
        String customColor = pillRequestDto.getCustomColor();

        Pill pill = pillService.addMyPill(username, productName, customColor);

        return ResponseEntity.status(HttpStatus.OK)
                .body(new PillResponseDto(pill));
    }

    //영양제 조회
    @GetMapping("/schedule/{username}")
    @ApiOperation(value = "내 영양제 조회")
    public ResponseEntity<List<PillResponseDto>> getMyPillList(@PathVariable String username) {
        List<PillResponseDto> pillList = pillService.getMyPillList(username);
        return ResponseEntity.ok().body(pillList);
    }

    //영양제 복용 완료
    @PatchMapping("/schedule/week")
    @ApiOperation(value = "영양제 복용 완료")
    public ResponseEntity<PillHistoryResponseDto> checkMyPill(
            @RequestBody PillHistoryRequestDto pillHistoryRequestDto, @AuthenticationPrincipal UserDetailsImpl userDetails){
        if (userDetails == null) {
            throw new CustomException(ErrorCode.NOT_FOUND_USER);
        }
        PillHistoryResponseDto dto = pillService.checkMyPill(pillHistoryRequestDto, userDetails);
        return ResponseEntity.ok().body(dto);

    }

    //주간 영양제 복용 여부 조회
    @GetMapping("/schedule/{username}/week")
    @ApiOperation(value = "주간 영양제 복용 완료 기록 조회")
    public ResponseEntity<List<WeekPillHistoryResponseDto>> getPillHistory
    (@PathVariable String username,
     @ApiParam(value = "주간 조회 시작 날짜", required = true, example = "20220821")
     @RequestParam(value = "startDate", required = false) String startDate,
     @ApiParam(value = "주간 조회 마지막 날짜", required = true, example = "20220827")
     @RequestParam(value = "endDate", required = false) String endDate){

        List<WeekPillHistoryResponseDto> weekPillList = pillService.getPillList(username, startDate, endDate);

        return ResponseEntity.status(HttpStatus.OK)
                .body(weekPillList);
    }
}
