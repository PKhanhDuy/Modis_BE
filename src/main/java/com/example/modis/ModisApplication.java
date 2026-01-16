package com.example.modis;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
// import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@EnableScheduling
@SpringBootApplication
public class ModisApplication {

	public static void main(String[] args) {
		SpringApplication.run(ModisApplication.class, args);
	}

}
