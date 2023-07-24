package com.finalproject.dontbeweak;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableCaching
@EnableScheduling
@EnableJpaAuditing
@SpringBootApplication
public class DontbeweakApplication {
    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "optional:application-swagger.yml,"
            + "classpath:application.properties";


    public static void main(String[] args) {
        new SpringApplicationBuilder(DontbeweakApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }


}