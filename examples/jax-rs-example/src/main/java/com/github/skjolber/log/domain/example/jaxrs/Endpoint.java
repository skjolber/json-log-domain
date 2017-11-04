package com.github.skjolber.log.domain.example.jaxrs;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.document.DocumentStoreMarker;
import com.github.skjolber.log.domain.logback.jaxrs.Logged;

@Component
@Path("/")
public class Endpoint {

    private final static Logger logger = LoggerFactory.getLogger(Endpoint.class);

	@GET
    @Path("/{id}/hello")
	@Logged(value = DocumentStoreMarker.class)
	public String message(@PathParam("id") String id) {
		logger.info("Say hello");
		
		return "Hello " + id;
	}
	
	@GET
    @Path("/some/{id}/hello")
	@Logged(value = DocumentStoreMarker.class)
	public String someMessage(@PathParam("id") String id) {
		logger.warn("Say some hello");
		
		return "Some hello " + id;
	}

}