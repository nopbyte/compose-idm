package de.passau.uni.sec.compose.id.rest.messages;


import javax.validation.constraints.NotNull;


public class ServiceSourceCodeCreateMessage 
{
	@NotNull
	private String id;
	
	@NotNull
	private String name;
	
	@NotNull
	private String version;
	
	//null defaults to false
	private boolean payment = false;
	
	@NotNull
	private String Authorization;
	
	public String getAuthorization() {
		return Authorization;
	}

	public void setAuthorization(String authorization) {
		Authorization = authorization;
	}


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}
	
	
}
