package com.finalproject.dontbeweak.publicdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ApiResponseDto {
    private String product;
    private String entrps;
    private String srv_use;
}
