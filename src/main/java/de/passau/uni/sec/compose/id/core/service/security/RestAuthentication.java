package de.passau.uni.sec.compose.id.core.service.security;

import java.util.Collection;
import java.util.LinkedList;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeComponentPrincipal;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;

@Service
public class RestAuthentication
{
	 private static final String UPPER_CASE_TOKEN_OAUTH2 = "BEARER";
	 
	 private static final String[] Roles = {"COMPONENT"};
 
	private static Logger LOG = LoggerFactory.getLogger(RestAuthentication.class);
	
	@Resource
	private Environment env;
	
	 @Autowired
	private UsersAuthzAndAuthClient uaa;
	 
	@Autowired
	UserRepository userRepository;
	
	/**
	 * This method only builds the Collection of Principals.It needs to be checked by the sub classes whether the principals have
	 * sufficient permissions for the requested action.
	 * @param credentials list of credentials provided.
	 * @throws IdManagementException when the credentials are not valid 
	 */
	public  Collection<IPrincipal> authenticatePrincipals(Logger logger,Collection<String> credentials) throws IdManagementException
	{
		Collection<IPrincipal> principals = new LinkedList<IPrincipal>();
		// This object comes from Spring authentication to the API.
		Authentication auth =  SecurityContextHolder.getContext().getAuthentication();
		
		IPrincipal principal = null;
		String credentialsUsedToAuthenticate="none";
		boolean componentThere=false,userThere=false;
		
		//if there are tokens for users
		for( String cred: credentials)
		{
			if(cred == null)
				throw new IdManagementException("Field with empty credentials",null, LOG," A null credential string was found while trying to authenticate: ",Level.ERROR, 401);
			if(cred.toUpperCase().startsWith(UPPER_CASE_TOKEN_OAUTH2))
			{
				
				cred = cred.substring(UPPER_CASE_TOKEN_OAUTH2.length());
				cred = cred.trim();
				credentialsUsedToAuthenticate=cred;
				OpenIdUserData openId = uaa.getOpenIdData(cred);
				principal = new ComposeUserPrincipal();
				principal.setTokenCredentials(cred);
				((ComposeUserPrincipal)principal).setOpenId(openId);
				userThere=true;
				principals.add(principal);
				LOG.debug("User with user_id: "+openId.getUser_id()+" has been authenticated");
			}
			
		}
		//If there is a component calling the API
		if(auth!=null && auth.getPrincipal()!=null)
		{
			Object o = auth.getPrincipal();
			if(!userThere && o instanceof String)
				LOG.error("The principal seems to be an anonymous user (unauthenticated Compose Component)?: \""+o+"\" disregarding credentials");
			else if(o instanceof org.springframework.security.core.userdetails.User)
			{
				credentialsUsedToAuthenticate="HTTP Digest credentials";
				UserDetails component = (UserDetails)o;
				//authentication successful with Spring methods (used for components)
				ComposeComponentPrincipal p = new ComposeComponentPrincipal();
				p.setComposeComponentName(component.getUsername()+": authenticated with http digest auth");
				principal = p;
				componentThere=true;
				principals.add(principal);
				LOG.debug("Compose entity authentication successfull: entity: "+component.getUsername());
				
				
			}
			else if(!userThere)
				LOG.error("Unexpected Principal from spring security framework: class coming from autheorization.getUserDetails(): "+o.getClass().toString());
			
			
		}
		if(!componentThere&&!userThere)
			throw new IdManagementException("Authentication failed, wrong credentials ",null, LOG," Incorrect credentials: \""+credentialsUsedToAuthenticate+"\"",Level.ERROR, 401);
		
		return principals;
	}
	/**
	 * 
	 * @return basic information for the authenticated principals
	 */
	public static String getBasicInfoPrincipals(Collection<IPrincipal> principals)
	{
		String ret = "";
		for(IPrincipal p: principals)
		{
			    ret += p.getStringBasicInfo()!=null?p.getStringBasicInfo():"";
		}
		return ret;
	}
	
	/**
	 * This method is used to get the actual user from the database, which corresponds to the first user in the list of Principals for the event.
	 * @param event
	 * @return
	 * @throws IdManagementException if there is not user found in the list of principals of the event, or if the user is not found in the local database
	 */
	public User getUserFromEvent(Event event) throws IdManagementException
	{
		Collection<IPrincipal> principals = event.getPrincipals();
		ComposeUserPrincipal user = null;
		for(IPrincipal p: principals)
			if(p instanceof ComposeUserPrincipal)
				user = (ComposeUserPrincipal) p;
		if(user != null)
		{
			User u = userRepository.getOne(user.getOpenId().getUser_id());
			if(u != null)
				return u;
			else
				LOG.error("User with id "+user.getOpenId().getUser_id()+" not found in the local database");
		}
		throw new IdManagementException("No user authenticated successfully for the request",null,LOG,"There is no user in principals priovided for the request, or it is not in the local database: "+ event.getLoggingDetails(),Level.ERROR,401);
	}
	
	/*private String createBasicHTTPAuthenticationToken(String username, String password)
	{
		String plainCreds = username + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        String base64Creds = DatatypeConverter.printBase64Binary(plainCredsBytes);
        return base64Creds;
	}*/
}
