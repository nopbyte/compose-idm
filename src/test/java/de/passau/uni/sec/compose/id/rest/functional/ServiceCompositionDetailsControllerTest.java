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

import de.passau.uni.sec.compose.id.rest.messages.AuthenticatedEmptyMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceCompositionCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class ServiceCompositionDetailsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String SERVICECOMPID = "testServiceId";

    private static final String URL = "http://localhost:8080/";

    private String accessToken;

    private String userId;

    private long userLastModified;

    private long serviceCompLastModified;

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

        // create a service composition
        ServiceCompositionCreateMessage serviceCompositionCreateMessage = new ServiceCompositionCreateMessage();
        serviceCompositionCreateMessage.setAuthorization("BEARER "
                + accessToken);
        serviceCompositionCreateMessage.setId(SERVICECOMPID);

        HttpEntity<ServiceCompositionCreateMessage> creationEntity = new HttpEntity<ServiceCompositionCreateMessage>(
                serviceCompositionCreateMessage);

        ResponseEntity<Object> responseEntityCreationSc = digestRestTemplate
                .exchange(URL + "idm/servicecomposition/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> scCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreationSc
                .getBody();

        serviceCompLastModified = (long) scCreateResponse.get("lastModified");
    }

    @After
    public void tearDown() {
        // delete service composition
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since",
                String.valueOf(serviceCompLastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        digestRestTemplate.exchange(URL + "idm/servicecomposition/"
                + SERVICECOMPID, HttpMethod.DELETE, deletionEntity,
                Object.class);

        // Delete user
        header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        HttpEntity<String> deletionEntityUser = new HttpEntity<String>(header);

        digestRestTemplate.exchange(URL + "idm/user/" + userId,
                HttpMethod.DELETE, deletionEntityUser, Object.class);
    }

    @Test
    public void requestServiceCompositionDetailsTest() {

        // Request service composition details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken);
        HttpEntity<String> serviceDetails = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/servicecomposition/" + SERVICECOMPID,
                HttpMethod.GET, serviceDetails, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> scCreateResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(SERVICECOMPID, (String) scCreateResponse.get("id"));
        assertEquals(userId, (String) scCreateResponse.get("owner_id"));
    }

    @Test
    public void unauthorizedServiceCompositionDetailsRequestTest() {

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken + "modified");
        HttpEntity<String> groupDetails = new HttpEntity<String>(header);

        try {
            restTemplate
                    .exchange(URL + "idm/servicecomposition/" + SERVICECOMPID,
                            HttpMethod.GET, groupDetails, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void anonymousRequestServiceCompositionDetailsTest() {

        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }

        // Request service composition details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + props.getProperty("anontoken"));
        HttpEntity<String> serviceDetails = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/servicecomposition/" + SERVICECOMPID,
                HttpMethod.GET, serviceDetails, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> scCreateResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(SERVICECOMPID, (String) scCreateResponse.get("id"));
        assertEquals(userId, (String) scCreateResponse.get("owner_id"));
    }
}
