package com.shivu.swiggy_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
public class SwiggyPaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiggyPaymentServiceApplication.class, args);
	}

}
