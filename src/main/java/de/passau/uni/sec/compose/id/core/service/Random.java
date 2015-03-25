
package de.passau.uni.sec.compose.id.core.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Service
public class Random {

	private static Logger LOG = LoggerFactory.getLogger(ServiceObjectService.class);

	public String getHexRandomToken()
	{
		char[] allowed = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
	    StringBuilder result = new StringBuilder();
	    try {
		    	for (int i=0;i<38; i++)
		    	{
		    		result.append(allowed[Math.abs(SecureRandom.getInstance("SHA1PRNG").nextInt())%allowed.length]);
		    	}
		    	return result.toString();
	    
	    } catch (NoSuchAlgorithmException e) {
	    	
	    	byte[] array = new byte[33];
			sun.security.provider.SecureRandom r = new sun.security.provider.SecureRandom();
			r.engineNextBytes(array);
			LOG.warn("Using a newly created SecureRandom object to generate tokens for SO: SHA1PRNG instance of SecureRandom was not found!");
			return Hex.encodeHexString(array);
			
		}
	    
	}
	
	public String getInitialToken() 
	{
		byte[] array = new byte[33];
		SecureRandom random;
		try {
			random = SecureRandom.getInstance("SHA1PRNG");
			random.nextBytes(array);
			
		} catch (NoSuchAlgorithmException e) {

			sun.security.provider.SecureRandom r = new sun.security.provider.SecureRandom();
			r.engineNextBytes(array);
			LOG.warn("Using a newly created SecureRandom object to generate tokens for SO: SHA1PRNG instance of SecureRandom was not found!");
			
		}
		String token = DatatypeConverter.printBase64Binary(array);
		return token;
	}

	public String getRandomToken() 
	{
		return getHexRandomToken();
	}

}
