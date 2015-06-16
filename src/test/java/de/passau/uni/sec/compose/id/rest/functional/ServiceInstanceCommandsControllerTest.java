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
import de.passau.uni.sec.compose.id.rest.messages.ServiceInstanceCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class ServiceInstanceCommandsControllerTest {
    
    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String SERVICESOURCEID = "testServiceSourcId";

    private static final String SERVICESOURCENAME = "testServiceName";

    private static final String SERVICESOURCEVERSION = "testServiceVersion";

    private static final String SERVICEINSTID = "testServiceInstId";

    private static final String URL = "http://localhost:8080/";

    private String accessToken;

    private String userId;

    private long userLastModified;

    private long sourceCodeLastModified;

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

    }

    @After
    public void tearDown() {

        // delete service source code
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
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
        HttpEntity<String> deletionEntity = new HttpEntity<String>(header);

        digestRestTemplate.exchange(URL + "idm/user/" + userId,
                HttpMethod.DELETE, deletionEntity, Object.class);

    }

    @Test
    public void createAndDeleteServiceInstance() {

        // create a service instance
        ServiceInstanceCreateMessage serviceInstanceCreateMessage = new ServiceInstanceCreateMessage();
        serviceInstanceCreateMessage.setAuthorization("BEARER " + accessToken);
        serviceInstanceCreateMessage.setData_provenance_collection(false);
        serviceInstanceCreateMessage.setPayment(false);
        serviceInstanceCreateMessage.setId(SERVICEINSTID);
        serviceInstanceCreateMessage.setSource_code_id(SERVICESOURCEID);
        serviceInstanceCreateMessage.setUri("https://google.com");

        HttpEntity<ServiceInstanceCreateMessage> creationEntity = new HttpEntity<ServiceInstanceCreateMessage>(
                serviceInstanceCreateMessage);

        ResponseEntity<Object> responseEntityCreation = digestRestTemplate
                .exchange(URL + "idm/serviceinstance/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> siCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(SERVICEINSTID, (String) siCreateResponse.get("id"));
        assertEquals(userId, (String) siCreateResponse.get("owner_id"));
        assertEquals(SERVICESOURCEID,
                (String) siCreateResponse.get("source_code_id"));

        long lastModified = (long) siCreateResponse.get("lastModified");

        // delete service instance
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(lastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        RestTemplate digestDeleteRestTemplate = digestRestTemplate();
        ResponseEntity<Object> responseEntityDeletion = digestDeleteRestTemplate
                .exchange(URL + "idm/serviceinstance/" + SERVICEINSTID,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }

    @Test
    public void createAndDeleteServiceInstanceInvalidIfUnmoidified() {

        // create a service instance
        ServiceInstanceCreateMessage serviceInstanceCreateMessage = new ServiceInstanceCreateMessage();
        serviceInstanceCreateMessage.setAuthorization("BEARER " + accessToken);
        serviceInstanceCreateMessage.setData_provenance_collection(false);
        serviceInstanceCreateMessage.setPayment(false);
        serviceInstanceCreateMessage.setId(SERVICEINSTID);
        serviceInstanceCreateMessage.setSource_code_id(SERVICESOURCEID);
        serviceInstanceCreateMessage.setUri("https://google.com");

        HttpEntity<ServiceInstanceCreateMessage> creationEntity = new HttpEntity<ServiceInstanceCreateMessage>(
                serviceInstanceCreateMessage);

        ResponseEntity<Object> responseEntityCreation = digestRestTemplate
                .exchange(URL + "idm/serviceinstance/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> siCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(SERVICEINSTID, (String) siCreateResponse.get("id"));
        assertEquals(userId, (String) siCreateResponse.get("owner_id"));
        assertEquals(SERVICESOURCEID,
                (String) siCreateResponse.get("source_code_id"));

        long lastModified = (long) siCreateResponse.get("lastModified");

        // Attempt deletion of service instance with modified lastModified
        long modifiedLastModified = lastModified + 1;
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(modifiedLastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        RestTemplate digestDeleteRestTemplate = digestRestTemplate();
        try {
            digestDeleteRestTemplate.exchange(URL + "idm/serviceinstance/"
                    + SERVICEINSTID, HttpMethod.DELETE, deletionEntity,
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

        digestDeleteRestTemplate = digestRestTemplate();
        ResponseEntity<Object> responseEntityDeletion = digestDeleteRestTemplate
                .exchange(URL + "idm/serviceinstance/" + SERVICEINSTID,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }

    @Test
    public void createAndDeleteServiceInstanceUnauthorized() {

        // create a service instance
        ServiceInstanceCreateMessage serviceInstanceCreateMessage = new ServiceInstanceCreateMessage();
        serviceInstanceCreateMessage.setAuthorization("BEARER " + accessToken);
        serviceInstanceCreateMessage.setData_provenance_collection(false);
        serviceInstanceCreateMessage.setPayment(false);
        serviceInstanceCreateMessage.setId(SERVICEINSTID);
        serviceInstanceCreateMessage.setSource_code_id(SERVICESOURCEID);
        serviceInstanceCreateMessage.setUri("https://google.com");

        HttpEntity<ServiceInstanceCreateMessage> creationEntity = new HttpEntity<ServiceInstanceCreateMessage>(
                serviceInstanceCreateMessage);

        ResponseEntity<Object> responseEntityCreation = digestRestTemplate
                .exchange(URL + "idm/serviceinstance/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> siCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(SERVICEINSTID, (String) siCreateResponse.get("id"));
        assertEquals(userId, (String) siCreateResponse.get("owner_id"));
        assertEquals(SERVICESOURCEID,
                (String) siCreateResponse.get("source_code_id"));

        long lastModified = (long) siCreateResponse.get("lastModified");

        // Attempt deletion of service instance with modified access token
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken
                + "modified");

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(lastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        RestTemplate digestDeleteRestTemplate = digestRestTemplate();
        try {
            digestDeleteRestTemplate.exchange(URL + "idm/serviceinstance/"
                    + SERVICEINSTID, HttpMethod.DELETE, deletionEntity,
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

        digestDeleteRestTemplate = digestRestTemplate();
        ResponseEntity<Object> responseEntityDeletion = digestDeleteRestTemplate
                .exchange(URL + "idm/serviceinstance/" + SERVICEINSTID,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }
    
    @Test
    public void anonymousCreateAndDeleteServiceInstance() {
        
        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }

        // create a service instance
        ServiceInstanceCreateMessage serviceInstanceCreateMessage = new ServiceInstanceCreateMessage();
        serviceInstanceCreateMessage.setAuthorization("BEARER " + props.getProperty("anontoken"));
        serviceInstanceCreateMessage.setData_provenance_collection(false);
        serviceInstanceCreateMessage.setPayment(false);
        serviceInstanceCreateMessage.setId(SERVICEINSTID);
        serviceInstanceCreateMessage.setSource_code_id(SERVICESOURCEID);
        serviceInstanceCreateMessage.setUri("https://google.com");

        HttpEntity<ServiceInstanceCreateMessage> creationEntity = new HttpEntity<ServiceInstanceCreateMessage>(
                serviceInstanceCreateMessage);

        ResponseEntity<Object> responseEntityCreation = digestRestTemplate
                .exchange(URL + "idm/serviceinstance/", HttpMethod.POST,
                        creationEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> siCreateResponse = (LinkedHashMap<String, Object>) responseEntityCreation
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityCreation.getStatusCode());
        assertEquals(SERVICEINSTID, (String) siCreateResponse.get("id"));
        assertEquals(props.getProperty("anonid"), (String) siCreateResponse.get("owner_id"));
        assertEquals(SERVICESOURCEID,
                (String) siCreateResponse.get("source_code_id"));

        long lastModified = (long) siCreateResponse.get("lastModified");

        // delete service instance
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + props.getProperty("anontoken"));

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(lastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        RestTemplate digestDeleteRestTemplate = digestRestTemplate();
        ResponseEntity<Object> responseEntityDeletion = digestDeleteRestTemplate
                .exchange(URL + "idm/serviceinstance/" + SERVICEINSTID,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }

}
