package de.passau.uni.sec.compose.id.core.service;


import iotp.model.GenericSenderOTPData;
import iotp.model.communication.DataReceiver;
import iotp.model.communication.DataSender;
import iotp.model.exception.IOTPException;
import iotp.model.storage.RESTApiStorage;
import iotp.model.storage.StorageProviderFactory;
import iotp.model.storage.model.EncodedAttributeValue;
import iotp.model.storage.model.EncodedMembership;
import iotp.model.storage.model.EncodedSenderOTPData;
import iotp.model.storage.model.EncodedUser;
import iotp.model.utils.Utils;
import iotp.service.otp.OneTimePasswordManager;
import iotp.service.otp.SecretDerivatorFactory;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import de.passau.uni.sec.compose.id.common.exception.IdManagementException;
import de.passau.uni.sec.compose.id.common.exception.IdManagementException.Level;
import de.passau.uni.sec.compose.id.core.domain.ComposeUserPrincipal;
import de.passau.uni.sec.compose.id.core.domain.IDMEncodedSenderOTPData;
import de.passau.uni.sec.compose.id.core.domain.IPrincipal;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.MembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import de.passau.uni.sec.compose.id.core.service.security.RestAuthentication;
import de.passau.uni.sec.compose.id.core.service.servioticy.ServioticyManager;
import de.passau.uni.sec.compose.id.rest.messages.AttributeValueResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.MembershipResponseMessage;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;


@Service
@PropertySource("classpath:servioticy.properties")
public class GenerateOTPData   
{

	private static Logger LOG = LoggerFactory.getLogger(GroupService.class);

	@Autowired
	private Environment env;
	
	@Autowired
	UserRepository userRepository;
	
	
	@Autowired 
	RestAuthentication authentication;
	
	
	@Autowired
	MembershipRepository membershipRepository;
	
	
	@Autowired
	UpdateManager updater;

	private String servioticyPrivateUrl;
	
	private String otpApiCall;
	
	private Map<String,Object> iotpParams;
	
	@PostConstruct
	public void postConstruct()
	{
		this.iotpParams = new HashMap<>();
		this.servioticyPrivateUrl = env.getProperty("servioticy.private.url");
		this.otpApiCall = env.getProperty("servioticy.url.otp");
		this.iotpParams.put("servioticy.url.otp", otpApiCall);
		this.iotpParams.put("servioticy.private.url", servioticyPrivateUrl);

	}
	public EncodedSenderOTPData generateOTPdataForUser(Collection<IPrincipal> principals)
			throws IdManagementException {
			
			byte[] key = iotp.service.otp.Cipher.generateKey(256);
			
			ComposeUserPrincipal user = authentication.getComposeUser(principals);
			User u = userRepository.getOne(user.getOpenId().getUser_id());
			UserResponseMessage resp = new UserResponseMessage(u);
			GenericSenderOTPData data = new GenericSenderOTPData();
			data.setMaxHeight(-1);
			data.setSecretDerivationType(SecretDerivatorFactory.RIGHT_BIT_SHIFT_IDENTITY);
			data.setMaxWidth(16);
			data.setFactorOfSecretDerivation(4);
			data.setIndex(0);
			data.setDepth(0);
			data.setRootSecret(key);
			EncodedUser eu = GenerateEncodedUserInfo(resp);
			data.setInfo(eu);
			
			try
			{
				DataReceiver dr = new DataReceiver(StorageProviderFactory.PROVIDER_SERVIOTICY, iotpParams);
				dr.addNewOTPSenderData(data.getRootSecret(), data);// this stores the next OTP in servioticy updated automattically....
				LOG.info("generating otp data for user: "+u.getId());
				
				
				//small test locally
				
				/*GenericSenderOTPData data2 = new GenericSenderOTPData();
				data2.setInfo(eu);
				data2.setMaxHeight(-1);
				data2.setMaxWidth(16);
				data2.setFactorOfSecretDerivation(8);
				data2.setIndex(0);
				data2.setDepth(0);
				data2.setRootSecret(key);
				DataSender ds = new DataSender(data2);
				int i = 0;
				while(i++<40){
					byte [] r = ds.encryptMessage(Utils.binaryFromUTF8String("hello world"));
					dr = new DataReceiver(StorageProviderFactory.PROVIDER_SERVIOTICY,iotpParams);
					dr.decryptMessage(r);
				}
				*/
				// end of test
				
				
				return new EncodedSenderOTPData(data);
			
			} catch (IOTPException e)
			{
				throw new IdManagementException("Unexpected exception generating the root key for encryption:"+e.getMessage(),null, LOG,"Unexpected exception generating the root key for encryption:"+e.getDebugMessage(),Level.ERROR, e.getHttpCode());
			
			}
				
	}


