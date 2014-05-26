package de.passau.uni.sec.compose.id.core.persistence;

import de.passau.uni.sec.compose.id.core.persistence.entities.Group;
import de.passau.uni.sec.compose.id.core.persistence.entities.User;
import de.passau.uni.sec.compose.id.core.persistence.repository.GroupRepository;
import de.passau.uni.sec.compose.id.core.persistence.repository.UserRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertTrue;

public class TestUserAndGroup extends IntegrationTestBase {

    @Autowired
    @Qualifier("userRepository")
    UserRepository userRepository;

    @Autowired
    @Qualifier("groupRepository")
    GroupRepository groupRepository;


    User user;
    String userId = UUID.randomUUID().toString();

    Collection<Group> groups;

    Group group1;
    String group1Id = UUID.randomUUID().toString();
    String group1Name = "group 1";

    Group group2;
    String group2Id = UUID.randomUUID().toString();
    String group2Name = "group 2";


    @Before
    public void createUserAndGroup() {
        user = new User();
        user.setId(userId);
        user.setLastModified(new Date());

        group1 = new Group();
        group1.setId(group1Id);
        group1.setName(group1Name);

        group2 = new Group();
        group2.setId(group2Id);
        group2.setName(group2Name);

        userRepository.save(user);

        groupRepository.save(group1);
        groupRepository.save(group2);

        userRepository.flush();
        groupRepository.flush();
    }

    @Test
    public void testAddGroupsToUser() {
        User fromDB = userRepository.getOne(userId);

        Assert.assertEquals(user, fromDB);

        fromDB.getGroups().add(group1);
        fromDB.getGroups().add(group2);

        fromDB.setLastModified(new Date());

        userRepository.saveAndFlush(fromDB);


        fromDB = userRepository.getOne(userId);

        assertTrue(fromDB.getGroups().size() == 2);
        assertTrue(fromDB.getGroups().contains(group1));
        assertTrue(fromDB.getGroups().contains(group2));
    }
}
