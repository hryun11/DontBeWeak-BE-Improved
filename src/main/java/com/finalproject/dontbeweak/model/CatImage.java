package com.finalproject.dontbeweak.model;

import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class CatImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private int changeLevel;

    @Column
    private String catImage;

}
