package de.passau.uni.sec.compose.id.rest.functional.util;

import java.net.URI;

import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

@SuppressWarnings("deprecation")
public class Fixtures {

    public static RestTemplate digestRestTemplate() {
        DefaultHttpClient client = new DefaultHttpClient();

        UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(
                "composecontroller", "composecontrollerpassword");

        client.getCredentialsProvider().setCredentials(
                new AuthScope("localhost", 8080, AuthScope.ANY_REALM),
                credentials);

        RestTemplate digestRestTemplate = new RestTemplate();
        digestRestTemplate
                .setRequestFactory(new DigestHttpComponentsClientHttpRequestFactory(
                        client) {
                    @Override
                    protected HttpUriRequest createHttpUriRequest(
                            HttpMethod httpMethod, URI uri) {
                        if (HttpMethod.DELETE == httpMethod) {
                            return new HttpEntityEnclosingDeleteRequest(uri);
                        }
                        return super.createHttpUriRequest(httpMethod, uri);
                    }
                });
        return digestRestTemplate;
    }
}
