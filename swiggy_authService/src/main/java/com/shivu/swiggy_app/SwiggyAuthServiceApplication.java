package com.shivu.swiggy_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class SwiggyAuthServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(SwiggyAuthServiceApplication.class, args);
	}

}
