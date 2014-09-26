package de.passau.uni.sec.compose.id.rest.messages;

import java.util.List;

/**
 * This class is extended by every entity that has group memberships and attributes. Except for Users since their group management is different. (Membership)
 * @author dp
 *
 */
public class AbstractCoreEnityResponse extends AbstractEnityResponse{


	/**
	 * groups that the entity belongs to
	 */
	protected List<EntityGroupMembershipResponseMessage> groups;
	
	protected List<AttributeValueResponseMessage> attributeValues;
	
	public List<EntityGroupMembershipResponseMessage> getGroups() {
		return groups;
	}

	public void setGroups(List<EntityGroupMembershipResponseMessage> groups) {
		this.groups = groups;
	}

	public List<AttributeValueResponseMessage> getAttributeValues() {
		return attributeValues;
	}

	public void setAttributeValues(
			List<AttributeValueResponseMessage> attributeValues) {
		this.attributeValues = attributeValues;
	}
	
	
	
	
}
