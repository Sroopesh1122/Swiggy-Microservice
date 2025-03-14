package com.shivu.swiggy_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class SwiggyServerRegistryApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiggyServerRegistryApplication.class, args);
	}

}
