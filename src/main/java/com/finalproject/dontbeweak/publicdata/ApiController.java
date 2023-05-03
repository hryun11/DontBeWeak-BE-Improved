package com.finalproject.dontbeweak.publicdata;


import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;


@RestController
@RequiredArgsConstructor
public class ApiController {
    private final ApiService apiService;
    public static final int pageSize = 7;

    //공공API 데이터 DB 저장
    @GetMapping("/api")
    public ResponseEntity<String> load_save() throws IOException, NullPointerException {
        apiService.parsing();
        return ResponseEntity.status(HttpStatus.OK)
                .body("공공 데이터가 담겼습니다");
    }

    // 모든 영양제 목록 조회
    @GetMapping("/api/list")
    public ResponseEntity<Page<ApiResponseDto>> api(@PageableDefault(sort = "id", direction = Sort.Direction.ASC, size = pageSize) Pageable pageNo){
        Page<ApiResponseDto> api = apiService.getApi(pageNo);
        return ResponseEntity.status(HttpStatus.OK).body(api);
    }

    // 영양제 검색
    @GetMapping("/api/search")
    public ResponseEntity<Page<ApiResponseDto>> searchProducts(@RequestParam(value = "product", required = false) String product, @PageableDefault(sort = "id", direction = Sort.Direction.ASC, size = pageSize) Pageable pageNo){
        Page<ApiResponseDto> products = apiService.searchProducts(product,pageNo);
        return ResponseEntity.status(HttpStatus.OK).body(products);
    }

}










