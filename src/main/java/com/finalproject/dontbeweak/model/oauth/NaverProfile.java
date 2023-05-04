package com.finalproject.dontbeweak.model.oauth;

import lombok.Data;


@Data
public class NaverProfile {
    public String resultcode;
    public String message;
    public Response response;

    @Data
    public class Response {
        public String id;
        public String name;
    }
}

