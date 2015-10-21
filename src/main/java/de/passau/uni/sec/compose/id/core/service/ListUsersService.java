package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.ListUsersEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;


@Service
public class ListUsersService extends AbstractListEntityService 
{

	private static final int MAX_PAGE = 10;

	private static Logger LOG = LoggerFactory.getLogger(ListUsersService.class);
	
	@Autowired
	private UserRepository userRepo;
	
	@Override
	public Object postACListAllEntities(Event event)
			throws IdManagementException 
	{
		
		ListUsersEvent ev = (ListUsersEvent) event;
		List<UserResponseMessage> res = new LinkedList<UserResponseMessage>();
		
		if(ev.getPage() == -1){
			Collection<User> users=userRepo.findAll();
			for(User u : users)
				res.add(new UserResponseMessage(u));
		}
		else{
			PageRequest req = new PageRequest(ev.getPage(), MAX_PAGE);
			Page<User> users = userRepo.findAll(req);
			for(User u : users)
				res.add(new UserResponseMessage(u));
		}
		return res;
	}

	@Override
	public void verifyACListAllEntities(Event event)
			throws IdManagementException {
		
		//TODO for now everyone can list all gruops
		
	}

	
	
	

	
	
	
	
	
}
