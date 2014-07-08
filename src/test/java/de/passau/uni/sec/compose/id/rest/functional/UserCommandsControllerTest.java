package de.passau.uni.sec.compose.id.rest.functional;

import static de.passau.uni.sec.compose.id.rest.functional.util.Fixtures.digestRestTemplate;
import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;

import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;

public class UserCommandsControllerTest {

    private RestTemplate digestRestTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String URL = "http://localhost:8080/";

    private String userId;

    private long userLastModified;

    @Before
    public void setup() {
        digestRestTemplate = digestRestTemplate();
    }

    @Test
    public void createAndDeleteUserTest() throws JsonProcessingException {

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

        assertEquals(USERNAME, (String) userCreationResponse.get("username"));
        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());

        // delete user
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        HttpEntity<String> deletionEntity = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDeletion = digestRestTemplate
                .exchange(URL + "idm/user/" + userId, HttpMethod.DELETE,
                        deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }

    @Test
    public void UnauthorizedCreateUserTest() {

        // Create user without authorization
        UserCreateMessage createMessage = new UserCreateMessage();
        createMessage.setUsername(USERNAME);
        createMessage.setPassword(PASSWORD);
        HttpEntity<UserCreateMessage> createUser = new HttpEntity<UserCreateMessage>(
                createMessage);
        RestTemplate restTemplate = new RestTemplate();

        try {
            restTemplate.exchange(URL + "idm/user/", HttpMethod.POST,
                    createUser, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void ConflictingUserCreationTest() {

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

        assertEquals(USERNAME, (String) userCreationResponse.get("username"));
        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());

        // Create the same user again
        RestTemplate difDigestRestTemplate = digestRestTemplate();
        try {
            difDigestRestTemplate.exchange(URL + "idm/user/", HttpMethod.POST,
                    createUser, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        }

        // delete user
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        HttpEntity<String> deletionEntity = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDeletion = digestRestTemplate
                .exchange(URL + "idm/user/" + userId, HttpMethod.DELETE,
                        deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }

    @Test
    public void UserDeletionIfUnmodifiedTest() {

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

        assertEquals(USERNAME, (String) userCreationResponse.get("username"));
        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());

        // delete user with modified last modified
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified + 1));
        HttpEntity<String> deletionEntity = new HttpEntity<String>(header);

        try {
            digestRestTemplate.exchange(URL + "idm/user/" + userId,
                    HttpMethod.DELETE, deletionEntity, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.PRECONDITION_FAILED, e.getStatusCode());
        }

        // delete user
        header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        deletionEntity = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDeletion = digestRestTemplate
                .exchange(URL + "idm/user/" + userId, HttpMethod.DELETE,
                        deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }
}
