package com.finalproject.dontbeweak.model.pill;

import com.finalproject.dontbeweak.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor
public class PillHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    //영양제 이름
    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private String customColor;

    //복용한 시간
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Column(nullable = true)
    private LocalDateTime usedAt;

    @Column(nullable = false)
    private long pillId;


    public PillHistory(Member member, String productName, String customColor, LocalDateTime usedAt, long pillId) {
        this.member = member;
        this.productName = productName;
        this.customColor = customColor;
        this.usedAt = usedAt;
        this.pillId = pillId;
    }
}
