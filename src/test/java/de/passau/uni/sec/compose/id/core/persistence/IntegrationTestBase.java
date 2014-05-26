package de.passau.uni.sec.compose.id.core.persistence;

import org.junit.After;
import org.junit.Before;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

@ContextConfiguration(classes = {TestConfig.class})
public abstract class IntegrationTestBase extends AbstractTransactionalJUnit4SpringContextTests {

    @Before
    public void beforeEachTest() {
    }

    @After
    public void afterEachTest() {
    }
}
