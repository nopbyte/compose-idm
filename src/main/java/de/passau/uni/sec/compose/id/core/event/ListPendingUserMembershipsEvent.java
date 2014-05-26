package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public class ListPendingUserMembershipsEvent extends AbstractEvent implements Event
{

	public ListPendingUserMembershipsEvent(Collection<IPrincipal> princi) {
		super.principals = princi;
	}
	@Override
	public String getLoggingDetails() {
		return "Listing all pending User Memberships that could be approved by principals:"+RestAuthentication.getBasicInfoPrincipals(super.principals);
	}

}
