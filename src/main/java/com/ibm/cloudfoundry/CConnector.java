package com.ibm.cloudfoundry;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class CConnector {

	private static int PUSH_TIMEOUT = 40;

	static enum ConnType {
		HTTP, HTTPS, UNKNOWN;
	}

	static ConnType connType = ConnType.UNKNOWN;
	private static CConnectorLogger logger;

	private String _idmURL;
	private String _ccURL;
	private String _uaaURL;
	private String _username;
	private String _password;
	private String _brokerName;
	private String _brokerPassword;
	private transient volatile Properties _tokenData;
	private transient Proxy _proxy;
	private transient String _proxyURL;

	public CConnector(String ccURL, String proxyURL, String idmURL,
			String username, String password) throws Exception {
		this(ccURL, proxyURL, idmURL, username, password, null, null, null);
	}
	
	public void deleteSpace(String org,String space) throws Exception {
		sendQuery("DELETE",getURLbySpace(org, space) , null);
	}

	public void deleteOrg(String orgName) throws Exception {
		String orgGuid = query("GET", "/v2/organizations", null)
				.walk("resources").search("entity", "name", orgName)
				.walk("metadata").get("guid");
		String quotaGuid = getQuotaGuidByOrg(orgName);
		sendQuery("DELETE", "/v2/organizations/" + orgGuid, null);
		deleteQuotaDef(quotaGuid);
	}
	
	private String getQuotaGuidByOrg(String orgName) throws Exception {
		return query("GET", "/v2/organizations", null)
				.walk("resources").search("entity", "name", orgName)
				.walk("entity").get("quota_definition_guid");
	}
	
	private void deleteQuotaDef(String quotaDefGuid) throws Exception {
		sendQuery("DELETE", "/v2/quota_definitions/" + quotaDefGuid,null);
	}
	
	public String createQuota(String quotaName,int totalMemoryInMB,int instanceMemoryInMB,int maxRouteAmount,int maxServicesAmount) throws Exception {
		String uri = "/v2/quota_definitions";
		JsonObject payload = new JsonObject();
		payload.addProperty("name", quotaName);
		payload.addProperty("non_basic_services_allowed", true);
		payload.addProperty("total_services",maxServicesAmount);
		payload.addProperty("total_routes", maxRouteAmount);
		payload.addProperty("memory_limit", totalMemoryInMB);
		payload.addProperty("instance_memory_limit", instanceMemoryInMB);
		return query("POST", uri, payload.toString()).walk("metadata").get("guid");
	}
	
	public String createOrg(String orgName,int totalMemoryInMB,int instanceMemoryInMB,int maxRouteAmount,int maxServicesAmount) throws Exception {
		String quotaName = "COMPOSE_" + orgName + "_Quota";
		String quotaGUID = createQuota(quotaName, totalMemoryInMB, instanceMemoryInMB, maxRouteAmount, maxServicesAmount);
		JsonObject payload = new JsonObject();
		payload.addProperty("name", orgName);
		payload.addProperty("quota_definition_guid", quotaGUID);
		return query("POST","/v2/organizations",payload.toString()).walk("metadata").get("guid");
	}
	
	public String createSpace(String orgName,String spaceName) throws Exception {
		String orgGUID = query("GET", "/v2/organizations", null)
				.walk("resources").search("entity", "name", orgName)
				.walk("metadata").get("guid");
		JsonObject payload = new JsonObject();
		payload.addProperty("name",spaceName);
		payload.addProperty("organization_guid",orgGUID);
		return query("POST", "/v2/spaces",payload.toString()).walk("metadata").get("guid");
	}
	
	public Properties getThresholdsByApp(String appGuid) throws Exception {
		JSONWalker jsw = query("GET","/v2/apps/" + appGuid+  "/stats",null);
		Properties worstThresholds = getThresholdsForInstance(jsw,0);
		String name = jsw.walk("0").walk("stats").get("name");
		worstThresholds.put("name", name);
		int instance = 1;
		while (jsw._jo.has(instance + "")) {
			Properties threshs = getThresholdsForInstance(jsw,instance);
			int cpu = (Integer) worstThresholds.get("cpu");
			long mem = (Long) worstThresholds.get("mem");
			long disk = (Long) worstThresholds.get("disk");
			worstThresholds.put("cpu", Math.max(cpu,(Integer) (threshs.get("cpu"))));
			worstThresholds.put("mem", Math.max(mem,(Long) (threshs.get("mem"))));
			worstThresholds.put("disk", Math.max(disk,(Long) (threshs.get("disk"))));
			instance++;
		}
		return worstThresholds;
	}
	
	public Map<String,Properties> getApplicationQuotas() throws Exception {
		Map<String,Properties> quotas = new HashMap<String, Properties>();
		for (String spaceGUID : getSpaceGids()) {
			for (String appGuid : getRunningAppsBySpaceGUID(spaceGUID)) {
				Properties worstThresholds = getThresholdsByApp(appGuid);
				String name = worstThresholds.getProperty("name");
				worstThresholds.remove("name");
				worstThresholds.put("id", appGuid);
				quotas.put(name,worstThresholds);
			}
		}
		return quotas;
	}

	private Properties getThresholdsForInstance(JSONWalker jsw,int instance) {
		String instanceStr = instance + "";
		long memQuota = Long.parseLong(jsw.walk(instanceStr).walk("stats").get("mem_quota"));
		long diskQuota = Long.parseLong(jsw.walk(instanceStr).walk("stats").get("disk_quota"));
		int cpuUsage = (int) Double.parseDouble(jsw.walk(instanceStr).walk("stats").walk("usage").get("cpu"));
		long memUsage = Long.parseLong(jsw.walk(instanceStr).walk("stats").walk("usage").get("mem"));
		long diskUsage = Long.parseLong(jsw.walk(instanceStr).walk("stats").walk("usage").get("disk"));
		Properties props = new Properties();
		props.put("cpu",cpuUsage);
		props.put("mem", (memUsage * 100) / memQuota);
		props.put("disk", (diskUsage * 100) / diskQuota);
		return props;
	}
	
	private List<String> getRunningAppsBySpaceGUID(String spaceGUID) throws Exception {
		List<String> runningGuids = new LinkedList<String>();
		List<JSONWalker> jwlist = getEntitiesOfType("spaces/" + spaceGUID + "/apps?inline-relations-depth=1");
		for (JSONWalker jsw : jwlist) {
			Properties p = new Properties();
			p.setProperty("id", jsw.walk("metadata").get("guid"));
			String state = jsw.walk("entity").get("state");
			if (state.equals("STARTED")) {
				runningGuids.add(jsw.walk("metadata").get("guid"));
			}
		}
		return runningGuids;
	}
	
	public void bindService(String org, String space, String app,
			String serviceInstance) throws Exception {
		String appGuid = getAppURLByNameNSpace(getURLbySpace(org, space), app)
				.replaceFirst("/v2/apps/", "");
		String instanceId = jsonWalkerFromList(
				sendQuery(
						"GET",
						getURLbySpace(org, space)
								+ "/service_instances?return_user_provided_service_instances=true&q=name%3A"
								+ serviceInstance + "&inline-relations-depth=1",
						null)).walk("resources").get(0).walk("metadata")
				.get("guid");
		String body = "{\"app_guid\":\"" + appGuid
				+ "\",\"service_instance_guid\":\"" + instanceId
				+ "\",\"async\":false}";
		sendQuery("POST", "/v2/service_bindings", body);
	}

	public void unbindAllServices(String org, String space, String app)
			throws Exception {
		String serviceBindingURL = jsonWalkerFromList(
				sendQuery("GET", getURLbySpace(org, space) + "/apps?q=name%3A"
						+ app + "&inline-relations-depth=1", null))
				.walk("resources").search("entity", "name", app).walk("entity")
				.get("service_bindings_url");
		JsonArray a = jsonWalkerFromList(
				sendQuery("GET", serviceBindingURL, null)).walk("resources")._ja;
		for (int i = 0; i < a.size(); i++) {
			String bindingId = a.get(i).getAsJsonObject().get("metadata")
					.getAsJsonObject().get("guid").getAsString();
			sendQuery("DELETE", "/v2/service_bindings/" + bindingId, null);
		}
	}

	public void unbindService(String org, String space, String app,
			String serviceInstance) throws Exception {
		String instanceId = jsonWalkerFromList(
				sendQuery(
						"GET",
						getURLbySpace(org, space)
								+ "/service_instances?return_user_provided_service_instances=true&q=name%3A"
								+ serviceInstance + "&inline-relations-depth=1",
						null)).walk("resources").get(0).walk("metadata")
				.get("guid");
		String serviceBindingURL = jsonWalkerFromList(
				sendQuery("GET", getURLbySpace(org, space) + "/apps?q=name%3A"
						+ app + "&inline-relations-depth=1", null))
				.walk("resources").search("entity", "name", app).walk("entity")
				.get("service_bindings_url");
		String bindingId = jsonWalkerFromList(
				sendQuery("GET", serviceBindingURL, null)).walk("resources")
				.search("entity", "service_instance_guid", instanceId)
				.walk("metadata").get("guid");
		sendQuery("DELETE", "/v2/service_bindings/" + bindingId, null);
	}

	public List<String> getServiceTypes(String org, String space)
			throws Exception {
		List<String> svs = new LinkedList<String>();
		String url = getURLbySpace(org, space) + "/services";
		JsonArray jsa = jsonWalkerFromList(sendQuery("GET", url, null)).walk(
				"resources")._ja;
		for (int i = 0; i < jsa.size(); i++) {
			String svcName = jsa.get(i).getAsJsonObject().get("entity")
					.getAsJsonObject().get("label").getAsString();
			svs.add(svcName);
		}
		return svs;
	}

	public void stopApp(String appName, String org, String space)
			throws Exception {
		sendQuery("PUT",
				getAppURLByNameNSpace(getURLbySpace(org, space), appName),
				"{\"state\":\"STOPPED\"");
		while (isAppOnline(org, space, appName)) {
			Thread.sleep(2000L);
		}
	}

	public int startApp(String appName, String org, String space,
			int stagingMinThreshold) throws Exception {
		return startApp(
				getAppURLByNameNSpace(getURLbySpace(org, space), appName),
				stagingMinThreshold);
	}

	public boolean isAppOnline(String org, String space, String appName)
			throws Exception {
		String appUrl = getAppURLByNameNSpace(getURLbySpace(org, space),
				appName);
		JSONWalker jsw = query("GET", appUrl + "/summary", null);
		return Integer.parseInt(jsw.get("running_instances")) > 0;
	}

	public List<String> getServices(String org, String space) throws Exception {
		List<String> services;
		try {
			services = new LinkedList<String>();
			List<String> out = sendQuery("GET", getURLbySpace(org, space)
					+ "/summary", null);
			StringBuffer sb = new StringBuffer();
			for (String s : out) {
				sb.append(s);
			}
			JsonParser p = new JsonParser();
			JsonElement json = p.parse(sb.toString());
			JsonArray a = json.getAsJsonObject().get("services")
					.getAsJsonArray();
			for (int i = 0; i < a.size(); i++) {
				services.add(a.get(i).getAsJsonObject().get("name")
						.getAsString());
			}
		} catch (NullPointerException e) {
			return new LinkedList<String>();
		}
		return services;
	}

	public String createUser(String username, String password, String org,
			String space) throws Exception {
		String uaaGid = createUAAuser(username, password);
		String data = "{" + "\"guid\":\"" + uaaGid + "\","
				+ "\"default_space_guid\":\""
				+ getURLbySpace(org, space).replaceFirst("/v2/spaces/", "")
				+ "\"" + "}";
		sendQuery("POST", "/v2/users", data);
		associateOrg(org, uaaGid);
		associateSpace(org, space, uaaGid);
		setSpaceDeveloper(org, space, uaaGid);
		return uaaGid;
	}

	private String createUAAuser(String username, String pass) throws Exception {
		String data = "{\"userName\":\"" + username
				+ "\",\"emails\":[{\"value\":\"" + username
				+ "\"}],\"password\":\"" + pass
				+ "\",\"name\":{\"givenName\":\"" + username
				+ "\",\"familyName\":\"" + username + "\"}}";

		List<String> response = sendQueryToEndpoint(_uaaURL, "POST", "/Users",
				data, false);
		StringBuilder sb = new StringBuilder();
		for (String s : response) {
			sb.append(s);
		}
		JsonParser parser = new JsonParser();
		return parser.parse(sb.toString()).getAsJsonObject().get("id")
				.getAsString();
	}

	public CConnector(String ccURL, String username, String token)
			throws Exception {
		this(ccURL, null, null, username, null, null, null, token);
	}

	public CConnector(String ccURL, String proxyURL, String username,
			String password, String brokerName, String brokerPassword)
			throws Exception {
		this(ccURL, proxyURL, null, username, password, brokerName,
				brokerPassword, null);
	}

	public void setOrgManager(String org, String userGid) throws Exception {
		String orgGUID = query("GET", "/v2/organizations", null)
				.walk("resources").search("entity", "name", org)
				.walk("metadata").get("guid");
		sendQuery("PUT", "/v2/organizations/" + orgGUID + "/managers/"
				+ userGid, null);
	}

	public void setSpaceManager(String org, String space, String userGid)
			throws Exception {
		String spaceGid = getURLbySpace(org, space).replaceFirst("/v2/spaces/",
				"");
		sendQuery("PUT", "/v2/spaces/" + spaceGid + "/managers/" + userGid,
				null);
	}

	public void associateOrg(String org, String userGid) throws Exception {
		String orgGUID = query("GET",
				"/v2/organizations?inline-relations-depth=0", null)
				.walk("resources").search("entity", "name", org)
				.walk("metadata").get("guid");
		sendQuery("PUT", "/v2/users/" + userGid + "/organizations/" + orgGUID,
				null);
	}

	public void deleteUser(String uid) throws Exception {
		sendQuery("DELETE", "/v2/users/" + uid, null);
		sendQueryToEndpoint(_uaaURL, "DELETE", "/Users", null, false);
	}

	public void associateSpace(String org, String space, String userGid)
			throws Exception {
		String spaceURL = getURLbySpace(org, space);
		String spaceGID = spaceURL.replaceFirst("/v2/spaces/", "");
		sendQuery("PUT", "/v2/users/" + userGid + "/spaces/" + spaceGID, null);
	}

	public void setSpaceDeveloper(String org, String space, String userGid)
			throws Exception {
		String spaceGid = getURLbySpace(org, space).replaceFirst("/v2/spaces/",
				"");
		sendQuery("PUT", "/v2/spaces/" + spaceGid + "/developers/" + userGid,
				null);
	}

	CConnector(String ccURL, String proxyURL, String idmURL,
			String username, String password, String brokerName,
			String brokerPassword, String token) throws Exception {

		_idmURL = idmURL;
		_ccURL = ccURL;
		_username = username;
		_brokerName = brokerName;
		_password = password;
		_brokerPassword = brokerPassword;

		if (System.getProperty("push.timeout") != null) {
			PUSH_TIMEOUT = Integer.parseInt(System.getProperty("push.timeout"));
		}

		if (proxyURL == null) {
			_proxy = Proxy.NO_PROXY;
		} else {
			Type t = Type.HTTP;
			String host;
			int port;
			if (proxyURL.startsWith("https")) {
				_proxyURL = proxyURL.replaceFirst("https://", "");
			} else if (proxyURL.startsWith("http")) { // https
				_proxyURL = proxyURL.replaceFirst("http://", "");
			} else {
				throw new Exception("Unsupported protocol");
			}
			host = _proxyURL.split(":")[0];
			port = Integer.parseInt(_proxyURL.split(":")[1]);
			_proxy = new Proxy(t, new InetSocketAddress(host, port));
		}

		String conn_Type = _ccURL.substring(0, _ccURL.indexOf(':'));

		if (conn_Type.equals("http")) {
			connType = ConnType.HTTP;
		}

		if (conn_Type.equals("https")) {
			connType = ConnType.HTTPS;
		}

		if (connType == ConnType.UNKNOWN) {
			throw new Exception(
					"Could not determine connection type! protocol must be either http:// or https://");
		}

		if (token != null) {
			injectToken(token);
		}

		if (_idmURL != null) {
			System.out.println("Logging in to IDM");
			idmLogin();
		}
		connect(_ccURL);

		if (_idmURL == null && token == null) {
			login();
		}

	}

	private void injectToken(String token) {
		_tokenData = new Properties();
		_tokenData.setProperty("access_token", token);
		_tokenData.setProperty("token_type", "bearer");
	}

	private JSONWalker query(String method, String query, String postData)
			throws Exception {
		return jsonWalkerFromList(sendQuery(method, query, postData));
	}

	static JSONWalker jsonWalkerFromList(List<String> list) {
		StringBuilder sb = new StringBuilder();
		for (String s : list) {
			sb.append(s);
		}
		return new JSONWalker(sb.toString());
	}

	public String getUserGUID() throws Exception {
		HttpURLConnection conn = connect(_uaaURL + "/userinfo");
		setRequestProperties(conn);
		StringBuilder sb = new StringBuilder();
		BufferedReader r = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String line;
		while ((line = r.readLine()) != null) {
			sb.append(line);
		}
		return new JSONWalker(sb.toString()).get("user_id");
	}

	public static boolean probeRoute(String host) throws IOException {
		HttpURLConnection conn = null;
		String line;
		StringBuilder sb = new StringBuilder();
		boolean httpNotFound = false;
		try {
			conn = (HttpURLConnection) new URL("http://" + host)
					.openConnection();
			conn.setReadTimeout(5000);
			BufferedReader r = new BufferedReader(new InputStreamReader(
					conn.getInputStream(), "UTF-8"));
			while ((line = r.readLine()) != null)
				;
			r.close();
		} catch (IOException e) {
			if (conn.getErrorStream() == null) {
				throw e;
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(
					conn.getErrorStream(), "UTF-8"));
			while ((line = r.readLine()) != null) {
				sb.append(line);
			}
		} finally {
			Pattern p = Pattern
					.compile("^404 Not Found: Requested route.+does not exist.");
			Matcher m = p.matcher(sb.toString());
			httpNotFound = m.matches();
		}
		return (!httpNotFound);
	}

	public List<String> listSpaces(String org) throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String s : sendQuery("GET",
				"/v2/organizations?inline-relations-depth=0", null)) {
			sb.append(s);
		}
		List<String> spaces = new LinkedList<String>();
		JSONWalker jw = new JSONWalker(sb.toString());
		for (int i = 0; i < jw.walk("resources").size(); i++) {
			if (jw.walk("resources").get(i).walk("entity").get("name")
					.equals(org)) {
				String guid = jw.walk("resources").get(i).walk("metadata")
						.get("guid");
				sb = new StringBuilder();
				for (String s : sendQuery("GET", "/v2/organizations/" + guid
						+ "/spaces?inline-relations-depth=0", null)) {
					sb.append(s);
				}
				jw = new JSONWalker(sb.toString());
				for (int j = 0; j < jw.walk("resources").size(); j++) {
					String space = jw.walk("resources").get(j).walk("entity")
							.get("name");
					spaces.add(space);
				}
			}
		}
		return spaces;
	}

	public List<String> getDevSpaces(String org) throws Exception {
		List<String> spaces = new LinkedList<String>();
		String myGUID = getUserGUID();
		String orgGUID = query("GET",
				"/v2/organizations?inline-relations-depth=0", null)
				.walk("resources").search("entity", "name", org)
				.walk("metadata").get("guid");
		JSONWalker jw = query("GET",
				"/v2/organizations/" + orgGUID + "/spaces", null).walk(
				"resources");
		for (int i = 0; i < jw.size(); i++) {
			JSONWalker jw2 = jw.get(i);
			String spaceGUID = jw2.walk("metadata").get("guid");
			JSONWalker jw3 = query("GET",
					"/v2/spaces/" + spaceGUID + "/developers", null).walk(
					"resources");
			for (int j = 0; j < jw3.size(); j++) {
				String guid = jw3.get(j).walk("metadata").get("guid");
				if (guid.equals(myGUID)) {
					spaces.add(jw2.walk("entity").get("name"));
				}
			}
		}
		return spaces;
	}

	public List<String> listOrgs() throws Exception {
		StringBuilder sb = new StringBuilder();
		for (String s : sendQuery("GET",
				"/v2/organizations?inline-relations-depth=0", null)) {
			sb.append(s);
		}
		List<String> orgs = new LinkedList<String>();
		JSONWalker jw = new JSONWalker(sb.toString());
		for (int i = 0; i < jw.walk("resources").size(); i++) {
			String org = jw.walk("resources").get(i).walk("entity").get("name");
			orgs.add(org);
		}
		return orgs;
	}

	public CConnector(String ccURL, String username, String password,
			String brokerName, String brokerPassword, boolean log)
			throws Exception {
		this(ccURL, System.getenv("HTTP_PROXY"), username, password,
				brokerName, brokerPassword);
		if (log && logger == null) {
			logger = new CConnectorLogger("CConnector", getClass()
					.getCanonicalName());
		}
	}

	private HttpURLConnection connect(String url) throws Exception {
		ConnType connType = ConnType.UNKNOWN;
		if (url.startsWith("http:")) {
			connType = ConnType.HTTP;
		}
		if (url.startsWith("https:")) {
			connType = ConnType.HTTPS;
		}
		if (connType == ConnType.HTTP) {
			if (System.getenv("cf_trace") != null
					&& System.getenv("cf_trace").equals("true")) {
				HTTPUtil.setHTTPHeaderDebug();
			}
			return (HttpURLConnection) new URL(url).openConnection(_proxy);
		}
		if (connType == ConnType.HTTPS) {
			HttpsURLConnection.setDefaultSSLSocketFactory(HTTPUtil
					.getSocketFactory(url, _proxyURL));
			HttpURLConnection conn = (HttpURLConnection) new URL(url)
					.openConnection(_proxy);
			return conn;
		}
		throw new RuntimeException("Failed connecting to " + url);
	}

	private static Properties extractToken(String data) throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		String processedString = data.replace("{", "").replace("}", "")
				.replace("\"", "").replace(":", "=").replace(",", "\n");
		baos.write(processedString.getBytes());
		baos.flush();
		baos.close();
		Properties prop = new Properties();
		prop.load(new ByteArrayInputStream(baos.toByteArray()));
		return prop;
	}

	private static String getValByKey(String key, String line) {
		Pattern p = Pattern.compile(".*\"" + key + "\":\\s*\"(.+)\".*");
		Matcher m = p.matcher(line);
		if (m.matches()) {
			return m.group(1);
		} else { // it is an int value
			p = Pattern.compile(".*\"" + key + "\":\\s*([0-9]+).*");
			m = p.matcher(line);
			if (m.matches()) {
				return m.group(1);
			}
		}
		return null;
	}

	public void deleteRoute(String app) {
		try {
			String routegid = getRoute(app);
			sendQuery("DELETE", "/v2/routes/" + routegid, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void makePlansPublic(List<String> serviceNames) throws Exception {
		boolean currentService = false;
		for (String line : sendQuery("GET", "/v2/services", null)) {
			if (line.contains("\"label\"")) {
				String lbl = getValByKey("label", line);
				log(Level.INFO, "Current service is " + lbl);
				if (serviceNames.contains(lbl)) {
					currentService = true;
				}
			}
			if (line.contains("\"service_plans_url\"") && currentService) {
				currentService = false;
				String spu = getValByKey("service_plans_url", line);
				log(Level.INFO, "service plan url is " + spu);
				// query CC for service plan URLs and make them public
				for (String spuLine : sendQuery("GET", spu, null)) {
					if (spuLine.contains("\"url\"")) {
						String servicePlanURL = getValByKey("url", spuLine);
						log(Level.INFO, "servicePlanURL is " + servicePlanURL);
						List<String> out = sendQuery("PUT", servicePlanURL,
								"{\"public\":true}");
						log(Level.INFO, "output=" + out.toString());
					}
				}
			}
		}
	}
	
	public List<String> getAppsBySpace(String spaceGUI) {
		try {
			return sendQuery("GET", "/v2/apps", null);
		} catch (Exception e) {
			e.printStackTrace();
			return new LinkedList<String>();
		}
	}
	
	private List<String> getOrgGUIDS() throws Exception {
		return enumerateEntityType("organizations");
	}
	
	private List<String> getSpaceGids() throws Exception {
		List<String> spaceGids = new LinkedList<String>();
		for (String orgGuid : getOrgGUIDS()) {
			for (String gids : enumerateEntityType("organizations/" + orgGuid + "/spaces")) {
				spaceGids.add(gids);
			}
		}
		return spaceGids;
	}
	
	
	private List<String> enumerateEntityType(String type) throws Exception {
		List<String> ids = new LinkedList<String>();
		JsonArray jsa = query("GET", "/v2/" + type, null).walk("resources")._ja;
		for (int i=0; i<jsa.size(); i++) {
			ids.add(jsa.get(i).getAsJsonObject().get("metadata").getAsJsonObject().get("guid").getAsString());
		}
		return ids;
	}
	
	private List<JSONWalker> getEntitiesOfType(String type) throws Exception {
		List<JSONWalker> walkers = new LinkedList<JSONWalker>();
		JsonArray jsa = query("GET", "/v2/" + type, null).walk("resources")._ja;
		for (int i=0; i<jsa.size(); i++) {
			walkers.add(new JSONWalker(jsa.get(i).getAsJsonObject().getAsJsonObject().toString()));
		}
		return walkers;
	}
	
	private List<Properties> getAppStatesBySpaceGUID(String spaceGUID) throws Exception {
		List<Properties> props = new LinkedList<Properties>();
		List<JSONWalker> jwlist = getEntitiesOfType("spaces/" + spaceGUID + "/apps?inline-relations-depth=1");
		for (JSONWalker jsw : jwlist) {
			Properties p = new Properties();
			p.setProperty("id", jsw.walk("metadata").get("guid"));
			p.setProperty("name", jsw.walk("entity").get("name"));
			p.setProperty("state", jsw.walk("entity").get("state"));
			String cmd = jsw.walk("entity").get("command");
			p.setProperty("command", cmd != null ?  cmd : "null");
			props.add(p);
		}
		return props;
	}
	
	public List<Properties> getAppStates() throws Exception {
		List<Properties> props = new LinkedList<Properties>();
		for (String spaceGUID : getSpaceGids()) {
			for (Properties p : getAppStatesBySpaceGUID(spaceGUID))
			props.add(p);
		}
		return props;
	}
	
	public void deleteApp(String org, String space, String app)
			throws Exception {
		String appURL = getAppURLByNameNSpace(getURLbySpace(org, space), app);
		if (appURL == null) {
			throw new Exception("Application " + app + " in space " + space
					+ " is not found");
		}
		unbindAllServices(org, space, app);
		sendQuery("DELETE", appURL, null);
		if (getRoute(app) != null) {
			deleteRoute(app);
		}
	}

	public List<String> getDEAs(String org, String space, String appName)
			throws Exception {
		String url = getAppURLByNameNSpace(getURLbySpace(org, space), appName);
		String lineData = null;
		if (url == null) {
			throw new Exception(
					"Unable to get application name by URL, check application exists");
		}

		for (String line : sendQuery("GET", url + "/stats", null)) {
			if (line.contains("RUNNING")) {
				lineData = line;
			}
		}

		if (lineData == null) {
			throw new Exception("Application " + appName
					+ " isn't in RUNNING state");
		}

		List<String> l = new ArrayList<String>();
		int start = 0;
		while (start++ != -1) {
			lineData = lineData.substring(start);
			start = lineData.indexOf("\"host\":");
			String hostPortStr = lineData.substring(0);
			String patt = "host\":\"(.+?)\",\"port\":([0-9]+?),.+";
			Pattern p = Pattern.compile(patt);
			Matcher m = p.matcher(hostPortStr);
			if (m.matches()) {
				String host = m.group(1);
				int port = Integer.parseInt(m.group(2));
				l.add(host + ":" + port);
			}
		}

		return l;
	}

	private static void listFiles(File dir, List<File> files) {
		for (File f : dir.listFiles()) {
			if (f.isDirectory()) {
				listFiles(f, files);
				continue;
			}
			files.add(f);
		}
	}

	public static void zipDir(String srcdir, String tmpDir) throws IOException {
		FileOutputStream fos = new FileOutputStream(tmpDir + File.separator
				+ "archive.zip");
		ZipOutputStream zos = new ZipOutputStream(fos);
		List<File> files = new ArrayList<File>();
		listFiles(new File(srcdir), files);
		for (File f : files) {
			String zes = f.getAbsolutePath().replace(
					srcdir + File.separatorChar, "");
			ZipEntry ze = new ZipEntry(zes);
			zos.putNextEntry(ze);
			FileInputStream fis = new FileInputStream(f);
			int byRead = 0;
			byte[] buff = new byte[1024 * 10];
			while ((byRead = fis.read(buff)) != -1) {
				zos.write(buff, 0, byRead);
				zos.flush();
			}
			fis.close();
		}
		zos.closeEntry();
		zos.close();
	}

	private String getDomainGid(String spaceGid) throws Exception {
		for (String line : sendQuery("GET", "/v2/spaces/" + spaceGid
				+ "/domains?inline-relations-depth=1", null)) {
			if (line.contains("\"guid\"")) {
				return getValByKey("guid", line);
			}
		}
		return null;
	}

	public String pushApp(String appname, String org, String spacename,
			int instanceCount, File jarFile, int stagingThreshold,
			boolean createRoute) throws Exception {
		return _pushApp(appname, org, spacename, null, instanceCount, jarFile,
				stagingThreshold, null, createRoute);
	}

	public String pushApp(String appname, String org, String spacename,
			String command, int instanceCount, File jarFile,
			int stagingThreshold, boolean createRoute) throws Exception {
		return _pushApp(appname, org, spacename, command, instanceCount,
				jarFile, stagingThreshold, null, createRoute);
	}

	public String pushApp(String appname, String org, String spacename,
			String command, int instanceCount, File jarFile,
			int stagingThreshold, String buildpack, boolean createRoute)
			throws Exception {
		return _pushApp(appname, org, spacename, command, instanceCount,
				jarFile, stagingThreshold, buildpack, createRoute);
	}

	private String _pushApp(String appname, String org, String spacename,
			String command, int instanceCount, File jarFile,
			int stagingThreshold, String buildpack, boolean createRoute)
			throws Exception {
		String spaceURL = getURLbySpace(org, spacename);
		boolean appExists = checkAppExists(spacename, org, appname);
		if (appExists) {
			throw new Exception("Application already exists");
		}
		String spacegid = spaceURL.replaceFirst("/v2/spaces/", "");
		String domainGid = getDomainGid(spacegid);
		if (domainGid == null) {
			throw new Exception("Couldn't acquire domain gid");
		}
		String appGuid = createApp(spacegid, appname, command, buildpack,
				instanceCount);
		String routeGid = getRoute(appname);

		if (createRoute) {
			if (routeGid == null) {
				String routeCreationData = "{\"host\":\"" + appname
						+ "\",\"domain_guid\":\"" + domainGid
						+ "\",\"space_guid\":\"" + spacegid + "\"}";
				sendQuery("POST", "/v2/routes", routeCreationData);
				routeGid = getRoute(appname);
			}
			sendQuery("PUT", "/v2/apps/" + appGuid + "/routes/" + routeGid,
					null);
		}

		String jobURL = uploadApp("/v2/apps/" + appGuid + "/bits?async=true",
				appGuid, jarFile);
		int seconds = 0;
		while (!pollJob(jobURL) && (seconds++ < PUSH_TIMEOUT)) {
			Thread.sleep(1000 * 1);
		}
		Thread.sleep(10 * 1000);
		return appGuid;
	}

	// appURL must be /v2/apps/appGuid
	public int startApp(String appURL, int staging_minutes_threshold)
			throws Exception {
		sendQuery("PUT", appURL, "{\"console\":true,\"state\":\"STARTED\"}");
		long startTime = System.currentTimeMillis();
		boolean started = false;
		while ((!started)
				&& ((System.currentTimeMillis() - startTime)) / 60000 < staging_minutes_threshold) {
			Thread.sleep(1000 * 5);
			for (String line : sendQueryToEndpoint(_ccURL, "GET", appURL
					+ "/stats", null, true)) {
				if (line.contains("RUNNING")) {
					started = true;
				}
			}
		}
		long endTime = System.currentTimeMillis();
		if ((endTime - startTime) > staging_minutes_threshold * 60000) {
			throw new Exception("Staging timed out: "
					+ ((endTime - startTime) / 60000) + "m, "
					+ ((endTime - startTime) % 60000 / 1000) + "s");
		}

		boolean finished = checkHasStart(appURL);
		while (finished == false) {
			finished = checkHasStart(appURL);
		}

		endTime = System.currentTimeMillis();
		return (int) ((endTime - startTime) / 1000);
	}

	private boolean pollJob(String jobURL) throws Exception {
		for (String line : sendQuery("GET", jobURL, null)) {
			if (line.contains("\"status\":")) {
				String stat = getValByKey("status", line);
				if (stat.equals("finished")) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean checkHasStart(String jobURL) throws Exception {
		for (String line : sendQuery("GET", jobURL, null)) {

			if (line.contains("\"state\":")) {

				String stat = getValByKey("state", line);

				if (stat.equals("STARTED")) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean checkHasStopped(String jobURL) throws Exception {
		for (String line : sendQuery("GET", jobURL, null)) {
			if (line.contains("\"state\":")) {
				String stat = getValByKey("state", line);
				if (stat.equals("STOPPED")) {
					return true;
				}
			}
		}
		return false;
	}

	private String uploadApp(String appURL, String appGid, File zip)
			throws Exception {
		try {
			HttpURLConnection conn = connect((connType == ConnType.HTTPS ? "https://"
					: "http://")
					+ _ccURL.replaceFirst(".*://", "") + appURL);
			conn.setRequestMethod("PUT");
			conn.setRequestProperty("Accept", " */*");
			conn.setRequestProperty("User-Agent", " Ruby");
			conn.setRequestProperty("Content-Type",
					" multipart/form-data; boundary=-----------RubyMultipartPost");
			conn.setRequestProperty("Content-Length", " "
					+ (354 + zip.length()));
			conn.setRequestProperty(
					"Authorization",
					_tokenData.getProperty("token_type") + " "
							+ _tokenData.getProperty("access_token"));
			conn.setRequestProperty("Host", _ccURL.replaceFirst(".*://", ""));

			conn.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(
					HTTPUtil.createTraceOutputStream(conn.getOutputStream()));

			wr.write("-------------RubyMultipartPost\n".getBytes());
			wr.write("Content-Disposition: form-data; name=\"resources\"\n\n[]\n"
					.getBytes());
			wr.write("-------------RubyMultipartPost\n".getBytes());
			wr.write(("Content-Disposition: form-data; name=\"application\"; filename=\""
					+ appGid + ".zip\"\n").getBytes());
			wr.write(("Content-Length: " + zip.length() + "\n").getBytes());
			wr.write("Content-Type: application/zip\n".getBytes());
			wr.write("Content-Transfer-Encoding: binary\n\n".getBytes());
			byte[] buff = new byte[1024 * 10];
			int byRead = -1;
			FileInputStream fis = new FileInputStream(zip);
			while ((byRead = fis.read(buff)) != -1) {
				wr.write(buff, 0, byRead);
			}
			wr.write("\n-------------RubyMultipartPost--\n\n".getBytes());
			fis.close();
			wr.close();

			BufferedReader r = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			String jobUrl = null;
			while ((line = r.readLine()) != null) {
				debug(line);
				if (line.contains("\"url\":")) {
					jobUrl = getValByKey("url", line);
					if (jobUrl != null) {
						return jobUrl;
					}
				}
			}
			throw new Exception("Couldn't find job URL field, presumed failed");
		} catch (Exception e) {
			throw e;
		}
	}

	private String getRoute(String appName) throws Exception {
		String query = "/v2/routes?inline-relations-depth=0&q=host%3A"
				+ appName;
		for (String line : sendQuery("GET", query, null)) {
			if (line.contains("\"guid\"")) {
				return getValByKey("guid", line);
			}
		}
		return null;
	}

	private String createApp(String spacegid, String appname, String command,
			String buildpack, int instanceCount) throws Exception {
		String createCmd = "{\"space_guid\":\"" + spacegid + "\",\"name\":\""
				+ appname
				+ (command != null ? "\",\"command\":\"" + command : "")
				+ "\",\"instances\":" + instanceCount + "," + "\"buildpack\":"
				+ (buildpack != null ? "\"" + buildpack + "\"" : "null")
				+ ",\"memory\":320,\"stack_guid\":null}";
		System.out.println(createCmd);
		boolean created = false;
		for (String line : sendQuery("POST", "/v2/apps", createCmd)) {
			if (line.contains("\"created_at\"")) {
				created = true;
			}
			if (line.contains("\"guid\"")) {
				return getValByKey("guid", line);
			}
		}
		if (!created) {
			throw new Exception(
					"App not created, missing created_at field in response output");
		}
		throw new Exception(
				"App not created, missing guid field in response output");
	}

	private List<String> sendQuery(String method, String requrl, String postData)
			throws Exception {
		return sendQueryToEndpoint(_ccURL, method, requrl, postData, false);
	}

	List<String> sendQueryToEndpoint(String endpointURL, String method,
			String requrl, String postData, boolean ignoreErrors)
			throws Exception {
		List<String> data = new ArrayList<String>();
		HttpURLConnection conn = null;
		try {
			conn = connect(endpointURL + requrl);
			conn.setRequestMethod(method);
			if (_tokenData != null) {
				setRequestProperties(conn);
			} else {
				conn.setRequestProperty("Accept",
						"text/plain, application/json, application/*+json, */*");
				conn.setRequestProperty("Content-Type",
						"application/json;charset=UTF-8");
				conn.setRequestProperty("Connection", "Keep-Alive");
			}

			if (postData != null) {
				conn.setDoOutput(true);
				DataOutputStream wr = new DataOutputStream(
						HTTPUtil.createTraceOutputStream(conn.getOutputStream()));
				wr.writeBytes(postData);
				wr.flush();
				wr.close();
			}

			BufferedReader r = new BufferedReader(new InputStreamReader(
					conn.getInputStream()));
			String line;
			while ((line = r.readLine()) != null) {
				debug(line);
				data.add(line);
			}
			int resCode = conn.getResponseCode();
			if (resCode / 100 != 2 && (!ignoreErrors)) {
				throw new Exception("Response code is " + resCode);
			}
		} catch (Exception e) {
			if (conn == null || conn.getErrorStream() == null) {
				throw e;
			}
			BufferedReader r = new BufferedReader(new InputStreamReader(
					conn.getErrorStream()));
			String line;
			while ((line = r.readLine()) != null) {
				//System.err.println(line);
				debug(line);
				data.add(line);
			}
			throw new Exception("Message from CF: " + data.toString());
		}
		return data;
	}

	public boolean checkAppExists(String space, String org, String appName)
			throws Exception {
		boolean exists;
		try {
			exists = checkAppExists(getURLbySpace(org, space), appName);
		} catch (NullPointerException e) {
			return false;
		}
		return exists;
	}

	public boolean checkAppExists(String spaceURL, String appName)
			throws Exception {
		try {
			return getAppURLByNameNSpace(spaceURL, appName) != null;
		} catch (NullPointerException e) {
			return false;
		}
	}

	public String getAppURLByNameNSpace(String spaceURL, String appName)
			throws Exception {
		if (spaceURL == null) {
			throw new Exception("Space URL is null");
		}
		String queryURL = spaceURL + "/apps?q=name%3A" + appName
				+ "&inline-relations-depth=1";
		try {
			return query("GET", queryURL, null).walk("resources")
					.search("entity", "name", appName).walk("metadata")
					.get("url");
		} catch (NullPointerException e) {
			return null;
		}
	}

	Properties getTokenData() {
		return _tokenData;
	}

	public String getURLbySpace(String org, String space) throws Exception {
		String orgGUID = query("GET",
				"/v2/organizations?inline-relations-depth=0", null)
				.walk("resources").search("entity", "name", org)
				.walk("metadata").get("guid");
		return query("GET", "/v2/organizations/" + orgGUID + "/spaces", null)
				.walk("resources").search("entity", "name", space)
				.walk("metadata").get("url");
	}

	private void login() throws Exception {
		HttpURLConnection conn = connect(_ccURL + "/v2/info");
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("User-Agent",
				"Apache-HttpClient/4.3.1 (java 1.5)");
		conn.setRequestProperty("Host", _ccURL.replaceFirst(".*://", ""));
		conn.setRequestProperty("Accept",
				"text/plain, application/json, application/*+json, */*");

		int responseCode = conn.getResponseCode();

		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			debug(inputLine);
			response.append(inputLine);
		}
		in.close();

		String regex = ".+\"authorization_endpoint\":\"(.+?)\".*";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(response.toString());
		_uaaURL = null;
		if (m.matches()) {
			_uaaURL = m.group(1);
		}

		if (_uaaURL == null) {
			System.err.println("Failed obtaining uaa URL");
			System.err.println(response.toString());
		}
		conn = connect(_uaaURL + "/oauth/token");
		conn.setRequestMethod("POST");
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("User-Agent",
				"Apache-HttpClient/4.3.1 (java 1.5)");
		conn.setRequestProperty("Host", _uaaURL.replaceFirst(".*://", ""));
		conn.setRequestProperty("Authorization", "Basic Y2Y6");
		conn.setRequestProperty("Accept",
				"text/plain, application/json, application/*+json, */*");
		String urlParameters = "grant_type=password&username=" + _username
				+ "&password=" + _password;
		conn.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(
				HTTPUtil.createTraceOutputStream(conn.getOutputStream()));
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		responseCode = conn.getResponseCode();
		if (responseCode != 200) {
			System.out.println("responseCode=" + responseCode);
		}
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			debug(inputLine);
			response.append(inputLine);
		}
		in.close();

		_tokenData = extractToken(response.toString());
	}

	private void idmLogin() throws Exception {
		List<String> idmloginData = sendQueryToEndpoint(_idmURL, "POST",
				"/auth/user/", "{\"username\":\"" + _username
						+ "\",\"password\":\"" + _password + "\"}", false);
		JSONWalker idmLogin = jsonWalkerFromList(idmloginData);
		_tokenData = new Properties();
		_tokenData.setProperty("access_token", idmLogin.get("accessToken"));
		_tokenData.setProperty("token_type", idmLogin.get("token_type"));
	}

	private void setRequestProperties(HttpURLConnection conn) {
		conn.setRequestProperty("Connection", "Keep-Alive");
		conn.setRequestProperty("User-Agent",
				"Apache-HttpClient/4.3.1 (java 1.5)");
		conn.setRequestProperty("Host", _ccURL.replaceFirst(".*://", ""));
		conn.setRequestProperty("Accept",
				"text/plain, application/json, application/*+json, */*");
		conn.setRequestProperty("Content-Type",
				"application/json;charset=UTF-8");
		String auth = _tokenData.getProperty("token_type") + " "
				+ _tokenData.getProperty("access_token");
		conn.setRequestProperty("Authorization", auth);
	}

	public boolean updateBroker() throws Exception {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}

		HttpURLConnection conn = connect(_ccURL
				+ "/v2/service_brokers?q=name%3A" + _brokerName
				+ "&inline-relations-depth=1");
		conn.setRequestMethod("GET");
		setRequestProperties(conn);
		int responseCode = conn.getResponseCode();

		if (responseCode != 200) {
			return false;
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(
				conn.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			debug(inputLine);
			response.append(inputLine);
		}
		in.close();
		// Pattern p = Pattern.compile(".+\"url\":\\s*\"(.+?)\",.+");
		// Matcher m = p.matcher(response.toString());
		String brokerUrl = new JsonParser().parse(response.toString())
				.getAsJsonObject().get("resources").getAsJsonArray().get(0)
				.getAsJsonObject().get("metadata").getAsJsonObject().get("url")
				.getAsString();
		// String brokerUrl = null;
		/*
		 * if (m.matches()) { brokerUrl = m.group(1);
		 * System.out.println("brokerURL is " + brokerUrl); } else {
		 * System.out.println("Failed resolving brokerURL, raw data is:\n" +
		 * response.toString()); return false; }
		 */

		// p = Pattern.compile(".+\"entity\":(.+)}\\s*}.*");
		Pattern p = Pattern
				.compile(".+\"entity\":.+\"name\":\\s*.+?,(.+)}\\s*}.*");
		Matcher m = p.matcher(response.toString());
		String brokerData = null;
		if (m.matches()) {
			brokerData = m.group(1);
			brokerData = "{" + brokerData + ",\"auth_password\":\""
					+ _brokerPassword + "\"    }";
			brokerData = brokerData.replaceAll("\\s+", "");
		}

		if (brokerData == null) {
			System.err.println("Invalid broker data received, raw data is:");
			System.err.println(response.toString());
		} else {
			System.out.println("brokerData: " + brokerData);
		}
		// send PUT
		conn = (HttpURLConnection) connect(_ccURL + brokerUrl);
		conn.setRequestMethod("PUT");
		conn.setDoOutput(true);
		setRequestProperties(conn);
		PrintWriter writer = new PrintWriter(
				HTTPUtil.createTraceOutputStream(conn.getOutputStream()));
		writer.println(brokerData);
		writer.flush();
		writer.close();
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			debug(inputLine);
			response.append(inputLine);
		}
		in.close();

		System.out.println("Response from CC: " + response.toString());

		return true;
	}

	private static void log(Level level, String msg, Exception e) {
		if (logger != null) {
			if (e == null) {
				logger.log(level, msg);
			} else {
				logger.log(level, msg, e);
			}
		}
	}

	private static void log(Level level, String msg) {
		log(level, msg, null);
	}

	private static void debug(String line) {
		if (HTTPUtil.trace) {
			System.err.println(line);
		}
	}

	static class JSONWalker {

		private static final JsonParser parser = new JsonParser();

		private String _json;
		private JsonObject _jo;
		private JsonArray _ja;

		private JSONWalker(String json) {
			if (json.startsWith("[") && json.endsWith("]")) {
				_json = json;
				_ja = (JsonArray) parser.parse(_json);
			} else {
				_json = json;
				_jo = (JsonObject) parser.parse(_json);
			}
		}

		JSONWalker walk(String attr) {
			Object obj = _jo.get(attr);
			if (obj instanceof JsonObject) {
				return new JSONWalker(((JsonObject) obj).toString());
			} else {
				return new JSONWalker(((JsonArray) obj).toString());
			}
		}

		private JSONWalker search(String field, String attr, String value) {
			for (int i = 0; i < _ja.size(); i++) {
				if (new JSONWalker(_ja.get(i).toString()).walk(field).get(attr)
						.equals(value)) {
					return new JSONWalker(_ja.get(i).toString());
				}
			}
			return null;
		}

		private int size() {
			return _ja.size();
		}

		String get(String attr) {
			return _jo.get(attr).isJsonNull() ? null :  _jo.get(attr).getAsString();
		}

		JSONWalker get(int i) {
			return new JSONWalker(_ja.get(i).toString());
		}

	}

}