package dbt.sda.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SDAGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(SDAGatewayApplication.class, args);
	}

}
