package com.finalproject.dontbeweak.dto.pill;

import com.finalproject.dontbeweak.model.pill.Pill;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PillResponseDto {
    private String productName;
    private String customColor;
    private boolean done;

    public PillResponseDto(Pill pill){
        this.productName = pill.getProductName();
        this.customColor = pill.getCustomColor();
        this.done = pill.getDone();
    }
}