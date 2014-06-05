package de.passau.uni.sec.compose.id.rest.controller.fixture;

import org.springframework.http.HttpHeaders;

public class RestDataFixture {

    public static HttpHeaders AuthorizationHttpHeader() {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "test:pass");
        
        return header;
    }

    public static String createUserDataJSON() {
        return "{\"username\":\"test\",\"password\":\"pass\"}";
    }

}
