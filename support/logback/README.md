# Logback support library
Artifact containing base-classes for generated code and some logger configuration.

## MDC-style logging
To enable MDC-style JSON logging for Logback, enable a [JsonProvider] in the configuration:

```xml
<encoder class="net.logstash.logback.encoder.LogstashEncoder">
    <!-- add provider for JSON MDC -->
    <provider class="com.github.skjolber.log.domain.utils.configuration.JsonMdcJsonProvider"/>
</encoder>
```

This will make sure to include JSON MDC also when doing plain text logging.

### Async logger + MDC
As MDC data must be captured before the logging event leaves the thread, so if you are using a multi-threaded approach, like `AsyncAppender`, make sure to include a call to capture the MDC data like [this example].


[JsonProvider]:					https://github.com/logstash/logstash-logback-encoder#providers-for-loggingevents
[this example]:					support/logback/src/main/java/com/github/skjolber/log/domain/utils/configuration/DomainAsyncAppender.java

