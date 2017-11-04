package com.github.skjolber.log.domain.example.spring;

import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.example.document.DocumentStoreMarker;
import com.github.skjolber.log.domain.example.spring.config.Logged;

@RestController
@RequestMapping("/greeting")
public class GreetingController {

    private final static Logger logger = LoggerFactory.getLogger(GreetingController.class);

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    @RequestMapping("/{id}/hello")
	@Logged(value = DocumentStoreMarker.class)
    public Greeting greeting(@PathVariable("id") String id) {
		logger.info("Say hello");
		
        return new Greeting(counter.incrementAndGet(), String.format(template, id));
    }
}