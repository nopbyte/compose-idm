
package de.passau.uni.sec.compose.id.core.service;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.xml.bind.DatatypeConverter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
@Service
public class Random {

	private static Logger LOG = LoggerFactory.getLogger(ServiceObjectService.class);

	public String getRandomToken() 
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

}
