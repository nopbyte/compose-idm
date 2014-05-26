package de.passau.uni.sec.compose.id.core.persistence.repository;

import de.passau.uni.sec.compose.id.core.persistence.entities.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {
}
