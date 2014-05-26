package de.passau.uni.sec.compose.id.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;




import de.passau.uni.sec.compose.id.core.service.security.UsersAuthzAndAuthClient;
import de.passau.uni.sec.compose.id.core.service.security.uaa.UAAClient;

@Configuration
@ComponentScan(basePackages={"de.passau.uni.sec.compose.id.core.service","de.passau.uni.sec.compose.id.core.service.security","de.passau.uni.sec.compose.id.core.service.reputation"})
public class CoreConfiguration
{
	
	//@Bean
	public UsersAuthzAndAuthClient getUAA()
	{
		return new UAAClient();
	}


}
