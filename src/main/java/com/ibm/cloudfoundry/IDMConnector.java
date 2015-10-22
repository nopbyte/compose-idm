package com.ibm.cloudfoundry;

import java.util.Properties;

import org.slf4j.Logger;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.service.security.TokenResponse;

public class IDMConnector extends CConnector {


	static enum ConnType {
		HTTP, HTTPS, UNKNOWN;
	}

	static ConnType connType = ConnType.UNKNOWN;

	
	/*public static void main(String[] args) throws IdManagementException {
		IDMConnector cc;
		Logger LOG = LoggerFactory.getLogger(IDMConnector.class);	
		
		try {
			cc = new IDMConnector("https://minerva.bsc.es:8094/proxy",null,"juan","juasn");
			TokenResponse res = cc.getTokenForUserAndPassword(null);
			System.out.println(res.getAccessToken()+res.getToken_type());
		} catch (IdManagementException ex)
		{
			System.out.println(ex.getMessage());
		}catch (Exception e) {
			if(e.getMessage().contains("401"))
				System.out.println("Authentication failed, wrong credentials ");
		}
		
		
		
	}*/

	public  TokenResponse getTokenForUserAndPassword(Logger log) throws Exception,IdManagementException {
		try {
			TokenResponse res = new TokenResponse();
			Properties prop = getTokenData();
			res.setToken_type(prop.getProperty("token_type"));
			res.setAccessToken(prop.getProperty("access_token"));
			return res;
		} catch (Exception e) {
			if(e.getMessage().contains("401"))
				throw new IdManagementException("Authentication failed, wrong credentials ",null, log," Incorrect credentials while authenticating with UAA",Level.ERROR, 401);
			throw e;
		}
	}
	
	public IDMConnector(String ccURL, String idmURL,String username,
			String password)
			throws Exception {
		super(ccURL,idmURL,username,password,null,null);
	}


}
