package de.passau.uni.sec.compose.id.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import de.passau.uni.sec.compose.id.core.service.UserMembershipService;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;



@Controller
@RequestMapping("/idm/memberships")
public class ApproveMembershipsCommandsController {

	private static Logger LOG = LoggerFactory.getLogger(ApproveMembershipsCommandsController.class);
	
	@Autowired
    private UserMembershipService membershipService;
	
	@Autowired
	private RestAuthentication authenticator;
	
    

}