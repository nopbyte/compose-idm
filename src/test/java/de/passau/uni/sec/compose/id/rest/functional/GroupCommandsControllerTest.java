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

import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class GroupCommandsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String GROUPNAME = "testGroupName";

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
    public void groupCreationTest() {

        // create group
        GroupCreateMessage groupCreateMessage = new GroupCreateMessage();
        groupCreateMessage.setName(GROUPNAME);

        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + accessToken);
        HttpEntity<GroupCreateMessage> requestEntity = new HttpEntity<GroupCreateMessage>(
                groupCreateMessage, tokenHeader);

        ResponseEntity<Object> responseEntityCreation = restTemplate.exchange(
                "http://localhost:8080/idm/group/", HttpMethod.POST,
                requestEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> groupCreationResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(GROUPNAME,
                (String) (String) groupCreationResponse.get("name"));
        assertEquals(userId, (String) groupCreationResponse.get("owner_id"));
    }

    @Test
    public void unautherizedGroupCreationTest() {

        // Create group without valid authorization
        GroupCreateMessage groupCreateMessage = new GroupCreateMessage();
        groupCreateMessage.setName(GROUPNAME);

        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + accessToken + "modified");
        HttpEntity<GroupCreateMessage> requestEntity = new HttpEntity<GroupCreateMessage>(
                groupCreateMessage, tokenHeader);

        try {
            restTemplate.exchange("http://localhost:8080/idm/group/",
                    HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void conflictGroupCreationTest() {

        // Create group
        GroupCreateMessage groupCreateMessage = new GroupCreateMessage();
        groupCreateMessage.setName(GROUPNAME);

        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + accessToken);
        HttpEntity<GroupCreateMessage> requestEntity = new HttpEntity<GroupCreateMessage>(
                groupCreateMessage, tokenHeader);

        ResponseEntity<Object> responseEntityCreation = restTemplate.exchange(
                "http://localhost:8080/idm/group/", HttpMethod.POST,
                requestEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> groupCreationResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(GROUPNAME,
                (String) (String) groupCreationResponse.get("name"));
        assertEquals(userId, (String) groupCreationResponse.get("owner_id"));

        // Create the same group again
        try {
            restTemplate.exchange("http://localhost:8080/idm/group/",
                    HttpMethod.POST, requestEntity, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());
        }

    }
}
