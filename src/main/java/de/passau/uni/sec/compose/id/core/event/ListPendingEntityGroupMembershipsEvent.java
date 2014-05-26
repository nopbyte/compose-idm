package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public class ListPendingEntityGroupMembershipsEvent extends AbstractEvent implements Event
{

	public ListPendingEntityGroupMembershipsEvent(Collection<IPrincipal> princi) {
		super.principals = princi;
	}
	@Override
	public String getLoggingDetails() {
		return "Listing all pending Entity Group Memberships that could be approved by principals:"+RestAuthentication.getBasicInfoPrincipals(super.principals);
	}

}
