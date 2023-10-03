package com.example.shaden;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShadenApplication {

	private static final Logger LOG = LoggerFactory.getLogger(ShadenApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(ShadenApplication.class, args);
		LOG.info("ShadenApplication started successfully.");
	}

}
