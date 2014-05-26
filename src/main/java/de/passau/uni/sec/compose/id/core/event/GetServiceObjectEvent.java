package de.passau.uni.sec.compose.id.core.event;



import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;


public class GetServiceObjectEvent extends AbstractGetEvent implements Event
{
	
	private static Logger LOG = LoggerFactory.getLogger(GetServiceObjectEvent.class);
	
	public GetServiceObjectEvent(String id, Collection<IPrincipal> principals)
	{
		super.id = id;
		super.principals = principals;
	}
	
	@Override
	public String getLoggingDetails() {
	
		return "Getting single Service Object with id : "+this.id+ " principals: "+RestAuthentication.getBasicInfoPrincipals(principals);

	}
	
	
}
