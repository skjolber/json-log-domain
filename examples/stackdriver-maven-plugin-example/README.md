# log-domain-stackdriver-maven-plugin-example
Example Stackdriver project. 

Running this example requires a Stackdriver account and

  * JWT credentials in a local JSON file
  * Project id (e.g. stackdriver-123)
  * Resource name (e.g. global)
      
## Maven plugin configuration

```xml
<plugin>
    <groupId>com.github.skjolber.log-domain</groupId>
    <artifactId>log-domain-maven-plugin</artifactId>
    <version>1.0.3</version>
    <executions>
        <execution>
            <id>generate</id>
            <goals>
                <goal>generate</goal>
            </goals>
        </execution>
    </executions>
    <configuration>
        <outputDirectory>${project.build.directory}/generated-sources/domain-log-codegen</outputDirectory>
        <types>
            <markdown>true</markdown>
            <java>
                <stackDriver>true</stackDriver>
            </java>
        </types>        
        <domains>
            <domain>
                <path>${basedir}/src/main/resources/yaml/network.yaml</path>
            </domain>
        </domains>
    </configuration>
</plugin>
```

## Logging
After generating code, add static imports

```java
import static com.example.network.NetworkPayloadBuilder.*;
```

and create a `LogEntry` with a custom payload type.

```java

// obtain logging instance
Logging logging = ..;

LogEntry entry = DomainLogEntry.newBuilder(port(123).host("localhost"))
    .setSeverity(Severity.INFO)
    .setLogName(logName)
    .setResource(MonitoredResource.newBuilder("global").build())
    .build();
    
// Write log entry
logging.write(Collections.singleton(entry));
```

and the result should be statements like

```json
{  
   "insertId":"1efylig9drkvsh1",
   "jsonPayload":{  
      "network":{  
         "host":"localhost",
         "port":123
      }
   },
   "logName":"projects/abcdef/logs/global",
   "receiveTimestamp":"2018-01-02T14:05:56.176942353Z",
   "resource":{  
      "labels":{  
         "project_id":"abcdef"
      },
      "type":"global"
   },
   "severity":"INFO",
   "timestamp":"2018-01-02T14:05:56.176942353Z"
}
```
in the [Stackdriver Logging] view. 

### Execute via command line
Run the command

    java -jar target/log-domain-stackdriver-maven-plugin-example-jar-with-dependencies.jar <path to jwt access credentials> <projectId> <log name>

i.e.

    java -jar target/log-domain-stackdriver-maven-plugin-example-jar-with-dependencies.jar "/home/me/stackdriver-123.json" "stackdriver-456" global
    
## Corresponding Gradle plugin configuration
Add

```groovy
apply plugin: 'com.github.skjolber.json-log-domain'
```

and configure a `jsonLogDomain` task

```groovy
jsonLogDomain { 
	definitions = files('src/main/resources/network.yaml')
	
	stackDriver {
	}
}

sourceSets {
    main.java.srcDirs += [jsonLogDomain.logback.outputDirectory]
}
```

[Stackdriver Logging]:			https://console.cloud.google.com/logs