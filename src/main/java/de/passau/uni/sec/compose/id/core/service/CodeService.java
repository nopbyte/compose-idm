
package de.passau.uni.sec.compose.id.core.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.persistence.entities.Code;
import de.passau.uni.sec.compose.id.core.persistence.repository.CodeRepository;

@Service
public class CodeService
{
	private static Logger LOG = LoggerFactory.getLogger(CodeService.class);
	
	// five minutes for now?
	private static long timeoutMilli = 5*60*1000;
	
	public static String TYPE_CAPTCHA = "captcha";
	
	public static String TYPE_OAUTH2_CODE = "oauth2_code";

	@Autowired
	CodeRepository codeRepo;
	
	@PostConstruct
	public void postConstruct()
	{
		cleanUp();
	}

	/**
	 * This method deletes everything in the database since now-timeoutMilli in miliseconds
	 */
	public void cleanUp()
	{
		Date since = new Date((new Date().getTime()-timeoutMilli));
		List<Code> list = codeRepo.findByLastModifiedBefore(since);
		for(Code c: list)
			codeRepo.delete(c);
		
	}
	/**
	 * 
	 * @param code
	 * @param reference
	 * @param type
	 * @return true or false if it was possible to insert the code
	 * @throws IdManagementException
	 */
	public boolean addCode(String code, String reference, String type) throws IdManagementException
	{
		boolean ret = false;
		List<Code> list = codeRepo.findByCodeAndType(code, type);
		if(list == null || list.isEmpty())
		{
			Code c = new Code();
			c.setId(UUID.randomUUID().toString());
			c.setCode(code);
			c.setReference(reference);
			c.setType(type);
			codeRepo.save(c);
			ret = true;
			LOG.debug("code added with code "+code+" and reference:"+reference);
		}
		
		cleanUp();
		return ret;
	}
	 public void deleteCode(Code c)
	 {
		 codeRepo.delete(c);
	 }
	/**
	 * 
	 * @param code
	 * @param type
	 * @return the code or null if not found.
	 */
	public Code getCode(String code, String type)
	{
		List<Code> list = codeRepo.findByCodeAndType(code, type);
		Code ret = null;
		if(list !=null && !list.isEmpty())
			ret = list.get(0);
		
		cleanUp();
		return ret;
	}
}