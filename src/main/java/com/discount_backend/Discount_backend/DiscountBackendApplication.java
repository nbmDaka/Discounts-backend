package com.discount_backend.Discount_backend;

import com.discount_backend.Discount_backend.service.auth.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
@EnableScheduling
public class DiscountBackendApplication {
	private static final Logger logger = LoggerFactory.getLogger(AuthService.class);


	public static void main(String[] args) {
		SpringApplication.run(DiscountBackendApplication.class, args);
	}

	@Bean
	public CommandLineRunner passwordEncoderTest(PasswordEncoder passwordEncoder) {
		return args -> {
			String rawPassword = "11wW_$$$$"; // <--- YOUR PASSWORD HERE
			String encodedPassword = passwordEncoder.encode(rawPassword);

			// Using logger.info for output
			logger.info("--------------------------------------------------");
			logger.info("Encoded password for '{}':", rawPassword);
			logger.info(encodedPassword);
			logger.info("--------------------------------------------------");

			// You can also verify it
			boolean matches = passwordEncoder.matches(rawPassword, encodedPassword);
			logger.info("Does raw password match encoded password? {}", matches);
			logger.info("--------------------------------------------------");
		};
	}

}
