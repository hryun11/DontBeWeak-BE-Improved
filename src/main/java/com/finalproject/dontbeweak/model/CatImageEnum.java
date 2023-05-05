package com.finalproject.dontbeweak.model;

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


    public String getImageUrl() {
        return imageUrl;
    }

    public int getLevel() {
        return level;
    }
}
