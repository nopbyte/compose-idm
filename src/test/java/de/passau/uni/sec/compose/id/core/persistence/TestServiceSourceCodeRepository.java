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
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceSourceCode;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.EntityGroupMembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceSourceCodeRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;

public class TestServiceSourceCodeRepository extends IntegrationTestBase {

    @Qualifier("serviceSourceCodeRepository")
    @Autowired
    ServiceSourceCodeRepository serviceSourceCodeRepository;

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
    public void testSourceCode() {

        ServiceSourceCode sc = new ServiceSourceCode();
        Group g = groupRepository.getOne(group_id);

        Collection<Group> gs = new LinkedList<>();
        gs.add(g);
        List<EntityGroupMembership> ms = new LinkedList<>();
        entityGroupMembershipRepository.saveAndFlush(memb);
        ms.add(memb);
      
        sc.setGroups(ms);
        sc.setDeveloper(userRepository.getOne(user_id));
        sc.setId(UUID.randomUUID().toString());
        sc.setLastModified(new Date(System.currentTimeMillis()));
        sc.setReputation(2);
        sc.setPayment(false);
        sc.setName("Name of my source");
        sc.setVersion("1.o0");
        sc.setVisible(false);

        serviceSourceCodeRepository.saveAndFlush(sc);

        assertEquals(sc, serviceSourceCodeRepository.getOne(sc.getId()));

    }


}
