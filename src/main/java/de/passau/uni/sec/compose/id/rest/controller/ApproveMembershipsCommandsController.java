package de.passau.uni.sec.compose.id.rest.controller;

import java.util.Collection;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.util.UriComponentsBuilder;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.ApproveMembershipEvent;
import de.passau.uni.sec.compose.id.core.event.DeleteUserMembershipEvent;
import de.passau.uni.sec.compose.id.core.service.UserMembershipService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;



@Controller
@RequestMapping("/idm/memberships")
public class ApproveMembershipsCommandsController {

	private static Logger LOG = LoggerFactory.getLogger(ApproveMembershipsCommandsController.class);
	
	@Autowired
    private UserMembershipService membershipService;
	
	@Autowired
	private RestAuthentication authenticator;
	
    

}