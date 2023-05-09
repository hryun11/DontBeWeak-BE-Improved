package com.finalproject.dontbeweak.model.item;

import com.finalproject.dontbeweak.dto.item.ItemRequestDto;
import lombok.*;

import javax.persistence.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private int itemPoint;

    @Column(nullable = false)
    private String itemImg;

    public Item(ItemRequestDto itemRequestDto){
        this.itemName = itemRequestDto.getItemName();
        this.itemImg = itemRequestDto.getItemImg();
        this.itemPoint = itemRequestDto.getItemPoint();
    }

    @Builder
    public Item(String itemName, int itemPoint, String itemImg) {
        this.itemName = itemName;
        this.itemPoint = itemPoint;
        this.itemImg = itemImg;
    }
}

