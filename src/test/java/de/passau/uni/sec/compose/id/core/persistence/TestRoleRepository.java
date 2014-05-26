package de.passau.uni.sec.compose.id.core.persistence;

import de.passau.uni.sec.compose.id.core.persistence.entities.Role;
import de.passau.uni.sec.compose.id.core.persistence.repository.RoleRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import javax.persistence.PersistenceException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class TestRoleRepository extends IntegrationTestBase {

    @Qualifier("roleRepository")
    @Autowired
    RoleRepository roleRepository;

    /**
     * Tests the role repository by adding a role.
     */
    @Test
    public void testAddRole() {
        Role role = new Role();
        role.setId(UUID.randomUUID().toString());
        role.setName("first Role");

        roleRepository.saveAndFlush(role);
        roleRepository.flush();

        assertEquals(role, roleRepository.getOne(role.getId()));
    }

    /**
     * Tests if the NotNull-constraint is correctly validated.
     */
    @Test(expected = javax.persistence.PersistenceException.class)
    public void testNotNullValidation() {
        Role role = new Role();
        role.setId(UUID.randomUUID().toString());

        roleRepository.saveAndFlush(role);
    }

    @Test
    public void testRoleNameMustNotBeNull() {
        Role role = new Role();
        role.setId(UUID.randomUUID().toString());

        String message = "";

        try {
            roleRepository.saveAndFlush(role);
        } catch (PersistenceException ex) {
            message = ex.getCause().getCause().getMessage();
        }

        assertEquals("Column 'name' cannot be null", message);
    }
}
