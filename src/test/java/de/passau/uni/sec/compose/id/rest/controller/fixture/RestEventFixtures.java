package de.passau.uni.sec.compose.id.rest.controller.fixture;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Role;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceCompositionAttributes;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstanceAttributes;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObjectAttributes;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCodeAttributes;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityGroupMembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.GroupResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.PendingUserMembershipMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceCompositionResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceInstanceResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceObjectResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.ServiceSourceCodeResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserAuthenticatedMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;

public class RestEventFixtures {

    public static UserResponseMessage createUserResponseMessage(String id) {
        UserResponseMessage responseMessage = new UserResponseMessage(
                new User());
        responseMessage.setId(id);

        return responseMessage;
    }

    public static ApplicationResponseMessage applicationResponseMessage(
            String name, String id, User owner,
            Collection<EntityGroupMembership> groups, Date lastModified) {

        Application application = new Application();

        application.setName(name);
        application.setId(id);
        application.setOwner(owner);
        application.setGroups(groups);
        application.setLastModified(lastModified);

        return new ApplicationResponseMessage(application);
    }

    public static UserAuthenticatedMessage authenticateUserMessage(
            String token, String type) {
        UserAuthenticatedMessage message = new UserAuthenticatedMessage();
        message.setAccessToken(token);
        message.setToken_type(type);

        return message;
    }

    public static User user(String id, String username) {
        User user = new User();
        user.setId(id);
        user.setUsername(username);

        return user;
    }

    public static UserCreateMessage createUserMessage(String password,
            String username) {
        UserCreateMessage message = new UserCreateMessage();
        message.setPassword(password);
        message.setUsername(username);

        return message;
    }

    public static EntityGroupMembershipResponseMessage entityGroupMembershipResponseMessage(
            String groupMembershipId) {
        EntityGroupMembership entityGroupMembership = new EntityGroupMembership();
        entityGroupMembership.setId(groupMembershipId);
        entityGroupMembership.setGroup(new Group());
        EntityGroupMembershipResponseMessage entityGroupMembershipResponseMessage = new EntityGroupMembershipResponseMessage(
                entityGroupMembership);

        return entityGroupMembershipResponseMessage;
    }

    public static PendingUserMembershipMessage pendingUserMembershipMessage() {
        PendingUserMembershipMessage pendingUserMembershipMessage = new PendingUserMembershipMessage(
                null, null);

        return pendingUserMembershipMessage;
    }

    public static GroupResponseMessage groupResponseMessage(String groupId) {
        GroupResponseMessage groupResponseMessage = new GroupResponseMessage();
        groupResponseMessage.setId(groupId);

        return groupResponseMessage;
    }

    public static MembershipResponseMessage membershipResponseMessage(
            String memId, Date date) {
        Membership membership = new Membership();
        membership.setId(memId);
        membership.setLastModified(date);
        membership.setGroup(new Group());
        membership.setRole(new Role());
        membership.setUser(new User());

        return new MembershipResponseMessage(membership);
    }

    public static UserResponseMessage userResponseMessage() {
        return new UserResponseMessage(new User());
    }

    public static ServiceCompositionResponseMessage serviceCompositionResponseMessage(
            String id, Date date) {
        ServiceCompositionResponseMessage serviceCompositionResponseMessage = new ServiceCompositionResponseMessage();
        serviceCompositionResponseMessage.setId(id);
        serviceCompositionResponseMessage.setLastModified(date);

        return serviceCompositionResponseMessage;
    }

    public static ServiceInstanceResponseMessage serviceInstanceResponseMessage(
            String id, Date date) {
        ServiceInstanceResponseMessage serviceInstanceResponseMessage = new ServiceInstanceResponseMessage();
        serviceInstanceResponseMessage.setId(id);
        serviceInstanceResponseMessage.setLastModified(date);

        return serviceInstanceResponseMessage;
    }

    public static ServiceObjectResponseMessage serviceObjectResponseMessage(
            String id, Date date) {
        ServiceObjectResponseMessage serviceObjectResponseMessage = new ServiceObjectResponseMessage();
        serviceObjectResponseMessage.setId(id);
        serviceObjectResponseMessage.setLastModified(date);

        return serviceObjectResponseMessage;
    }

    public static ServiceSourceCodeResponseMessage serviceSourceCodeResponseMessage(
            String id, Date date) {
        ServiceSourceCodeResponseMessage serviceSourceCodeResponseMessage = new ServiceSourceCodeResponseMessage();
        serviceSourceCodeResponseMessage.setId(id);
        serviceSourceCodeResponseMessage.setLastModified(date);

        return serviceSourceCodeResponseMessage;
    }

    public static ServiceCompositionResponseMessage serviceCompositionResponseMessage() {
        ServiceComposition serviceComposition = new ServiceComposition();
        serviceComposition.setGroups(new LinkedList<EntityGroupMembership>());
        serviceComposition.setOwner(new User());
        serviceComposition
                .setServiceCompositionAttributes(new LinkedList<ServiceCompositionAttributes>());

        return new ServiceCompositionResponseMessage(serviceComposition);
    }

    public static ServiceInstanceResponseMessage serviceInstanceResponseMessage() {
        ServiceInstance serviceInstance = new ServiceInstance();
        serviceInstance.setGroups(new LinkedList<EntityGroupMembership>());
        serviceInstance.setOwner(new User());
        serviceInstance
                .setServiceInstanceAttributes(new LinkedList<ServiceInstanceAttributes>());
        serviceInstance.setServiceSourceCode(new ServiceSourceCode());

        return new ServiceInstanceResponseMessage(serviceInstance);
    }

    public static ServiceObjectResponseMessage serviceObjectResponseMessage() {
        ServiceObject serviceObject = new ServiceObject();
        serviceObject.setGroups(new LinkedList<EntityGroupMembership>());
        serviceObject
                .setServiceObjectAttributes(new LinkedList<ServiceObjectAttributes>());
        serviceObject.setOwner(new User());

        return new ServiceObjectResponseMessage(serviceObject, null);
    }

    public static ServiceSourceCodeResponseMessage serviceSourceCodeResponseMessage() {
        ServiceSourceCode serviceSourceCode = new ServiceSourceCode();
        serviceSourceCode.setDeveloper(new User());
        serviceSourceCode.setGroups(new LinkedList<EntityGroupMembership>());
        serviceSourceCode
                .setServiceSourceCodeAttributes(new LinkedList<ServiceSourceCodeAttributes>());

        return new ServiceSourceCodeResponseMessage(serviceSourceCode);
    }
}