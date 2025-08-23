package dbt.sda.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SDAGatewayConfigurations {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder){
        return builder.routes()
                .route("sda-git",
                        r -> r.path("/git/**")
                                .filters(f -> f.rewritePath("/git/(?<segment>.*)", "/git/${segment}")
                                        .rewritePath("/git/webhook/(?<segment>.*)", "/git/webhook/${segment}"))
                                .uri("lb://sda-git")
                )
                .route("sda-ai",
                        r -> r.path("/ai/**")
                                .filters(f -> f.rewritePath("/ai/(?<segment>.*)", "/ai/${segment}")
                                        .rewritePath("/multiagent/(?<segment>.*)", "/multiagent/${segment}"))
                                .uri("lb://sda-ai")
                )
                .build();
    }
}
