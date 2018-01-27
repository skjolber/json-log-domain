package com.example.logging;

/**
 * This sample demonstrates writing logs using the Stackdriver Logging API.
 * 
 */
import static com.example.network.NetworkPayloadBuilder.port;

import java.io.File;
import java.io.FileInputStream;
import java.util.Collections;

import com.github.skjolber.log.domain.stackdriver.utils.DomainLogEntry;
import com.google.auth.Credentials;
import com.google.auth.oauth2.ServiceAccountJwtAccessCredentials;
import com.google.cloud.MonitoredResource;
import com.google.cloud.logging.LogEntry;
import com.google.cloud.logging.Logging;
import com.google.cloud.logging.LoggingOptions;
import com.google.cloud.logging.Severity;

public class LogSample {

	public static void main(String... args) throws Exception {

		if(args.length != 3) {
			System.out.println("Usage: <path to jwt access credentials> <projectId> <log name>");
			System.exit(0);
			return;
		}

		File file = new File(args[0]);
		if(!file.exists()) {
			System.out.println("File " + args[0] + " not found");
			System.exit(0);
			return;
		}
		// Instantiates a client
		Credentials credentials = ServiceAccountJwtAccessCredentials.fromStream(new FileInputStream(file));
		Logging logging = LoggingOptions.newBuilder().setProjectId(args[1]).setCredentials(credentials).build().getService();

		// The name of the log to write to
		String logName = args[2];

		LogEntry entry = DomainLogEntry.newBuilder(port(123).host("localhost"))
				.setSeverity(Severity.ERROR)
				.setLogName(logName)
				.setResource(MonitoredResource.newBuilder("global").build())
				.build();

		// Writes the log entry asynchronously
		logging.write(Collections.singleton(entry));

		System.out.printf("Logged: %s%n", entry.getPayload());
	}
}
