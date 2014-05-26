package de.passau.uni.sec.compose.id.core.service.security;

import java.util.Collection;

import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.domain.EntityAttribute;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;

//TODO write interface for this class to make it available in @Autowired...
@Service
public class AdditionalAttributesPDP 
{

	/**
	 * 
	 
	 * @param attribute attribute to be modified by the list of principals
	 * @param newValue new value that is attempted to be assigned to the attribute (in the field value).
	 * @param principals Collection of principals trying to execute the request
	 * @throws IdManagementException with 403 error code in case the principals collection doesn't contain the proper collection of principals for the attempted
	 * modification. 
	 */
	public void isModificationOfAttributeAllowed(EntityAttribute attribute, Object newValue, Collection<IPrincipal> principals) throws IdManagementException
	{
		//TODO verify, if principals are allowed don't throw exceptions. Do not change the attribute value
		//For now consider only principals which are instance of ComposeUserPrincipal (core.domain). Check class with instance of
		
	}
	
	public void updateApprovalOfAttribute(EntityAttribute attribute, boolean approvedValue,Collection<IPrincipal> principals) throws IdManagementException
	{
		
	}
	
}
