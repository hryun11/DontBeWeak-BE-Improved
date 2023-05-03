package com.finalproject.dontbeweak.publicdata;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApiService {

    private final ApiRepository apiRepository;

    //공공API 데이터 DB 저장
    @Transactional
    public String parsing() throws IOException, NullPointerException {
        StringBuilder result = null;

        for (int pageNumber = 1; pageNumber <= 354; pageNumber++) {
            String urla = "http://apis.data.go.kr/1471000/HtfsInfoService2/getHtfsItem?"
                    + "ServiceKey=AEwuEzexgJKaPYcUDyX8Z5ZLxbtExL6%2FnS5eaQp6%2Bq7sD%2BEIyFWTgMwUW1qkvL9ZTs30dx5H1xsZyOzFP9bNyA%3D%3D"
                    + "&numOfRows=" + 99
                    + "&pageNo=" + pageNumber
                    + "&type=json";

            URL url = new URL(urla);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(3000);
            urlConnection.setReadTimeout(80000);
            urlConnection.setRequestMethod("GET");
            BufferedReader br;
            br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), StandardCharsets.UTF_8));
            String returnLine;
            result = new StringBuilder();
            ArrayList<StringBuilder> urls = new ArrayList<>();
            while ((returnLine = br.readLine()) != null) {
                result.append(returnLine + "\n\r");
            }
            urls.add(result);
            urlConnection.disconnect();

            try {
               org.json.simple.JSONObject Object;
                //json 객체 생성
                JSONParser jsonParser = new JSONParser();
                //json 파싱 객체 생성
                org.json.simple.JSONObject jsonObject = (org.json.simple.JSONObject) jsonParser.parse(result.toString());

                //데이터 분해
                org.json.simple.JSONObject parseResponse = (org.json.simple.JSONObject) jsonObject.get("body");
                JSONArray array = (JSONArray) parseResponse.get("items");
                for (java.lang.Object o : array) {
                    Object = (JSONObject) o;

                    String entrps = (String) Object.get("ENTRPS");
                    if (entrps == null) entrps = "";
                    String srv_use = (String) Object.get("SRV_USE");
                    if (srv_use == null) srv_use = "";

                    String product = (String) Object.get("PRDUCT");

                    if (!product.equals("null")) {
                        List<Api> apis = new ArrayList<>();
                        Api api = Api.builder()
                                .ENTRPS(entrps)
                                .PRODUCT(product)
                                .SRV_USE(srv_use)
                                .build();
                        apis.add(api);
                        apiRepository.saveAll(apis);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result.toString();
    }

    // 모든 영양제 목록 조회
    public Page<ApiResponseDto> getApi(Pageable pageNo) {
        Page<Api> api = apiRepository.findAll(pageNo);
        return apiResponseDto(api);
    }

    //영양제 검색
    public Page<ApiResponseDto> searchProducts(String product, Pageable pageNo) {
        log.info("product -> {}", product);
        log.info("pageNo -> {}", pageNo);

        Page<Api> products = apiRepository.selectProduct(product, pageNo);

        log.info("result=> {}", products);
        log.info("result=> {}", products.getContent());

        return apiResponseDto(products);

    }

    private Page<ApiResponseDto> apiResponseDto(Page<Api> productSlice) {
        return productSlice.map(p ->
                ApiResponseDto.builder()
                        .product(p.getPRODUCT())
                        .entrps(p.getENTRPS())
                        .srv_use(p.getSRV_USE())
                        .build());
    }

}
