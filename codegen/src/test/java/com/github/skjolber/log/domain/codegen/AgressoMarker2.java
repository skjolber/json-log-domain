package com.github.skjolber.log.domain.codegen;

import java.io.IOException;
import java.util.Arrays;
import java.util.Date;

import com.example.agresso.AgressoTag;
import com.fasterxml.jackson.core.JsonGenerator;
import com.github.skjolber.log.domain.utils.DomainMarker;

/**
 * Agresso log-support.<br/>
 * Agresso economy system<br/><br/>
 *
 * Usage: <br/><br/>
 *
 * import static com.example.agresso.AgressoMarker.*
 *
 */
public class AgressoMarker2 extends DomainMarker {
  private static final long serialVersionUID = 1L;

  public static final String QUALIFIER = "agresso";

  private String username;
  private Integer timestamp;
  private Double version;
  private Date date;
  private AgressoTag[] tags;
  
  public AgressoMarker2() {
    super(QUALIFIER);
  }

  /**
   * The person's username */
  public final AgressoMarker2 username(String value) {
    map.put("username", value);
    this.username = value;
    return this;
  }

  /**
   * current time in milliseconds */
  public final AgressoMarker2 timestamp(int value) {
    map.put("timestamp", value);
    this.timestamp = value;
    return this;
  }

  /**
   * application version */
  public final AgressoMarker2 version(double value) {
    map.put("version", value);
    this.version = value;
    return this;
  }

  /**
   * date of document creation */
  public final AgressoMarker2 date(Date value) {
    map.put("date", value);
    this.date = value;
    return this;
  }

  public AgressoMarker2 tags(AgressoTag... value) {
    map.put("tags", Arrays.asList(value));
    this.tags = value;
    return this;
  }
  
  @Override
  public void writeTo(JsonGenerator generator) throws IOException {
  	// check if there is MDC JSON data
	if(qualifier != null && !qualifier.isEmpty()) {
  		// subtree
        generator.writeFieldName(qualifier);
        generator.writeStartObject();
    }
	
	if(username != null) {
        generator.writeFieldName("username");
        generator.writeObject(username);
	} else {
		// write mdc from secret stack
	}

	if(timestamp != null) {
        generator.writeFieldName("timestamp");
        generator.writeObject(timestamp);
	}

	if(version != null) {
        generator.writeFieldName("version");
        generator.writeObject(version);
	}

	if(username != null) {
        generator.writeFieldName("username");
        generator.writeObject(username);
	}

	if(date != null) {
        generator.writeFieldName("date");
        generator.writeObject(date);
	}

	if(tags != null) {
        generator.writeFieldName("tags");
        generator.writeObject(tags);
	}
  		
  	if(qualifier != null && !qualifier.isEmpty()) {
  		generator.writeEndObject();
  	}
  }
}
