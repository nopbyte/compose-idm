package de.passau.uni.sec.compose.id.core.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.util.LRUMap;
import com.github.cage.GCage;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.core.persistence.entities.Code;


@Service
public class CaptchaService extends GCage 
{
	
	@Autowired
	CodeService codeService;

	private static Logger LOG = LoggerFactory.getLogger(CaptchaService.class);
	
	public boolean addSession(String id) throws IdManagementException
	{
		return codeService.addCode(id, "", CodeService.TYPE_CAPTCHA);
	}
	
	public void removeSession(String id)
	{
		Code c = codeService.getCode(id, CodeService.TYPE_CAPTCHA);
		if(c !=null)
			codeService.deleteCode(c);
	}
	public boolean setText(String id, String text) throws IdManagementException
	{
		Code c = codeService.getCode(id, CodeService.TYPE_CAPTCHA);
		if(c != null)
		{
			return codeService.updateCodeReference(id, text, CodeService.TYPE_CAPTCHA );
		}
		LOG.info("code with id :"+id+" not found in the code database");
		return false;
	}
	
	public boolean verifyText(String id, String text)
	{
		Code c = codeService.getCode(id, CodeService.TYPE_CAPTCHA); 
		if(c!=null)
		{
			if(c.getReference() == null || c.getReference().equals(""))
			{
				LOG.info("found an empty text in the captcha");
				return false;
			}
			if(text != null && text.equals(c.getReference()))
				return true;
		}
		LOG.info("Code with id:"+id+" was not found attempting to check the text validation");
		return false;
	}

	public String getNewSession() throws IdManagementException 
	{
		int value;
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			value = random.nextInt();
			
		} catch (NoSuchAlgorithmException e) {

			sun.security.provider.SecureRandom r = new sun.security.provider.SecureRandom();
			byte [] b = new byte[4];
			r.engineNextBytes(b);
			value = ((0xFF & b[0]) << 24) | ((0xFF & b[1]) << 16) |
		            ((0xFF & b[2]) << 8) | (0xFF & b[3]);
			LOG.warn("Using a newly created SecureRandom object to generate tokens for captcha sessions: SHA1PRNG instance of SecureRandom was not found!");
			
		}
		value = Math.abs(value);
		String token = Integer.toString(value);
		addSession(token);
		return token;

	}
	
}
