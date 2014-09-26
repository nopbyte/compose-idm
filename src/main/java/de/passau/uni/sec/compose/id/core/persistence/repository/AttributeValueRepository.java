package de.passau.uni.sec.compose.id.core.persistence.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.AttributeDefinition;
import de.passau.uni.sec.compose.id.core.persistence.entities.AttributeValue;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;

@Repository
public interface AttributeValueRepository extends JpaRepository<AttributeValue, String> {

	 List<AttributeValue> findByDefinitionAndUser(AttributeDefinition definition, User user);

	 List<AttributeValue> findByDefinitionAndApplication(AttributeDefinition definition, Application application);
	 
	 List<AttributeValue> findByDefinitionAndServiceInstance(AttributeDefinition definition, ServiceInstance serviceInstance);
	 
	 List<AttributeValue> findByDefinitionAndServiceObject(AttributeDefinition definition, ServiceObject serviceObject);
	 
	 List<AttributeValue> findByDefinitionAndServiceComposition(AttributeDefinition definition, ServiceComposition serviceComposition);
	 
	 List<AttributeValue> findByDefinitionAndServiceSourceCode(AttributeDefinition definition, ServiceSourceCode serviceSourceCode);
		 
	 List<AttributeValue> findByDefinitionAndApproved(AttributeDefinition definition,boolean approved);
}
