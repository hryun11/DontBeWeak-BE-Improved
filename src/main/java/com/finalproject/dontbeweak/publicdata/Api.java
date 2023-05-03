package com.finalproject.dontbeweak.publicdata;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Api {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(length=500)
    private String ENTRPS;

    @Column(length=500)
    private String PRODUCT;

    @Column(length=500)
    private String SRV_USE;

}
