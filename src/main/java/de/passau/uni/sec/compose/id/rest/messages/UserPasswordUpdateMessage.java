package de.passau.uni.sec.compose.id.rest.messages;

//com.sun.istack.internal.NotNull  ??
import javax.validation.constraints.NotNull;


public class UserPasswordUpdateMessage 
{
	@NotNull
	private String old_password;
	
	@NotNull
	private String new_password;
	
	public String getOld_password() {
		return old_password;
	}

	public void setOld_password(String old_password) {
		this.old_password = old_password;
	}

	public String getNew_password() {
		return new_password;
	}

	public void setNew_password(String new_password) {
		this.new_password = new_password;
	}

	
	
	
}
