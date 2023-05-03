package com.finalproject.dontbeweak.model.item;

import com.finalproject.dontbeweak.common.BaseEntity;
import com.finalproject.dontbeweak.model.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class ItemHistory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column
    private String itemName;

    @Column
    private String username;

    public ItemHistory(Member member, Item item) {
        this.member = member;
        this.username = member.getUsername();
        this.item = item;
        this.itemName = item.getItemName();
    }

}
