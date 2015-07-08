package de.passau.uni.sec.compose.id.core.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.util.LRUMap;
import com.github.cage.GCage;


@Service
public class CaptchaService extends GCage 
{

	private static Logger LOG = LoggerFactory.getLogger(CaptchaService.class);
	LRUMap<String, String> sessions = new LRUMap<>(0, 100);
	
	public boolean addSession(String id)
	{
		if(sessions.containsKey(id))
			return false;
		sessions.put(id, "");
		return true;
	}
	
	public void removeSession(String id)
	{
		sessions.remove(id);
	}
	public boolean setText(String id, String text)
	{
		if(sessions.containsKey(id))
		{
			sessions.put(id, text);
			return true;
		}
		return false;
	}
	
	public boolean verifyText(String id, String text)
	{
		if(sessions.containsKey(id) && !sessions.get(id).equals(""))
		{
			if(text != null && text.equals(sessions.get(id)))
				return true;
		}
		return false;
	}

	public String getNewSession() 
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
