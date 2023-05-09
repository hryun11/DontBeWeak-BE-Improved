package com.finalproject.dontbeweak.repository.item;

import com.finalproject.dontbeweak.model.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Item findByItemName(String itemName);

}
