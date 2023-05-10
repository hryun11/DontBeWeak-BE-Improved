package com.finalproject.dontbeweak.model;

import com.finalproject.dontbeweak.common.BaseEntity;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Member extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column
    private String nickname;

    @Column
    private String oauth;

    @Column(nullable = true)
    private int point;

    @Column(nullable = true)
    private String role;

    @Column(length = 1000)
    private String refreshToken;

    @OneToOne(mappedBy = "member", cascade = CascadeType.ALL)
    private Cat cat;


    @Builder
    public Member(String username, String password, String nickname, String oauth, int point, String role, Cat cat) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
        this.oauth = oauth;
        this.point = point;
        this.role = role;
        this.cat = cat;
    }

    public void setPoint(int point) {
        this.point = point;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setCat(Cat cat) {
        this.cat = cat;
    }
}
