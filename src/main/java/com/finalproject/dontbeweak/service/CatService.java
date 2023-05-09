package com.finalproject.dontbeweak.service;

import com.finalproject.dontbeweak.model.Cat;
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
}
