package de.passau.uni.sec.compose.id.core.domain;

import iotp.model.utils.Utils;
import de.passau.uni.sec.compose.id.rest.messages.UserResponseMessage;


public class IDMEncodedSenderOTPData 
{

	private UserResponseMessage info;
	
	private String rootSecret;
	
	private String oneTimePad;
	
	private String currentOtp;
	
	private int index=0;

	private int depth=0;
	
	private int maxWidth=1;
	
	private int maxHeight=-1;
	
	//default is SHA 256
	private String hashAlgorithm;
	
	private String secretDerivationType;

	//should be at least one always..
	private int factorOfSecretDerivation;
	
	
	public IDMEncodedSenderOTPData(iotp.model.GenericSenderOTPData data, UserResponseMessage idmUserInfo){
		
		this.info = idmUserInfo;
		this.rootSecret = Utils.fromBinaryToHexString(data.getRootSecret());
		if(data.getOneTimePad() != null)
			this.oneTimePad = Utils.fromBinaryToHexString(data.getOneTimePad());
		if(data.getCurrentDerivedSecret()!=null)
			this.currentOtp  = Utils.fromBinaryToHexString(data.getCurrentDerivedSecret());
		this.index = data.getIndex();
		this.depth=data.getDepth();
		this.maxWidth = data.getMaxWidth();
		this.maxHeight=data.getMaxHeight();
		this.hashAlgorithm = data.getHashAlgorithm();
		this.secretDerivationType = data.getSecretDerivationType();
		this.factorOfSecretDerivation = data.getFactorOfSecretDerivation();
	}


	public UserResponseMessage getInfo()
	{
		return info;
	}


	public void setInfo(UserResponseMessage info)
	{
		this.info = info;
	}


	public String getRootSecret()
	{
		return rootSecret;
	}


	public void setRootSecret(String rootSecret)
	{
		this.rootSecret = rootSecret;
	}


	public String getOneTimePad()
	{
		return oneTimePad;
	}


	public void setOneTimePad(String oneTimePad)
	{
		this.oneTimePad = oneTimePad;
	}


	public String getCurrentOtp()
	{
		return currentOtp;
	}


	public void setCurrentOtp(String currentOtp)
	{
		this.currentOtp = currentOtp;
	}


	public int getIndex()
	{
		return index;
	}


	public void setIndex(int index)
	{
		this.index = index;
	}


	public int getDepth()
	{
		return depth;
	}


	public void setDepth(int depth)
	{
		this.depth = depth;
	}


	public int getMaxWidth()
	{
		return maxWidth;
	}


	public void setMaxWidth(int maxWidth)
	{
		this.maxWidth = maxWidth;
	}


	public int getMaxHeight()
	{
		return maxHeight;
	}


	public void setMaxHeight(int maxHeight)
	{
		this.maxHeight = maxHeight;
	}


	public String getHashAlgorithm()
	{
		return hashAlgorithm;
	}


	public void setHashAlgorithm(String hashAlgorithm)
	{
		this.hashAlgorithm = hashAlgorithm;
	}


	public String getSecretDerivationType()
	{
		return secretDerivationType;
	}


	public void setSecretDerivationType(String secretDerivationType)
	{
		this.secretDerivationType = secretDerivationType;
	}


	public int getFactorOfSecretDerivation()
	{
		return factorOfSecretDerivation;
	}


	public void setFactorOfSecretDerivation(int factorOfSecretDerivation)
	{
		this.factorOfSecretDerivation = factorOfSecretDerivation;
	}
	
	

	
			
}
