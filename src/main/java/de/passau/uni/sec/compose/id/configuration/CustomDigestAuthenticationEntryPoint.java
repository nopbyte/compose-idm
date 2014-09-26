package de.passau.uni.sec.compose.id.configuration;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.NonceExpiredException;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;


public class CustomDigestAuthenticationEntryPoint extends
DigestAuthenticationEntryPoint {

	@Override
	public void commence(
			HttpServletRequest request,
			HttpServletResponse response,
			org.springframework.security.core.AuthenticationException authException)
			throws IOException, ServletException {
		
		//super.commence(request, response, authException);
		

	    HttpServletResponse httpResponse = (HttpServletResponse) response;

	    // compute a nonce (do not use remote IP address due to proxy farms)
	    // format of nonce is:
	    //   base64(expirationTime + ":" + md5Hex(expirationTime + ":" + key))
	    long expiryTime = System.currentTimeMillis() + (getNonceValiditySeconds() * 1000);
	    String signatureValue = DigestUtils.md5Hex(expiryTime + ":" + getKey());
	    String nonceValue = expiryTime + ":" + signatureValue;
	    String nonceValueBase64 = new String(Base64.encode(nonceValue.getBytes()));

	    // qop is quality of protection, as defined by RFC 2617.
	    // we do not use opaque due to IE violation of RFC 2617 in not
	    // representing opaque on subsequent requests in same session.
	    String authenticateHeader = "Digest realm=\"" + getRealmName() + "\", " + "qop=\"auth\", nonce=\""
	        + nonceValueBase64 + "\"";

	    if (authException instanceof NonceExpiredException) {
	        authenticateHeader = authenticateHeader + ", stale=\"true\"";
	    }

	    httpResponse.addHeader("WWW-Authenticate", authenticateHeader);
	    
	    //old HTML response
	    //httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
	  
	    //custom response for digest authentication
	    httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    httpResponse.setContentType("application/json");
	    httpResponse.setCharacterEncoding("UTF-8");
	    httpResponse.getWriter().write("{\"error\":\"Authentication failed, wrong credentials for HTTP-Digest authentication\"}");
	 
	}




}