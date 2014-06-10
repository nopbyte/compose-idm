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
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateApplicationEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteApplicationEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authorizationHttpHeader;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.tokenUnmodifiedHttpHeader;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.createUserDataJSON;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.ifUnmodifiedHttpHeader;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.tokenAuthenticationDataJSON;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.createApplicationDataJSON;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.applicationResponseMessage;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ApplicationService.class)
public class ApplicationCommandsControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    ApplicationCommandsController applicationCommandsController;

    @Mock
    RestAuthentication authenticator;

    @Spy
    ApplicationService applicationService = new ApplicationService();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(applicationCommandsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        when(
                authenticator.authenticatePrincipals(any(Logger.class),
                        any(Collection.class))).thenReturn(
                new LinkedList<IPrincipal>());
    }

    @Test
    public void createEntityHttpCreatedTest() throws Exception {

        PowerMockito
                .doReturn(
                        applicationResponseMessage("name", "id",
                                user("testId", "testName"),
                                new LinkedList<EntityGroupMembership>(), null))
                .when(applicationService)
                .createEntity(any(CreateApplicationEvent.class));

        this.mockMvc.perform(
                post("/idm/application/").headers(authorizationHttpHeader())
                        .content(createApplicationDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
    }

    @Test
    public void createApplicationRenderAsJsonTest() throws Exception {

        PowerMockito
                .doReturn(
                        applicationResponseMessage("testName", "testId",
                                user("testId", "testName"),
                                new LinkedList<EntityGroupMembership>(), null))
                .when(applicationService)
                .createEntity(any(CreateApplicationEvent.class));

        this.mockMvc
                .perform(
                        post("/idm/application/")
                                .headers(authorizationHttpHeader())
                                .content(createApplicationDataJSON())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("testId"))
                .andExpect(jsonPath("$.name").value("testName"));
    }

    @Test
    public void createApplicationLocationHeaderTest() throws Exception {

        PowerMockito
                .doReturn(
                        applicationResponseMessage("testName", "testId",
                                user("testId", "testName"),
                                new LinkedList<EntityGroupMembership>(), null))
                .when(applicationService)
                .createEntity(any(CreateApplicationEvent.class));

        this.mockMvc.perform(
                post("/idm/application/").headers(authorizationHttpHeader())
                        .content(createApplicationDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                header().string("Location", "http://localhost/testId"));
    }

    @Test
    public void createApplicationIdmExceptionTest() throws Exception {

        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(applicationService)
                .createEntity(any(CreateApplicationEvent.class));

        this.mockMvc.perform(
                post("/idm/application/").headers(authorizationHttpHeader())
                        .content(createApplicationDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }

    @Test
    public void updateApplicationNotImplementedReminder() throws Exception {

        this.mockMvc.perform(
                put("/idm/application/test")
                        .headers(tokenUnmodifiedHttpHeader())
                        .content(createUserDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isNotImplemented());
    }

    @Test
    public void deleteApplicationHttpOkTest() throws Exception {

        PowerMockito.doNothing().when(applicationService)
                .deleteEntity(any(DeleteApplicationEvent.class));

        this.mockMvc.perform(
                delete("/idm/application/test")
                        .headers(ifUnmodifiedHttpHeader())
                        .content(tokenAuthenticationDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void deleteApplicationIdmExceptionTest() throws Exception {

        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(applicationService)
                .deleteEntity(any(DeleteApplicationEvent.class));

        this.mockMvc.perform(
                delete("/idm/application/test")
                        .headers(ifUnmodifiedHttpHeader())
                        .content(tokenAuthenticationDataJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }
}
