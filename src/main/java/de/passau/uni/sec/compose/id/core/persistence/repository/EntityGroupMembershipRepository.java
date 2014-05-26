package de.passau.uni.sec.compose.id.core.persistence.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.Membership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Role;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;


@Repository
public interface EntityGroupMembershipRepository extends JpaRepository<EntityGroupMembership, String> {

    List<EntityGroupMembership> findByGroup(Group group);
    
    List<EntityGroupMembership> findByApplicationAndApprovedBySelfOwner(Application app, boolean approvedBySelfOwner);
    
    List<EntityGroupMembership> findByApplicationAndGroup(Application app, Group group);
    
    List<EntityGroupMembership> findByServiceInstanceAndApprovedBySelfOwner(ServiceInstance serviceInstance, boolean approvedBySelfOwner);
    
    List<EntityGroupMembership> findByServiceInstanceAndGroup(ServiceInstance serviceInstance, Group group);
    
    List<EntityGroupMembership> findByServiceObjectAndApprovedBySelfOwner(ServiceObject serviceObject, boolean approvedBySelfOwner);
    
    List<EntityGroupMembership> findByServiceObjectAndGroup(ServiceObject serviceObject, Group group);
    
    List<EntityGroupMembership> findByServiceCompositionAndApprovedBySelfOwner( ServiceComposition serviceComposition, boolean approvedBySelfOwner);
    
    List<EntityGroupMembership> findByServiceCompositionAndGroup( ServiceComposition serviceComposition, Group group);
        
    List<EntityGroupMembership> findByServiceSourceCodeAndApprovedBySelfOwner(ServiceSourceCode serviceSourceCode,boolean approvedBySelfOwner);
    
    List<EntityGroupMembership> findByServiceSourceCodeAndGroup(ServiceSourceCode serviceSourceCode,Group group);
    
    
    
    
}
