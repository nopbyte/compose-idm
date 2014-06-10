package de.passau.uni.sec.compose.id.rest.controller.fixture;

import java.util.Collection;
import java.util.Date;

import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationResponseMessage;
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
}
