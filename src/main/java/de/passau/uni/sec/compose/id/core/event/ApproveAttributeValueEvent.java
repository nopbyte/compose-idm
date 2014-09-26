package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public class ApproveAttributeValueEvent extends AbstractUpdateEvent implements DetailsIdEvent
{
	
	private static Logger LOG = LoggerFactory.getLogger(ApproveAttributeValueEvent.class);
	
	public ApproveAttributeValueEvent( String id, Collection<IPrincipal> principals, String lastMod) throws IdManagementException
	{
		super.entityId = id;
		this.principals = principals;
		super.setLastModifiedKnown(lastMod);
		
	}
	@Override
	public String getLoggingDetails() {
	
		return "Approving attribute value with id :"+this.entityId+ " principals: "+RestAuthentication.getBasicInfoPrincipals(principals);

	}
	
	
}
