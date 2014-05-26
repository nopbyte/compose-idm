package de.passau.uni.sec.compose.id.core.event;

import java.util.Date;

public interface DetailsIdEvent extends Event
{
	public void setLastModifiedKnown(long timeStampRequest);
	
	public Date getLastModifiedKnown();

	public String getEntityId();	


}
