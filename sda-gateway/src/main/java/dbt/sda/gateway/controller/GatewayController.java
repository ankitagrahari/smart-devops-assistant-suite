package dbt.sda.gateway.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GatewayController {

    @GetMapping("/echo")
    public String isUp(){
        return "Gateway is UP and running..!";
    }
}
