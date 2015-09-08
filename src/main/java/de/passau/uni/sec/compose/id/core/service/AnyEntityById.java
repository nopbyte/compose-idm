package de.passau.uni.sec.compose.id.core.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeComponentPrincipal;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.event.CreateApplicationEvent;
import de.passau.uni.sec.compose.id.core.event.Event;
import de.passau.uni.sec.compose.id.core.event.GetApplicationEvent;
import de.passau.uni.sec.compose.id.core.event.DetailsIdEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceCompositionEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceInstanceEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceObjectEvent;
import de.passau.uni.sec.compose.id.core.event.GetServiceSourceCodeEvent;
import de.passau.uni.sec.compose.id.core.event.GetUserEvent;
import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.Global;
import de.passau.uni.sec.compose.id.core.persistence.entities.IEntity;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UniqueRepository;
import de.passau.uni.sec.compose.id.core.service.reputation.ReputationManager;
import de.passau.uni.sec.compose.id.core.service.security.Authorization;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationCreateMessage;
import de.passau.uni.sec.compose.id.rest.messages.ApplicationResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.EntityResponseMessage;


@Service
public class AnyEntityById  
{
	
	@Autowired
    private ApplicationService applicationService;
	@Autowired
	private ServiceInstanceService siService;
	@Autowired
	private ServiceSourceCodeService ssService;
	@Autowired
	private ServiceObjectService soService;
	@Autowired
	private ServiceCompositionService scService;
	@Autowired
	private UserService uService;
	

	private static Logger LOG = LoggerFactory.getLogger(AnyEntityById.class);
	public  Map<String, Object> getAnyEntity(String uid,
			Collection<IPrincipal> principals) throws IdManagementException
	{
		EntityResponseMessage r =null;
		 Map<String,Object> res = new HashMap<>();
		 try{
			 r = applicationService.getEntity(new GetApplicationEvent(uid,principals));
			 res.put("application",r);
			 r=null;
		 }catch(IdManagementException ex)
		 {
			 if(ex.getHTTPErrorCode()!=404)
				 throw ex;
		 }
		 
		 try{
			 r = siService.getEntity(new GetServiceInstanceEvent(uid,principals));
			 res.put("service_instance",r);
			 r=null;
		 }catch(IdManagementException ex)
		 {
			 if(ex.getHTTPErrorCode()!=404)
				 throw ex;
		 }
		 
		 try{
			 r = ssService.getEntity(new GetServiceSourceCodeEvent(uid,principals));
			 res.put("service_source_code",r);
			 r=null;
		 }catch(IdManagementException ex)
		 {
			 if(ex.getHTTPErrorCode()!=404)
				 throw ex;
		 }
		 
		 try{
			 r = soService.getEntity(new GetServiceObjectEvent(uid,principals));
			 res.put("service_object",r);
			 r=null;
		 }catch(IdManagementException ex)
		 {
			 if(ex.getHTTPErrorCode()!=404)
				 throw ex;
		 }
		 
		 
		 try{
			 r = scService.getEntity(new GetServiceCompositionEvent(uid,principals));
			 res.put("service_composition",r);
			 r=null;
		 }catch(IdManagementException ex)
		 {
			 if(ex.getHTTPErrorCode()!=404)
				 throw ex;
		 }
		 
		 try{
			 r = uService.getEntity(new GetUserEvent(uid,principals));
			 res.put("user",r);
			 r=null;
		 }catch(IdManagementException ex)
		 {
			 if(ex.getHTTPErrorCode()!=404)
				 throw ex;
		 }
		return res;
	}
}
