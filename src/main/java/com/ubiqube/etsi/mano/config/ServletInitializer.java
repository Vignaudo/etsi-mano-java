package com.ubiqube.etsi.mano.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;

import com.ubiqube.etsi.mano.Application;

@SpringBootApplication
@ComponentScan("com.ubiqube.etsi.mano")
public class ServletInitializer extends SpringBootServletInitializer {
	public static void main(String[] args) {
		SpringApplication.run(ServletInitializer.class, args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(Application.class);
	}

}
