package de.passau.uni.sec.compose.id.core.persistence.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import de.passau.uni.sec.compose.id.core.persistence.entities.AttributeDefinition;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;

@Repository
public interface AttributeDefinitionRepository extends JpaRepository<AttributeDefinition, String> {

	List<AttributeDefinition> findByNameAndGroup(String name, Group group);
	
	List<AttributeDefinition> findByGroup(Group group);
}
