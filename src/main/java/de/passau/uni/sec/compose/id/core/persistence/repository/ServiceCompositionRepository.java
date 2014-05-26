package de.passau.uni.sec.compose.id.core.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;

@Repository
public interface ServiceCompositionRepository extends JpaRepository<ServiceComposition, String> {
	List<ServiceComposition> findByOwner(User owner);
}
