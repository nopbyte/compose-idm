package de.passau.uni.sec.compose.id.rest.functional.util;

import java.net.URI;

import org.apache.http.HttpHost;
import org.apache.http.client.AuthCache;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.DigestScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@SuppressWarnings("deprecation")
public class DigestHttpComponentsClientHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {
    
    public DigestHttpComponentsClientHttpRequestFactory(DefaultHttpClient client){
        super(client);
    }
    
    @Override
    protected HttpContext createHttpContext(HttpMethod httpMethod, URI uri) {
        return createHttpContext(uri);
    }
    
    private HttpContext createHttpContext(URI uri){
        AuthCache authCache = new BasicAuthCache();
        DigestScheme digestScheme = new DigestScheme();
        HttpHost targetHost = new HttpHost(uri.getHost(), uri.getPort());
        authCache.put(targetHost, digestScheme);
        
        BasicHttpContext localcontext = new BasicHttpContext();
        localcontext.setAttribute(HttpClientContext.AUTH_CACHE, authCache);
        return localcontext;
    }
}