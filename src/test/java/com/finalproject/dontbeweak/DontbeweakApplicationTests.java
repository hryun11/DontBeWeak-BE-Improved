package com.finalproject.dontbeweak;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.PropertySource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@TestPropertySource("classpath:application.properties")
@ExtendWith(SpringExtension.class)
@SpringBootTest
class DontbeweakApplicationTests {

    @Test
    void contextLoads() {
    }

}
