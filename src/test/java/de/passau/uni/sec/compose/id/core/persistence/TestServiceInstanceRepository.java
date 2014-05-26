package de.passau.uni.sec.compose.id.core.persistence;


import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.ServiceInstance;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ServiceInstanceRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;

public class TestServiceInstanceRepository extends IntegrationTestBase {

    @Qualifier("serviceInstanceRepository")
    @Autowired
    ServiceInstanceRepository serviceInstanceRepository;

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
        

    }

    /**
     * Tests the user repository by adding a user.
     */
    @Test
    public void testAddServiceInstance() {

        ServiceInstance si= new ServiceInstance();
        Group g = groupRepository.getOne(group_id);
        Collection<EntityGroupMembership> gs = new LinkedList<EntityGroupMembership>();
        memb.setServiceInstance(si);
        gs.add(memb);
        si.setGroups(gs);
        
        si.setOwner(userRepository.getOne(user_id));
        si.setId(UUID.randomUUID().toString());
        si.setLastModified(new Date(System.currentTimeMillis()));
        si.setReputation(2);
        si.setPayment(false);
        //the service source code should be set here!

        serviceInstanceRepository.saveAndFlush(si);

        assertEquals(si, serviceInstanceRepository.getOne(si.getId()));

    }
}
