package com.finalproject.dontbeweak.dto;

import com.finalproject.dontbeweak.model.Cat;
import lombok.Getter;

@Getter
public class CatResponseDto {
    private Long id;
    private int level;
    private int exp;
    private int maxExp;
    private String catImg;


    public CatResponseDto(Cat cat) {
        this.id = cat.getId();
        this.level = cat.getLevel();
        this.exp = cat.getExp();
        this.maxExp = cat.getMaxExp();
        this.catImg = cat.getCatImage();
    }
}
