package com.ibm.cloudfoundry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.event.CreateUserEvent;


public class CloudUserRegistration 
{
	private static Logger LOG = LoggerFactory.getLogger(CloudUserRegistration.class);

	public String setupUserInCloud(String cCurl, String username, String password,String org, String space, String adminUser, String adminPass,
			int totalMemoryInMB,int  instanceMemoryInMB,int  maxRouteAmount,int  maxServicesAmount) throws IdManagementException
	{
		String id = null;
		try{
			
			
			
			CConnector cc  = new CConnector(cCurl,null, null, adminUser, adminPass);
			String orgname = username+"-org";
			String spacename = username+"-space";
			String orgGuid = cc.createOrg(orgname,totalMemoryInMB,instanceMemoryInMB,maxRouteAmount,maxServicesAmount);
			LOG.info("Create Org executed succesfully for orgname: "+orgname);
			String spaceUid = cc.createSpace(orgname,spacename);
			LOG.info("Create Space executed succesfully for orgname : "+orgname+" for space: "+spacename);
			id = cc.createUser(username, password, org, space);
			if(id == null){
				throw new IdManagementException("problem trying to create user in the cloud. User null",null, LOG,"Exception while creating user with CConnector: id returned by connector is null",Level.ERROR, 500);			
			}
			LOG.info("User registered in the cloud with username: "+username+" space: "+space+" and uaaGid: "+id);
			
		}
		catch(NullPointerException e)
		{
			throw new IdManagementException("Null pointer exceptions from IBM CConnector",e, LOG,"Exception while setting user with uaaUID"+id+" in org: "+org+" with space: "+space,Level.ERROR, 500);
		}
		catch(Exception ex)
		{
			throw new IdManagementException("problem trying to set the username in the organization- using IBM CConnector",ex, LOG,"Exception while setting user with uaaUID"+id+" in org: "+org+" with space: "+space,Level.ERROR, 500);			
		}
		return id;
	}
	
	private String getUserUID(String cCurl, String username, String password) throws Exception {
		
		CConnector cc = new CConnector(cCurl,null, null, username, password);
		return  cc.getUserGUID();
		
	}

	public void unregisterUserFromCloud(String username, String cCurl, String uid, String adminUser, String adminPassword) throws IdManagementException
	{
		try{
		    CConnector cc  = new CConnector(cCurl,null, null, adminUser, adminPassword);
		    LOG.info("Attemtpting to delete the user with uid:"+uid);
			cc.deleteUser(uid);			
		    LOG.info("calling CConnector.deleteUser with uid:"+uid+" user executing the request, username: "+adminUser);
			String orgname = username+"-org";
			String spacename = username+"-space";
		    LOG.info("Attemtpting to delete the space "+spacename+" in org:"+orgname);
			cc.deleteSpace(orgname, spacename);
		    LOG.info("Attemtpting to delete the org:"+orgname);
			cc.deleteOrg(orgname);
		    
		}catch(Exception ex)
		{
			
			throw new IdManagementException("problem trying to delete user from Cloud Controller- using IBM CConnector",ex, LOG,"Exception while deleting user with guid:"+uid+" from the cloud controller",Level.ERROR, 500);			
		}
		
	}
}
