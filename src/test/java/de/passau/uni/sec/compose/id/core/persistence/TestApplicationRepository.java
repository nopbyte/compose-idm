package de.passau.uni.sec.compose.id.core.persistence;


import de.passau.uni.sec.compose.id.core.persistence.entities.Application;
import de.passau.uni.sec.compose.id.core.persistence.entities.ApplicationAttributes;
import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.EntityGroupMembership;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.EntityGroupMembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

public class TestApplicationRepository extends IntegrationTestBase {

    @Qualifier("applicationRepository")
    @Autowired
    ApplicationRepository applicationRepository;

    @Qualifier("userRepository")
    @Autowired
    UserRepository userRepository;

    @Qualifier("groupRepository")
    @Autowired
    GroupRepository groupRepository;

    @Qualifier("entityGroupMembershipRepository")
    @Autowired
    EntityGroupMembershipRepository entityGroupMembershipRepository ;
    
    private String user_id;
    
    private String user2_id;

    private String group_id;
    
    EntityGroupMembership appGroup;

    @Before
    public void createUserAndGroup() {
        User u = new User();
        user_id = UUID.randomUUID().toString();
        u.setId(user_id);
        userRepository.saveAndFlush(u);
        
        User u2 = new User();
        user2_id = UUID.randomUUID().toString();
        u2.setId(user2_id);
        u2.setUsername("a");
        u2.setReputation(2);
        userRepository.saveAndFlush(u2);

        Group g = new Group();
        g.setOwner(u);
        group_id = UUID.randomUUID().toString();
        g.setLastModified(new Date(System.currentTimeMillis()));
        g.setId(group_id);
        g.setName("new group");
        groupRepository.saveAndFlush(g);
        
        appGroup = new EntityGroupMembership();
        appGroup.setGroup(g);
        appGroup.setId(UUID.randomUUID().toString());
        entityGroupMembershipRepository.saveAndFlush(appGroup);
    }

    /**
     * Tests the user repository by adding a user.
     */
    @Test
    public void testAddApplication() {

        Application app = new Application();
        app.setId(UUID.randomUUID().toString());
        applicationRepository.saveAndFlush(app);
        
        Group g = groupRepository.getOne(group_id);
        appGroup.setGroup(g);
        appGroup.setApplication(app);
        appGroup.setApprovedByGroupOwner(true);
        appGroup.setApprovedBySelfOwner(true);
        entityGroupMembershipRepository.saveAndFlush(appGroup);
        
        Collection<EntityGroupMembership> gs = new LinkedList<EntityGroupMembership>();
        gs.add(appGroup);
        app.setGroups(gs);
        app.setOwner(userRepository.getOne(user_id));
        app.setId(UUID.randomUUID().toString());
        app.setLastModified(new Date(System.currentTimeMillis()));
        app.setName("application name");
        applicationRepository.save(app);
        assertEquals(app, applicationRepository.getOne(app.getId()));
   }

    /**
     * Tests the user repository by adding a user.
     */
    @Test
    public void testFindApplicationByOwner() {

        Application app = new Application();
        app.setId(UUID.randomUUID().toString());
//        applicationRepository.saveAndFlush(app);
        app.setOwner(userRepository.getOne(user_id));
        app.setId(UUID.randomUUID().toString());
        app.setName("application name1");
        
        
        Application app2 = new Application();
        app2.setId(UUID.randomUUID().toString());
//        applicationRepository.saveAndFlush(app2);
        app2.setOwner(userRepository.getOne(user_id));
        app2.setId(UUID.randomUUID().toString());
        app2.setName("application name2");
        
        
        Application app3 = new Application();
        app3.setId(UUID.randomUUID().toString());
 //       applicationRepository.saveAndFlush(app3);
        app3.setOwner(userRepository.getOne(user2_id));
        app3.setId(UUID.randomUUID().toString());
        app3.setName("application name3");
        
        applicationRepository.saveAndFlush(app);
        applicationRepository.saveAndFlush(app2);
        applicationRepository.saveAndFlush(app3);
        
        List<Application> ret = applicationRepository.findByOwner(userRepository.getOne(user_id));
        assertTrue(ret.contains(app) && ret.contains(app2) && ret.size()==2);
        
        ret = applicationRepository.findByOwner(userRepository.getOne(user2_id));
        assertTrue(ret.contains(app3) && ret.size()==1);
        
        
        
        
   }

