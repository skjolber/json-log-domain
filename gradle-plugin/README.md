# gradle plugin
Gradle plugin with support for incremental builds. 

Available at  [plugins.gradle.com].

## build
First build the root Maven project. Then run


	./gradlew clean build publishToMavenLocal --info

See [gradle-plugin-example].

[gradle-plugin-example]:				../examples/gradle-plugin-example
[plugins.gradle.com]:					https://plugins.gradle.org/plugin/com.github.skjolber.json-log-domain