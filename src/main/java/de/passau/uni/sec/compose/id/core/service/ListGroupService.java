package de.passau.uni.sec.compose.id.core.service;

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
import de.passau.uni.sec.compose.id.core.event.ListGroupsEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.rest.messages.GroupResponseMessage;


@Service
public class ListGroupService extends AbstractListEntityService 
{

	private static final int MAX_PAGE = 10;

	private static Logger LOG = LoggerFactory.getLogger(ListGroupService.class);
	
	@Autowired
	private GroupRepository groupRepo;
	
	@Override
	public Object postACListAllEntities(Event event)
			throws IdManagementException 
	{
		
		ListGroupsEvent ev = (ListGroupsEvent) event;
		
		PageRequest req = new PageRequest(ev.getPage(), MAX_PAGE);
		Page<Group> groups = groupRepo.findAll(req);
		List<GroupResponseMessage> res = new LinkedList<GroupResponseMessage>();
		for(Group g : groups)
		{
			res.add(new GroupResponseMessage(g));
		}
		return res;
	}

	@Override
	public void verifyACListAllEntities(Event event)
			throws IdManagementException {
		
		//TODO for now everyone can list all gruops
		
	}

	
	
	

	
	
	
	
	
}
