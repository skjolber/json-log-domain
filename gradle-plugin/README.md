# gradle plugin
Gradle plugin with support for incremental builds. 

Available at  [plugins.gradle.com].

## build
First build the root Maven project. Then run


	./gradlew clean build publishToMavenLocal --info

See the [log-domain-support-stackdriver] and [gradle-plugin-example].

[log-domain-support-stackdriver]:		../../support/stackdriver
[gradle-plugin-example]:				../../examples/gradle-plugin-example
[plugins.gradle.com]:					https://plugins.gradle.org/plugin/com.github.skjolber.json-log-domain