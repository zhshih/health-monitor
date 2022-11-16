package com.example.healthmonitor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class HealthMonitorApplication {

	public static void main(String[] args) {
		SpringApplication.run(HealthMonitorApplication.class, args);
	}
}
