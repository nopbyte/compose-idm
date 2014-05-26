package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public class AbstractEvent {

	protected Collection<IPrincipal> principals;

	public Collection<IPrincipal> getPrincipals() {
		return principals;
	}

	public void setPrincipals(Collection<IPrincipal> principals) {
		this.principals = principals;
	}
	
	
	
	
}
