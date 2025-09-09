package org.sopt36.ninedotserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@ConfigurationPropertiesScan(basePackages = "org.sopt36.ninedotserver")
@EnableScheduling
@SpringBootApplication
public class NinedotServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NinedotServerApplication.class, args);
    }

}
