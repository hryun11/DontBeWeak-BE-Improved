package com.finalproject.dontbeweak.dto.item;

import com.finalproject.dontbeweak.model.item.Item;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class BuyItemResponseDto {
    private String username;
    private String itemName;
    private String itemImg;
    private int point;

    public BuyItemResponseDto(Item item, UserDetailsImpl userDetails) {
        this.username = userDetails.getUsername();
        this.itemName = item.getItemName();
        this.itemImg = item.getItemImg();
        this.point = userDetails.getUser().getPoint();
    }
}
