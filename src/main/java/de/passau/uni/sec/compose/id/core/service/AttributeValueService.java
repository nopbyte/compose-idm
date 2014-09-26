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
import de.passau.uni.sec.compose.id.core.event.CreateAttributeValueEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
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
import de.passau.uni.sec.compose.id.rest.messages.AttributeValueResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;


@Service
public class AttributeValueService extends AbstractBasicListEntityService implements EntityService 
{

	private static Logger LOG = LoggerFactory.getLogger(AttributeValueService.class);

	
	@Autowired
    GroupRepository groupRepository;
	
	@Autowired
	AttributeValueRepository attributeRepository;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	ApplicationRepository appRepository;
	
	@Autowired
	ServiceObjectRepository serviceObjectRepository;
	
	@Autowired
	ServiceInstanceRepository serviceInstanceRepository;
	
	@Autowired
	ServiceCompositionRepository serviceCompositionRepository;
	
	@Autowired
	ServiceSourceCodeRepository serviceSourceCodeRepository;
	
	@Autowired
	AttributeDefinitionRepository attributeDefinitionRepository;
	
	
	@Autowired 
	RestAuthentication authentication;

	/**
	 * 
	 * @param event Event for creation of the entity
	 * @return The entity corresponding to the event
	 * @throws IdManagementException exception if the type of entity is not found, or if the proper repository failed to find the identity for the given id.
	 */
	private CoreEntity getEntity(CreateAttributeValueEvent event) throws IdManagementException
	{
		CoreEntity ret = null;
		if(event.getEntityType().equals(AttributeValue.USER))
			ret = userRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(AbstractMultiInstanceRelationship.APPLICATION))
			ret = appRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(AbstractMultiInstanceRelationship.SERVICECOMPOSITION))
			ret = serviceCompositionRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(AbstractMultiInstanceRelationship.SERVICEINSTANCE))
			ret = serviceInstanceRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(AbstractMultiInstanceRelationship.SERVICEOBJECT))
			ret =serviceObjectRepository.getOne(event.getEntityId());
		else if(event.getEntityType().equals(AbstractMultiInstanceRelationship.SERVICESOURCECODE))
			ret =serviceSourceCodeRepository.getOne(event.getEntityId());
		else 
			throw new IdManagementException("The entity type is not valid",null, LOG,"Principal attempting to create a entity group membership with a wrong entity type"+event.getEntityType()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.ERROR, 404);
		return ret;
	}
	
	private  AttributeValue getAttributeForAttributeDefinition(CoreEntity entity, AttributeDefinition definition)
	{
		List<?> ret = null;
		if(entity instanceof User)
			ret = attributeRepository.findByDefinitionAndUser(definition, (User) entity);
		else if(entity instanceof Application)
			ret = attributeRepository.findByDefinitionAndApplication(definition, (Application)entity);
		else if(entity instanceof ServiceComposition)
			ret = attributeRepository.findByDefinitionAndServiceComposition(definition,(ServiceComposition) entity);
		else if(entity instanceof ServiceInstance)
			ret = attributeRepository.findByDefinitionAndServiceInstance(definition,(ServiceInstance) entity);
		else if(entity instanceof ServiceObject)
			ret = attributeRepository.findByDefinitionAndServiceObject(definition,(ServiceObject) entity);
		else if(entity instanceof ServiceSourceCode)
			ret = attributeRepository.findByDefinitionAndServiceSourceCode(definition,(ServiceSourceCode) entity);
		if( ret!=null&&ret.size()>0)
			return (AttributeValue) ret.iterator().next();
		
		return null;
	}
	
	private AttributeValue buildAttributeVlue(User u, CreateAttributeValueEvent event) throws IdManagementException {
	
		CoreEntity entity = getEntity(event);
		AttributeValue attributeValue = new AttributeValue();
		if (entity instanceof User)
			attributeValue.setUser(userRepository.getOne(event.getEntityId()));
		else if(entity instanceof Application)
			attributeValue.setApplication(appRepository.getOne(event.getEntityId()));
		else if(entity instanceof ServiceComposition)
			attributeValue.setServiceComposition(serviceCompositionRepository.getOne(event.getEntityId()));
		else if(entity instanceof ServiceInstance)
			attributeValue.setServiceInstance(serviceInstanceRepository.getOne(event.getEntityId()));
		else if(entity instanceof ServiceObject)
			attributeValue.setServiceObject(serviceObjectRepository.getOne(event.getEntityId()));
		else if(entity instanceof ServiceSourceCode)
			attributeValue.setServiceSourceCode(serviceSourceCodeRepository.getOne(event.getEntityId()));
		else 
			throw new IdManagementException("The entity type is not valid",null, LOG,"Principal attempting to create a entity group membership with a wrong entity type"+event.getEntityType()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.ERROR, 404);
		return attributeValue;
	}

	@Override
	protected IEntity getEntityById(String entityId) {
		
		return attributeRepository.findOne(entityId);
	}
	
	@Override
	public Object postACListAllEntities(Event event)
			throws IdManagementException {
		if(event instanceof ListPendingAttributeValueEvent)
		{
			return getPendingAttributeApprovals((ListPendingAttributeValueEvent) event, event.getPrincipals());
		}
		return null;
	}

	private List<AttributeValueResponseMessage> getPendingAttributeApprovals(
			ListPendingAttributeValueEvent event,
			Collection<IPrincipal> principals) throws IdManagementException {
		
		Set<AttributeValue>   valueSet = new HashSet< AttributeValue>()  ;
		User u = authentication.getUserFromEvent(event);
		Collection<Group> groups = u.getGroups();
		
		for(Group group: groups)
			valueSet.addAll(getPendingAttributeValues(group));
		
		for(Membership principaMembership: u.getMemberships())
		{
			Group group = principaMembership.getGroup();
			if(u.isGroupAdmin(group.getId()))
				valueSet.addAll(getPendingAttributeValues(group));
		}
		
		List<AttributeValueResponseMessage> returnValue = new LinkedList<AttributeValueResponseMessage>();
		for(AttributeValue val: valueSet)
			returnValue.add(new AttributeValueResponseMessage(val));
		return returnValue;
	}

	private Collection<AttributeValue> getPendingAttributeValues(Group g) {
		
		List<AttributeValue> ret = new LinkedList<AttributeValue>();
		List<AttributeDefinition> defs = attributeDefinitionRepository.findByGroup(g);
		for(AttributeDefinition definition: defs)
			ret.addAll(
					attributeRepository.findByDefinitionAndApproved(definition, false)
					);
		return ret;
	}

	@Override
	public void verifyACListAllEntities(Event event)
			throws IdManagementException {
		if(event instanceof ListPendingAttributeValueEvent)
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
		
		CreateAttributeValueEvent attrEvent = (CreateAttributeValueEvent) event;
		AttributeDefinition def = attributeDefinitionRepository.findOne(attrEvent.getMessage().getAttribute_definition_id());
		if(def == null)
			 throw new IdManagementException("Attribute definition not found",null, LOG,"Attempt to create an attribute value for non-existing attribute definition. attribute_id: "+attrEvent.getMessage().getAttribute_definition_id()+", principals "+RestAuthentication.getBasicInfoPrincipals(attrEvent.getPrincipals()),Level.ERROR, 404);
		
		CoreEntity entity = getEntity(attrEvent);
		AttributeValue value = getAttributeForAttributeDefinition(entity, def);
		if(value != null)
		    throw new IdManagementException("Entity with id:"+attrEvent.getEntityId()+" with type:"+attrEvent.getEntityType()+" has already an attribute value for the same attribute value definition" ,null, LOG,
		    		"Entity with id:"+attrEvent.getEntityId()+" with type:"+attrEvent.getEntityType()+" has already an attribute value for the same attribute value definition"
		    		+ ""+ "Principals: "+RestAuthentication.getBasicInfoPrincipals(attrEvent.getPrincipals()),Level.INFO, 409);
	
		ComposeUserPrincipal up = RestAuthentication.getComposeUser(event.getPrincipals());
		User userCreatingAttributeValue = userRepository.findOne(up.getOpenId().getUser_id());
		value = buildAttributeVlue(userCreatingAttributeValue, attrEvent);
		if(value.getEntity().getOwner().equals(userCreatingAttributeValue))
		{
			Group groupAttrDefinition = def.getGroup();
			boolean groupOwner = userCreatingAttributeValue.equals(groupAttrDefinition.getOwner());
			boolean groupAdmin = userCreatingAttributeValue.isGroupAdmin(groupAttrDefinition.getId());
			value.setApproved(groupAdmin || groupOwner);			
			value.setValue(attrEvent.getMessage().getValue());
			value.setDefinition(def);
			value.setId(UUID.randomUUID().toString());
			attributeRepository.save(value);
			return new AttributeValueResponseMessage(value);
		}
		else 
			throw new IdManagementException("The principal calling the API does not have enough permissions to create the attribute value for the entity with id: "+attrEvent.getEntityId(),
											null, LOG,
											"The principal calling the API does not have enough permissions to create the attribute value for the entity with id: "+attrEvent.getEntityId()+" and entity type: "+attrEvent.getEntityType()
											+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 403);
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
		
		if(event instanceof ApproveAttributeValueEvent)
			return attemptApproval(event);
		if(event instanceof UpdateAttributeValueEvent)
			return updateAttributeValue((UpdateAttributeValueEvent) event);
		throw new IdManagementException("Method not implemented",null, LOG,"The type of list functionality is not implemented",Level.DEBUG, 501);
	}

	private EntityResponseMessage updateAttributeValue(UpdateAttributeValueEvent event) throws IdManagementException 
	{
		Collection<IPrincipal> principals = event.getPrincipals();
		AttributeValue value = attributeRepository.findOne(event.getEntityId());
		if(value == null)
			throw new IdManagementException("Entity not found",null, LOG,"Principal attempting to update a non-existing attribute value with id: "+event.getEntityId()+" principals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 404);
		
		IPrincipal prin = principals.iterator().next();
		if(prin instanceof ComposeUserPrincipal)
		{
			ComposeUserPrincipal userPrincipal = ((ComposeUserPrincipal)prin);
			//it is a membership that needs authorization of the owner of the entity
			User u = userRepository.getOne(userPrincipal.getOpenId().getUser_id());
			if(value.getEntity().getOwner().equals(u))
			{
				value.setValue(event.getMessage().getValue());
				value.setApproved(false);
				value.setLastModified(new Date( (new Date().getTime()/1000)*1000 ));
				value = attributeRepository.save(value);
				return new AttributeValueResponseMessage(value);
			}
			else
				throw new IdManagementException("The principal calling the API does not have enough permissions to update the attribute with id: "+event.getEntityId(),null, LOG,"Principal attempting to approve the attribute value didn't have sufficient permissions. Attribute Vaue id"+value.getId()+" Attribute definition "+value.getDefinition().getId()+" Group for the definition: "+value.getDefinition().getGroup().getId()
						+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 403);
		}
		else
			throw new IdManagementException("To approve a membership, the principal must be a user",null, LOG,"Principal attempting to approve a membership is not a user. princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 401);
	
		

	}

	private EntityResponseMessage attemptApproval(DetailsIdEvent event)
			throws IdManagementException {
		
		Collection<IPrincipal> principals = event.getPrincipals();
		ApproveAttributeValueEvent approve = ((ApproveAttributeValueEvent  ) event);
		AttributeValue value = attributeRepository.getOne(approve.getEntityId());
		IPrincipal prin = principals.iterator().next();
		if(prin instanceof ComposeUserPrincipal)
		{
			ComposeUserPrincipal userPrincipal = ((ComposeUserPrincipal)prin);
			CoreEntity core = value .getEntity();
			//it is a membership that needs authorization of the owner of the entity
			User u = userRepository.getOne(userPrincipal.getOpenId().getUser_id());
			if(u.isGroupAdmin(value.getDefinition().getGroup().getId()) ||
					value.getDefinition().getGroup().getOwner().equals(u))
			{
				value.setApprovedBy(u.getId());
				value.setApproved(true);
			}
			else
					
				throw new IdManagementException("The principal calling the API does not have enough permissions to approve the attribute",null, LOG,"Principal attempting to approve the attribute value didn't have sufficient permissions. Attribute Vaue id"+value.getId()+" Attribute definition "+value.getDefinition().getId()+" Group for the definition: "+value.getDefinition().getGroup().getId()
						+"Entity involved owning the attribute id: "+core.getId()+", Entity id : "+value.getEnityId()+" of type:"+value.getEntityType()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 403);
		}
		else
			throw new IdManagementException("To approve a membership, the principal must be a user",null, LOG,"Principal attempting to approve a membership is not a user. princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 401);
	
		value.setLastModified(new Date( (new Date().getTime()/1000)*1000 ));
		value = attributeRepository.save(value);
		return new AttributeValueResponseMessage(value);
	}

	@Override
	protected void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {
		
		CoreEntity entity = getEntity((CreateAttributeValueEvent) event);
		CreateAttributeValueEvent attrEvent= ((CreateAttributeValueEvent)event);
		Collection<IPrincipal> principals = event.getPrincipals();
		for(IPrincipal p: principals)
		{
			if(p instanceof ComposeUserPrincipal)
			{
				ComposeUserPrincipal user = ((ComposeUserPrincipal)p);
				OpenIdUserData userData = user.getOpenId();
				User u = userRepository.getOne(userData.getUser_id());
				AttributeDefinition def = attributeDefinitionRepository.findOne(attrEvent.getMessage().getAttribute_definition_id());
				if(def == null)
					 throw new IdManagementException("Attribute definition not found",null, LOG,"Attempt to create an attribute value for non-existing attribute definition. attribute_id: "+attrEvent.getMessage().getAttribute_definition_id()+", principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 404);
				if(!entity.getOwner().equals(u))
					 throw new IdManagementException("Not sufficient permissions for the action requred : Attempt to create in a group for an entity for which the user is no the owner",null, LOG,"Attempt to create in a group for an entity for which the user is no the owner. Entity id: "+entity.getId()+", principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);
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
		
		AttributeValue attempted = attributeRepository.getOne(event.getEntityId());
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
		
		AttributeValue memb = attributeRepository.getOne(event.getEntityId());
		IPrincipal prin = principals.iterator().next();
		if(prin instanceof ComposeUserPrincipal)
		{
			if(!userCanDelete(memb, (ComposeUserPrincipal) prin))
				throw new IdManagementException("User does not have sufficient permissions to delete the membesrhip ",null, LOG,"Principal attempting to delete an attribute value with id:"+event.getEntityId()+" without sufficient permissions."
						+" entity id: "+memb.getEnityId()+", entity type: "+memb.getEntityType()+". princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 403);
		}
		else
			throw new IdManagementException("To delete an attribute, the principal must be a user",null, LOG,"Principal attempting to approve a membership is not a user. princpals: "+RestAuthentication.getBasicInfoPrincipals(event.getPrincipals()),Level.INFO, 401);
	
	}
	private boolean userCanDelete(AttributeValue  attributeValue, ComposeUserPrincipal user)
	{
		//check if the user is admin of the group of the membership
		User u = userRepository.getOne(user.getOpenId().getUser_id());
		if(u.isGroupAdmin(attributeValue.getDefinition().getGroup().getId()))
			return true;
		// if it was not an admin of the group, then verify if it is the group owner 
		if ( user.getOpenId().getUser_id().equals(attributeValue.getDefinition().getGroup().getOwner().getId()) )
			return true;
		//if it was not any of the previous conditions, the last chance it is that the user owns the entity
		return attributeValue.getEntity().getOwner().getId().equals(u.getId());
	}

	@Override
	protected Logger getLogger() 
	{
		return LOG;
	}

	

	
}
