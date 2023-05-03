package com.finalproject.dontbeweak.dto.item;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@Builder
public class ItemRequestDto {
    private String itemName;
    private String itemImg;
    private int itemPoint;

}
