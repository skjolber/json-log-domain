# Google Stackdriver support library

Artifact containing base-classes for generated code.

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

## Usage
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
 insertId:  "1efylig9drkvsh1"  
 jsonPayload: {
  network: {
   host:  "localhost"    
   port:  123    
  }
 }
 logName:  "projects/abcdef/logs/global"  
 receiveTimestamp:  "2018-01-02T14:05:56.176942353Z"  
 resource: {
  labels: {
   project_id:  "abcdef"    
  }
  type:  "global"   
 }
 severity:  "INFO"  
 timestamp:  "2018-01-02T14:05:56.176942353Z"  
}
```