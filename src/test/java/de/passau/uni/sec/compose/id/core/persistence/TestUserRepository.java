package de.passau.uni.sec.compose.id.core.persistence;


import de.passau.uni.sec.compose.id.core.persistence.entities.*;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.RoleRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.EntityNotFoundException;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestUserRepository extends IntegrationTestBase {

    @Qualifier("userRepository")
    @Autowired
    UserRepository userRepository;


    @Qualifier("groupRepository")
    @Autowired
    GroupRepository groupRepository;

    @Qualifier("roleRepository")
    @Autowired
    RoleRepository roleRepository;

    /**
     * Tests the user repository by adding a user.
     */
    @Test
    public void testAddUser() {
        User u = new User();
        u.setId(UUID.randomUUID().toString());
        u.setUsername("somenewguy");
        u.setLastModified(new Date(System.currentTimeMillis()));
        userRepository.saveAndFlush(u);
        User res = userRepository.getOne(u.getId());
        assertEquals(res, u);
    }

    @Test
    public void testGetUser() {
        User u = new User();
        u.setId(UUID.randomUUID().toString());
        userRepository.saveAndFlush(u);
        u.setLastModified(new Date(System.currentTimeMillis()));
        User res = userRepository.getOne(u.getId());

        assertEquals(res, u);
    }

    @Test
    public void testUpdateUser() {

        String userId = UUID.randomUUID().toString();
        User u = new User();
        u.setId(userId);
        u.setLastModified(new Date(System.currentTimeMillis()));
        userRepository.saveAndFlush(u);
        User res = userRepository.getOne(u.getId());
        assertEquals(res, u);

        Group g = new Group();
        g.setId(UUID.randomUUID().toString());
        g.setLastModified(new Date(System.currentTimeMillis()));
        g.setName("new group");
        groupRepository.saveAndFlush(g);

        Collection<Group> list = new LinkedList<>();
        list.add(g);
        u.setGroups(list);
        u.setLastModified(new Date(System.currentTimeMillis()));
        userRepository.saveAndFlush(u);

        assertEquals(userRepository.getOne(u.getId()), u);

        User userFromDB = userRepository.getOne(u.getId());

        Collection<Group> groupsFromDB = userFromDB.getGroups();
        assertEquals(g.getId(), groupsFromDB.iterator().next().getId());

        Role r = new Role();
        r.setId(UUID.randomUUID().toString());
        r.setName("ADMIN");
        roleRepository.save(r);
        assertEquals(r, roleRepository.getOne(r.getId()));

        Membership memb = new Membership();
        memb.setId(UUID.randomUUID().toString());
        memb.setGroup(g);
        memb.setRole(r);

        Collection<Membership> memberships = new LinkedList<>();
        memberships.add(memb);
        u.setMemberships(memberships);

        userRepository.saveAndFlush(u);
        assertEquals(memb, userRepository.getOne(u.getId()).getMemberships().iterator().next());
    }

    @Test
    public void testAdditionalAttributes() {
        String user_id = UUID.randomUUID().toString();
        User u = new User();
        u.setId(user_id);
        u.setLastModified(new Date(System.currentTimeMillis()));
        userRepository.saveAndFlush(u);
        User res = userRepository.getOne(u.getId());
        assertEquals(res, u);

        UserAttributes att = new UserAttributes();
        att.setUser(u);
        att.setAuthority("something");
        att.setId(UUID.randomUUID().toString());
        att.setName("some attribute name");
        att.setType("type1");
        att.setValue("some string value");
        att.setVerified(false);
        att.setLastModified(new Date(System.currentTimeMillis()));

        Collection<UserAttributes> list = new LinkedList<>();
        list.add(att);
        u.setUserAttributes(list);
        userRepository.save(u);

        User u2 = userRepository.getOne(u.getId());
        //only one time in the loop...
        for (UserAttributes at : u2.getUserAttributes()) {
            assertEquals(at.getId(), att.getId());
        }
    }

    @Test
    public void testDeleteUser() {
        String user_id = UUID.randomUUID().toString();
        User u = new User();
        u.setId(user_id);
        u.setLastModified(new Date(System.currentTimeMillis()));
        userRepository.saveAndFlush(u);
        User res = userRepository.getOne(u.getId());
        assertEquals(res, u);

        userRepository.delete(u);
        try {
            userRepository.getOne(user_id);
            fail();
        } catch (EntityNotFoundException ex) {
            assertTrue(true);
        }
    }
}
