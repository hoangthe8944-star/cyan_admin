package com.example.cyan;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@SpringBootApplication
@EnableMongoAuditing
public class CyanApplication {

	public static void main(String[] args) {
		SpringApplication.run(CyanApplication.class, args);
	}

}
