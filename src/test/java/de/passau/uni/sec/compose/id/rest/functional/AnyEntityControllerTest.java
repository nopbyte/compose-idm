package de.passau.uni.sec.compose.id.rest.functional;

import static de.passau.uni.sec.compose.id.rest.functional.util.Fixtures.digestRestTemplate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

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
import org.springframework.web.client.RestTemplate;

import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class AnyEntityControllerTest {
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
    public void requestUserDetailsWithIdTest() {

        // Request user details
        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<String>(
                detailsTokenHeader);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/any/" + userId + "/", HttpMethod.GET, requestEntity,
                Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertNotNull(userDetailsResponse.get("user"));
     
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetails = (LinkedHashMap<String, Object>) userDetailsResponse
                .get("user");

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(userId, (String) userDetails.get("id"));
        assertEquals(USERNAME, (String) userDetails.get("username"));
    }
    
    @Test
    public void requestUserDetailsWithTokenTest() {

        // Request user details
        LinkedHashMap<String,String> idMap= new LinkedHashMap<String, String>();
        idMap.put("id", userId);
     
        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization", "Bearer " + accessToken);
        HttpEntity<LinkedHashMap<String,String>> requestEntity = new HttpEntity<LinkedHashMap<String,String>>(
                idMap,detailsTokenHeader);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/any/", HttpMethod.POST, requestEntity,
                Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();
        
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetailsResponseUser = (LinkedHashMap<String, Object>) userDetailsResponse.get("user");

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(USERNAME, (String) userDetailsResponseUser.get("username"));
        assertEquals(userId, (String) userDetailsResponseUser.get("id"));
    }
    
    @Test
    public void requestAnonymousUserDetailsWithIdTest() {

        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }

        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization", "Bearer " + props.getProperty("anontoken"));
        HttpEntity<String> requestEntity = new HttpEntity<String>(
                detailsTokenHeader);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/any/" + props.getProperty("anonid") + "/", HttpMethod.GET, requestEntity,
                Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertNotNull(userDetailsResponse.get("user"));
     
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetails = (LinkedHashMap<String, Object>) userDetailsResponse
                .get("user");

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(props.getProperty("anonid"),
                (String) userDetails.get("id"));
        assertEquals(props.getProperty("anonusername"),
                (String) userDetails.get("username"));
        assertEquals(props.getProperty("anontoken"),
                (String) userDetails.get("random_auth_token"));
    }
    
    @Test
    public void requestAnonymousUserDetailsWithTokenTest() {

        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }
        
        LinkedHashMap<String,String> idMap= new LinkedHashMap<String, String>();
        idMap.put("id", props.getProperty("anonid"));
     
        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization", "Bearer " + props.getProperty("anontoken"));
        HttpEntity<LinkedHashMap<String,String>> requestEntity = new HttpEntity<LinkedHashMap<String,String>>(
                idMap,detailsTokenHeader);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/any/", HttpMethod.POST, requestEntity,
                Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();
        
        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> userDetailsResponseUser = (LinkedHashMap<String, Object>) userDetailsResponse.get("user");

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(props.getProperty("anonusername"), (String) userDetailsResponseUser.get("username"));
        assertEquals(props.getProperty("anonid"), (String) userDetailsResponseUser.get("id"));
    }
}
