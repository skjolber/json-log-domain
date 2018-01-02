# google stackdriver support library

Base classes for generated code.

## Generating Java helper sources

YAML-files are converted to helper classes using `log-domain-maven-plugin`.

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
        <outputDirectory>target/generated-sources/domain-log-codegen</outputDirectory>
        <configuration>
            <markdown>true</markdown>
            <java>
                <stackDriver>true</stackDriver>
            </java>
        </configuration>        
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

and create a `LogEntry` with a JSON payload type.

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

