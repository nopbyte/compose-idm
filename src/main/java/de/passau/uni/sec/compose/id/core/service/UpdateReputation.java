package de.passau.uni.sec.compose.id.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceInstanceRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceObjectRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceSourceCodeRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.servioticy.ServioticyManager;



@Service
public class UpdateReputation  
{
	
	@Autowired
    private ApplicationRepository application;
	@Autowired
	private ServiceInstanceRepository serviceInstance;
	@Autowired
	private ServiceSourceCodeRepository serviceSource;
	@Autowired
	private ServiceObjectRepository so;
	@Autowired
	private UserRepository user;
	@Autowired
	private UpdateManager manager;

	private static Logger LOG = LoggerFactory.getLogger(UpdateReputation.class);
	
	public void updateReputation(String entitytype, String entityId, int reputation) throws IdManagementException{
		
		boolean success = false;
		
		if(entitytype.equals("service_object"))
			success |=updateServiceObject(entityId, reputation);
		else if(entitytype.equals("user"))
			success |= updateUser(entityId, reputation);	
		else if(entitytype.equals("application"))
			success |=updateApplication(entityId,reputation);
		else if(entitytype.equals("service")){
			success |=updateServiceInstance(entityId,reputation);
			success |=updateServiceSource(entityId, reputation);
		}
		else
			LOG.error("unknwon entity with id: "+entityId+" and type "+entitytype+" for reputation update!");
		
		//if(success)
		//	manager.handleUpdateForEntity(entityId);
			
	}

	private boolean updateServiceSource(String entityId, int reputation)
	{
		ServiceSourceCode entity = serviceSource.findOne(entityId);
		if(entity == null)
			return false;
		entity.setReputation(reputation);
		serviceSource.save(entity);
		LOG.info(" update reputation for service source with entity id: "+entityId);
		return true;
	}

	private boolean updateServiceInstance(String entityId, int reputation)
	{
		ServiceInstance entity = serviceInstance.findOne(entityId);
		if(entity == null)
			return false;
		entity.setReputation(reputation);
		serviceInstance.save(entity);
		LOG.info(" update reputation for service instance with entity id: "+entityId);
		return true;
	}


	private boolean updateServiceObject(String entityId, int reputation) throws IdManagementException
	{
		ServiceObject entity = so.findOne(entityId);
		if(entity == null)
			return false;
		entity.setReputation(reputation);
		so.save(entity);
		LOG.info(" update reputation for service object with entity id: "+entityId);
		return true;
		
	}
	

	private boolean updateUser(String entityId, int reputation)
	{
		User entity = user.findOne(entityId);
		if(entity == null)
			return false;
		entity.setReputation(reputation);
		user.save(entity);
		LOG.info(" update reputation for user with entity id: "+entityId);
		return true;
	}
	

	private boolean updateApplication(String entityId, int reputation)
	{
		Application entity = application.findOne(entityId);
		if(entity == null)
			return false;
		entity.setReputation(reputation);
		application.save(entity);
		LOG.info(" update reputation for application with entity id: "+entityId);
		return true;
	}
}
 