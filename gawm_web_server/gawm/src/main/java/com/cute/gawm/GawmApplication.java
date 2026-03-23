package com.cute.gawm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EntityScan(basePackages = "com.cute.gawm.domain")
@EnableJpaRepositories(basePackages = "com.cute.gawm.domain")
@EnableMongoRepositories(basePackages = "com.cute.gawm.domain")
public class GawmApplication {
	public static void main(String[] args) {
		SpringApplication.run(GawmApplication.class, args);
	}

}
