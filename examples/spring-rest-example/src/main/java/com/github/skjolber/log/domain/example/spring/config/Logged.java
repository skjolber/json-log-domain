package com.github.skjolber.log.domain.example.spring.config;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.github.skjolber.log.domain.utils.DomainMarker;

@Target({ ElementType.TYPE, ElementType.METHOD })
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Logged {
	
	Class<? extends DomainMarker> value();

}
