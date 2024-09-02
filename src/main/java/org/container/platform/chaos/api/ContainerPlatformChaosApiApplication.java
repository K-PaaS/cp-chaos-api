package org.container.platform.chaos.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
public class ContainerPlatformChaosApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContainerPlatformChaosApiApplication.class, args);
    }

}
