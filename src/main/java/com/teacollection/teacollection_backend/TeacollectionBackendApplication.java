package com.teacollection.teacollection_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan("com.teacollection.teacollection_backend")
@EnableJpaRepositories("com.teacollection.teacollection_backend.repository")
public class TeacollectionBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(TeacollectionBackendApplication.class, args);
	}

}