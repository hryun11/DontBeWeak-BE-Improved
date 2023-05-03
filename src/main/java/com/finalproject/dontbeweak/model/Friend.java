package com.finalproject.dontbeweak.model;

import lombok.*;

import javax.persistence.*;
@Getter
@Entity
@NoArgsConstructor
public class Friend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String nickname;

    @Column
    private String friendname;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;

    @Builder
    public Friend(String nickname, String friendname, Member member) {
        this.nickname = nickname;
        this.friendname = friendname;
        this.member = member;
    }











}
