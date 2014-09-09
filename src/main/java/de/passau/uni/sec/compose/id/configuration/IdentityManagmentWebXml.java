package de.passau.uni.sec.compose.id.configuration;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

import de.passau.uni.sec.compose.id.start.Application;

public class IdentityManagmentWebXml extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        //TODO changed return to application class
        return application.sources(Application.class);
    }

}