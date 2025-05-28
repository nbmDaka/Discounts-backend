package com.discount_backend.Discount_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class DiscountBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(DiscountBackendApplication.class, args);
	}


}
