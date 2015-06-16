package de.passau.uni.sec.compose.id.rest.functional;

import static de.passau.uni.sec.compose.id.rest.functional.util.Fixtures.digestRestTemplate;
import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Properties;

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
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

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
    public void createUpdateAndDeleteUserTest() throws JsonProcessingException {

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

        // Authenticate user
        UserCredentials ucredentials = new UserCredentials();
        ucredentials.setUsername(USERNAME);
        ucredentials.setPassword(PASSWORD);

        HttpEntity<UserCredentials> authUser = new HttpEntity<UserCredentials>(
                ucredentials);

        ResponseEntity<Object> responseEntityAuthentication = digestRestTemplate
                .exchange(URL + "auth/user/", HttpMethod.POST, authUser,
                        Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> authResponse = (LinkedHashMap<String, Object>) responseEntityAuthentication
                .getBody();

        String accessToken = (String) authResponse.get("accessToken");

        // update user
        createMessage = new UserCreateMessage();
        createMessage.setUsername("newUsername");
        createMessage.setPassword(PASSWORD);
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        header.set("Authorization", "Bearer " + accessToken);

        HttpEntity<UserCreateMessage> updateUser = new HttpEntity<UserCreateMessage>(
                createMessage, header);

        ResponseEntity<Object> responseEntityUpdate = digestRestTemplate
                .exchange(URL + "idm/user/" + userId, HttpMethod.PUT,
                        updateUser, Object.class);

        assertEquals(HttpStatus.OK, responseEntityUpdate.getStatusCode());

        // delete user
        header = new HttpHeaders();
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

    @Test
    public void deleteAnonymousUserTest() {

        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }

        // Request anonymous user details for lastModified
        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization",
                "Bearer " + props.getProperty("anontoken"));
        HttpEntity<String> requestEntity = new HttpEntity<String>(
                detailsTokenHeader);

        ResponseEntity<Object> responseEntityDetails = digestRestTemplate
                .exchange(URL + "idm/user/info/", HttpMethod.GET,
                        requestEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        // delete anonymous user
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since",
                String.valueOf(userDetailsResponse.get("lastModified")));
        HttpEntity<String> deletionEntity = new HttpEntity<String>(header);

        try {
            digestRestTemplate.exchange(
                    URL + "idm/user/" + props.getProperty("anonid"),
                    HttpMethod.DELETE, deletionEntity, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void updateAnonymousUserTest() {

        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }

        // Request user details for last modified
        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization",
                "Bearer " + props.getProperty("anontoken"));
        HttpEntity<String> requestEntity = new HttpEntity<String>(
                detailsTokenHeader);

        ResponseEntity<Object> responseEntityDetails = digestRestTemplate
                .exchange(URL + "idm/user/info/", HttpMethod.GET,
                        requestEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        // update anonymous user
        UserCreateMessage createMessage = new UserCreateMessage();
        createMessage.setUsername(props.getProperty("anonusername"));
        createMessage.setPassword(props.getProperty("anonpassword"));
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since",
                String.valueOf(userDetailsResponse.get("lastModified")));
        header.set("Authorization", "Bearer " + props.getProperty("anontoken"));

        HttpEntity<UserCreateMessage> updateUser = new HttpEntity<UserCreateMessage>(
                createMessage, header);

        try {
            digestRestTemplate.exchange(
                    URL + "idm/user/" + props.getProperty("anonid"),
                    HttpMethod.PUT, updateUser, Object.class);

        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }
}
