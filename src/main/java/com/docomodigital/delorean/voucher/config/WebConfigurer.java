package com.docomodigital.delorean.voucher.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.ServletContextInitializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import javax.servlet.ServletContext;

/**
 * Configuration of web application with Servlet 3.0 APIs.
 */
@Configuration
public class WebConfigurer implements ServletContextInitializer {

    private final Logger logger = LoggerFactory.getLogger(WebConfigurer.class);

    private final Environment env;

    public WebConfigurer(Environment env) {
        this.env = env;
    }

    @Override
    public void onStartup(ServletContext servletContext) {
        if (env.getActiveProfiles().length != 0) {
            logger.info("Web application configuration, using profiles: {}", env.getActiveProfiles());
        }
        logger.info("Web application fully configured");
    }


}
