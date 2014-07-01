package de.passau.uni.sec.compose.id.rest.controller;

import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authorizationHttpHeaderToken;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.tokenUnmodifiedHttpHeader;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.createGroupJSON;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.groupResponseMessage;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.userUpdateMessageJSON;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateGroupEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteGroupEvent;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.GroupService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(GroupService.class)
@PowerMockIgnore(value = {"org.apache.log4j.*"})
public class GroupCommandsControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    GroupCommandsController groupCommandsController;

    @Mock
    RestAuthentication authenticator;

    @Spy
    GroupService groupService = new GroupService();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(groupCommandsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        when(
                authenticator.authenticatePrincipals(any(Logger.class),
                        any(Collection.class))).thenReturn(
                new LinkedList<IPrincipal>());
    }
    
    @Test
    public void createEntityHttpCreatedTest() throws Exception {
        PowerMockito.doReturn(groupResponseMessage("groupTestId"))
                .when(groupService).createEntity(any(CreateGroupEvent.class));

        this.mockMvc.perform(
                post("/idm/group/").headers(authorizationHttpHeaderToken())
                        .content(createGroupJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
    }

    @Test
    public void createGroupRenderAsJsonTest() throws Exception {
        PowerMockito.doReturn(groupResponseMessage("groupTestId"))
                .when(groupService).createEntity(any(CreateGroupEvent.class));

        this.mockMvc.perform(
                post("/idm/group/").headers(authorizationHttpHeaderToken())
                        .content(createGroupJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                jsonPath("$.id").value("groupTestId"));
    }

    @Test
    public void createGroupIdmExceptionTest() throws Exception {

        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(groupService)
                .createEntity(any(CreateGroupEvent.class));

        this.mockMvc.perform(
                post("/idm/group/").headers(authorizationHttpHeaderToken())
                        .content(createGroupJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }

    @Test
    public void changeEntityHttpisOk() throws Exception {
        this.mockMvc.perform(
                put("/idm/group/testId/").headers(tokenUnmodifiedHttpHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(userUpdateMessageJSON())
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void deleteEntityHttpOkTest() throws Exception {

        PowerMockito.doNothing().when(groupService)
                .deleteEntity(any(DeleteGroupEvent.class));

        this.mockMvc.perform(
                delete("/idm/group/testId/")
                        .headers(tokenUnmodifiedHttpHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void deleteEntityIdmExceptionTest() throws Exception {

        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(groupService)
                .deleteEntity(any(DeleteGroupEvent.class));

        this.mockMvc.perform(
                delete("/idm/group/testId/")
                        .headers(tokenUnmodifiedHttpHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }
}
