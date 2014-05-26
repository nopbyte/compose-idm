package de.passau.uni.sec.compose.id.rest.messages;

import java.util.List;

public class PendingUserMembershipMessage {

	private List<?> approveAsGroupAdminOrOwner;
	
	private  List<?> selfApprovals;
	
	public PendingUserMembershipMessage(List<?> go,List<?> sa) {
		approveAsGroupAdminOrOwner = go;
		selfApprovals = sa;
	}


	public List<?> getApproveAsGroupAdminOrOwner() {
		return approveAsGroupAdminOrOwner;
	}

	public void setApproveAsGroupAdminOrOwner(
			List<?> approveAsGroupAdminOrOwner) {
		this.approveAsGroupAdminOrOwner = approveAsGroupAdminOrOwner;
	}

	public List<?> getSelfApprovals() {
		return selfApprovals;
	}

	public void setSelfApprovals(List<Object> selfApprovals) {
		this.selfApprovals = selfApprovals;
	}
	
	
	
}
