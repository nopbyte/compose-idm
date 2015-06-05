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
import org.springframework.web.client.RestTemplate;

import de.passau.uni.sec.compose.id.rest.messages.AttributeDefinitionCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class AttributeDefinitionsControllerTest {

    private RestTemplate digestRestTemplate;

    private RestTemplate restTemplate;

    private static final String USERNAME = "testUsername";

    private static final String PASSWORD = "testPassword";

    private static final String GROUPNAME = "testGroupName";

    private static final String ATTDEFNAME = "attdefname";

    private static final String ATTDEFTYPE = "attdeftype";

    private static final String URL = "http://localhost:8080/";

    private String accessToken;

    private String userId;

    private String groupId;

    private long userLastModified;

    private long groupLastModified;

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
        groupLastModified = (long) groupCreationResponse.get("lastModified");
    }

    @After
    public void tearDown() {

        // delete group
        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + accessToken);
        tokenHeader.set("If-Unmodified-Since", String.valueOf(groupLastModified));
        HttpEntity<String> deleteEntity = new HttpEntity<String>(tokenHeader);

        ResponseEntity<Object> responseEntityDeletion = restTemplate.exchange(
                "http://localhost:8080/idm/group/" + groupId,
                HttpMethod.DELETE, deleteEntity, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());

        // delete user
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", String.valueOf(userLastModified));
        HttpEntity<String> deletionEntity = new HttpEntity<String>(header);

        digestRestTemplate.exchange(URL + "idm/user/" + userId,
                HttpMethod.DELETE, deletionEntity, Object.class);
    }

    @Test
    public void createAndDeleteAttributeDefinitionsTest() {

        AttributeDefinitionCreateMessage attDef = new AttributeDefinitionCreateMessage();
        attDef.setName(ATTDEFNAME);
        attDef.setType(ATTDEFTYPE);

        // Set attribute details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken);
        HttpEntity<AttributeDefinitionCreateMessage> setAttDef = new HttpEntity<AttributeDefinitionCreateMessage>(
                attDef, header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/group/" + groupId + "/attribute_definition/",
                HttpMethod.POST, setAttDef, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> groupResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityDetails.getStatusCode());
        assertEquals(ATTDEFNAME, (String) groupResponse.get("name"));
        assertEquals(ATTDEFTYPE, (String) groupResponse.get("type"));
        assertEquals(userId, (String) groupResponse.get("owner_id"));
        assertEquals(groupId, (String) groupResponse.get("group_id"));

        // delete attribute details

        System.out.println(groupResponse.get("lastModified"));
        long attLastModified = (long) groupResponse.get("lastModified");

        header = new HttpHeaders();
        header.set("Authorization", "BEARER " + accessToken);
        header.set("If-Unmodified-Since", String.valueOf(attLastModified));
        HttpEntity<String> deleteEntity = new HttpEntity<String>(header);

        ResponseEntity<Object> responseDeleteEntityDetails = restTemplate
                .exchange(URL + "idm/group/attribute_definition/"
                        + groupResponse.get("id") + "/", HttpMethod.DELETE,
                        deleteEntity, Object.class);

        assertEquals(HttpStatus.OK, responseDeleteEntityDetails.getStatusCode());
    }
    
    @Test
    public void AnonymousCreateAndDeleteAttributeDefinitionsTest() {
        
        Properties props = new Properties();
        InputStream is = ClassLoader
                .getSystemResourceAsStream("anonymousTestUser.properties");
        try {
            props.load(is);
        } catch (IOException e) {
        }
        
        // Create Group
        GroupCreateMessage groupCreateMessage = new GroupCreateMessage();
        groupCreateMessage.setName(GROUPNAME + "anon");

        HttpHeaders tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + props.getProperty("anontoken"));
        HttpEntity<GroupCreateMessage> requestEntity = new HttpEntity<GroupCreateMessage>(
                groupCreateMessage, tokenHeader);

        ResponseEntity<Object> responseEntityGroupCreation = restTemplate
                .exchange("http://localhost:8080/idm/group/", HttpMethod.POST,
                        requestEntity, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> groupCreationResponse = (LinkedHashMap<String, Object>) responseEntityGroupCreation
                .getBody();

        String groupIdAnon = (String) groupCreationResponse.get("id");

        AttributeDefinitionCreateMessage attDef = new AttributeDefinitionCreateMessage();
        attDef.setName(ATTDEFNAME);
        attDef.setType(ATTDEFTYPE);

        // Set attribute details
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "BEARER " + props.getProperty("anontoken"));
        HttpEntity<AttributeDefinitionCreateMessage> setAttDef = new HttpEntity<AttributeDefinitionCreateMessage>(
                attDef, header);

        ResponseEntity<Object> responseEntityDetails = restTemplate.exchange(
                URL + "idm/group/" + groupIdAnon + "/attribute_definition/",
                HttpMethod.POST, setAttDef, Object.class);

        @SuppressWarnings("unchecked")
        LinkedHashMap<String, Object> groupResponse = (LinkedHashMap<String, Object>) responseEntityDetails
                .getBody();

        assertEquals(HttpStatus.CREATED, responseEntityDetails.getStatusCode());
        assertEquals(ATTDEFNAME, (String) groupResponse.get("name"));
        assertEquals(ATTDEFTYPE, (String) groupResponse.get("type"));
        assertEquals(groupIdAnon, (String) groupResponse.get("group_id"));

        // delete attribute details
        long attLastModified = (long) groupResponse.get("lastModified");

        header = new HttpHeaders();
        header.set("Authorization", "BEARER " + props.getProperty("anontoken"));
        header.set("If-Unmodified-Since", String.valueOf(attLastModified));
        HttpEntity<String> deleteEntity = new HttpEntity<String>(header);

        ResponseEntity<Object> responseDeleteEntityDetails = restTemplate
                .exchange(URL + "idm/group/attribute_definition/"
                        + groupResponse.get("id") + "/", HttpMethod.DELETE,
                        deleteEntity, Object.class);

        assertEquals(HttpStatus.OK, responseDeleteEntityDetails.getStatusCode());
        
        // delete group
        tokenHeader = new HttpHeaders();
        tokenHeader.set("Authorization", "Bearer " + props.getProperty("anontoken"));
        tokenHeader.set("If-Unmodified-Since", String.valueOf(groupLastModified));
        HttpEntity<String> deleteEntityGroup = new HttpEntity<String>(tokenHeader);

        ResponseEntity<Object> responseEntityDeletion = restTemplate.exchange(
                "http://localhost:8080/idm/group/" + groupIdAnon,
                HttpMethod.DELETE, deleteEntityGroup, Object.class);

        assertEquals(HttpStatus.OK, responseEntityDeletion.getStatusCode());
    }

}
