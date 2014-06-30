package de.passau.uni.sec.compose.id.rest.controller.fixture;

import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.passau.uni.sec.compose.id.rest.messages.ApplicationCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.AuthenticatedEmptyMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityGroupMembershipCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceCompositionCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceInstanceCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;
import de.passau.uni.sec.compose.id.rest.messages.UserUpdateMessage;

public class RestDataFixture {

    private static ObjectMapper mapper = new ObjectMapper();

    public static HttpHeaders authorizationHttpHeader() {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "test:pass");

        return header;
    }

    public static HttpHeaders authorizationHttpHeaderToken() {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "Bearer TOKEN");

        return header;
    }

    public static HttpHeaders tokenUnmodifiedHttpHeader() {
        HttpHeaders header = new HttpHeaders();
        header.set("Authorization", "token");
        header.set("If-Unmodified-Since", "0");

        return header;
    }

    public static HttpHeaders ifUnmodifiedHttpHeader() {
        HttpHeaders header = new HttpHeaders();
        header.set("If-Unmodified-Since", "0");

        return header;
    }

    public static String createUserDataJSON() throws JsonProcessingException {
        UserCreateMessage userCreateMessage = new UserCreateMessage();
        userCreateMessage.setPassword("pass");
        userCreateMessage.setUsername("test");

        return mapper.writeValueAsString(userCreateMessage);
    }

    public static String createApplicationDataJSON()
            throws JsonProcessingException {
        ApplicationCreateMessage applicationCreateMessage = new ApplicationCreateMessage();
        applicationCreateMessage.setAuthorization("Bearer");
        applicationCreateMessage.setId("id");
        applicationCreateMessage.setName("name");

        return mapper.writeValueAsString(applicationCreateMessage);
    }

    public static String authenticateUserDataJSON()
            throws JsonProcessingException {
        UserCredentials userCredentials = new UserCredentials();
        userCredentials.setPassword("pass");
        userCredentials.setUsername("test");

        return mapper.writeValueAsString(userCredentials);
    }

    public static String entityGroupMembershipCreateMessageJSON()
            throws JsonProcessingException {
        EntityGroupMembershipCreateMessage entityGroupMembershipCreateMessage = new EntityGroupMembershipCreateMessage();
        entityGroupMembershipCreateMessage.setGroup_id("testGroupId");

        return mapper.writeValueAsString(entityGroupMembershipCreateMessage);
    }

    public static String createGroupJSON() throws JsonProcessingException {
        GroupCreateMessage groupCreateMessage = new GroupCreateMessage();
        groupCreateMessage.setName("groupTest");

        return mapper.writeValueAsString(groupCreateMessage);
    }

    public static String userUpdateMessageJSON() throws JsonProcessingException {
        UserUpdateMessage userUpdateMessage = new UserUpdateMessage();
        userUpdateMessage.setExtraAttributes(null);
        userUpdateMessage.setMemberships(null);

        return mapper.writeValueAsString(userUpdateMessage);
    }

    public static String membershipCreateMessageJSON()
            throws JsonProcessingException {
        MembershipCreateMessage membershipCreateMessage = new MembershipCreateMessage();
        membershipCreateMessage.setGroup_id("testId");
        membershipCreateMessage.setRole("testRole");

        return mapper.writeValueAsString(membershipCreateMessage);
    }

    public static String serviceCompositionCreateMessageJSON()
            throws JsonProcessingException {
        ServiceCompositionCreateMessage serviceCompositionCreateMessage = new ServiceCompositionCreateMessage();
        serviceCompositionCreateMessage.setAuthorization("authorization");
        serviceCompositionCreateMessage.setId("testId");

        return mapper.writeValueAsString(serviceCompositionCreateMessage);
    }

    public static String authenticatedEmptyMessageJSON()
            throws JsonProcessingException {
        AuthenticatedEmptyMessage authenticatedEmptyMessage = new AuthenticatedEmptyMessage();
        authenticatedEmptyMessage.setAuthorization("authorization");

        return mapper.writeValueAsString(authenticatedEmptyMessage);
    }

    public static String serviceInstanceCreateMessageJSON()
            throws JsonProcessingException {
        ServiceInstanceCreateMessage serviceInstanceCreateMessage = new ServiceInstanceCreateMessage();
        serviceInstanceCreateMessage.setAuthorization("authorization");
        serviceInstanceCreateMessage.setId("id");
        serviceInstanceCreateMessage.setData_provenance_collection(true);
        serviceInstanceCreateMessage.setPayment(true);
        serviceInstanceCreateMessage.setSource_code_id("sourceCodeId");
        serviceInstanceCreateMessage.setUri("Uri");

        return mapper.writeValueAsString(serviceInstanceCreateMessage);
    }

    public static String serviceObjectCreateMessageJSON()
            throws JsonProcessingException {
        ServiceObjectCreateMessage serviceObjectCreateMessage = new ServiceObjectCreateMessage();
        serviceObjectCreateMessage.setAuthorization("authorization");
        serviceObjectCreateMessage.setData_provenance_collection(true);
        serviceObjectCreateMessage.setId("id");
        serviceObjectCreateMessage.setPayment(true);
        serviceObjectCreateMessage.setRequires_token(true);

        return mapper.writeValueAsString(serviceObjectCreateMessage);
    }

    public static String serviceSourceCodeCreateMessageJSON()
            throws JsonProcessingException {
        ServiceSourceCodeCreateMessage serviceSourceCodeCreateMessage = new ServiceSourceCodeCreateMessage();
        serviceSourceCodeCreateMessage.setAuthorization("authorization");
        serviceSourceCodeCreateMessage.setId("id");
        serviceSourceCodeCreateMessage.setName("name");
        serviceSourceCodeCreateMessage.setPayment(true);
        serviceSourceCodeCreateMessage.setVersion("version");

        return mapper.writeValueAsString(serviceSourceCodeCreateMessage);
    }
}