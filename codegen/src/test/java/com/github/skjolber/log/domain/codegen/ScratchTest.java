package com.github.skjolber.log.domain.codegen;

import static com.example.agresso.AgressoMarkerBuilder.username;
import static com.example.agresso.AgressoMarkerBuilder.version;
import static com.example.network.NetworkMarkerBuilder.host;

import java.io.Closeable;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.example.agresso.AgressoLogger;
import com.example.agresso.AgressoMarker;
import com.example.agresso.AgressoMdc;
import com.example.global.GlobalMarkerBuilder;
import com.github.skjolber.log.domain.utils.DomainMarker;
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

	@Test
	public void testLoggerMdc2() throws Exception {
		Logger logger = LoggerFactory.getLogger(ScratchTest.class);
		
		AgressoMarker mdc1 = new AgressoMarker().username("first").timestamp(1).and(GlobalMarkerBuilder.system("linux"));
		DomainMdc.mdc(mdc1);
		
		AgressoMarker mdc2 = new AgressoMarker().username("second").timestamp(2);
		DomainMdc.mdc(mdc2);
		
		AgressoMarker m = new AgressoMarker().username("third");
		
		logger.info(new AgressoMarker().username("third"), "Marker + message inside context 1 + 2");
		
		mdc2.close();
		logger.info(m, "Marker + message inside context DEFERRED 2");
		
		//logger.info(new AgressoMarker().version(123.4), "Marker + message outside context 2 ");
		
		mdc1.close();

		//logger.info(m, "Marker + message inside context DEFERRED 1");
	}
	
	@Test
	public void testLoggerMdc3() throws Exception {
		Logger logger = LoggerFactory.getLogger(ScratchTest.class);
		
		AgressoMarker mdc1 = new AgressoMarker().username("first").timestamp(1);
		DomainMdc.mdc(mdc1);

		System.out.println("Username is " + new AgressoMarker().getUsername());

		AgressoMarker mdc2 = new AgressoMarker().username("second");
		DomainMdc.mdc(mdc2);

		System.out.println("Username is " + new AgressoMarker().getUsername());

		mdc2.close();
		
		System.out.println("Username is " + new AgressoMarker().getUsername());
		
		mdc1.close();

		System.out.println("Username is " + new AgressoMarker().getUsername());
		
	}
	
	@Test
	public void testLoggerMdc4() throws Exception {
		Logger logger = LoggerFactory.getLogger(ScratchTest.class);
		
		AgressoMarker mdc1 = new AgressoMarker().username("first").timestamp(1);
		DomainMdc.mdc(mdc1);
		
		logger.info("Message inside context");

	}

	@Test
	public void testLoggerMdc5() throws Exception {
		Logger logger = LoggerFactory.getLogger(ScratchTest.class);
		
		AgressoMarker mdc1 = new AgressoMarker().username("first").timestamp(1);
		DomainMdc.mdc(mdc1);
		
		//for(int i = 0; i < 1000000; i++) {
		logger.info(new AgressoMarker().username("second"), "Marker + message inside context 1 + 2");
		
		mdc1.close();
	}
}