	public void deleteOTPForUser(Collection<IPrincipal> principals, String key) throws IdManagementException{
		
		try
		{
			RESTApiStorage<GenericSenderOTPData> storage = new RESTApiStorage<GenericSenderOTPData>();
			GenericSenderOTPData data = storage.readObjectFromKey(key);
			if(data == null)
				throw new IdManagementException("key "+key+" not found ",null, LOG,"key not found :"+key,Level.DEBUG, 404);
			if(data.getInfo().getId().equals(authentication.getComposeUser(principals).getOpenId().getUser_id()))
				storage.deleteObjectFromKey(key);
			else 
				throw new IdManagementException("You cannot delete this key, for it belongs to someone else:",null, LOG,"User with id "+authentication.getComposeUser(principals).getOpenId().getUser_id()+" attempted to delete key:"+key+" which belongs to user with id: "+data.getInfo().getId(),Level.INFO, 403);
		} catch (IOTPException e)
		{
			throw new IdManagementException("Unexpected exception generatic storage provider for OTPS in IDM encryption:",e, LOG,"Unexpected exception generatic storage provider for OTPS in IDM encryption:"+e.getMessage(),Level.ERROR, 500);

		}
		
		
	}

	private EncodedUser GenerateEncodedUserInfo(UserResponseMessage resp)
	{
		EncodedUser u = new EncodedUser();
		u.setId(resp.getId());
		u.setLastModified(resp.getLastModified());
		u.setRandom_auth_token(resp.getRandom_auth_token());
		u.setUsername(resp.getUsername());
		List<AttributeValueResponseMessage> att = resp.getApprovedAttributes();
		List<EncodedAttributeValue> newvals = new LinkedList<EncodedAttributeValue>();
		for(AttributeValueResponseMessage a: att){
			EncodedAttributeValue v = new EncodedAttributeValue();
			v.setApproved(a.isApproved());
			v.setAttribute_definition_id(a.getAttribute_definition_id());
			v.setEntity_id(a.getEntity_id());
			v.setEntity_type(a.getEntity_type());
			v.setId(a.getId());
			v.setOwner_id(a.getOwner_id());
			v.setValue(a.getValue());
			v.setLastModified(a.getLastModified());
			v.setGroup_id(a.getGroup_id());
			newvals.add(v);
		}
		u.setApprovedAttributes(newvals);

		List<MembershipResponseMessage> mem = resp.getApprovedMemberships();
		List<EncodedMembership> memberships= new LinkedList<EncodedMembership>();
		for(MembershipResponseMessage m: mem){
			EncodedMembership newMem = new EncodedMembership();
			newMem.setGroup_id(m.getGroup_id());
			newMem.setGroup_name(m.getGroup_name());
			newMem.setId(m.getId());
			newMem.setLastModified(m.getLastModified());
			newMem.setRole(m.getRole());
			newMem.setUser_id(m.getUser_id());
			newMem.setUser_name(m.getUser_name());
			memberships.add(newMem);			
		}
		u.setApprovedMemberships(memberships);
		return u;
	}
	
	

	
	
	
	
	
}
