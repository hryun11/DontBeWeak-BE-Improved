package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.dto.pill.*;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import com.finalproject.dontbeweak.exception.CustomException;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.model.pill.PillHistory;
import com.finalproject.dontbeweak.repository.pill.PillHistoryRepository;
import com.finalproject.dontbeweak.repository.pill.PillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.finalproject.dontbeweak.exception.ErrorCode.PILL_DUPLICATION_CODE;

@Slf4j
@Service
@RequiredArgsConstructor
public class PillService {
    private final PillRepository pillRepository;
    private final MemberRepository memberRepository;
    private final PillHistoryRepository pillHistoryRepository;

    private static final Integer GET_POINT = 10;

    //내 영양제 추가
    @Transactional
    public Pill addMyPill(String username, String productName, String customColor){
        Member member = memberRepository.findByUsername(username).orElseThrow(
                () -> new IllegalArgumentException("회원이 존재하지 않습니다.")
        );

        boolean isProductName = pillRepository.existsByMemberAndProductName(member, productName);

        if(isProductName){
            throw new CustomException(PILL_DUPLICATION_CODE);
        }

        Pill pill = new Pill(member, productName, customColor);
        pillRepository.save(pill);

        return pill;
    }

    //영양제 조회
    public List<PillResponseDto> getMyPillList(String username) {
        List<PillResponseDto> pillResponseDtoList = new ArrayList<>();
        List<Pill> pillList = pillRepository.findByMember_Username(username);

        for (Pill pill : pillList) {
            PillResponseDto pillResponseDto = new PillResponseDto(pill);
            pillResponseDtoList.add(pillResponseDto);
        }
        return pillResponseDtoList;
    }

    //영양제 복용 완료
    @Transactional
    public PillHistoryResponseDto checkMyPill(String productName, LocalDateTime usedAt, Member member) {

        Pill pill = pillRepository.findByMemberAndProductName(member, productName);
        pill.donePill();

        PillHistory pillHistory = new PillHistory(member, productName, pill.getCustomColor(), usedAt, pill.getId());
        pillHistoryRepository.save(pillHistory);

        int userPoint = member.getPoint();
        member.setPoint(userPoint + GET_POINT);

        return new PillHistoryResponseDto(pill, pillHistory);
    }

    //영양제 복용 여부 초기화
    @Transactional
    public Long update(Long id, boolean done){
        Pill pill = pillRepository.findById(id).orElseThrow(
                () -> new IllegalArgumentException("영양제가 존재하지 않습니다.")
        );

        pill.reset(done);
        return id;
    }

    //주간 영양제 복용 조회
    @Transactional
    public List<WeekPillHistoryResponseDto> getPillList(String username, String startDate, String endDate) {
        Member member = memberRepository.findByUsername(username).orElseThrow(()-> new CustomException(ErrorCode.NOT_FOUND_USER));

        LocalDateTime startDateTime = LocalDate.parse(startDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(0, 0, 0);
        LocalDateTime endDateTime = LocalDate.parse(endDate, DateTimeFormatter.ofPattern("yyyyMMdd")).atTime(23, 59, 59);

        List<PillHistory> pillHistoryList = pillHistoryRepository.findAllByMemberAndUsedAtBetween(member, startDateTime, endDateTime);
        List<WeekPillHistoryResponseDto> pillHistoryResponseDtoList = new ArrayList<>();

        for (PillHistory pillHistory : pillHistoryList) {
            int dayOfWeekValue = pillHistory.getUsedAt().getDayOfWeek().getValue();
            int dayOfWeek = dayOfWeekValue - 1;

            Pill pill = pillRepository.findByMemberAndProductName(member, pillHistory.getProductName());

            WeekPillHistoryResponseDto weekPillDto = new WeekPillHistoryResponseDto(pillHistory, dayOfWeek, pill);

            pillHistoryResponseDtoList.add(weekPillDto);
        }

        return pillHistoryResponseDtoList;
    }

    @Transactional
    public String deleteMyPill(String productname, Member member) {
        pillRepository.deletePillByProductNameAndMember(productname, member);
        log.info("영양제 삭제 완료!");

        return productname;
    }
}

