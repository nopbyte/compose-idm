package com.ibm.cloudfoundry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;


public class CloudUserRegistration 
{
	private static Logger LOG = LoggerFactory.getLogger(CloudUserRegistration.class);

	public void setupUserInCloud(String cCurl, String username, String password, String uaaGid,String org, String space, String adminUser, String adminPass,
			int totalMemoryInMB,int  instanceMemoryInMB,int  maxRouteAmount,int  maxServicesAmount) throws IdManagementException
	{
		/*LOG.info("Trying to register a user in CC... adminuser: "+adminUser+" and password: "+adminPass+" in url:"+cCurl);
		LOG.info("Trying to create user in CC. Creating user with uaaUID: "+uaaGid+" with controller created with admin user:"+adminUser+" in url:"+cCurl);
		cc  = new CConnector(cCurl,null, null, adminUser, adminPass);
		String data = "{" +
				"\"guid\":\"" +  uaaGid + "\"," + 
				"\"default_space_guid\":\"" + cc.getURLbySpace(org, space).replaceFirst("/v2/spaces/", "") + "\"" + 
				"}";
		cc.sendQuery("POST", "/v2/users",data);
		LOG.info("Trying to associate org with user. Org: "+org+" guid: "+uaaGid);
		cc.associateOrg(org, uaaGid);
		LOG.info("Trying to associate space with user. Org: "+org+" Space: "+space+" guid: "+uaaGid);
		cc.associateSpace(org, space, uaaGid);
		LOG.info("Trying to set org manager. Org: "+org+" guid"+uaaGid);
		cc.setOrgManager(org, uaaGid);
		LOG.info("Trying to set space manager. Org: "+org+" space: "+space+" guid: "+uaaGid);
		cc.setSpaceManager(org, space, uaaGid);
		LOG.info("Trying to set space developer. Org: "+org+" space: "+space+" guid: "+uaaGid);
		cc.setSpaceDeveloper(org, space, uaaGid);
		
		/*String guid = getUserUID(cCurl,username, password);
		LOG.info("guid obtained from CC for user :"+username+" and password: "+password+" ==  "+guid);
		LOG.info("Trying to set org manager. Org: "+org+" guid"+guid);
		cc.setOrgManager( org, guid);
		LOG.info("Trying to associate org with user. Org: "+org+" guid: "+guid);
		cc.associateOrg(org, guid);
		LOG.info("Trying to set space manager. Org: "+org+" space: "+space+" guid: "+guid);
		cc.setSpaceManager(org,space,guid); 
		LOG.info("Trying to set space developer  Org: "+org+" space: "+space+" guid: "+guid);
		cc.setSpaceDeveloper(org,space,guid); 
		LOG.info("Trying to associate space with user. Org: "+org+" Space: "+space+" guid: "+guid);
		cc.associateSpace(org,space,guid);
		
		LOG.info("Done setting up the user in the Cloud Controller... Seems it went OK");
		*/
		try{
			
			
			
			CConnector cc  = new CConnector(cCurl,null, null, adminUser, adminPass);
			String orgname = username+"-org";
			String spacename = username+"-space";
			String orgGuid = cc.createOrg(orgname,totalMemoryInMB,instanceMemoryInMB,maxRouteAmount,maxServicesAmount);
			String spaceUid = cc.createSpace(orgname,spacename);
			LOG.info("calling CConnector.createUser with username: "+username+" space: "+space+" and uaaGid: "+uaaGid);
			cc.createUser(username, password, org, space);

			
		}
		catch(NullPointerException e)
		{
			throw new IdManagementException("Null pointer exceptions from IBM CConnector",e, LOG,"Exception while setting user with uaaUID"+uaaGid+" in org: "+org+" with space: "+space,Level.ERROR, 500);
		}
		catch(Exception ex)
		{
			throw new IdManagementException("problem trying to set the username in the organization- using IBM CConnector",ex, LOG,"Exception while setting user with uaaUID"+uaaGid+" in org: "+org+" with space: "+space,Level.ERROR, 500);			
		}
	}
	
	private String getUserUID(String cCurl, String username, String password) throws Exception {
		
		CConnector cc = new CConnector(cCurl,null, null, username, password);
		return  cc.getUserGUID();
		
	}

	public void unregisterUserFromCloud(String username, String cCurl, String uid, String adminUser, String adminPassword) throws IdManagementException
	{
		try{
		    CConnector cc  = new CConnector(cCurl,null, null, adminUser, adminPassword);
		    LOG.info("calling CConnector.deleteUser with uid:"+uid+" user executing the request, username: "+adminUser);
			cc.deleteUser(uid);
			String orgname = username+"-org";
			String spacename = username+"-space";
			cc.deleteSpace(orgname, spacename);
			cc.deleteOrg(orgname);
		    
		}catch(Exception ex)
		{
			
			throw new IdManagementException("problem trying to delete user from Cloud Controller- using IBM CConnector",ex, LOG,"Exception while deleting user with guid:"+uid+" from the cloud controller",Level.ERROR, 500);			
		}
		
	}
}
