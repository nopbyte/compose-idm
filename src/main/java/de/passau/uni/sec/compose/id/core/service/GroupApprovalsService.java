package de.passau.uni.sec.compose.id.core.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;

@Service
public class GroupApprovalsService 
{

	private static Logger LOG = LoggerFactory.getLogger(GroupService.class);
	
	@Autowired
	GroupRepository groupRepository;
	
	
	@Autowired
	UsersAuthzAndAuthClient uaa;
	
	@Autowired 
	RestAuthentication authentication;
	
	
}
