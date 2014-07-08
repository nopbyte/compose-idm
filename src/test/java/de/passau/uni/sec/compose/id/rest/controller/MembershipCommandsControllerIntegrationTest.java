package de.passau.uni.sec.compose.id.rest.controller;

import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authorizationHttpHeaderToken;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.tokenUnmodifiedHttpHeader;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.membershipResponseMessage;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.membershipCreateMessageJSON;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.junit.After;
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
import de.passau.uni.sec.compose.id.core.event.ApproveMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.CreateMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteUserMembershipEvent;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.UserMembershipService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(UserMembershipService.class)
@PowerMockIgnore(value = {"org.apache.log4j.*"})
public class MembershipCommandsControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    MembershipCommandsController membershipCommandsController;

    @Mock
    RestAuthentication authenticator;

    @Spy
    UserMembershipService userMembershipService = new UserMembershipService();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(membershipCommandsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        when(
                authenticator.authenticatePrincipals(any(Logger.class),
                        any(Collection.class))).thenReturn(
                new LinkedList<IPrincipal>());
    }
    
    @After
    public void tearDown(){
        userMembershipService = null;
        mockMvc = null;
        System.out.println("tearDown");
    }

    @Test
    public void createEntityHttpCreatedTest() throws Exception {
        Date date = new Date();
        PowerMockito.doReturn(membershipResponseMessage("memberId", date))
                .when(userMembershipService)
                .createEntity(any(CreateMembershipEvent.class));

        this.mockMvc.perform(
                post("/idm/memberships/user/testId/")
                        .headers(authorizationHttpHeaderToken())
                        .content(membershipCreateMessageJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
    }

    @Test
    public void createMembershipRenderAsJsonTest() throws Exception {
        Date date = new Date();
        PowerMockito.doReturn(membershipResponseMessage("memberId", date))
                .when(userMembershipService)
                .createEntity(any(CreateMembershipEvent.class));

        this.mockMvc
                .perform(
                        post("/idm/memberships/user/testId/")
                                .headers(authorizationHttpHeaderToken())
                                .content(membershipCreateMessageJSON())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("memberId"))
                .andExpect(jsonPath("$.lastModified").value(date.getTime()));
    }

    @Test
    public void createMembershipIdmExceptionTest() throws Exception {
        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(userMembershipService)
                .createEntity(any(CreateMembershipEvent.class));

        this.mockMvc.perform(
                post("/idm/memberships/user/testId/")
                        .headers(authorizationHttpHeaderToken())
                        .content(membershipCreateMessageJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }

    @Test
    public void changeEntityHttpisOk() throws Exception {
        Date date = new Date();
        PowerMockito.doReturn(membershipResponseMessage("memberId", date))
                .when(userMembershipService)
                .updateEntity(any(ApproveMembershipEvent.class));

        this.mockMvc.perform(
                put("/idm/memberships/approve/testId")
                        .headers(tokenUnmodifiedHttpHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isOk());
    }

    @Test
    public void deleteEntityHttpOkTest() throws Exception {
        PowerMockito.doNothing().when(userMembershipService)
                .deleteEntity(any(DeleteUserMembershipEvent.class));

        this.mockMvc.perform(
                delete("/idm/memberships/testId")
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
                                Level.ERROR, 500)).when(userMembershipService)
                .deleteEntity(any(DeleteUserMembershipEvent.class));

        this.mockMvc.perform(
                delete("/idm/memberships/testId")
                        .headers(tokenUnmodifiedHttpHeader())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }
}
