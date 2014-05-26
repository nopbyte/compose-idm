package de.passau.uni.sec.compose.id.core.event;

public abstract class AbstractGetEvent extends AbstractEvent{

	protected String id;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
	
}
