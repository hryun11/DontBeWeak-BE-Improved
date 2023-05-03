package com.finalproject.dontbeweak.dto.item;

import com.finalproject.dontbeweak.model.item.Item;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ItemResponseDto {
    private Long itemId;
    private String itemName;
    private String itemImg;
    private int itemPoint;


    public ItemResponseDto(Item item) {
        this.itemId = item.getId();
        this.itemName = item.getItemName();
        this.itemImg = item.getItemImg();
        this.itemPoint = item.getItemPoint();
    }
}
