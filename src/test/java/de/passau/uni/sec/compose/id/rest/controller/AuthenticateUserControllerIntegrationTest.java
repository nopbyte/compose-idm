package de.passau.uni.sec.compose.id.rest.controller;

import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authenticateUserDataJSON;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.authenticateUserMessage;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.service.UserAuthenticate;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.rest.controller.authentication.AuthenticateUserController;
import de.passau.uni.sec.compose.id.rest.messages.UserAuthenticatedMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserCredentials;

public class AuthenticateUserControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    AuthenticateUserController authenticateUserController;

    @Mock
    UserAuthenticate userAuthenticate;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(authenticateUserController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();
    }

    @Test
    public void authenticateIdmExceptionTest() throws Exception {

        when(userAuthenticate.authenticateUser(any(UserCredentials.class)))
                .thenThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(UserService.class), null,
                                Level.ERROR, 500));

        this.mockMvc.perform(
                post("/auth/user/").content(authenticateUserDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }

    @Test
    public void authenticateUserHttpOkTest() throws Exception {

        when(userAuthenticate.authenticateUser(any(UserCredentials.class)))
                .thenReturn(new UserAuthenticatedMessage());

        this.mockMvc.perform(
                post("/auth/user/").content(authenticateUserDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void authenticateUserRendersAsJsonTest() throws Exception {

        when(userAuthenticate.authenticateUser(any(UserCredentials.class)))
                .thenReturn(authenticateUserMessage("testToken","testType"));

        this.mockMvc
                .perform(
                        post("/auth/user/").content(authenticateUserDataJSON())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.accessToken").value("testToken"))
                .andExpect(jsonPath("$.token_type").value("testType"));
    }
}