    @Test
    public void testGetApplication() {
        Application app = new Application();
        String id = UUID.randomUUID().toString(); 
        app.setId(id);
        applicationRepository.saveAndFlush(app);
        
        Group g = groupRepository.getOne(group_id);
        appGroup.setApplication(app);
        appGroup.setApprovedByGroupOwner(true);
        appGroup.setApprovedBySelfOwner(true);
        entityGroupMembershipRepository.saveAndFlush(appGroup);
        
        Collection<EntityGroupMembership> gs = new LinkedList<EntityGroupMembership>();
        gs.add(appGroup);
        app.setGroups(gs);
        app.setOwner(userRepository.getOne(user_id));
        app.setId(id);
        app.setLastModified(new Date(System.currentTimeMillis()));
        app.setName("application name");
        applicationRepository.save(app);
        assertEquals(app, applicationRepository.getOne(app.getId()));
    }

    @Test(expected = javax.persistence.EntityNotFoundException.class)
    public void testGetApplicationThatNotExists() {
        assertNull(applicationRepository.getOne(UUID.randomUUID().toString()));
    }

    @Test
    public void testUpdateApplication() {

        Application app = new Application();
        String id = UUID.randomUUID().toString();
        app.setId(id);
        applicationRepository.saveAndFlush(app);
        
        Group g = groupRepository.getOne(group_id);
        appGroup.setGroup(g);
        appGroup.setApplication(app);
        appGroup.setApprovedByGroupOwner(true);
        appGroup.setApprovedBySelfOwner(true);
        entityGroupMembershipRepository.saveAndFlush(appGroup);
        
        Collection<EntityGroupMembership> gs = new LinkedList<EntityGroupMembership>();
        gs.add(appGroup);
        app.setGroups(gs);
        app.setOwner(userRepository.getOne(user_id));
        app.setId(id);
        app.setName("application name");
        applicationRepository.save(app);
        assertEquals(app, applicationRepository.getOne(app.getId()));
        Application app2 = applicationRepository.getOne(app.getId());

        app2.setName("new name");
        app2.setLastModified(new Date(System.currentTimeMillis()));
        applicationRepository.save(app2);

        Application app3 = applicationRepository.getOne(app2.getId());
        assertEquals(app3.getLastModified(), app2.getLastModified());
        assertEquals(app3.getName(), app2.getName());

    }

    @Test
    public void testAdditionalAttributes() {
        Application app = new Application();
        String id = UUID.randomUUID().toString();
        app.setId(id);
        applicationRepository.saveAndFlush(app);
        Group g = groupRepository.getOne(group_id);
        appGroup.setApplication(app);
        appGroup.setApprovedByGroupOwner(true);
        appGroup.setApprovedBySelfOwner(true);
        entityGroupMembershipRepository.saveAndFlush(appGroup);
        
        Collection<EntityGroupMembership> gs = new LinkedList<EntityGroupMembership>();
        gs.add(appGroup);
        app.setGroups(gs);
        app.setOwner(userRepository.getOne(user_id));
        app.setId(id);
        app.setLastModified(new Date(System.currentTimeMillis()));
        app.setName("application name");
        applicationRepository.save(app);
        assertEquals(app, applicationRepository.getOne(app.getId()));

        ApplicationAttributes att = new ApplicationAttributes();
        att.setApplication(app);
        att.setAuthority("something");
        att.setId(UUID.randomUUID().toString());
        att.setName("some attribute nane");
        att.setType("type1");
        att.setValue("some string value");
        att.setVerified(false);
        att.setLastModified(new Date(System.currentTimeMillis()));


        Collection<ApplicationAttributes> list = new LinkedList<ApplicationAttributes>();
        list.add(att);
        app.setApplicationAttributes(list);
        applicationRepository.save(app);

        Application app2 = applicationRepository.getOne(app.getId());
        //only one time in the loop...
        for (ApplicationAttributes at : app2.getApplicationAttributes()) {
            assertEquals(at.getId(), att.getId());
        }
    }

    @Test
    public void testDeleteApplication() {
        String id = UUID.randomUUID().toString();
        Application app = new Application();
        app.setId(id);
        applicationRepository.saveAndFlush(app);
        
        Group g = groupRepository.getOne(group_id);
        appGroup.setApplication(app);
        appGroup.setApprovedByGroupOwner(true);
        appGroup.setApprovedBySelfOwner(true);
        entityGroupMembershipRepository.saveAndFlush(appGroup);
        
        Collection<EntityGroupMembership> gs = new LinkedList<EntityGroupMembership>();
        gs.add(appGroup);
        app.setGroups(gs);
        app.setOwner(userRepository.getOne(user_id));
        app.setId(id);
        app.setLastModified(new Date(System.currentTimeMillis()));
        app.setName("application name");
        applicationRepository.save(app);
        assertEquals(app, applicationRepository.getOne(app.getId()));
        applicationRepository.delete(app);
        try {

            applicationRepository.getOne(id);
            fail();
        } catch (EntityNotFoundException ex) {
            assertEquals(true, true);
        }
    }
}
