package com.idataitech.gdg.devfest2016.ml;


import com.idataitech.gdg.devfest2016.ml.config.Constants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.SimpleCommandLinePropertySource;

@ComponentScan
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);


    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(Application.class);
        app.setAdditionalProfiles(getDefaultProfile(args));
        app.run(args).getEnvironment();
    }

    /**
     * Get a default profile if it has not been set.
     */
    public static String getDefaultProfile(String... args) {
        SimpleCommandLinePropertySource source = new SimpleCommandLinePropertySource(args);
        String profile = source.getProperty(Constants.SPRING_PROFILE_ACTIVE);
        if (profile == null) {
            profile = System.getProperty(Constants.SPRING_PROFILE_ACTIVE,
                    System.getenv(Constants.SPRING_PROFILE_ACTIVE));
        }

        if (profile != null) {
            LOG.info("Running with Spring profile(s) : {}", profile);
            return profile;
        }

        // 开发环境都会加上 -Dspring.profiles.active=dev; 所以默认用 prod 简化产品正式部署
        LOG.warn("No Spring profile configured, running with default configuration");
        return Constants.SPRING_PROFILE_PRODUCTION;
    }
}
