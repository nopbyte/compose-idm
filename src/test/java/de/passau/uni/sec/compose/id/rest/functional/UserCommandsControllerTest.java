package de.passau.uni.sec.compose.id.rest.functional;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;

import de.passau.uni.sec.compose.id.rest.functional.util.DigestHttpComponentsClientHttpRequestFactory;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

@SuppressWarnings("deprecation")
public class UserCommandsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    @Before
    public void setup() {
        DefaultHttpClient client = new DefaultHttpClient();

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                "composecontroller", "composecontrollerpassword");

        client.getCredentialsProvider().setCredentials(
                new AuthScope("localhost", 8080, AuthScope.ANY_REALM),
                credentials);

        HttpComponentsClientHttpRequestFactory requestFactory = new DigestHttpComponentsClientHttpRequestFactory(
                client);

        digestRestTemplate = new RestTemplate();
        digestRestTemplate.setRequestFactory(requestFactory);

        restTemplate = new RestTemplate();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void createNewUser() throws JsonProcessingException {

        UserCreateMessage createMessage = new UserCreateMessage();
        createMessage.setUsername("testusername");
        createMessage.setPassword("testpassword");

        HttpEntity<UserCreateMessage> request = new HttpEntity<UserCreateMessage>(
                createMessage);

        ResponseEntity<Object> responseEntity = digestRestTemplate.exchange(
                "http://localhost:8080/idm/user/", HttpMethod.POST, request,
                Object.class);

        String path = responseEntity.getHeaders().getLocation().getPath();

        LinkedHashMap<String, Object> response = (LinkedHashMap<String, Object>) responseEntity
                .getBody();

        String id = (String) response.get("id");
        String username = (String) response.get("username");

        assertEquals(HttpStatus.CREATED, responseEntity.getStatusCode());
        assertEquals("testusername", username);
        assertTrue(path.endsWith(id));

        // authenticate
        UserCredentials credentials = new UserCredentials();
        credentials.setPassword("testpassword");
        credentials.setUsername(username);

        HttpEntity<UserCredentials> userAuth = new HttpEntity<UserCredentials>(
                credentials);

        ResponseEntity<Object> responseEntityAuthentication = restTemplate
                .exchange("http://localhost:8080/auth/user/", HttpMethod.POST,
                        userAuth, Object.class);

        LinkedHashMap<String, Object> authRes = (LinkedHashMap<String, Object>) responseEntityAuthentication
                .getBody();

        String accessToken = (String) authRes.get("accessToken");
        String tokenType = (String) authRes.get("token_type");

        assertEquals("BEARER", tokenType);
        assertNotNull(accessToken);
        assertEquals(HttpStatus.OK, responseEntityAuthentication.getStatusCode());

        // delete user
        String mod = response.get("lastModified").toString();

        org.springframework.http.HttpHeaders header = new org.springframework.http.HttpHeaders();
        header.set("If-Unmodified-Since", mod);

        HttpEntity<String> deletionEntity = new HttpEntity<String>(header);

        ResponseEntity<Object> responseEntityDeletion = digestRestTemplate
                .exchange("http://localhost:8080/idm/user/" + id,
                        HttpMethod.DELETE, deletionEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());

    }
}
