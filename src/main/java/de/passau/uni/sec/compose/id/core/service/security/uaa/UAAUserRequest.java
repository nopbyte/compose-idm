package de.passau.uni.sec.compose.id.core.service.security.uaa;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;


public class UAAUserRequest implements Serializable
{
	private String username;
	private String password;
	private UAAUserRequestName name;
	private List<EmailValue> emails;
	
	
	public UAAUserRequest() {
		
		emails = new LinkedList<>();
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public UAAUserRequestName getName() {
		return name;
	}
	public void setName(UAAUserRequestName name) {
		this.name = name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public List<EmailValue> getEmails() {
		return emails;
	}
	public void setEmails(List<EmailValue> emails) {
		this.emails = emails;
	}
	public void addEmail(String email) {
		EmailValue e = new EmailValue();
		e.setValue(email);
		emails.add(e);
	}
	
	private class EmailValue{
		
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}
}
