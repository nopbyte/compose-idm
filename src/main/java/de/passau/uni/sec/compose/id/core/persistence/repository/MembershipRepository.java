package de.passau.uni.sec.compose.id.core.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Role;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;


@Repository
public interface MembershipRepository extends JpaRepository<Membership, String> {

    List<Membership> findByGroup(Group group);
    
    @Query("FROM Membership m where m.group = ?1 and m.role = ?2 and m.user = ?3")
    List<Membership> findByGroupRoleAndUser(Group group,Role role, User user);
    
    
}
