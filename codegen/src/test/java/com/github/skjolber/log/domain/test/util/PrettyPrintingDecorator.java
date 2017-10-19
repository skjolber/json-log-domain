package com.github.skjolber.log.domain.test.util;

import com.fasterxml.jackson.core.JsonGenerator;

import net.logstash.logback.decorate.JsonGeneratorDecorator;

/**
 * 
 * Helper class for pretty-printing logs during testing
 * 
 * @author skjolber
 *
 */

public class PrettyPrintingDecorator implements JsonGeneratorDecorator {

    @Override
    public JsonGenerator decorate(JsonGenerator generator) {
        return generator.useDefaultPrettyPrinter();
    }

}
