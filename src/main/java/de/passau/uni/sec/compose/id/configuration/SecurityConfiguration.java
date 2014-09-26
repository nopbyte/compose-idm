package de.passau.uni.sec.compose.id.configuration;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.DigestAuthenticationFilter;

@Configuration
@EnableWebSecurity
@PropertySource("classpath:authentication.properties")
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

	
	private static Logger LOG = LoggerFactory.getLogger(SecurityConfiguration.class);
	
	private static String REALMNAME = "COMPOSE Digest Idm Realm name";
	
    @Autowired
    private Environment env;
    
    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth)
            throws Exception {
    	
    	//auth.inMemoryAuthentication();
        auth.inMemoryAuthentication().withUser(env.getProperty("userName"))
                .password(env.getProperty("password")).roles(env.getProperty("role"));
    }
        
    
    
    @Override
    protected void configure (HttpSecurity http) throws Exception
    {
      http
      	  .csrf().disable()
          .exceptionHandling()
              // this entry point handles when you request a protected page and
              // you are not yet authenticated
              .authenticationEntryPoint(digestEntryPoint())
              .and()
          .authorizeRequests()
           	  //.antMatchers("/**").permitAll() //don't panic here... the RestAuthentication class will take care of authentication of users.
              //.antMatchers("/firstres/*").permitAll()
              .antMatchers(HttpMethod.POST,"/idm/user/").authenticated() //authenticate when creating a user
              .antMatchers(HttpMethod.POST,"/idm/application/").authenticated() //create service source code
              .antMatchers(HttpMethod.POST,"/idm/servicecomposition/").authenticated() //create service source code
              .antMatchers(HttpMethod.POST,"/idm/serviceinstance/").authenticated() //create service instance
              .antMatchers(HttpMethod.POST,"/idm/serviceobject/").authenticated() //create service object
              .antMatchers(HttpMethod.POST,"/idm/servicesourcecode/").authenticated() //create service source code
              .antMatchers(HttpMethod.DELETE,"/idm/user/*").authenticated()// also when deleting a user
              .antMatchers(HttpMethod.DELETE,"/idm/application/*").authenticated() //create service source code
              .antMatchers(HttpMethod.DELETE,"/idm/servicecomposition/*").authenticated() //create service source code
              .antMatchers(HttpMethod.DELETE,"/idm/serviceinstance/*").authenticated() //create service instance
              .antMatchers(HttpMethod.DELETE,"/idm/serviceobject/*").authenticated() //create service object
              .antMatchers(HttpMethod.DELETE,"/idm/servicesourcecode/*").authenticated() //create service source code
              .antMatchers(HttpMethod.GET,"/idm/serviceobject/api_token_data/*").authenticated() //getting api_token from SO as component
              
              .antMatchers("/**").permitAll()
          // the entry point on digest filter is used for failed authentication attempts
          .and()
          .addFilter(digestAuthenticationFilter(digestEntryPoint()));
    }

    @Override
    @Bean
    public UserDetailsService userDetailsServiceBean() throws Exception {
        return super.userDetailsServiceBean();
    	//return new InMemoryUserDetailsManager(loadUsers());
    }

    public DigestAuthenticationFilter digestAuthenticationFilter (
        DigestAuthenticationEntryPoint digestAuthenticationEntryPoint) throws Exception
    {
      DigestAuthenticationFilter digestAuthenticationFilter = new DigestAuthenticationFilter();
      digestAuthenticationFilter.setAuthenticationEntryPoint(digestEntryPoint());
      digestAuthenticationFilter.setUserDetailsService(userDetailsServiceBean());
      return digestAuthenticationFilter;
    }
    
    @Bean
	  public BasicAuthenticationEntryPoint entryPoint ()
	  {

	    BasicAuthenticationEntryPoint basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
	    basicAuthenticationEntryPoint.setRealmName("Basic WF Realm");
	    
	    return basicAuthenticationEntryPoint;
	  }

	  @Bean
	  public DigestAuthenticationEntryPoint digestEntryPoint ()
	  {
	    //DigestAuthenticationEntryPoint digestAuthenticationEntryPoint = new DigestAuthenticationEntryPoint();
		DigestAuthenticationEntryPoint digestAuthenticationEntryPoint = new CustomDigestAuthenticationEntryPoint();
	    digestAuthenticationEntryPoint.setKey(env.getProperty("digest.key"));
	    digestAuthenticationEntryPoint.setRealmName(env.getProperty("digest.realm"));
	    digestAuthenticationEntryPoint.setNonceValiditySeconds(3);
	    return digestAuthenticationEntryPoint;
	  }

	  public Properties loadUsers()
	  {
		  
		    Properties prop = new Properties();
		    String entit = env.getProperty("compose.entities");
			if(entit != null)
			{
				String entities[] = entit.split(",");
				for(String entity: entities)
				{
					entity = entity.trim();
					//get username and password for the entity
					String credentialsProp = env.getProperty(entity);
					if(credentialsProp!= null)
					{
						//if(fields.length<2)
						//	LOG.warn("Entity "+entity+" doesn't have enough fields in the authentication.properties file");
						//else 
						prop.put(entity, credentialsProp);
						LOG.info("Loading entity:"+entity+" pass: "+credentialsProp);
					}
					else
						LOG.warn("There is no username and passowrd for entity: "+entity+" in the autnentication.properties file");
				}
			}
			return prop;
	  }
	
  
}
