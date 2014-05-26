package de.passau.uni.sec.compose.id.core.persistence.repository;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstanceAttributes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceInstanceAttributesRepository extends JpaRepository<ServiceInstanceAttributes, String> {
}
