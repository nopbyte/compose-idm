package com.ibm.cloudfoundry;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

public class SSLProbe {

	/*public static void main(String[] args) {
		try {
			trustEveryone();
			HttpsURLConnection conn = (HttpsURLConnection) new URL(
					"https://api.172.20.0.1.xip.io").openConnection(new Proxy(
					Type.HTTP, new InetSocketAddress("minerva.bsc.es", 8093)));
			BufferedReader r = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = r.readLine()) != null) {
				System.out.flush();
				System.out.println(line);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}*/

	private static void trustEveryone() {
		try {
			HttpsURLConnection
					.setDefaultHostnameVerifier(new HostnameVerifier() {
						public boolean verify(String hostname,
								javax.net.ssl.SSLSession session) {
							return true;
						}
					});
			SSLContext context = SSLContext.getInstance("SSL");
			context.init(null, new X509TrustManager[] { new X509TrustManager() {
				public void checkClientTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public void checkServerTrusted(X509Certificate[] chain,
						String authType) throws CertificateException {
				}

				public X509Certificate[] getAcceptedIssuers() {
					return new X509Certificate[0];
				}
			} }, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(context
					.getSocketFactory());
		} catch (Exception e) { // should never happen
			e.printStackTrace();
		}
	}
	/*
	 * static class MySSLSocketFactory extends SSLSocketFactory { SSLContext
	 * sslContext = SSLContext.getInstance("TLS");
	 * 
	 * public MySSLSocketFactory(KeyStore truststore) throws
	 * NoSuchAlgorithmException, KeyManagementException, KeyStoreException,
	 * UnrecoverableKeyException { super(truststore);
	 * 
	 * TrustManager tm = new X509TrustManager() { public void
	 * checkClientTrusted(X509Certificate[] chain, String authType) throws
	 * CertificateException { }
	 * 
	 * public void checkServerTrusted(X509Certificate[] chain, String authType)
	 * throws CertificateException { }
	 * 
	 * public X509Certificate[] getAcceptedIssuers() { return null; } };
	 * 
	 * sslContext.init(null, new TrustManager[] { tm }, null); }
	 * 
	 * @Override public Socket createSocket(Socket socket, String host, int
	 * port, boolean autoClose) throws IOException, UnknownHostException {
	 * return sslContext.getSocketFactory().createSocket(socket, host, port,
	 * autoClose); }
	 * 
	 * @Override public Socket createSocket() throws IOException { return
	 * sslContext.getSocketFactory().createSocket(); }
	 * 
	 * @Override public String[] getDefaultCipherSuites() { // TODO
	 * Auto-generated method stub return null; }
	 * 
	 * @Override public String[] getSupportedCipherSuites() { // TODO
	 * Auto-generated method stub return null; }
	 * 
	 * @Override public Socket createSocket(String host, int port) throws
	 * IOException, UnknownHostException { // TODO Auto-generated method stub
	 * return null; }
	 * 
	 * @Override public Socket createSocket(InetAddress host, int port) throws
	 * IOException { // TODO Auto-generated method stub return null; }
	 * 
	 * @Override public Socket createSocket(String host, int port, InetAddress
	 * localHost, int localPort) throws IOException, UnknownHostException {
	 * return new Socket(host, port,localHost,localPort); }
	 * 
	 * @Override public Socket createSocket(InetAddress address, int port,
	 * InetAddress localAddress, int localPort) throws IOException { return new
	 * Socket(address, port, localAddress, localPort); } }
	 */

}
