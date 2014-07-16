package de.passau.uni.sec.compose.id.rest.messages;

import java.util.List;

public class UsersInGroupMessage {

	private List<?> approvedMemberships;

	public List<?> getApprovedMemberships() {
		return approvedMemberships;
	}

	public void setApprovedMemberships(List<?> approvedMemberships) {
		this.approvedMemberships = approvedMemberships;
	}
	
}
