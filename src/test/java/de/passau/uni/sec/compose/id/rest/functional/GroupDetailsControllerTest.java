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
import org.springframework.web.client.RestTemplate;

import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class GroupDetailsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String GROUPNAME = "testGroupName";

    private static final String URL = "http://localhost:8080/";

    private String accessToken;

    private String userId;

    private String groupId;

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

        // Create Group
        GroupCreateMessage groupCreateMessage = new GroupCreateMessage();
        groupCreateMessage.setName(GROUPNAME);

        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + accessToken);
        HttpEntity<GroupCreateMessage> requestEntity = new HttpEntity<GroupCreateMessage>(
                groupCreateMessage, tokenHeader);

        ResponseEntity<Object> responseEntityGroupCreation = restTemplate
                .exchange("http://localhost:8080/idm/group/", HttpMethod.POST,
                        requestEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> groupCreationResponse = (LinkedHashMap<String, Object>) responseEntityGroupCreation
                .getBody();

        groupId = (String) groupCreationResponse.get("id");
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
    public void requestGroupDetailsTest() {

        // Request group details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken);
        HttpEntity<String> groupDetails = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/group/" + groupId, HttpMethod.GET, groupDetails,
                Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> groupDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(GROUPNAME, (String) groupDetailsResponse.get("name"));
        assertEquals(userId, (String) groupDetailsResponse.get("owner_id"));
        assertEquals(groupId, (String) groupDetailsResponse.get("id"));
    }

    @Test
    public void unauthorizedUserDetailsRequestTest() {

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken + "modified");
        HttpEntity<String> groupDetails = new HttpEntity<String>(header);

        try {
            restTemplate.exchange(URL + "idm/group/" + groupId, HttpMethod.GET,
                    groupDetails, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }
    
    @Test
    public void anonymousRequestGroupDetailsTest() {
        
        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }
        
        // create group
        GroupCreateMessage groupCreateMessage = new GroupCreateMessage();
        groupCreateMessage.setName(GROUPNAME);

        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + props.getProperty("anontoken"));
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
        assertEquals(props.getProperty("anonid"), (String) groupCreationResponse.get("owner_id"));
        
        // Request group details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + props.getProperty("anontoken"));
        HttpEntity<String> groupDetails = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/group/" + groupCreationResponse.get("id"), HttpMethod.GET, groupDetails,
                Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> groupDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(GROUPNAME, (String) groupDetailsResponse.get("name"));
        assertEquals(props.getProperty("anonid"), (String) groupDetailsResponse.get("owner_id"));
        assertEquals(groupCreationResponse.get("id"), (String) groupDetailsResponse.get("id"));
        
        //delete group
        tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + props.getProperty("anontoken"));
        tokenHeader.set("If-Unmodified-Since", String.valueOf(groupCreationResponse.get("lastModified")));
        HttpEntity<String> deleteEntity = new HttpEntity<String>(tokenHeader);
        
        ResponseEntity<Object> responseEntityDeletion = restTemplate.exchange(
                "http://localhost:8080/idm/group/" + groupCreationResponse.get("id"), HttpMethod.DELETE,
                deleteEntity, Object.class);
        
        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }

}
