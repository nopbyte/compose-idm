package de.passau.uni.sec.compose.id.core.event;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;

public abstract class AbstractUpdateEvent extends AbstractEvent implements DetailsIdEvent 
{

	private static Logger LOG = LoggerFactory.getLogger(AbstractUpdateEvent.class);
	
	protected Date lastKnownModification;
	
	protected String entityId;
	
	@Override
	public abstract String getLoggingDetails();


	@Override
	public void setLastModifiedKnown(long ts) 
	{
		lastKnownModification = new Date(ts);
	}

	@Override
	public Date getLastModifiedKnown() {
		return lastKnownModification;
	}


	public String getEntityId() {
		return entityId;
	}


	public void setEntityId(String entityId) {
		this.entityId = entityId;
	}


	public void setLastModifiedKnown(String lastMod) throws IdManagementException 
	{
		try{
				lastKnownModification = new Date(Long.parseLong(lastMod));
		}catch(Exception e)
		{
			throw new IdManagementException("Last Modification format is incorrect. Verify that this value corresponds to the LastModified field from the entity being updated",e,LOG,"Incorrect last modification value, not parseable to Long, or to Date",Level.DEBUG,404);
		}
		
	}

	
}
