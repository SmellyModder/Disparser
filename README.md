[![](https://cdn.discordapp.com/attachments/667088262287851551/765724389244534825/disparser-1.3.0.PNG)](https://jitpack.io/#SmellyModder/Disparser/1.3.0)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/7eff67ac4c1d49bfb356ff1028bc9028)](https://www.codacy.com/gh/SmellyModder/Disparser/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SmellyModder/Disparser&amp;utm_campaign=Badge_Grade)

<img align="right" src="https://cdn.discordapp.com/attachments/667088262287851551/740459139141992469/disparser_logo.png" height="200" width="200">

# About Disparser
Disparser is a simple command parser for JDA, the Java Discord API.
It allows for easy and performant usage of commands with multiple aliases, permission requirements, and various arguments.
Disparser will stay relatively small and simple forever, offerring simple and efficient command parsing for JDA.
<br> *It is a WIP, so expect new features!* </br>

# Installing
Latest Stable Version: [![](https://cdn.discordapp.com/attachments/667088262287851551/765724389244534825/disparser-1.3.0.PNG)](https://jitpack.io/#SmellyModder/Disparser/1.3.0)
<br>Disparser's master branch is frequently updated, making it unstable, so it's recommended you use one of the release tags on the repository here as they are stable.</br>
## Gradle
* Add the maven in your `repositories` section:
```gradle
repositories {
    maven { url 'https://jitpack.io' }
}
```
* Add it as a dependency in your `dependencies` section:
```gradle
dependencies {
    implementation 'com.github.SmellyModder:Disparser:{version}'
}
```
## Maven
* Add the repository to your build file
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```
* Add the dependency
```xml
<dependency>
    <groupId>com.github.SmellyModder</groupId>
    <artifactId>Disparser</artifactId>
    <version>VERSION</version>
</dependency>
```
<br> You can also download the source for this repository and build Disparser using the `gradlew build` command.</br>

## Features
* Command Handlers for processing commands from messages.
* An index-based argument system that's simple and easy to work with.
* A feedback system for command message output.
* A command exception system.
* A Command Context system for parsing arguments from a message to be executed by a command.
* Command prefix support.
* A `ArgumentReader` class for reading components of a string to parse them into arguments.
* An aliases system for commands.
* A permissions system for commands.
* A large amount of built-in arguments for parsing objects in Java and JDA.
* A simple annotation system for easier registration of commands.
* A `EitherArgument` class for parsing either arguments.
* An optional system for arguments, allowing some arguments to not always have to be parsed.
* An `InfoCommand` class for easier making of info/help commands.

## Upcoming Features
* Compound Arguments. Compound Arguments are arguments that include multiple arguments or parameters in themselves, this allows for more complex use and using a system for parsing objects without needing certain information across many commands.
* A thread-safe infrastructure to make concurrent command processing possible (Already in master branch, no public release yet).
* A command properties system (Already in master branch, no public release yet).
* Private Message Support
* Localization Support (Already in master branch, no public release yet).
