package de.passau.uni.sec.compose.id.core.event;

import java.util.Collection;

import de.passau.uni.sec.compose.id.core.domain.IPrincipal;

public interface Event 
{
	public Collection<IPrincipal> getPrincipals() ;

	public void setPrincipals(Collection<IPrincipal> principals) ;
	/**
	 * 
	 * @return logging details, including who is takin the action and the relevant parameteres for logging.
	 */
	public String getLoggingDetails();
}
