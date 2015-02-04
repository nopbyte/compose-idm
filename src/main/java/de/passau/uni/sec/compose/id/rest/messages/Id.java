package de.passau.uni.sec.compose.id.rest.messages;
import javax.validation.constraints.NotNull;

public class Id {
	
	@NotNull
	private String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	

}
