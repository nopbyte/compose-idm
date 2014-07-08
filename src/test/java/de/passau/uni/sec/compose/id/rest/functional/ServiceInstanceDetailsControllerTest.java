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
import de.passau.uni.sec.compose.id.rest.messages.ServiceInstanceCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class ServiceInstanceDetailsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String SERVICESOURCEID = "testServiceId";

    private static final String SERVICESOURCENAME = "testServiceName";

    private static final String SERVICESOURCEVERSION = "testServiceVersion";

    private static final String SERVICEINSTID = "testServiceId";

    private static final String URL = "http://localhost:8080/";

    private String accessToken;

    private String userId;

    private long userLastModified;

    private long sourceCodeLastModified;

    private long serviceInstanceLastModified;

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

        // create a service source code
        ServiceSourceCodeCreateMessage serviceSourceCodeCreateMessage = new ServiceSourceCodeCreateMessage();
        serviceSourceCodeCreateMessage
                .setAuthorization("BEARER " + accessToken);
        serviceSourceCodeCreateMessage.setId(SERVICESOURCEID);
        serviceSourceCodeCreateMessage.setName(SERVICESOURCENAME);
        serviceSourceCodeCreateMessage.setVersion(SERVICESOURCEVERSION);
        serviceSourceCodeCreateMessage.setPayment(false);

        HttpEntity<ServiceSourceCodeCreateMessage> creationEntity = new HttpEntity<ServiceSourceCodeCreateMessage>(
                serviceSourceCodeCreateMessage);

        ResponseEntity<Object> responseEntityCreationSourceCode = digestRestTemplate
                .exchange(URL + "idm/servicesourcecode/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> sscCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreationSourceCode
                .getBody();

        sourceCodeLastModified = (long) sscCreateResponse.get("lastModified");

        // create a service instance
        ServiceInstanceCreateMessage serviceInstanceCreateMessage = new ServiceInstanceCreateMessage();
        serviceInstanceCreateMessage.setAuthorization("BEARER " + accessToken);
        serviceInstanceCreateMessage.setData_provenance_collection(false);
        serviceInstanceCreateMessage.setPayment(false);
        serviceInstanceCreateMessage.setId(SERVICEINSTID);
        serviceInstanceCreateMessage.setSource_code_id(SERVICESOURCEID);
        serviceInstanceCreateMessage.setUri("https://google.com");

        HttpEntity<ServiceInstanceCreateMessage> creationEntityInstance = new HttpEntity<ServiceInstanceCreateMessage>(
                serviceInstanceCreateMessage);

        ResponseEntity<Object> responseEntityCreationInstance = digestRestTemplate
                .exchange(URL + "idm/serviceinstance/", HttpMethod.POST,
                        creationEntityInstance, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> siCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreationInstance
                .getBody();

        serviceInstanceLastModified = (long) siCreateResponse
                .get("lastModified");

    }

    @After
    public void tearDown() {

        // delete service instance
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since",
                String.valueOf(serviceInstanceLastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        RestTemplate digestDeleteRestTemplate = digestRestTemplate();
        ResponseEntity<Object> responseEntityDeletion = digestDeleteRestTemplate
                .exchange(URL + "idm/serviceinstance/" + SERVICEINSTID,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());

        // delete service source code
        authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        header = new HttpHeaders();
        header.set("If-Unmodified-Since",
                String.valueOf(sourceCodeLastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntitySourceCode = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        digestRestTemplate.exchange(URL + "idm/servicesourcecode/"
                + SERVICESOURCEID, HttpMethod.DELETE, deletionEntitySourceCode,
                Object.class);

        // Delete user
        header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        HttpEntity<String> deletionEntityUser = new HttpEntity<String>(header);

        digestRestTemplate.exchange(URL + "idm/user/" + userId,
                HttpMethod.DELETE, deletionEntityUser, Object.class);
    }

    @Test
    public void requestServiceInstanceDetailsTest() {

        // Request service composition details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken);
        HttpEntity<String> serviceDetails = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/serviceinstance/" + SERVICEINSTID, HttpMethod.GET,
                serviceDetails, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> siCreateResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(SERVICEINSTID, (String) siCreateResponse.get("id"));
        assertEquals(userId, (String) siCreateResponse.get("owner_id"));
        assertEquals(SERVICESOURCEID,
                (String) siCreateResponse.get("source_code_id"));
    }

    @Test
    public void unauthorizedServiceInstanceDetailsRequestTest() {

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken + "modified");
        HttpEntity<String> groupDetails = new HttpEntity<String>(header);

        try {
            restTemplate.exchange(URL + "idm/serviceinstance/" + SERVICEINSTID,
                    HttpMethod.GET, groupDetails, Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }
}
