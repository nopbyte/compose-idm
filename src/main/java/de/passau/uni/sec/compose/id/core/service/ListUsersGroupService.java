package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.ListUsersInGroupEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.MembershipRepository;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UsersInGroupMessage;


@Service
public class ListUsersGroupService extends AbstractListEntityService  
{

	private static Logger LOG = LoggerFactory.getLogger(ListUsersGroupService.class);

	
	@Autowired
    GroupRepository groupRepository;
	
	
	@Autowired
    MembershipRepository membershipRepository;
	
	@Autowired
	Authorization authz;
	
	@Override
	public Object postACListAllEntities(Event event)
			throws IdManagementException {
		
		ListUsersInGroupEvent lsitEvent = ((ListUsersInGroupEvent)event);
		Group group = groupRepository.getOne(lsitEvent.getId());
		List<Membership> membs = membershipRepository.findByGroup(group);
		List<MembershipResponseMessage> response = new LinkedList<MembershipResponseMessage>();
		for(Membership memb: membs)
		{
			if(memb.isApprovedByGroupOwner() && memb.isApprovedByUser())
				response.add(new MembershipResponseMessage(memb));
		}
		UsersInGroupMessage ret = new UsersInGroupMessage();
		ret.setApprovedMemberships(response);
		return ret;
		
	}

	@Override
	public void verifyACListAllEntities(Event event)
			throws IdManagementException {
		
		Collection<IPrincipal> p = event.getPrincipals();
		if(p == null || p.size()==0)
			throw new IdManagementException("Authentication required.",null, LOG," Attempt to access GET without providing credentials",Level.DEBUG, 401);

		
	}

	
}
