package de.passau.uni.sec.compose.id.rest.functional;

import static de.passau.uni.sec.compose.id.rest.functional.util.Fixtures.digestRestTemplate;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;
import de.passau.uni.sec.compose.id.rest.messages.UserPasswordUpdateMessage;

public class UserPasswordControllerTest {
    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String URL = "http://localhost:8080/";

    private String accessToken;

    private String userId;

    private long userLastModified;

    @Before
    public void setup() {
        digestRestTemplate = digestRestTemplate();
        restTemplate = new RestTemplate();

        // Create user
        UserCreateMessage createMessage = new UserCreateMessage();
        createMessage.setUsername(USERNAME);
        createMessage.setPassword(PASSWORD);

        HttpEntity<UserCreateMessage> createUser = new HttpEntity<UserCreateMessage>(
                createMessage);

        ResponseEntity<Object> responseEntityCreation = digestRestTemplate
                .exchange(URL + "idm/user/", HttpMethod.POST, createUser,
                        Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userCreationResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        userId = (String) userCreationResponse.get("id");
        userLastModified = (long) userCreationResponse.get("lastModified");

        // Authenticate user
        UserCredentials ucredentials = new UserCredentials();
        ucredentials.setUsername(USERNAME);
        ucredentials.setPassword(PASSWORD);

        HttpEntity<UserCredentials> authUser = new HttpEntity<UserCredentials>(
                ucredentials);

        ResponseEntity<Object> responseEntityAuthentication = restTemplate
                .exchange(URL + "auth/user/", HttpMethod.POST, authUser,
                        Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> authResponse = (LinkedHashMap<String, Object>) responseEntityAuthentication
                .getBody();

        accessToken = (String) authResponse.get("accessToken");
    }

    @After
    public void tearDown() {
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        HttpEntity<String> deletionEntity = new HttpEntity<String>(header);

        digestRestTemplate.exchange(URL + "idm/user/" + userId,
                HttpMethod.DELETE, deletionEntity, Object.class);
    }

    @Test
    public void changeUserPasswordTest() {

        UserPasswordUpdateMessage updateMsg = new UserPasswordUpdateMessage();
        updateMsg.setOld_password(PASSWORD);
        updateMsg.setNew_password("newPassword");

        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization", "Bearer " + accessToken);
        HttpEntity<UserPasswordUpdateMessage> requestEntity = new HttpEntity<UserPasswordUpdateMessage>(
                updateMsg, detailsTokenHeader);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/user/password/", HttpMethod.PUT, requestEntity,
                Object.class);

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
    }

    @Test
    public void unauthorizedChangeUserPasswordTest() {

        // Request user details with modified access token
        UserPasswordUpdateMessage updateMsg = new UserPasswordUpdateMessage();
        updateMsg.setOld_password(PASSWORD);
        updateMsg.setNew_password("newPassword");

        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization", "Bearer " + accessToken
                + "modified");
        HttpEntity<UserPasswordUpdateMessage> requestEntity = new HttpEntity<UserPasswordUpdateMessage>(
                updateMsg, detailsTokenHeader);

        try {
            restTemplate.exchange(URL + "idm/user/password/", HttpMethod.PUT,
                    requestEntity, Object.class);

        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void changeAnonymousUserPasswordTest() {

        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }

        UserPasswordUpdateMessage updateMsg = new UserPasswordUpdateMessage();
        updateMsg.setOld_password(props.getProperty("anonpassword"));
        updateMsg.setNew_password("newPassword");

        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization",
                "Bearer " + props.getProperty("anontoken"));
        HttpEntity<UserPasswordUpdateMessage> requestEntity = new HttpEntity<UserPasswordUpdateMessage>(
                updateMsg, detailsTokenHeader);

        try {
            restTemplate.exchange(URL + "idm/user/password/", HttpMethod.PUT,
                    requestEntity, Object.class);
        } catch (HttpServerErrorException e) {

            // internal server error with unauthorized message
            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, e.getStatusCode());
        }
    }
}
