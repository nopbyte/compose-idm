package de.passau.uni.sec.compose.id.rest.messages;

//com.sun.istack.internal.NotNull  ??
import javax.validation.constraints.NotNull;


public class ServiceObjectCreateMessage 
{
	@NotNull
	private String Authorization;
	
	@NotNull
	private String id;
	
	private boolean requires_token=true;
	
	@NotNull
	private boolean data_provenance_collection;
	
	@NotNull
	private boolean payment;

	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isData_provenance_collection() {
		return data_provenance_collection;
	}

	public void setData_provenance_collection(boolean data_provenance_collection) {
		this.data_provenance_collection = data_provenance_collection;
	}

	public boolean isPayment() {
		return payment;
	}

	public void setPayment(boolean payment) {
		this.payment = payment;
	}

	public String getAuthorization() {
		return Authorization;
	}

	public void setAuthorization(String authorization) {
		Authorization = authorization;
	}

	public boolean isRequires_token() {
		return requires_token;
	}

	public void setRequires_token(boolean requires_token) {
		this.requires_token = requires_token;
	}

	
}
