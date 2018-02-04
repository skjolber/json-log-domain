# Logback test 

Verify that testing is performed using the test library. 

Capture log statements using a [JUnit Rule]

```java
public LogbackJUnitRule rule = LogbackJUnitRule.newInstance();
```

and verify logging using

```java
assertThat(rule, message("Hello world"));

assertThat(rule, contains(host("localhost").port(8080)));

// check non-JSON value MDC
assertThat(rule, mdc("uname", "magnus"));
```

optionally also using `Class` and `Level` filtering. Import the library using

```xml
<dependency>
    <groupId>com.github.skjolber.log-domain</groupId>
    <artifactId>log-domain-test-logback</artifactId>
    <version>1.0.3</version>
    <scope>test</scope>
</dependency>
```

## Pretty-printer
The test library contains JSON pretty-printers which is more friendly on the eyes if you are logging JSON to console during testing. For your `logback-test.xml` file, use for example

```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <!-- add provider for custom JSON MDC -->
    <provider class="com.github.skjolber.log.domain.utils.configuration.JsonMdcJsonProvider"/>
    
    <!-- add pretty-printing for testing -->
    <jsonGeneratorDecorator class="com.github.skjolber.decorators.SyntaxHighligtingDecorator"/>
</encoder>
```
