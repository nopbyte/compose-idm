package de.passau.uni.sec.compose.id.core.service.security;



import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.UUID;




import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;


import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.configuration.CoreConfiguration;
import de.passau.uni.sec.compose.id.configuration.UAAConfiguration;
import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.core.service.security.uaa.OpenIdUserData;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAClient;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAUserRequest;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAUserRequestName;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {UAAConfiguration.class})
public class TestUAA 
{

	  @Autowired
	  private UsersAuthzAndAuthClient client;
	  
	  private String adminUsername;
	  
	  private String adminPassword;
	  
	  
	  private List<String> usersCreated_toDelete;
	  
	  @Before
	  public void setupUnitUnderTest() 
	  {
		  //TODO extract this in a config file properly! 
		  client = new UAAClient();
		  adminUsername = "admin";
		  adminPassword = "adminsecret";
		  usersCreated_toDelete = new LinkedList<>();
	  }

	  @After
	  public void tearDonwn()
	  {
		  for(String id: usersCreated_toDelete)
		  {
			  try {
				client.deleteUser(id);
			} catch (IdManagementException e) {
				//nevermind... just cleaning up... don't care!
			}
		  }
	  }
	  
	  @Test
	  public void getClientCredentialsToken() 
	  {
		  
		try {
			
			TokenResponse res = client.getClientCredentialsToken(adminUsername,adminPassword);
			System.out.println(res.getAccessToken());
			
		} catch (IdManagementException e) {
			
			fail();
			
		}
		 
		  
	  }
	  
	  @Test
	  public void testGetImplicitTokenCredentials()
	  {
		  //get token for default user vnc
			String tokenData;
			try { 
				 
				 TokenResponse res = client.getImplicitTokenCredentials(null,"marissa","koala");
				 System.out.println(res.getAccessToken());
				 
			} catch (IdManagementException e) {
				
				fail();
				
			}
	  }
	  
	  @Test
	  public void testGetImplicitTokenCredentialsFail()
	  {
		  try {
			     TokenResponse res = client.getImplicitTokenCredentials(null,"some non existing","user");
				 //fail();
			     System.out.println(res.getAccessToken());
			} catch (IdManagementException e) {
				//ensure that a propper exception with 401 http message is created
				assertTrue(e.getHTTPErrorCode()==401);
				
			}
		    catch(Exception ex)
		    {
		    	System.out.println("SOME EXCEPTION!!!");
		    }
	  }
	  
	  @Test
	  public void testGetOpenIdData()
	  {
		  TokenResponse tokenData;
		try {
			tokenData = client.getImplicitTokenCredentials(null,"marissa","koala");
			 assertTrue(tokenData.getAccessToken()!=null);
			 OpenIdUserData data = client.getOpenIdData(tokenData.getAccessToken());
			 System.out.println(data.getUser_id());
			  
		} catch (IdManagementException e) {
			fail();
		}
		  
	  }

