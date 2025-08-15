package dbt.sda.sda_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SdaGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SdaGatewayApplication.class, args);
	}

}
