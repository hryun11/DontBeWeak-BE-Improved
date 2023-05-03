package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.model.Cat;
import com.finalproject.dontbeweak.model.CatImage;
import com.finalproject.dontbeweak.repository.CatImageRepository;
import com.finalproject.dontbeweak.repository.CatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.finalproject.dontbeweak.exception.CustomException;
import com.finalproject.dontbeweak.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class CatService {
    private final CatRepository catRepository;
    private final CatImageRepository catImageRepository;

    // 최소 레벨
    public static final Integer MIN_LEVEL = 1;


    // 내 고양이 조회
    @Transactional
    public Cat getMyCat(String username) {
        return catRepository.findByMember_Username(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CAT));
    }

    // 친구 고양이 조회
    @Transactional
    public Cat getFriendsCat(String username) {
        return catRepository.findByMember_Username(username)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CAT));
    }

    // 고양이 경험치 상승
    @Transactional
    public void addExp(Cat cat) {
        cat.addExpAndLevel();
        int resultLevel = cat.getLevel();
        if (resultLevel >= 10 && resultLevel % 10 == 0) {
            changeCatImage(cat, resultLevel);
        }
    }

    // 고양이 이미지 변경
    @Transactional
    public void changeCatImage(Cat cat, int level) {
        CatImage catImage = catImageRepository.findCatImageByChangeLevel(level)
                .orElseThrow(() -> new CustomException(ErrorCode.NOT_FOUND_CATIMAGE));
        int changelevel = catImage.getChangeLevel();
        String changeImage = catImage.getCatImage();

        if (level == changelevel) {
            cat.setImage(changeImage);
        }
    }
}
