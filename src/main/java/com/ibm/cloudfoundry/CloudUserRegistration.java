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
		try{
			
			
			
			CConnector cc  = new CConnector(cCurl,null, null, adminUser, adminPass);
			String orgname = username+"-org";
			String spacename = username+"-space";
			String orgGuid = cc.createOrg(orgname,totalMemoryInMB,instanceMemoryInMB,maxRouteAmount,maxServicesAmount);
			LOG.info("Create Org executed succesfully for orgname: "+orgname);
			String spaceUid = cc.createSpace(orgname,spacename);
			LOG.info("Create Space executed succesfully for orgname : "+orgname+" for space: "+spacename);
			cc.createUser(username, password, org, space);
			LOG.info("User registered in the cloud with username: "+username+" space: "+space+" and uaaGid: "+uaaGid);
			
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
