package dbt.ai;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * author: ankit.agrahari
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class SDAAIMain {

    public static void main(String[] args) {
        SpringApplication.run(SDAAIMain.class);
    }
}
