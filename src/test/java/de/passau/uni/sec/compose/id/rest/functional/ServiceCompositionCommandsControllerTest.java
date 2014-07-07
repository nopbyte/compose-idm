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

import de.passau.uni.sec.compose.id.rest.messages.AuthenticatedEmptyMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceCompositionCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class ServiceCompositionCommandsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String SERVICECOMPID = "testServiceId";

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
    public void createAndDeleteUserServiceComposition() {

        // create a service composition
        ServiceCompositionCreateMessage serviceCompositionCreateMessage = new ServiceCompositionCreateMessage();
        serviceCompositionCreateMessage.setAuthorization("BEARER "
                + accessToken);
        serviceCompositionCreateMessage.setId(SERVICECOMPID);

        HttpEntity<ServiceCompositionCreateMessage> creationEntity = new HttpEntity<ServiceCompositionCreateMessage>(
                serviceCompositionCreateMessage);

        ResponseEntity<Object> responseEntityCreation = digestRestTemplate
                .exchange(URL + "idm/servicecomposition/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> scCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(SERVICECOMPID, (String) scCreateResponse.get("id"));
        assertEquals(userId, (String) scCreateResponse.get("owner_id"));

        long lastModified = (long) scCreateResponse.get("lastModified");

        // delete service composition
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(lastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        ResponseEntity<Object> responseEntityDeletion = digestRestTemplate
                .exchange(URL + "idm/servicecomposition/" + SERVICECOMPID,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());

    }

    @Test
    public void createAndDeleteServiceCompositionInvalidIfUnmoidified() {

        // create a service composition
        ServiceCompositionCreateMessage serviceCompositionCreateMessage = new ServiceCompositionCreateMessage();
        serviceCompositionCreateMessage.setAuthorization("BEARER "
                + accessToken);
        serviceCompositionCreateMessage.setId(SERVICECOMPID);

        HttpEntity<ServiceCompositionCreateMessage> creationEntity = new HttpEntity<ServiceCompositionCreateMessage>(
                serviceCompositionCreateMessage);

        ResponseEntity<Object> responseEntityCreation = digestRestTemplate
                .exchange(URL + "idm/servicecomposition/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> scCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(SERVICECOMPID, (String) scCreateResponse.get("id"));
        assertEquals(userId, (String) scCreateResponse.get("owner_id"));

        long lastModified = (long) scCreateResponse.get("lastModified");

        // Attempt deletion of service composition with modified lastModified
        long modifiedLastModified = lastModified + 1;
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(modifiedLastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        try {
            digestRestTemplate.exchange(URL + "idm/servicecomposition/"
                    + SERVICECOMPID, HttpMethod.DELETE, deletionEntity,
                    Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.PRECONDITION_FAILED, e.getStatusCode());
        }

        // Delete with correct lastModified
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(lastModified));
        deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        ResponseEntity<Object> responseEntityDeletion = digestRestTemplate
                .exchange(URL + "idm/servicecomposition/" + SERVICECOMPID,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }

    @Test
    public void createAndDeleteServiceCompositionUnauthorized() {

        // create a service composition
        ServiceCompositionCreateMessage serviceCompositionCreateMessage = new ServiceCompositionCreateMessage();
        serviceCompositionCreateMessage.setAuthorization("BEARER "
                + accessToken);
        serviceCompositionCreateMessage.setId(SERVICECOMPID);

        HttpEntity<ServiceCompositionCreateMessage> creationEntity = new HttpEntity<ServiceCompositionCreateMessage>(
                serviceCompositionCreateMessage);

        ResponseEntity<Object> responseEntityCreation = digestRestTemplate
                .exchange(URL + "idm/servicecomposition/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> scCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(SERVICECOMPID, (String) scCreateResponse.get("id"));
        assertEquals(userId, (String) scCreateResponse.get("owner_id"));

        long lastModified = (long) scCreateResponse.get("lastModified");

        // Attempt deletion of service composition with modified access token
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken
                + "modified");

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(lastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        try {
            digestRestTemplate.exchange(URL + "idm/servicecomposition/"
                    + SERVICECOMPID, HttpMethod.DELETE, deletionEntity,
                    Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }

        // Delete with correct token
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(lastModified));
        deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        ResponseEntity<Object> responseEntityDeletion = digestRestTemplate
                .exchange(URL + "idm/servicecomposition/" + SERVICECOMPID,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }
}
