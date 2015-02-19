package de.passau.uni.sec.compose.id.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.persistence.entities.Global;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceCompositionRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceInstanceRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceObjectRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceSourceCodeRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UniqueRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;

@Service
public class UniqueValidation {

	public static final String USER = "user";

	public static final String SERVICE_OBJECT = "so";
	
	public static final String SERVICE_SOURCE = "ssource";
	
	public static final String SERVICE_INSTANCE = "sinstance";
	
	public static final String SERVICE_COMPOSITION= "scomposition";
	
	public static final String APPLICATION = "application";
	
	private Logger LOG = LoggerFactory.getLogger(UniqueValidation.class);
	
	@Autowired
	private ApplicationRepository applicationRepository;
	@Autowired
	private ServiceInstanceRepository siRepository;
	@Autowired
	private ServiceSourceCodeRepository ssRepository;
	@Autowired
	private ServiceObjectRepository soRepository;
	@Autowired
	private ServiceCompositionRepository scRepository;
	@Autowired
	private UserRepository uRepository;
	
	@Autowired
	private UniqueRepository uniqueRepository;
	
	public void insertUnique(String id, String type) throws IdManagementException
	{
		Global g = new Global();
		g.setId(id);
		g.setType(type);
		uniqueRepository.save(g);					
	}

	public void verifyUnique(String id) throws IdManagementException {
		
		if(applicationRepository.findOne(id)!=null ||
				siRepository.findOne(id)!=null ||
				ssRepository.findOne(id)!=null ||
				soRepository.findOne(id)!=null ||
				scRepository.findOne(id)!=null ||
				uRepository.findOne(id)!=null )
			{
				 
			}
			else{
				throw new IdManagementException("Entity with the same id already exists",null,LOG,"Entity with id "+id+" already exists",Level.DEBUG,409);
			}		
	}
}
