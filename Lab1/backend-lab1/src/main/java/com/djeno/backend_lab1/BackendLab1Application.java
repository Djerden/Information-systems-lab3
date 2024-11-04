package com.djeno.backend_lab1;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;

@SpringBootApplication
public class BackendLab1Application {

	public static void main(String[] args) {
		ApplicationContext context = SpringApplication.run(BackendLab1Application.class, args);
	}
}
