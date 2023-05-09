package com.finalproject.dontbeweak.model;

import java.util.Arrays;

public enum CatImageEnum {

    LEVEL01(1, "catImage01Url"),
    LEVEL10(10, "catImage10Url"),
    LEVEL20(20, "catImage20Url"),
    LEVEL30(30, "catImage30Url");

    private final int level ;
    private final String imageUrl;

    CatImageEnum(int level, String imageUrl) {
        this.level = level;
        this.imageUrl = imageUrl;
    }

    public int level() {
        return level;
    }
    public String getImageUrl() {
        return imageUrl;
    }


    public static CatImageEnum getValueOfLevel(int level) {
        return Arrays.stream(values())
                .filter(catImageEnum -> catImageEnum.level == level)
                .findFirst()
                .orElseThrow(NullPointerException::new);
    }

}
