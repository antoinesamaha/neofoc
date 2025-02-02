package com.neofoc.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"com.neofoc.app", "com.neofoc.springboot"})
public class NeoFocAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(NeoFocAppApplication.class, args);
	}

}
