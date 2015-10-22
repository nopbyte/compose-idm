package com.ibm.cloudfoundry;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Formatter;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;

import javax.net.ssl.HandshakeCompletedListener;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class HTTPUtil {


	/*static final boolean trace = (System.getenv("cf_trace") != null
			&& System.getenv("cf_trace").equals("true") ? true : false);*/
	
	static final boolean trace = false;
			

	private static final String KEYSTORE_FILE = System.getProperty("user.home")
			+ File.separator + "cconnector-keystore.jks";
	private static final char[] passPhrase = { 'C', 'l', 'o', 'u', 'd', 'C',
			'o', 'n', 'n', 'e', 'c', 't', 'o', 'r' };

	private static SSLSocketFactory factory;

	static SSLSocketFactory getSocketFactory(String ccURL, String proxyURL)
			throws IOException, GeneralSecurityException {

		if (factory != null) {
			return factory;
		}

		KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());

		boolean addToKeyStore = false;

		File keyStore = new File(KEYSTORE_FILE);
		if (!keyStore.exists()) {
			ks.load(null, passPhrase);
			addToKeyStore = true;
		} else {
			InputStream in = new FileInputStream(KEYSTORE_FILE);
			ks.load(in, passPhrase);
			in.close();
			System.setProperty("javax.net.ssl.trustStore", KEYSTORE_FILE);
		}

		SSLContext context = SSLContext.getInstance("SSL");

		TrustManagerFactory tmf = TrustManagerFactory
				.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(ks);
		NaiveTrustManager tm = new NaiveTrustManager(
				(X509TrustManager) tmf.getTrustManagers()[0]);
		context.init(null, new TrustManager[] { tm }, null);
		// factory = context.getSocketFactory();
		factory = new TraceSSLSocketFactory(context);
		String urlTmp = ccURL.replace("https://", "");
		int endPos = urlTmp.indexOf('/');
		// strip everything but the host name, from the URL
		String host = urlTmp.substring(0, (endPos == -1 ? urlTmp.length()
				: endPos));
		int port = 443;
		if (host.contains(":")) {
			port = Integer.parseInt(host.split(":")[1]);
		}
		host = host.split(":")[0];
		SSLSocket socket;
		if (proxyURL == null) {
			socket = (SSLSocket) factory.createSocket(host, port);
		} else {
			socket = (SSLSocket) factory.createSocket(proxyURL.split(":")[0],
					Integer.parseInt(proxyURL.split(":")[1]));
		}

		socket.setSoTimeout(10000);
		try {
			socket.startHandshake();
			socket.close();
			// if an error is thrown, then this certificate is unknown to us
		} catch (SSLException e) {
			if (!addToKeyStore) {
				throw new SSLException(
						"Certificate changed! may be a MITM attack, aborting!");
			}
		}
		X509Certificate[] chain = tm.getCerts();
		if (chain == null) {
			throw new SSLException("Could not obtain server certificate chain");
		}
		if (addToKeyStore) {
			System.err.println("Adding to key store...");
			MessageDigest sha1 = MessageDigest.getInstance("SHA1");
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			for (int i = 0; i < chain.length; i++) {
				X509Certificate cert = chain[i];
				sha1.update(cert.getEncoded());
				md5.update(cert.getEncoded());
			}
			X509Certificate cert = chain[0];
			String alias = host + "-" + 1;
			ks.setCertificateEntry(alias, cert);
			OutputStream out = new FileOutputStream(KEYSTORE_FILE);
			ks.store(out, passPhrase);
			out.close();
		}

		CFHostNameVerifier.setHostNameVerifier(host);
		return factory;
	}

	private static class CFHostNameVerifier implements HostnameVerifier {

		private static HostnameVerifier verifier;
		private static String suffix;

		static void setHostNameVerifier(final String host) {
			if (verifier != null) {
				return;
			}

			// strip application name from route to get the domain
			suffix = host.substring(host.indexOf('.') + 1);

			HostnameVerifier allHostsValid = new HostnameVerifier() {
				@Override
				public boolean verify(String hostname, SSLSession session) {
					if (suffix == null) {
						System.err
								.println("CFHostNameVerifier.setHostNameVerifier.verify: Suffix is null");
						return false;
					}
					return hostname.endsWith(suffix);
				}

			};

			verifier = allHostsValid;
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

		}

		@Override
		public boolean verify(String hostname, SSLSession session) {
			return false;
		}

	}

	private static class NaiveTrustManager implements X509TrustManager {

		private X509Certificate[] certs;
		@SuppressWarnings("unused")
		X509TrustManager pTM;

		NaiveTrustManager(X509TrustManager parentTM) {
			pTM = parentTM;
		}

		public java.security.cert.X509Certificate[] getAcceptedIssuers() {
			return certs;

		}

		public void checkClientTrusted(X509Certificate[] certs, String authType) {

		}

		public void checkServerTrusted(X509Certificate[] certs, String authType)
				throws CertificateException {
			this.certs = certs;
			// pTM.checkServerTrusted(certs, authType);
		}

		public X509Certificate[] getCerts() {
			return certs;
		}
	}

	private static class TraceSSLSocketFactory extends SSLSocketFactory {

		private SSLSocketFactory delegate;

		private TraceSSLSocketFactory(SSLContext context) {
			delegate = context.getSocketFactory();
		}

		@Override
		public Socket createSocket(Socket s, String host, int port,
				boolean autoClose) throws IOException {
			return new TraceSSLSocket((SSLSocket) delegate.createSocket(s,
					host, port, autoClose));
		}

		@Override
		public String[] getDefaultCipherSuites() {
			return delegate.getDefaultCipherSuites();
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return delegate.getDefaultCipherSuites();
		}

		@Override
		public Socket createSocket(String host, int port) throws IOException,
				UnknownHostException {
			return new TraceSSLSocket((SSLSocket) delegate.createSocket(host,
					port));
		}

		@Override
		public Socket createSocket(InetAddress host, int port)
				throws IOException {
			return new TraceSSLSocket((SSLSocket) delegate.createSocket(host,
					port));
		}

		@Override
		public Socket createSocket(String host, int port,
				InetAddress localHost, int localPort) throws IOException,
				UnknownHostException {
			return new TraceSSLSocket((SSLSocket) delegate.createSocket(host,
					port, localHost, localPort));
		}

		@Override
		public Socket createSocket(InetAddress address, int port,
				InetAddress localAddress, int localPort) throws IOException {
			return new TraceSSLSocket((SSLSocket) delegate.createSocket(
					address, port, localAddress, localPort));
		}

	}

	private static class TraceSSLSocket extends SSLSocket {

		private SSLSocket delegate;

		private TraceSSLSocket(SSLSocket sslSock) {
			delegate = sslSock;
		}

		public OutputStream getOutputStream() throws IOException {
			return createTraceOutputStream(delegate.getOutputStream());
		}

		public InputStream getInputStream() throws IOException {
			return createTraceInputStream(delegate.getInputStream());
		}

		@Override
		public void addHandshakeCompletedListener(
				HandshakeCompletedListener listener) {
			delegate.addHandshakeCompletedListener(listener);
		}

		@Override
		public boolean getEnableSessionCreation() {
			return delegate.getEnableSessionCreation();
		}

		@Override
		public String[] getEnabledCipherSuites() {
			return delegate.getEnabledCipherSuites();
		}

		@Override
		public String[] getEnabledProtocols() {
			return delegate.getEnabledProtocols();
		}

		@Override
		public boolean getNeedClientAuth() {
			return delegate.getNeedClientAuth();
		}

		@Override
		public SSLSession getSession() {
			return delegate.getSession();
		}

		@Override
		public String[] getSupportedCipherSuites() {
			return delegate.getSupportedCipherSuites();
		}

		@Override
		public String[] getSupportedProtocols() {
			return delegate.getSupportedProtocols();
		}

		@Override
		public boolean getUseClientMode() {
			return delegate.getUseClientMode();
		}

		@Override
		public boolean getWantClientAuth() {
			return delegate.getWantClientAuth();
		}

		@Override
		public void removeHandshakeCompletedListener(
				HandshakeCompletedListener listener) {
			delegate.removeHandshakeCompletedListener(listener);
		}

		@Override
		public void setEnableSessionCreation(boolean flag) {
			delegate.setEnableSessionCreation(flag);
		}

		@Override
		public void setEnabledCipherSuites(String[] suites) {
			delegate.setEnabledCipherSuites(suites);
		}

		@Override
		public void setEnabledProtocols(String[] protocols) {
			delegate.setEnabledProtocols(protocols);
		}

		@Override
		public void setNeedClientAuth(boolean need) {
			delegate.setNeedClientAuth(need);
		}

		@Override
		public void setUseClientMode(boolean mode) {
			delegate.setUseClientMode(mode);
		}

		@Override
		public void setWantClientAuth(boolean want) {
			delegate.setWantClientAuth(want);
		}

		@Override
		public void startHandshake() throws IOException {
			delegate.startHandshake();
		}
	}

	static OutputStream createTraceOutputStream(final OutputStream out) {
		if (trace) {
			System.err.println("\n\nREQUEST:");
		}
		return new OutputStream() {

			public void write(int b) throws IOException {
				out.write(b);
				if (trace) {
					System.err.write(b);
				}
			}

			public void write(byte b[]) throws IOException {
				out.write(b);
				if (trace) {
					System.err.write(b);
				}
			}

			public void write(byte b[], int off, int len) throws IOException {
				out.write(b, off, len);
				if (trace) {
					System.err.write(b, off, len);
				}
			}

			public void close() throws IOException {
				out.close();
			}

			public void flush() throws IOException {
				out.flush();
				if (trace) {
					System.err.flush();
				}
			}

		};
	}

	private static InputStream createTraceInputStream(final InputStream in) {
		if (trace) {
			System.err.println("\nRESPONSE:");
		}

		return new InputStream() {

			@Override
			public int read() throws IOException {
				int i = in.read();
				if (trace && i != -1) {
					System.err.write(i);
				}
				return i;
			}

			public int read(byte b[]) throws IOException {
				int i = in.read(b);
				if (trace && i != -1) {
					System.err.write(b, 0, i);
				}
				return i;
			}

			public int read(byte b[], int off, int len) throws IOException {
				int i = in.read(b, off, len);
				if (trace && i != -1) {
					System.err.write(b, off, i);
				}
				return i;
			}
		};
	}

	static void setHTTPHeaderDebug() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			String props = ("handlers= java.util.logging.ConsoleHandler\n"
					+ ".level= ALL\n"
					+ "java.util.logging.FileHandler.pattern = %h/java%u.log\n"
					+ "java.util.logging.FileHandler.limit = 50000"
					+ "java.util.logging.FileHandler.count = 1\n"
					+ "java.util.logging.FileHandler.formatter = java.util.logging.SimpleFormatter\n"
					+ "java.util.logging.ConsoleHandler.level = FINE\n"
					+ "java.util.logging.ConsoleHandler.formatter = java.util.logging.SimpleFormatter");
			baos.write(props.getBytes());
			baos.flush();
			baos.close();
			ByteArrayInputStream bais = new ByteArrayInputStream(
					baos.toByteArray());
			LogManager.getLogManager().readConfiguration(bais);

			LogManager.getLogManager().getLogger("").getHandlers()[0]
					.setFormatter(new Formatter() {

						@Override
						public String format(LogRecord record) {
							if (record.getMessage().startsWith("sun.net.www")) {
								String msg = record
										.getMessage()
										.substring(
												record.getMessage()
														.indexOf(':') + 2)
										.replace("null: ", "")
										.replace(": null", "");
								int i = 0;
								int c = 0;
								int j = 1;
								boolean request = true;
								StringBuilder sb = new StringBuilder();
								while (++i < msg.length()) {
									if (msg.charAt(i) == '}' && c == 0) {
										if (msg.substring(j, i).startsWith(
												"HTTP/")) {
											request = false;
										}
										sb.append(msg.substring(j, i) + "\n");
										j = i + 2;
									}
									if (msg.charAt(i) == '{') {
										c++;
									}

									if (msg.charAt(i) == '}') {
										c--;
									}
								}
								return "\n"
										+ (request ? "REQUEST:" : "RESPONSE:")
										+ "\n" + sb.toString();
							} else {
								return record.getMessage() + "\n";
							}
						}
					});

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
