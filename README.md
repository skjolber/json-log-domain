[![Build Status](https://travis-ci.org/skjolber/json-log-domain.svg?branch=master)](https://travis-ci.org/skjolber/json-log-domain)

# json-log-domain
Library supporting JSON-logging. Currently working with [Logback] and [logstash-logback-encoder] and native [Google Stackdriver].

Users will benefit from

 * JSON-logging with domain-specific subtrees
 * Simple YAML-based definition format
 * User-friendly helper-classes generated via [Maven] plugin
 * Markdown documentation generator
 * Elasticsearch configuration generator
 * JAX-RS log-annotation for [automatic MDC population]

Multiple domains can be combined in the same log statement.

Bugs, feature suggestions and help requests can be filed with the [issue-tracker].

## License
[Apache 2.0]

# Obtain
The project is based on [Maven] and is available from central Maven repository. See further down for dependencies.

# Usage
Using `static` imports, log expressions can be simplified to (usually) a single line.

The generated sources allow for writing statements like

```java
logger.info(system("fedora").tags(LINUX), "Hello world");
```

for `SLF4J`, or

```java
LogEntry entry = DomainLogEntry.newBuilder(system("fedora").tags(LINUX).message("Hello world"))
    .setSeverity(Severity.INFO)
    .setLogName(logName)
    .setResource(MonitoredResource.newBuilder("global").build())
    .build();
```
for `Stackdriver`. Resulting in

```json
{
  "message": "Hello world",
  "system": "fedora",
  "tags": ["linux"]
}
```

#### Multiple domains
Combine multiple domains in a single log statement via `and(..)`:

```java
logger.info(name("java").version(1.7).tags(JIT) // programming language
        .and(host("127.0.0.1").port(8080)) // network
        .and(system("Fedora").tags(LINUX)), // global
        "Hello world"); 
```

for `SLF4J` or

```java
LogEntry entry = DomainLogEntry.newBuilder(
            name("java").version(1.7).tags(JIT)  // programming language
            .and(host("127.0.0.1").port(8080)) // network
            .and(system("Fedora").tags(LINUX).message("Hello world")) // global
        .setSeverity(Severity.INFO)
        .setLogName(logName)
        .setResource(MonitoredResource.newBuilder("global").build())
        .build();
```
for `Stackdriver`. This outputs domain-specific subtrees:

```json
{
  "message": "Hello world",
  "language": {
    "name": "java",
    "version": 1.7,
    "tags": ["JIT"]
  },
  "network": {
    "port": 8080,
    "host": "127.0.0.1"
  },
  "system": "fedora",
  "tags": ["linux"]
}
```

where the `global` fields are at the root of the message. 

## MDC-style logging
Create `AutoClosable` scopes using

```java
try (AutoCloseable a =  mdc(host("localhost").port(8080))) { // network
    logger.info().name("java").version(1.7).tags(JIT)  // programming language
        .and(system("Fedora").tags(LINUX)) // global
        .message("Hello world");
}
```

or the equivalent using try-finally; 

```java
Closeable mdc = mdc(host("localhost").port(8080); // network
try {
    ...
} finally {
    mdc.close();
}
```

Unlike the built-in SLF4J MDC, the JSON MDC works like a stack. For Logback, see [Logback support] artifact for configuration.

# YAML definition format
The relevant fields and tags are defined in a YAML file, from which Java, Markdown and Elastic sources are generated. 

![alt text][intro1.png]

Example definition:

```yaml
version: '1.0'
name: Global
package: com.example.global
description: Global values
keys:
  - system:
      name: operating system name
      type: string
      description: The system name
      example: Ubuntu, Windows 10 or Fedora
  - memory:
      name: physical memory
      type: integer
      format: int32
      description: Physical memory in megabytes
      example: 1024, 2048, 16384
tags:
 - linux: Linux operating system
 - mac: Apple operating system
 - windows: Microsoft windows operating system
```

The definition format consists of the fields

  * `version` - file version
  * `name` - domain name (will prefix generated java sources)
  * `package` - package of generated sources
  * `qualifier` - name of domain subtree in logged JSON output (optional) 
  * `description` - textual description of domain
  * `keys` - list of key-value definitions (see below). 
  * `tags` - list of tag definitions (see below)

In the above JSON example output, the optional `qualifier` corresponds to `network` and `language` while `keys` include `system`, `port`, `host`, `version`.

### Keys
Each key is defined by:

 * `name` - name of field (Operting System etc)
 * `type` - datatype (string, number, integer etc)
 * `format` - datatype subformat (int32, int64 etc)
 * `description` - textual description of key
 * `example` - example of legal value

The list item itself is the key in the logged key-value. The type/format datatype definition is borrowed from [Swagger Code Generator]. The intention is that log statements and REST services use the exact same definition for the same data type. Furthermore, framework-level interceptors should be able to pick up interesting fields in JSON objects and/or paths and automatically add those as context to a service invocation, saving the developer valuable time.

### Tags
Each tag is defined by:

 - `name` - a valid Java Enum name
 - `description` - textual description of the tag

# Maven plugin
Files in the above YAML format can be used to generate Java helper classes, Elastic message configuration and/or Markdown documents.

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
        <types>
            <markdown>true</markdown>
            <java>
                <logback>true</logback>
                <!-- OR -->
                <stackDriver>true</stackDriver>
            </java>
        </types>    
        <domains>
            <domain>
                <path>${basedir}/src/main/resources/yaml/network.yaml</path>logger
            </domain>
        </domains>
    </configuration>
</plugin>
```

In a multi-domain setup, the recommended approach is to generate per-domain artifacts, so that each project only generates helper classes for its own application-specific YAML file and accesses the helper classes for the other domains via a Gradle/Maven dependency.

## Support-library
A few common classes are not part of the generated sources:

```xml
<dependency>
    <groupId>com.github.skjolber.log-domain</groupId>
    <artifactId>log-domain-support-logback</artifactId>
    <version>1.0.3</version>
</dependency>
```

or


```xml
<dependency>
    <groupId>com.github.skjolber.log-domain</groupId>
    <artifactId>log-domain-support-stackdriver</artifactId>
    <version>1.0.3</version>
</dependency>
```

## Markdown documentation
A [markdown file] can also be generated for online documentation. 

## Elasticsearch configuration files
Elasticsearch properties can be generated. One or more of these files can be combined into an application-specific message field mapping, typically at deploy time. See [Elastic example].

# Testing
Logging is an essential part of any application, verify that logging is performed during unit testing using the [test] libraries.

# Alternatives
If you do not like this prosject, maybe you'll like

  * [godaddy-logger]
  * [slf4jtesting]
  * [slf4j-json-logger]
  * [logback-more-appenders]

# History

 - [1.0.3-SNAPSHOT]: Stackdriver support, minor improvements.
 - 1.0.2: JAX-RS helper library, various improvements.
 - 1.0.1: Added MDC support, various improvements.
 - 1.0.0: Initial version

[Apache 2.0]:					http://www.apache.org/licenses/LICENSE-2.0.html
[issue-tracker]:				https://github.com/skjolber/log-domain/issues
[Maven]:						http://maven.apache.org/
[1.0.3]:						https://github.com/skjolber/json-log-domain/releases
[Logback]:						https://logback.qos.ch/
[logstash-logback-encoder]:		https://github.com/logstash/logstash-logback-encoder
[Swagger Code Generator]:		https://github.com/swagger-api/swagger-codegen
[JUnit Rule]:					https://github.com/junit-team/junit4/wiki/rules
[markdown file]:				https://gist.github.com/skjolber/b79b5c7e4ae40d50305d8d1c9b0c1f71
[JsonProvider]:					https://github.com/logstash/logstash-logback-encoder#providers-for-loggingevents
[pretty-printer]:				test/logback/src/main/java/com/github/skjolber/log/domain/test/util/PrettyPrintingDecorator.java
[Elastic example]: 				examples/elastic-example
[automatic MDC population]:		examples/jax-rs-example
[intro1.png]: 					https://raw.githubusercontent.com/skjolber/json-log-domain/master/docs/images/intro1.png "YAML to multiple formats"
[godaddy-logger]:				https://github.com/godaddy/godaddy-logger
[slf4jtesting]:					https://github.com/portingle/slf4jtesting
[slf4j-json-logger]:			https://github.com/savoirtech/slf4j-json-logger
[Google Stackdriver]:			https://cloud.google.com/stackdriver
[logback-more-appenders]:		https://github.com/sndyuk/logback-more-appenders
[Logback support]:              support/logback
[test]:                         test