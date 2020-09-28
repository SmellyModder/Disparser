[![](https://cdn.discordapp.com/attachments/667088262287851551/758404732539895828/badge.png)](https://jitpack.io/#SmellyModder/Disparser/1.2.0)

<img align="right" src="https://cdn.discordapp.com/attachments/667088262287851551/740459139141992469/disparser_logo.png" height="200" width="200">

# About Disparser
Disparser is a simple command parser for JDA, the Java Discord API.
It allows for easy and performant usage of commands with multiple aliases, permission requirements, and various arguments.
Disparser will stay relatively small and simple forever, offerring simple and efficient command parsing for JDA.
<br> *It is a WIP, so expect new features!* </br>

## Usage
If you wish to use Disparser you can add it as a dependency by following these steps in your `build.gradle`
* Add the maven in your `repositories` section:
```
repositories {
    maven { url 'https://jitpack.io' }
}
```
* Add it as a dependency in your `dependencies` section:
```
dependencies {
    implementation 'com.github.SmellyModder:Disparser:{version}'
}
```
<br> You can also download the source for this repository and build Disparser using the `gradlew build` command. </br>

## Features
* Command Handlers for processing commands from messages
* An index-based argument system that's simple and easy to work with.
* A feedback system for command message output.
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
