package com.github.skjolber.log.domain.utils.fluentd;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.github.skjolber.log.domain.utils.DomainMarker;

public class MarkerJsonSerializer extends JsonSerializer<DomainMarker> {

	@Override
	public void serialize(DomainMarker value, JsonGenerator gen, SerializerProvider serializers) throws IOException, JsonProcessingException {
		
	}
	
	@Override
	public Class<DomainMarker> handledType() {
		return DomainMarker.class;
	}

}