	  @Test
	  public void testgetDetailsFromImplicitToken()
	  {
		  /*String clientId ="admin";
		  String clientSecret = "adminsecret";
		  String token = "eyJhbGciOiJSUzI1NiJ9.eyJqdGkiOiI0ZmM2NGM1YS1jYjkxLTQzYmEtOTNmYS05ZmQxZDRhNzBmYzgiLCJzdWIiOiJhYjMxNjZiMi1iZmZkLTQwMTAtOWZiZi0xZmU5ODU0M2VjMWEiLCJzY29wZSI6WyJjb21wb3NlX2FkbWluIiwib3BlbmlkIl0sImNsaWVudF9pZCI6ImlkbSIsImNpZCI6ImlkbSIsInVzZXJfaWQiOiJhYjMxNjZiMi1iZmZkLTQwMTAtOWZiZi0xZmU5ODU0M2VjMWEiLCJ1c2VyX25hbWUiOiJqb2huIiwiZW1haWwiOiJqdWFuQGIuY29tIiwiaWF0IjoxMzk2ODkyNTUyLCJleHAiOjEzOTY5MzU3NTIsImlzcyI6Imh0dHA6Ly9sb2NhbGhvc3Q6ODA4MC91YWEvb2F1dGgvdG9rZW4iLCJhdWQiOlsib3BlbmlkIl19.Oh-KyZWQLLVCwaz0TP1iUBwh4aE_lggv4R96xCZMJlXALKcoQaLJ5-4yr8H1586L_4LTTfC3qoYE4LqVREAJyWZ45NCt33_XwQsLrqZpWrhEi305yPl6UZuwjsrKuzVkKhYVj0r3d8gGpbAYoOiW0JT_A_hfSflKGqA_QWBsmENgsvolNmXYLeqBt6FsF6Mb6anZ0WLUJyYu85b0DIQ0B_MvnhQcpqU97NMPHqWiqF1FYUj_KkR89ZprdXpnB-bO-OMw3VWEPy0MVJWuY9lPgRHol32QkKoCy-P9YSxYEtH9t5vXC9XH4U4KNbTeNvI78W-dS6E6DeecOk89kqgvHw";
		  Map<String, Object> tokenData = client.getDetailsFromImplicitToken(clientId, clientSecret, token);
		  System.out.println(tokenData);*/
	  }
	  
	  
	  @Test
	  public void testCreateUser()
	  {
		  UAAUserRequest req = new UAAUserRequest();
		  UAAUserRequestName name = new UAAUserRequestName();
		  name.setFormatted("Ms. Barbara Jensen");
		  name.setFamilyName("Jensen");
		  name.setGivenName("Barbara");
		  req.setName(name);
		  req.setPassword("pass");
		  req.setUsername("littlegirl");
		  req.addEmail("a@b.c");
		  try {
			Map<String, Object> res = client.createUser(req);
			usersCreated_toDelete.add((String) res.get("id"));
			System.out.println(res);
			
		  } catch (IdManagementException e) {
			  fail();
		  }
		  
	  }
	  
	  @Test
	  public void testDeleteUser()
	  {
		  UAAUserRequest req = new UAAUserRequest();
		  UAAUserRequestName name = new UAAUserRequestName();
		  name.setFormatted("Ms. Barbara Jensen");
		  name.setFamilyName("Jensen");
		  name.setGivenName("Barbara");
		  req.setName(name);
		  req.setPassword("pass");
		  req.setUsername("deleteuser");
		  req.addEmail("a@b.c");
		  try {
			Map<String, Object> res = client.createUser(req);
			String id = (String) res.get("id");
			client.deleteUser(id);
			
		  } catch (IdManagementException e) {
			  fail();
		  }
		  
	  }
	  
	  @Test
	  public void testDeleteNonExistingUser()
	  {
		  try {
			client.deleteUser(UUID.randomUUID().toString());
			fail();
		  } catch (IdManagementException e) {
			  assertTrue(e.getHTTPErrorCode()==404);
		  }
		  
	  }
	  
	  @Test
	  public void testCreateUserExists()
	  {
		  UAAUserRequest req = new UAAUserRequest();
		  UAAUserRequestName name = new UAAUserRequestName();
		  name.setFormatted("Mr. Bill Gates");
		  name.setFamilyName("Bill");
		  name.setGivenName("Gates");
		  req.setName(name);
		  req.setPassword("pass");
		  req.setUsername("mr.pressident");
		  req.addEmail("b@c.com");
		  try {
			Map<String, Object> res = client.createUser(req);
			usersCreated_toDelete.add((String) res.get("id"));
			System.out.println(res);
			
		  } catch (IdManagementException e) {
			  fail();
		  }
		  try {
				Map<String, Object> res = client.createUser(req);
				fail();
				
			  } catch (IdManagementException e) {
				assertTrue(e.getHTTPErrorCode() == 409);
				  
			  }
		  
	  }
	  

}
