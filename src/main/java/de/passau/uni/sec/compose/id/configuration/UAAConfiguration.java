package de.passau.uni.sec.compose.id.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages={"de.passau.uni.sec.compose.id.core.service.security.uaa"})
public class UAAConfiguration
{
	
	//@Bean
	/*public UsersAuthzAndAuthClient getUAA()
	{
		return new UAAClient();
	}*/


}
