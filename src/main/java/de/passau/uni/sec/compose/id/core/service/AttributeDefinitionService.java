package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.ApproveAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.event.CreateAttributeDefinitionEvent;
import de.passau.uni.sec.compose.id.core.event.CreateAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.ListAttributesInGroupEvent;
import de.passau.uni.sec.compose.id.core.event.ListPendingAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.event.UpdateAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.AbstractMultiInstanceRelationship;
import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.AttributeDefinition;
import de.passau.uni.sec.compose.id.core.persistence.entities.AttributeValue;
import de.passau.uni.sec.compose.id.core.persistence.entities.CoreEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.AttributeDefinitionRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.AttributeValueRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceCompositionRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceInstanceRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceObjectRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceSourceCodeRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;
import de.passau.uni.sec.compose.id.rest.messages.AttributeDefinitionResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.AttributeValueResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;


@Service
public class AttributeDefinitionService extends AbstractBasicListEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(AttributeDefinitionService.class);

	
	@Autowired
    GroupRepository groupRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired 
	RestAuthentication authentication;
	
	@Autowired
	AttributeDefinitionRepository attributeRepository;
	
	@Override
	protected IEntity getEntityById(String entityId) {
		
		return attributeRepository.findOne(entityId);
	}
	
	@Override
	public Object postACListAllEntities(Event event)
			throws IdManagementException {
		
		if(event instanceof ListAttributesInGroupEvent)
		{
			ListAttributesInGroupEvent gevent = (ListAttributesInGroupEvent)event;
			Group g = groupRepository.findOne(gevent.getId() );
			if(g == null)
				throw new IdManagementException("Group not found",null, LOG," Attempt to list attribute definitions for non-existing group with id: "+gevent.getId()+", principals "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 404);
			return getAttrubyteDefinitions(g);
		}
		throw new IdManagementException("Method not implemented",null, LOG,"The type of list functionality is not implemented",Level.ERROR, 501);	
	}
	

	private Collection<AttributeDefinitionResponseMessage> getAttrubyteDefinitions(Group g) {
		
		Collection<AttributeDefinitionResponseMessage> res = new LinkedList<>();
		List<AttributeDefinition> defs = attributeRepository.findByGroup(g);
		for(AttributeDefinition definition: defs)
		{
			res.add(new AttributeDefinitionResponseMessage(definition));
		}
		return res;
	}

	@Override
	public void verifyACListAllEntities(Event event)
			throws IdManagementException {
		if(event instanceof ListAttributesInGroupEvent)
		{
			//Just check that there is only one principal 
			Collection<IPrincipal> principals = event.getPrincipals();
			if(principals == null || principals.size()==0)
				throw new IdManagementException("Authentication required.",null, LOG," Attempt to list unapproved memberships without providing credentials",Level.DEBUG, 401);
	
			if(principals.size()!=1)
				throw new IdManagementException("Only one user principal should call this API endpoint.",null, LOG,"There is more than one principal for getting pending attribute value approvals",Level.DEBUG, 401);
		}
		else
			throw new IdManagementException("Method not implemented",null, LOG,"The type of list functionality is not implemented",Level.DEBUG, 501);
	}


	@Override
	protected EntityResponseMessage postACCreateEntity(Event event)
			throws IdManagementException {
		
		CreateAttributeDefinitionEvent attrEvent= (CreateAttributeDefinitionEvent) event;
		Group groupDefinition = groupRepository.findOne(attrEvent.getGroup_id());
		if(groupDefinition == null)
			throw new IdManagementException("Group not found",null, LOG," Attempt to list attribute definitions for non-existing group with id: "+attrEvent.getGroup_id()+", principals "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.ERROR, 404);
		
		List<AttributeDefinition> defs = attributeRepository.findByNameAndGroup(attrEvent.getMessage().getName(), groupDefinition);
		if(defs.size()!=0)
			throw new IdManagementException("Attribute definition already extists",null, LOG,"Attempt to create an attribute definition already exists in group with id: "+attrEvent.getGroup_id()+" with name: "+attrEvent.getMessage().getName()+", principals "+RestAuthentication.getBasicInfoPrincipals(attrEvent.getPrincipals()),Level.DEBUG, 409);
		AttributeDefinition attributeDef = new AttributeDefinition();
		//ownership or admin role in the the Group is checked in ACcess control method already
		attributeDef.setGroup(groupDefinition);
		attributeDef.setId(UUID.randomUUID().toString());
		attributeDef.setName(attrEvent.getMessage().getName());
		attributeDef.setType(attrEvent.getMessage().getType());
		attributeRepository.save(attributeDef);
		return new AttributeDefinitionResponseMessage(attributeDef);
		
	}

	@Override
	protected EntityResponseMessage postACGetEntity(Event event)
			throws IdManagementException {
			
			//TODO 
			return null;
		}

	@Override
	protected EntityResponseMessage postACUpdateEntity(DetailsIdEvent event,
			IEntity previous) throws IdManagementException {
		
		throw new IdManagementException("Method not implemented",null, LOG,"The type of list functionality is not implemented",Level.DEBUG, 501);
	}

	
	@Override
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {
		
		
		CreateAttributeDefinitionEvent attrEvent= ((CreateAttributeDefinitionEvent)event);
		Collection<IPrincipal> principals = event.getPrincipals();
		for(IPrincipal p: principals)
		{
			if(p instanceof ComposeUserPrincipal)
			{
				ComposeUserPrincipal user = ((ComposeUserPrincipal)p);
				OpenIdUserData userData = user.getOpenId();
				User u = userRepository.getOne(userData.getUser_id());
				Group g = groupRepository.findOne(attrEvent.getGroup_id());
				if(g == null)
					throw new IdManagementException("Group not found",null, LOG," Attempt to list attribute definitions for non-existing group with id: "+attrEvent.getGroup_id()+", principals "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.ERROR, 404);

				if(!u.isGroupAdmin(g.getId()) && !u.equals(g.getOwner()))				
					
					 throw new IdManagementException("Not sufficient permissions for the action requred : Attempt to create attribute defintition in a group for which the user is no the owner nor the admin",
							 						null, LOG,
							 						"Attempt to create an attribute defintion in a group for which the user is no the owner nor the admin. Group id: "+attrEvent.getGroup_id()+", principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);
			}
		}		
	}

	@Override
	protected void verifyAccessControlUpdateEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		Collection<IPrincipal> principals = event.getPrincipals();
		if(principals == null || principals.size()==0)
			throw new IdManagementException("Authentication required.",null, LOG," Attempt to list attribute values without providing credentials",Level.DEBUG, 401);
		
		if(principals.size()!=1)
			throw new IdManagementException("Only one user principal should call this API endpoint.",null, LOG,"There is more than one principal for getting pending user approvals ",Level.DEBUG, 401);
	
		//The rest of checks need to be done with the update since it must be specifically noticed whether the update is being done by the user, or the group owner/admin... see postACUPdate method...
	}

	@Override
	protected void verifyAccessControlGetEntity(Event event)
			throws IdManagementException {
		
		
	}

	@Override
	protected void postACDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		AttributeDefinition attempted = attributeRepository.findOne(event.getEntityId());
		attributeRepository.delete(attempted);
		
	}

	@Override
	protected void verifyAccessControlDeleteEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		Collection<IPrincipal> principals = event.getPrincipals();
		if(principals == null || principals.size()==0)
			throw new IdManagementException("Authentication required.",null, LOG," Attempt to list unapproved memberships without providing credentials",Level.DEBUG, 401);
		
		if(principals.size()!=1)
			throw new IdManagementException("Only one user principal should call this API endpoint.",null, LOG,"There is more than one principal while trying to remove a user approval",Level.DEBUG, 401);
		
		AttributeDefinition memb = attributeRepository.findOne(event.getEntityId());
		IPrincipal prin = principals.iterator().next();
		if(prin instanceof ComposeUserPrincipal)
		{
			Group g = memb.getGroup();
			User u = userRepository.findOne(((ComposeUserPrincipal) prin).getOpenId().getUser_id());
			if(!u.isGroupAdmin(g.getId()) && !u.equals(g.getOwner()))				
				 throw new IdManagementException("Not sufficient permissions for the action requred : Attempt to delete attribute defintition for a group for which the user is no the owner nor the admin",
						 						null, LOG,
						 						"Attempt to delete an attribute defintion with id: "+event.getEntityId()+" in a group for which the user is no the owner nor the admin. Group id: "+g.getId()+", principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);
		
		}
		else
			throw new IdManagementException("To delete an attribute, the principal must be a user",null, LOG,"Principal attempting to approve a membership is not a user. princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 401);
	
	}
	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}

	

	
}
