package de.passau.uni.sec.compose.id.configuration;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

/**
 * This class replaces the XML configuration of the web application.
 */
public class WebAppInitializer extends
        AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[] {  };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] {  };
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] { "/" };
    }
}
