package com.finalproject.dontbeweak.dto.pill;

import com.finalproject.dontbeweak.model.pill.Pill;
import com.finalproject.dontbeweak.model.pill.PillHistory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WeekPillHistoryResponseDto {
    private LocalDateTime usedAt;
    private int dayOfWeek;
    private String productName;
    private String customColor;
    private boolean done;

    public WeekPillHistoryResponseDto(PillHistory pillHistory, int dayOfWeek, Pill pill) {
        this.usedAt = pillHistory.getUsedAt();
        this.dayOfWeek = dayOfWeek;
        this.productName = pillHistory.getProductName();
        this.customColor = pill.getCustomColor();
        this.done = pill.getDone();
    }
}
