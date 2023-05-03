package com.finalproject.dontbeweak.dto.pill;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor  // 빈 생성자가 없으면 jackson library가 빈 생성자가 없는 모델을 생성하지 못함.  json데이터를 java object로 바꾸지 못해 error
public class PillHistoryRequestDto {

    private String productName;

    @NotNull(message = "현재 시간은 필수입니다.")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime usedAt;

//    private boolean done;

}
