package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.LinkedList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.slf4j.Logger;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.*;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserService.class)
@PowerMockIgnore(value = {"org.apache.log4j.*"})
public class UserCommandsControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    UserCommandsController userCommandsController;

    @Mock
    private RestAuthentication authenticator;

    @Spy
    private UserService userService = new UserService();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);
        this.mockMvc = standaloneSetup(userCommandsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        when(
                authenticator.authenticatePrincipals(any(Logger.class),
                        any(Collection.class))).thenReturn(
                new LinkedList<IPrincipal>());

        PowerMockito.doReturn(createUserResponseMessage("test"))
                .when(userService).createEntity(any(CreateUserEvent.class));
    }

    @Test
    public void createUserHttpCreatedTest() throws Exception {

        this.mockMvc.perform(
                post("/idm/user/").headers(authorizationHttpHeader())
                        .content(createUserDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
    }

    @Test
    public void createUserRendersAsJson() throws Exception {
        this.mockMvc.perform(
                post("/idm/user/").headers(authorizationHttpHeader())
                        .content(createUserDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                jsonPath("$.id").value("test"));
    }

    @Test
    public void createUserLocationHeaderTest() throws Exception {

        this.mockMvc.perform(
                post("/idm/user/").headers(authorizationHttpHeader())
                        .content(createUserDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                header().string("Location", "http://localhost/test"));
    }
}
