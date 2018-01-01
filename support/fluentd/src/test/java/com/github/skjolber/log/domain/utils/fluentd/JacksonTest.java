package com.github.skjolber.log.domain.utils.fluentd;

import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.skjolber.log.domain.utils.DomainMarker;

import static com.google.common.truth.Truth.assertThat;
import static com.google.common.truth.Truth.assertWithMessage;
import static org.mockito.Mockito.mock;

import java.util.Collections;

import org.junit.Test;
import org.komamitsu.fluency.Fluency;
import org.komamitsu.fluency.buffer.PackedForwardBuffer;
import org.komamitsu.fluency.sender.Sender;
import org.komamitsu.fluency.sender.TCPSender;

public class JacksonTest {

	private Fluency fluency = mock(Fluency.class);

	@Test
	public void test() {
		Sender sender = new TCPSender.Config().createInstance();
		
		SimpleModule simpleModule = new SimpleModule();

		JsonSerializer<? extends DomainMarker> a = new MarkerJsonSerializer();
		simpleModule.addSerializer(a);

		PackedForwardBuffer.Config bufferConfig = new PackedForwardBuffer.Config()
				.setJacksonModules(Collections.<Module>singletonList(simpleModule));

		Fluency fluency = new Fluency.Builder(sender).setBufferConfig(bufferConfig).build();


	}
}
