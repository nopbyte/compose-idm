package de.passau.uni.sec.compose.id.configuration;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;




import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAClient;

@Configuration
@ComponentScan(basePackages={"de.passau.uni.sec.compose.id.core.service.security.uaa"})
public class UAAConfiguration
{
	
	//@Bean
	public UsersAuthzAndAuthClient getUAA()
	{
		return new UAAClient();
	}


}
