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
@Path("/document")
@Logged(value = DocumentStoreMarker.class)
public class DocumentEndpoint {

	private final static Logger logger = LoggerFactory.getLogger(DocumentEndpoint.class);

	@GET
	@Path("/{id}/hello")
	public String message(@PathParam("id") String id) {
		logger.info("Say hello info");
		logger.warn("Say hello warning");
		logger.error("Say hello error");
		
		return "Hello " + id;
	}
	
	@GET
	@Path("/some/{id}/hello")
	public String someMessage(@PathParam("id") String id) {
		logger.info("Say some hello info");
		logger.warn("Say some hello warning");
		logger.error("Say some hello error");
		
		return "Some hello " + id;
	}

}