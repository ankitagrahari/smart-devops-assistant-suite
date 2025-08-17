package dbt.sda.eureka;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SDAServiceRegistryApplication {

    public static void main(String[] args) {
        SpringApplication.run(SDAServiceRegistryApplication.class);
    }
}
