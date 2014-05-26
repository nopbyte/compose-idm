package de.passau.uni.sec.compose.id.core.persistence;


import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceComposition;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceObject;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.EntityGroupMembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceCompositionRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceObjectRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;

public class TestServiceCompositionRepository extends IntegrationTestBase {

    @Qualifier("serviceCompositionRepository")
    @Autowired
    ServiceCompositionRepository serviceCompositionRepository;
    
    @Qualifier("entityGroupMembershipRepository")
    @Autowired
    EntityGroupMembershipRepository entityGroupMembershipRepository ;

    @Qualifier("userRepository")
    @Autowired
    UserRepository userRepository;

    @Qualifier("groupRepository")
    @Autowired
    GroupRepository groupRepository;

    private String user_id;

    private String group_id;
    
    private EntityGroupMembership memb; 

    @Before
    public void createUserAndGroup() {
        User u = new User();
        user_id = UUID.randomUUID().toString();
        u.setId(user_id);
        u.setLastModified(new Date(System.currentTimeMillis()));
        userRepository.saveAndFlush(u);

        Group g = new Group();
        g.setOwner(u);
        group_id = UUID.randomUUID().toString();
        g.setLastModified(new Date(System.currentTimeMillis()));
        g.setId(group_id);
        g.setName("new group");
        groupRepository.saveAndFlush(g);
        
        memb = new EntityGroupMembership();
        memb.setGroup(g);
        memb.setId(UUID.randomUUID().toString());
        memb.setGroup(g);
    }

    /**
     * Tests the user repository by adding a user.
     */
    @Test
    public void testAddServiceComposition() {

        ServiceComposition scomp = new ServiceComposition();
        Group g = groupRepository.getOne(group_id);
        List<EntityGroupMembership> ms = new LinkedList<>();
        entityGroupMembershipRepository.saveAndFlush(memb);
        ms.add(memb);
        scomp.setGroups(ms);
        scomp.setOwner(userRepository.getOne(user_id));
        scomp.setId(UUID.randomUUID().toString());
        scomp.setLastModified(new Date(System.currentTimeMillis()));
        serviceCompositionRepository.save(scomp);
        assertEquals(scomp, serviceCompositionRepository.getOne(scomp.getId()));

    }


}
