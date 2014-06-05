package de.passau.uni.sec.compose.id.rest.controller.fixture;


import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;

public class RestEventFixtures {
    
    public static UserResponseMessage createUserResponseMessage(String id){
        UserResponseMessage responseMessage = new UserResponseMessage(new User());
        responseMessage.setId(id);
        
        return responseMessage;
    }

}
