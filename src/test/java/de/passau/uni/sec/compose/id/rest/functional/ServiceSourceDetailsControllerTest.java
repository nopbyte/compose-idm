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
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class ServiceSourceDetailsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String SERVICESOURCEID = "testServiceId";

    private static final String SERVICESOURCENAME = "testServiceName";

    private static final String SERVICESOURCEVERSION = "testServiceVersion";

    private static final String URL = "http://localhost:8080/";

    private String accessToken;

    private String userId;

    private long userLastModified;

    private long serviceSourceLastModified;

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

        serviceSourceLastModified = (long) sscCreateResponse
                .get("lastModified");
    }

    @After
    public void tearDown() {

        // delete service source code
        AuthenticatedEmptyMessage authenticateEmptyMes = new AuthenticatedEmptyMessage();
        authenticateEmptyMes.setAuthorization("Bearer " + accessToken);

        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since",
                String.valueOf(serviceSourceLastModified));
        HttpEntity<AuthenticatedEmptyMessage> deletionEntity = new HttpEntity<AuthenticatedEmptyMessage>(
                authenticateEmptyMes, header);

        digestRestTemplate.exchange(URL + "idm/servicesourcecode/"
                + SERVICESOURCEID, HttpMethod.DELETE, deletionEntity,
                Object.class);

        // Delete user
        header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        HttpEntity<String> deletionEntityUser = new HttpEntity<String>(header);

        digestRestTemplate.exchange(URL + "idm/user/" + userId,
                HttpMethod.DELETE, deletionEntityUser, Object.class);
    }

    @Test
    public void requestServiceSourceCodeDetailsTest() {

        // Request service source code details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken);
        HttpEntity<String> serviceDetails = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/servicesourcecode/" + SERVICESOURCEID,
                HttpMethod.GET, serviceDetails, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> sscCreateResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(SERVICESOURCEID, (String) sscCreateResponse.get("id"));
        assertEquals(userId, (String) sscCreateResponse.get("owner_id"));
        assertEquals(SERVICESOURCENAME, (String) sscCreateResponse.get("name"));
        assertEquals(SERVICESOURCEVERSION,
                (String) sscCreateResponse.get("version"));
        assertEquals(false, (boolean) sscCreateResponse.get("payment"));
    }

    @Test
    public void unauthorizedServiceSourceCodeDetailsRequestTest() {

        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken + "modified");
        HttpEntity<String> groupDetails = new HttpEntity<String>(header);

        try {
            restTemplate.exchange(URL + "idm/servicesourcecode/"
                    + SERVICESOURCEID, HttpMethod.GET, groupDetails,
                    Object.class);
        } catch (HttpClientErrorException e) {
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());
        }
    }

    @Test
    public void requestAnonymousServiceSourceCodeDetailsTest() {
        
        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }

        // Request service source code details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + props.getProperty("anontoken"));
        HttpEntity<String> serviceDetails = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/servicesourcecode/" + SERVICESOURCEID,
                HttpMethod.GET, serviceDetails, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> sscRequestResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.OK, responseEntityDetails.getStatusCode());
        assertEquals(SERVICESOURCEID, (String) sscRequestResponse.get("id"));
        assertEquals(userId, (String) sscRequestResponse.get("owner_id"));
        assertEquals(SERVICESOURCENAME, (String) sscRequestResponse.get("name"));
        assertEquals(SERVICESOURCEVERSION,
                (String) sscRequestResponse.get("version"));
        assertEquals(false, (boolean) sscRequestResponse.get("payment"));

    }
}
