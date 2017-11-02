package com.github.skjolber.log.domain.example.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
@Path("/")
public class Endpoint {

    private final static Logger logger = LoggerFactory.getLogger(Endpoint.class);

	@GET
    @Path("/{id}/hello")
	public String message(@PathParam("id") String id) {
		logger.info("Say hello");
		
		return "Hello " + id;
	}

}