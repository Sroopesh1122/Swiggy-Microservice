package com.shivu.gateway_swiggy;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewaySwiggyApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewaySwiggyApplication.class, args);
	}

}
