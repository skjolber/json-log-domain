package com.github.skjolber.log.domain.example.jaxrs;

import org.glassfish.jersey.server.ResourceConfig;

import org.springframework.stereotype.Component;

@Component
public class JerseyConfig extends ResourceConfig {

	public JerseyConfig() {
		register(Endpoint.class);
        packages("com.github.skjolber.log.domain.example.jaxrs");

	}

}