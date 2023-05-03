package com.finalproject.dontbeweak.utils;

import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.repository.pill.PillRepository;
import com.finalproject.dontbeweak.service.PillService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@RequiredArgsConstructor // final 멤버 변수를 자동으로 생성합니다.
@Component // 스프링이 필요 시 자동으로 생성하는 클래스 목록에 추가합니다.
public class Scheduler {
    private final PillRepository pillRepository;
    private final PillService pillService;

    @Scheduled(cron = "0 0 5 * * *")
    public void updateDone() {
        List<Pill> pillList = pillRepository.findAll();
        for(int i = 0; i <pillList.size(); i++) {
            Pill pill = pillList.get(i);
            Long id = pill.getId();
            pillService.update(id, false);
        }
    }
}
