package de.passau.uni.sec.compose.id.rest.controller;

import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.tokenUnmodifiedHttpHeader;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authorizationHttpHeader;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.authenticatedEmptyMessageJSON;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestEventFixtures.serviceObjectResponseMessage;
import static de.passau.uni.sec.compose.id.rest.controller.fixture.RestDataFixture.serviceObjectCreateMessageJSON;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Collection;
import java.util.Date;
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
import de.passau.uni.sec.compose.id.core.event.CreateServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.service.ApplicationService;
import de.passau.uni.sec.compose.id.core.service.ServiceObjectService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

@RunWith(PowerMockRunner.class)
@PrepareForTest(ServiceObjectService.class)
@PowerMockIgnore(value = {"org.apache.log4j.*"})
public class ServiceObjectCommandsControllerIntegrationTest {

    MockMvc mockMvc;

    @InjectMocks
    ServiceObjectCommandsController serviceObjectCommandsController;

    @Mock
    RestAuthentication authenticator;

    @Spy
    ServiceObjectService serviceObjectService = new ServiceObjectService();

    @SuppressWarnings("unchecked")
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        this.mockMvc = standaloneSetup(serviceObjectCommandsController)
                .setMessageConverters(new MappingJackson2HttpMessageConverter())
                .build();

        when(
                authenticator.authenticatePrincipals(any(Logger.class),
                        any(Collection.class))).thenReturn(
                new LinkedList<IPrincipal>());
    }

    @Test
    public void createEntityHttpCreatedTest() throws Exception {
        Date date = new Date();
        PowerMockito.doReturn(serviceObjectResponseMessage("testId", date))
                .when(serviceObjectService)
                .createEntity(any(CreateServiceObjectEvent.class));

        this.mockMvc.perform(
                post("/idm/serviceobject/").headers(authorizationHttpHeader())
                        .content(serviceObjectCreateMessageJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().isCreated());
    }

    @Test
    public void createServiceObjectRenderAsJsonTest() throws Exception {
        Date date = new Date();
        PowerMockito.doReturn(serviceObjectResponseMessage("testId", date))
                .when(serviceObjectService)
                .createEntity(any(CreateServiceObjectEvent.class));

        this.mockMvc
                .perform(
                        post("/idm/serviceobject/")
                                .headers(authorizationHttpHeader())
                                .content(serviceObjectCreateMessageJSON())
                                .contentType(MediaType.APPLICATION_JSON)
                                .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value("testId"))
                .andExpect(jsonPath("$.lastModified").value(date.getTime()));
    }

    @Test
    public void createServiceObjectIdmExceptionTest() throws Exception {
        PowerMockito
                .doThrow(
                        new IdManagementException(null, null, LoggerFactory
                                .getLogger(ApplicationService.class), null,
                                Level.ERROR, 500)).when(serviceObjectService)
                .createEntity(any(CreateServiceObjectEvent.class));

        this.mockMvc.perform(
                post("/idm/serviceobject/").headers(authorizationHttpHeader())
                        .content(serviceObjectCreateMessageJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }

    @Test
    public void deleteEntityHttpOkTest() throws Exception {
        PowerMockito.doNothing().when(serviceObjectService)
                .deleteEntity(any(DeleteServiceObjectEvent.class));

        this.mockMvc.perform(
                delete("/idm/serviceobject/testId")
                        .headers(tokenUnmodifiedHttpHeader())
                        .content(authenticatedEmptyMessageJSON())
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
                                Level.ERROR, 500)).when(serviceObjectService)
                .deleteEntity(any(DeleteServiceObjectEvent.class));

        this.mockMvc.perform(
                delete("/idm/serviceobject/testId")
                        .headers(tokenUnmodifiedHttpHeader())
                        .content(authenticatedEmptyMessageJSON())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)).andExpect(
                status().is5xxServerError());
    }
}
