package de.passau.uni.sec.compose.id.core.persistence.repository;

import java.util.List;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceInstanceRepository extends JpaRepository<ServiceInstance, String> {
	
	List<ServiceInstance> findByOwner(User owner);
	
}
