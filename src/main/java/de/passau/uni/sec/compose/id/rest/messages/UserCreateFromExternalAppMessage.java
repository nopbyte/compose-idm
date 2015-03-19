package de.passau.uni.sec.compose.id.rest.messages;

import javax.validation.constraints.NotNull;




public class UserCreateFromExternalAppMessage extends UserCreateMessage 
{

	@NotNull
	private String session_id;
	
	@NotNull
	private String captcha_text;
	
	public String getSession_id() {
		return session_id;
	}
	public void setSession_id(String session_id) {
		this.session_id = session_id;
	}
	public String getCaptcha_text() {
		return captcha_text;
	}
	public void setCaptcha_text(String captcha_text) {
		this.captcha_text = captcha_text;
	}
	
	

}
