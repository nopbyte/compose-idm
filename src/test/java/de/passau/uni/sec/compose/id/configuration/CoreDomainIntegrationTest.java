package de.passau.uni.sec.compose.id.configuration;


import static junit.framework.TestCase.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.passau.uni.sec.compose.id.core.persistence.TestConfig;
import de.passau.uni.sec.compose.id.core.service.GroupService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {CoreConfiguration.class, TestConfig.class})
public class CoreDomainIntegrationTest {

	@Autowired
	GroupService service;
    

    
  //This is to check that the wiring in Configurations work.
  @Test
  public void testUAAConfiguration() {


      assertNotNull(service);
      

  }
}
