package de.passau.uni.sec.compose.id.rest.functional;

import static de.passau.uni.sec.compose.id.rest.functional.util.Fixtures.digestRestTemplate;
import static org.junit.Assert.assertEquals;

import java.util.LinkedHashMap;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class AuthenticateUserControllerTest {
    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String URL = "http://localhost:8080/";

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
    public void authenticateUserTest() {
        UserCredentials ucredentials = new UserCredentials();
        ucredentials.setUsername(USERNAME);
        ucredentials.setPassword(PASSWORD);

        HttpEntity<UserCredentials> authUser = new HttpEntity<UserCredentials>(
                ucredentials);

        ResponseEntity<Object> responseEntityAuthentication = restTemplate
                .exchange(URL + "auth/user/", HttpMethod.POST, authUser,
                        Object.class);

        assertEquals(HttpStatus.OK,
                responseEntityAuthentication.getStatusCode());
    }

    @Test
    public void authenticateInvalidUserTest() {
        UserCredentials ucredentials = new UserCredentials();
        ucredentials.setUsername("Invalid");
        ucredentials.setPassword("Invalid");

        HttpEntity<UserCredentials> authUser = new HttpEntity<UserCredentials>(
                ucredentials);

        try {
            restTemplate.exchange(URL + "auth/user/", HttpMethod.POST,
                    authUser, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }
}
