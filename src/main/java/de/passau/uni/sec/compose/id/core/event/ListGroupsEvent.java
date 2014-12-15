package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public class ListGroupsEvent extends AbstractEvent implements Event
{

	private int page;
	
	public ListGroupsEvent(Collection<IPrincipal> princi, int page) {
		this.page = page;
		super.principals = princi;
	}
	@Override
	public String getLoggingDetails() {
		return "Listing all all groups.. page "+page+" principals:"+RestAuthentication.getBasicInfoPrincipals(super.principals);
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	
	
}
