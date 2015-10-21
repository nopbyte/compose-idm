package de.passau.uni.sec.compose.id.core.service;

import javax.annotation.PostConstruct;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.servioticy.ServioticyManager;

@Service
public class UpdateManager
{
	private static Logger LOG = LoggerFactory.getLogger(UpdateManager.class);

	@Autowired
	private CloudPublisher pub;
	
	@Autowired
	ServioticyManager servioticy;
	
	public int handleUpdateForEntity(String id){
		int status = 0;
		try{
			servioticy.attemptToUpdateSO(id);
			status +=1;
			
		}catch(IdManagementException ex)
		{
			LOG.error("something went wrong while updating the SO in servioticy!");
		}
		try{
			
			pub.updateEntity(id);
			status +=2;
			
		}catch(IdManagementException ex)
		{
			LOG.error("something went wrong while attempting to pubhish in Pub Sub!");
		}
		return status;
	}
	
	/**
	 * Takes care to notify entities about the change through pub sub, and in case it is a servioticy instance it is updated through the private API.
	 * @param id
	 * @param collection
	 * @throws IdManagementException
	 */
	public int handleUpdateForEntity(String id, Collection<IPrincipal> collection) throws IdManagementException
	{
		return handleUpdateForEntity(id);
	}


	
	
}
