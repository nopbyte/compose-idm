package de.passau.uni.sec.compose.id.rest.messages;


import javax.validation.constraints.NotNull;


public class ServiceInstanceCreateMessage 
{
	@NotNull
	private String Authorization;
	
	@NotNull
	private String id;
	
	@NotNull
	private String uri;
	
	@NotNull
	private String source_code_id;
	

	private boolean data_provenance_collection=false;
	
	//null defaults to false
	private boolean payment=false;

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

	public String getUri() {
		return uri;
	}

	public void setUri(String uRI) {
		uri = uRI;
	}

	public String getSource_code_id() {
		return source_code_id;
	}

	public void setSource_code_id(String source_code_id) {
		this.source_code_id = source_code_id;
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
	
	
}
