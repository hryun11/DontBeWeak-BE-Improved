package com.finalproject.dontbeweak.service;


import com.finalproject.dontbeweak.dto.item.BuyItemResponseDto;
import com.finalproject.dontbeweak.dto.item.ItemRequestDto;
import com.finalproject.dontbeweak.dto.item.ItemResponseDto;
import com.finalproject.dontbeweak.exception.CustomException;
import com.finalproject.dontbeweak.exception.ErrorCode;
import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.CatImageEnum;
import com.finalproject.dontbeweak.model.Member;
import com.finalproject.dontbeweak.model.item.Item;
import com.finalproject.dontbeweak.model.item.ItemHistory;
import com.finalproject.dontbeweak.repository.CatRepository;
import com.finalproject.dontbeweak.repository.item.ItemHistoryRepository;
import com.finalproject.dontbeweak.repository.item.ItemRepository;
import com.finalproject.dontbeweak.repository.MemberRepository;
import com.finalproject.dontbeweak.auth.UserDetailsImpl;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Setter
@Service
@RequiredArgsConstructor
public class ItemService {
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CatRepository catRepository;
    private final ItemHistoryRepository itemHistoryRepository;



    //아이템 등록
    @Transactional
    public void inputItem(ItemRequestDto requestDto) throws IOException {
        String itemName = requestDto.getItemName();
        String itemImg = requestDto.getItemImg();
        int itemPoint = requestDto.getItemPoint();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .itemName(itemName)
                .itemImg(itemImg)
                .itemPoint(itemPoint)
                .build();

        Item item = new Item(itemRequestDto);
        itemRepository.save(item);

    }


    //아이템 목록 조회
    public List<ItemResponseDto> getItem(){

        List<Item> items = itemRepository.findAll();
        List<ItemResponseDto> itemResponseDtoList = new ArrayList<>();
        for(Item item : items) {
            ItemResponseDto itemResponseDto = new ItemResponseDto(item);
            itemResponseDtoList.add(itemResponseDto);
        }
        return itemResponseDtoList;
    }

    //아이템 구입 및 적용
    @Transactional
    public BuyItemResponseDto patchItem(Long itemId, UserDetailsImpl userDetails) {

        Member member = userDetails.getUser();
        Item item = findItem(itemId);
        String username = userDetails.getUsername();

        // 사용자가 가진 포인트가 부족한 경우
        if (member.getPoint() < item.getItemPoint()) {
            throw new CustomException(ErrorCode.NOT_ENOUGH_MONEY);
        }

        //포인트 감소, 고양이 경험치 상승
        int newPoint = member.getPoint() - item.getItemPoint();
        member.setPoint(newPoint);

        // 아이템 구입 기록 저장
        ItemHistory itemHistory = new ItemHistory(member, item);
        itemHistoryRepository.save(itemHistory);

        // 고양이 경험치 상승
        addCatExp(username);

        return BuyItemResponseDto.builder()
                .username(member.getUsername())
                .itemName(item.getItemName())
                .itemImg(item.getItemImg())
                .point(member.getPoint())
                .build();
    }

    private void addCatExp(String username) {
        Cat cat = catRepository.findByMember_Username(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CAT));
        int currentLevel = cat.getLevel();
        log.info("현재 레벨은 {}입니다.", currentLevel);

        cat.addExpAndLevel(currentLevel, cat.getExp(), cat.getMaxExp());
        log.info("아이템 사용 후 레벨은 {} 입니다.", cat.getLevel());

        if (currentLevel != cat.getLevel() && cat.getLevel() % 10 == 0) {
            String newCatImage = CatImageEnum.valueOf("LEVEL" + currentLevel).getImageUrl();
            cat.setImage(newCatImage);
            log.info("고양이가 진화했습니다!");
        }
    }


    //USER 찾기
    private Member getUser(Long userId) {
        return memberRepository.findById(userId).orElseThrow(
                () -> new CustomException(ErrorCode.NOT_FOUND_USER));
    }

    //아이템 찾기
    private Item findItem(Long itemId) {
        return itemRepository.findById(itemId).orElseThrow(
                () -> new CustomException(ErrorCode.NO_ITEM));
    }
}