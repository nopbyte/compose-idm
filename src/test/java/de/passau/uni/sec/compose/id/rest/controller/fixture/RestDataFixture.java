package de.passau.uni.sec.compose.id.rest.controller.fixture;

import org.springframework.http.HttpHeaders;

public class RestDataFixture {

    public static HttpHeaders authorizationHttpHeader() {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "test:pass");

        return header;
    }

    public static HttpHeaders tokenUnmodifiedHttpHeader() {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "token");
        header.set("If-Unmodified-Since", "0");

        return header;
    }
    
    public static HttpHeaders ifUnmodifiedHttpHeader(){
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", "0");

        return header;
    }

    public static String createUserDataJSON() {
        return "{\"username\":\"test\",\"password\":\"pass\"}";
    }

    public static String createApplicationDataJSON() {
        return "{\"authorization\": \"Bearer\",\"id\":\"app_id\",\"name\":\"application1\"}";
    }

    public static String authenticateUserDataJSON() {
        return "{\"username\":\"test\",\"password\":\"pass\"}";
    }
    
    public static String tokenAuthenticationDataJSON(){
        return "{\"authorization\": \"Bearer\"}";
    }

}
