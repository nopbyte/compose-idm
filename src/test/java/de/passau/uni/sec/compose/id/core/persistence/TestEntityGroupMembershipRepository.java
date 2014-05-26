package de.passau.uni.sec.compose.id.core.persistence;


import de.passau.uni.sec.compose.id.core.persistence.entities.*;
import de.passau.uni.sec.compose.id.core.persistence.repository.ApplicationRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.EntityGroupMembershipRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.RoleRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;


import org.junit.After;
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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestEntityGroupMembershipRepository extends IntegrationTestBase {

    @Qualifier("entityGroupMembershipRepository")
    @Autowired
    EntityGroupMembershipRepository entityGroupMembershipRepository;

    @Qualifier("userRepository")
    @Autowired
    UserRepository userRepository;

    @Qualifier("groupRepository")
    @Autowired
    GroupRepository groupRepository;

    @Qualifier("roleRepository")
    @Autowired
    RoleRepository roleRepository;

    @Qualifier("applicationRepository")
    @Autowired
    ApplicationRepository applicationRepository;
   
     private User user;
     
     private Group group;
     
     private EntityGroupMembership appGroup1; 
    
     private EntityGroupMembership appGroup2; 
     
     private EntityGroupMembership appGroup3; 
     
     private Application app;
     
    @Before
    public void createUserAndGroup() {
        User user = new User();
        String user_id = UUID.randomUUID().toString();
        user.setId(user_id);
        userRepository.saveAndFlush(user);

        Group group = new Group();
        group.setOwner(user);
        String group_id = UUID.randomUUID().toString();
        group.setLastModified(new Date(System.currentTimeMillis()));
        group.setId(group_id);
        group.setName("new group");
        groupRepository.saveAndFlush(group);
        
        appGroup1 = new EntityGroupMembership();
        appGroup1.setGroup(group);
        appGroup1.setId(UUID.randomUUID().toString());
        appGroup1.setApplication(app);
        entityGroupMembershipRepository.saveAndFlush(appGroup1);
        
        appGroup2 = new EntityGroupMembership();
        appGroup2.setGroup(group);
        appGroup2.setId(UUID.randomUUID().toString());
        appGroup2.setApplication(app);
        entityGroupMembershipRepository.saveAndFlush(appGroup2);
        
        appGroup3 = new EntityGroupMembership();
        appGroup3.setGroup(group);
        appGroup3.setId(UUID.randomUUID().toString());
        appGroup3.setApplication(app);
        entityGroupMembershipRepository.saveAndFlush(appGroup3);
        
        app= new Application();
        app.setId(UUID.randomUUID().toString());
        app.setName("my app");
        app.setOwner(user);
        app.setReputation(3);
        
        List<EntityGroupMembership> memb = new LinkedList<>();
        memb.add(appGroup1);
        memb.add(appGroup2);
        memb.add(appGroup3);
        
        app.setGroups(memb);
        applicationRepository.saveAndFlush(app);
        
        
    }
    
    @Test
    public void getAll()
    {
    	appGroup1.setApprovedBySelfOwner(true);
    	appGroup1.setApprovedByGroupOwner(false);
    	entityGroupMembershipRepository.saveAndFlush(appGroup1);
    	
    	
    	appGroup2.setApprovedBySelfOwner(false);
    	appGroup2.setApprovedByGroupOwner(true);
    	entityGroupMembershipRepository.saveAndFlush(appGroup2);
    	
    	appGroup3.setApprovedBySelfOwner(true);
    	appGroup3.setApprovedByGroupOwner(true);
    	entityGroupMembershipRepository.saveAndFlush(appGroup3);
    	assertTrue(entityGroupMembershipRepository.findAll().size()>0);
    }
    @Test
    public void testGetApplicationByApprovalStateAndUser()
    {
    	boolean b1,b2,b3,b4;
    	
    	appGroup1.setApprovedBySelfOwner(true);
    	appGroup1.setApprovedByGroupOwner(false);
    	entityGroupMembershipRepository.saveAndFlush(appGroup1);
    	
    	
    	appGroup2.setApprovedBySelfOwner(false);
    	appGroup2.setApprovedByGroupOwner(true);
    	entityGroupMembershipRepository.saveAndFlush(appGroup2);
    	
    	appGroup3.setApprovedBySelfOwner(true);
    	appGroup3.setApprovedByGroupOwner(true);
    	entityGroupMembershipRepository.saveAndFlush(appGroup3);
    	
    	/*order of booleans selfowner,group owner
    	List<EntityGroupMembership> data = entityGroupMembershipRepository.findByUserAndApprovalState( user, true, false);
    	System.out.println(data);
    	b1=data.size()>0;
    	assertTrue(data.size()==1);
    	assertTrue(data.iterator().next().equals(appGroup1));
    	
    	data = entityGroupMembershipRepository.findByUserAndApprovalState( user, false, true);
    	b2=data.size()>0;
    	System.out.println(data);
    	assertTrue(data.size()==1);
    	assertTrue(data.iterator().next().equals(appGroup2));
    	
    	data = entityGroupMembershipRepository.findByUserAndApprovalState( user, true, true);
    	b3=data.size()>0;
    	
    	assertTrue(data.size()==1);
    	assertTrue(data.iterator().next().equals(appGroup3));
    	
    	data = entityGroupMembershipRepository.findByUserAndApprovalState( user, false, false);
    	b4=data.size()>0;
    	appGroup2.setApprovedBySelfOwner(true);
    	appGroup2.setApprovedByGroupOwner(true);
    	entityGroupMembershipRepository.saveAndFlush(appGroup2);
    	
    	data = entityGroupMembershipRepository.findByUserAndApprovalState( user, true, true);
    	assertTrue(data.size()==2);
    	assertTrue(data.contains(appGroup3));
    	assertTrue(data.contains(appGroup2));
    	System.out.println(data);
    	*/
    	
    	
    	
    	
    }
    
    @After
    public void tearDown()
    {
    	 /*entityGroupMembershipRepository.delete(appGroup1);
    	 entityGroupMembershipRepository.delete(appGroup2);
    	 entityGroupMembershipRepository.delete(appGroup3);
    	 groupRepository.delete(group);
    	 userRepository.delete(user);*/
    	
    }

}
