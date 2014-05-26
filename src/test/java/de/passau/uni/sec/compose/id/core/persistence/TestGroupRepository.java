package de.passau.uni.sec.compose.id.core.persistence;


import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.*;

public class TestGroupRepository extends IntegrationTestBase {

    @Qualifier("groupRepository")
    @Autowired
    GroupRepository groupRepository;

    @Qualifier("userRepository")
    @Autowired
    UserRepository userRepository;

    private String user_id;

    @Before
    public void createUser() {
        User u = new User();
        u.setLastModified(new Date(System.currentTimeMillis()));
        user_id = UUID.randomUUID().toString();
        u.setId(user_id);
        userRepository.saveAndFlush(u);
    }

    /**
     * Tests the user repository by adding a user.
     */
    @Test
    public void testAddGroup() {
        Group g = new Group();
        User u = userRepository.getOne(user_id);
        g.setOwner(u);
        g.setId(UUID.randomUUID().toString());
        g.setLastModified(new Date(System.currentTimeMillis()));
        g.setName("new group");
        groupRepository.saveAndFlush(g);
        Group g1 = groupRepository.getOne(g.getId());
        assertEquals(g, g1);
    }


    @Test
    public void testGetGroup() {
        Group g = new Group();
        g.setId(UUID.randomUUID().toString());
        g.setName("new group");
        g.setLastModified(new Date(System.currentTimeMillis()));
        groupRepository.saveAndFlush(g);

        Group g1 = groupRepository.getOne(g.getId());
        assertEquals(g, g1);
    }

    @Test(expected = javax.persistence.EntityNotFoundException.class)
    public void testGetNotExistentGroup() {
        /*
            javax.persistence.EntityNotFoundException is only thrown
            if the returned value is accessed, NOT during getOne()
         */

        assertNull(groupRepository.getOne(UUID.randomUUID().toString()));
    }

    @Test
    public void testUpdateGroup() {

        Group g = new Group();
        User u = userRepository.getOne(user_id);
        g.setOwner(u);
        g.setId(UUID.randomUUID().toString());
        g.setName("new group");
        g.setLastModified(new Date(System.currentTimeMillis()));
        groupRepository.saveAndFlush(g);
        g = groupRepository.getOne(g.getId());
        g.setName("new group for");
        groupRepository.saveAndFlush(g);
        Group g1 = groupRepository.getOne(g.getId());
        assertEquals(g1.getName(), "new group for");


    }
}
