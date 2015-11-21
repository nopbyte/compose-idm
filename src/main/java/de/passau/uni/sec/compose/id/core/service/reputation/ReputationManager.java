package de.passau.uni.sec.compose.id.core.service.reputation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReputationManager 
{
	private static Logger LOG = LoggerFactory.getLogger(ReputationManager.class);
	
	public int getReputationValueforNewUser()
	{
		return 5;
	}
	//TODO fill with calls to the reputation manager or calculations.
	public int getReputationValueForNewServiceObject(String ownerUserId)
	{
		LOG.debug("returning initial reputation for a new service object for user with id"+ownerUserId);
		return 5;
		
	}
	public int getReputationValueForNewSourceCode(String ownerUserId)
	{
		LOG.debug("returning initial reputation for new source code for user with id"+ownerUserId);
		return 5;
	}
	public int getReputationValueForNewApplication(String ownerUserId)
	{
		LOG.debug("returning initial reputation for a new application for user with id"+ownerUserId);
		return 5;
	}
	public int getReputationValueForNewServiceInstance(String ownerUserId) {
		LOG.debug("returning initial reputation for a new service instance for user with id"+ownerUserId);
		return 5;
	}
}
