package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;

public abstract class AbstractSecureEntityBasicEntityService extends AbstractBasicEntityService{

	private static Logger LOG = LoggerFactory.getLogger(AbstractSecureEntityBasicEntityService.class);
	
	
	
	@Override
	protected  void verifyAccessControlCreateEntity(Event event)
			throws IdManagementException {

		boolean ok = false;
		Collection<IPrincipal> principals = event.getPrincipals();
		for(IPrincipal p: principals)
		{
			//check that user exists
			if(p instanceof ComposeUserPrincipal)
			{
				ComposeUserPrincipal user = (ComposeUserPrincipal) p;
				if(((ComposeUserPrincipal) p).getOpenId().getUser_id() != null)
					ok = true;
					LOG.debug("Compose User with userId: "+((ComposeUserPrincipal)p).getOpenId().getUser_id()+" executing User creation: ");
			}
		}
		if(!ok)
			throw new IdManagementException("Not sufficient permissions for the action requred ",null, LOG,"The entities authenticated for the request do not have sufficient permissions to execute it, principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);
		
	}


	@Override
	protected   void verifyAccessControlUpdateEntity(DetailsIdEvent event)
			throws IdManagementException {
		
		//TODO implement properly
		verifyAccessControlCreateEntity(event);
		
		
	}


	/**
	 * This method should be re implemented if the GET requires some additional verifications.
	 * @param event
	 * @throws IdManagementException if there isn't any principal authenticated. 
	 */
	@Override
	protected void verifyAccessControlGetEntity(Event event) throws IdManagementException
	{
		Collection<IPrincipal> p = event.getPrincipals();
		if(p == null || p.size()==0)
			throw new IdManagementException("Authentication required.",null, LOG," Attempt to access GET without providing credentials",Level.DEBUG, 401);
	}

}
