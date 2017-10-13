package com.github.skjolber.log.domain.codegen;

import com.example.agresso.AgressoLogger;
import com.example.agresso.AgressoMarker;
import com.example.agresso.AgressoMarkerBuilder;
import com.github.skjolber.log.domain.codegen.DomainFactory;
import com.github.skjolber.log.domain.codegen.JavaGenerator;
import com.github.skjolber.log.domain.codegen.LoggerGenerator;
import com.github.skjolber.log.domain.model.Domain;
import com.github.skjolber.log.domain.utils.JsonMdcJsonProvider;
import com.squareup.javapoet.JavaFile;

import static com.example.agresso.AgressoMarkerBuilder.*;
import static com.example.network.NetworkMarkerBuilder.*;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.agresso.AgressoLogger;
import com.github.skjolber.log.domain.utils.DomainMdc;
public class ScratchTest {
	
	
	
	@Test
	public void testLogger() throws Exception {
		LoggerFactory.getLogger(ScratchTest.class);
		AgressoLogger l = new AgressoLogger(LoggerFactory.getLogger(ScratchTest.class));
		
		l.info().username("thomas").message("message");
		l.debug().message("TEST");
		
		l.info().username("vegar").message("message 2");
	}

	@Test
	public void testLoggerMdc() throws Exception {
		Logger logger = LoggerFactory.getLogger(ScratchTest.class);
		AgressoLogger wrapper = new AgressoLogger(logger);
		
		try (Closeable a =  DomainMdc.mdc(version(1).timestamp(1).and(host("1")))) {
			try (Closeable b = DomainMdc.mdc(version(2).timestamp(2).and(host("2")))) {

				logger.info("Message inside context");
				
				logger.info(username("thomas").timestamp(6), "Marker + message inside context");
				
				wrapper.info().username("thomas").timestamp(6).message("Wrapped logger marker + message inside context");
				wrapper.debug().message("TEST");
			}
		}
		logger.info(username("vegar").timestamp(-1), "Marker + message outside context");
	}
	
}
