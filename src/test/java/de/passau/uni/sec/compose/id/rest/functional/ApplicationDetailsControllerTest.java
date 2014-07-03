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
import org.springframework.web.client.RestTemplate;

import de.passau.uni.sec.compose.id.rest.messages.ApplicationCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.AuthenticatedEmptyMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class ApplicationDetailsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String APPID = "testApplicationId";

    private static final String APPNAME = "testApplicationName";

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
    public void applicationRequestDetails() {

        // Create application
        ApplicationCreateMessage applicationCreateMessage = new ApplicationCreateMessage();
        applicationCreateMessage.setAuthorization("BEARER " + accessToken);
        applicationCreateMessage.setId(APPID);
        applicationCreateMessage.setName(APPNAME);

        HttpEntity<ApplicationCreateMessage> createApp = new HttpEntity<ApplicationCreateMessage>(
                applicationCreateMessage);

        ResponseEntity<Object> responseEntityAppCreated = digestRestTemplate
                .exchange(URL + "idm/application/", HttpMethod.POST, createApp,
                        Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> appCreateResponse = (LinkedHashMap<String, Object>) responseEntityAppCreated
                .getBody();
        long lastModified = (long) appCreateResponse.get("lastModified");

        assertEquals(APPNAME, (String) appCreateResponse.get("name"));
        assertEquals(APPID, (String) appCreateResponse.get("id"));
        assertEquals(HttpStatus.CREATED, responseEntityAppCreated.getStatusCode());

        // Request application details
        HttpHeaders detailsTokenHeader = new HttpHeaders();
        detailsTokenHeader.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> requestEntity = new HttpEntity<String>(
                detailsTokenHeader);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/application/" + APPID, HttpMethod.GET,
                requestEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> appDetailsResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(APPNAME, (String) appDetailsResponse.get("name"));
        assertEquals(APPID, (String) appDetailsResponse.get("id"));
        assertEquals(userId, (String) appDetailsResponse.get("owner_id"));
        assertEquals(lastModified,
                (long) appDetailsResponse.get("lastModified"));

        // Delete application
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(lastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        ResponseEntity<Object> responseEntityDeletion = digestRestTemplate
                .exchange(URL + "idm/application/" + APPID, HttpMethod.DELETE,
                        deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }
}