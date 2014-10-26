package de.passau.uni.sec.compose.id.configuration;

import java.io.IOException;
import java.util.Properties;

import javax.annotation.Resource;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.passau.uni.sec.compose.id.core.service.AbstractBasicEntityService;



@Configuration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = "de.passau.uni.sec.compose.id.core.persistence.repository")
@PropertySource("classpath:datasource.properties")
public class DatabaseConfiguration {

    // keys in datasource.properties
    private static final String PROPERTY_NAME_DATABASE_DRIVER = "db.driver";
    private static final String PROPERTY_NAME_DATABASE_URL = "db.url";
    private static final String PROPERTY_NAME_DATABASE_USERNAME = "db.username";
    private static final String PROPERTY_NAME_DATABASE_PASSWORD = "db.password";
    private static final String PROPERTY_NAME_HIBERNATE_DIALECT = "hibernate.dialect";
    private static final String PROPERTY_NAME_HIBERNATE_SHOW_SQL = "hibernate.show_sql";
    private static final String PROPERTY_NAME_HIBERNATE_FORMAT_SQL = "hibernate.format_sql";
    private static final String PROPERTY_NAME_HIBERNATE_HBM2DDL = "hibernate.hbm2ddl.auto";
    private static final String PROPERTY_NAME_ENTITY_MANAGER_PACKAGES_TO_SCAN = "entitymanager.packages_to_scan";

    private String database;
    private String port;
    private String user;
    private String host;
    private String password;
	
    @Resource
    private Environment env;

    private Logger LOG = LoggerFactory.getLogger(DatabaseConfiguration.class);
	
	
    
    @Bean
    public DataSource dataSource() {

        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        
        ObjectMapper mapper = new ObjectMapper();
	    JsonNode root;
		try {
			root = mapper.readTree(System.getenv("VCAP_SERVICES"));
			root.findValue("mysqlShared");
			String database = root.findValue("name").asText();
			String port = root.findValue("port").asText();
			String user = root.findValue("user").asText();
			String host = root.findValue("hostname").asText();
			String password = root.findValue("password").asText();
			
			
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
        /*dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        dataSource.setUrl(env.getRequiredProperty(PROPERTY_NAME_DATABASE_URL));
        dataSource.setUsername(env.getRequiredProperty(PROPERTY_NAME_DATABASE_USERNAME));
        dataSource.setPassword(env.getRequiredProperty(PROPERTY_NAME_DATABASE_PASSWORD));
         */
		dataSource.setDriverClassName(env.getRequiredProperty(PROPERTY_NAME_DATABASE_DRIVER));
        //jdbc:mysql://localhost:3306/composeidentity2
		String url = "jdbc:mysql://"+host+":"+port+"/"+database;
		dataSource.setUrl(url);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        LOG.error("NOT ERROR: this is the url used for the database connectoin: "+url+". This is the username:"+user+" this is the pass:"+password);
        
        
         
        return dataSource;
    }

    @Bean
    public JpaVendorAdapter jpaVendorAdapter() {
        HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
        jpaVendorAdapter.setDatabase(Database.MYSQL);

        return jpaVendorAdapter;
    }

    @Bean
    public EntityManagerFactory entityManagerFactory() {

        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();
        factoryBean.setDataSource(dataSource());
        factoryBean.setJpaDialect(new HibernateJpaDialect());
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter());
        factoryBean.setPersistenceUnitName("persistenceUnit");
        factoryBean.setJpaProperties(jpaProperties());
        factoryBean.setPackagesToScan(env.getRequiredProperty(PROPERTY_NAME_ENTITY_MANAGER_PACKAGES_TO_SCAN));
        factoryBean.afterPropertiesSet();

        return factoryBean.getObject();
    }

    private Properties jpaProperties() {

        Properties props = new Properties();
        props.setProperty(PROPERTY_NAME_HIBERNATE_DIALECT, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_DIALECT));
        props.setProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_SHOW_SQL));
        props.setProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_FORMAT_SQL));
        props.setProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL, env.getRequiredProperty(PROPERTY_NAME_HIBERNATE_HBM2DDL));
        return props;
    }

    @Bean
    public PlatformTransactionManager transactionManager() {
        JpaTransactionManager jpaTransactionManager = new JpaTransactionManager();
        jpaTransactionManager.setEntityManagerFactory(entityManagerFactory());
        return jpaTransactionManager;
    }

    @Bean
    public HibernateExceptionTranslator hibernateExceptionTranslator(){
        return new HibernateExceptionTranslator();
    }
}
