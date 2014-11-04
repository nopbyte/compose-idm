package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public class ListServiceInstancesOwnedByUserEvent extends AbstractGetEvent implements Event
{

	public ListServiceInstancesOwnedByUserEvent(Collection<IPrincipal> princi, String id) {
		super.principals = princi;
		super.id = id;
	}
	@Override
	public String getLoggingDetails() {
		return "Listing all service instances owned by user id : "+super.id+" by principals: "+RestAuthentication.getBasicInfoPrincipals(super.principals);
	}

}
