package com.finalproject.dontbeweak.model.pill;

import com.finalproject.dontbeweak.model.Member;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Pill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    //영양제 이름
    @Column(nullable = false)
    private String productName;

    //영양제 색상
    @Column(nullable = false)
    private String customColor;

    //영양제 복용 완료 여부
    @Column(nullable = false)
    private Boolean done;


    @Builder
    public Pill(Member member, String productname, String color) {
        this.member = member;
        this.productName = productname;
        this.customColor = color;
        this.done = false;
    }

    public void donePill() {
        this.done = true;
    }

    public void reset(boolean done){
        this.done = done;
    }
}
