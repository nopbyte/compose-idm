package de.passau.uni.sec.compose.id.core.service.security;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Resource;
import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeComponentPrincipal;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.persistence.entities.CoreEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.UserService;
import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;
import de.passau.uni.sec.compose.id.rest.client.HTTPClient;

@Service
public class Authorization
{
	 private static Logger LOG = LoggerFactory.getLogger(Authorization.class);
	 
	 
	 public void authorizeIfAnyComponentWithAnyUser(Collection<IPrincipal> principals ) throws IdManagementException
	 {
			boolean userThere = false;
			boolean composeComponentThere =false;
			for(IPrincipal p: principals)
			{
				//check that user exists
				if(p instanceof ComposeUserPrincipal)
				{
					ComposeUserPrincipal user = (ComposeUserPrincipal) p;
					if(((ComposeUserPrincipal) p).getOpenId().getUser_id() != null)
						userThere = true;
					
				}
				if(p instanceof ComposeComponentPrincipal)
					 composeComponentThere = true;
				
			}
			if(!userThere || !composeComponentThere)
				throw new IdManagementException("Not sufficient permissions for the action requred ",null, LOG,"The entities authenticated for the request do not have sufficient permissions to execute it, principals "+RestAuthentication.getBasicInfoPrincipals(principals),Level.ERROR, 403);
			
			LOG.debug("Authorization granted for creation of service object to principals: "+RestAuthentication.getBasicInfoPrincipals(principals));
	 }
	 
	 public void authorizeIfOwner(Collection<IPrincipal> principals, CoreEntity entity) throws IdManagementException
	 {
		 if(principals == null || principals.size()==0)
				throw new IdManagementException("Authentication required.",null, LOG," Attempt to access without providing credentials",Level.DEBUG, 401);
			boolean containsowner = false;
			for(IPrincipal principal: principals)
			{
				if(principal instanceof ComposeUserPrincipal)
				{
					String userid = ((ComposeUserPrincipal)principal).getOpenId().getUser_id();
					if(entity.getOwner().getId().equals(userid))
						containsowner = true;
				}
			}
			if(!containsowner)
				throw new IdManagementException("Forbidden action. User executing the action should be owner of the entity under modification",null, LOG," Attempt to execute an action without being the owner",Level.ERROR, 403);
	 }
	 
	 public void authorizeIfOwnerOrComponent(Collection<IPrincipal> principals, CoreEntity entity) throws IdManagementException
	 {
		 if(principals == null || principals.size()==0)
				throw new IdManagementException("Authentication required.",null, LOG," Attempt to access without providing credentials",Level.DEBUG, 401);
			for(IPrincipal principal: principals)
			{
				if(principal instanceof ComposeComponentPrincipal)
				{
					return;
				}
				if(principal instanceof ComposeUserPrincipal)
				{
					String userid = ((ComposeUserPrincipal)principal).getOpenId().getUser_id();
					if(entity.getOwner().getId().equals(userid))
						return;
				}
			}
			throw new IdManagementException("Forbidden action. Principals calling the API must contain either the owner, or a component",null, LOG," Attempt to execute an action without providing credentials for the owner of the entity, nor the credentiasl for a component",Level.ERROR, 403);
	 }
	 
	
}
