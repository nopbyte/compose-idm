package de.passau.uni.sec.compose.id.core.persistence.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;

@Repository
public interface ServiceSourceCodeRepository extends JpaRepository<ServiceSourceCode, String> {
	List<ServiceSourceCode> findByDeveloper(User owner);
}
