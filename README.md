[![](https://cdn.discordapp.com/attachments/667088262287851551/765724389244534825/disparser-1.3.0.PNG)](https://jitpack.io/#SmellyModder/Disparser/1.3.0)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/7eff67ac4c1d49bfb356ff1028bc9028)](https://www.codacy.com/gh/SmellyModder/Disparser/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SmellyModder/Disparser&amp;utm_campaign=Badge_Grade)

<img align="right" src="https://cdn.discordapp.com/attachments/667088262287851551/740459139141992469/disparser_logo.png" height="200" width="200">

# About Disparser
Disparser is a multi-threaded command parser and executor for JDA, the Java Discord API.
It allows for sufficient and performant usage of commands with multiple aliases, permission requirements, and various arguments.
Disparser tries to not have that many niche things in it as possible but paves the way for people to implement them with ease, offering extensible and efficient command parsing for JDA.
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

# Usage
This section goes over the basics and foundations of Disparser's core features. 
<br> Disparser doesn't include many niche features but provides various built-in systems to make them easily implementable. </br>
For a list of all of Disparser's features and capabilities, go to the Features section.
## CommandContext
At the core of Disparser, there is `CommandContext`, a wrapper class for events in JDA used in command processing.
<br> A `CommandContext` is composed of a few things. </br>
- A `List` of `ParsedArgument`s for getting the parsed arguments for a `Command`.
- A `FeedbackHandler` for sending feedback when processing commands.
- A `BuiltInExceptionProvider` for constructing built-in exceptions when processing commands.
- An `Event` that the context wraps around for commands to process from.

`CommandContext` has a generic type parameter `<E>`, which extends an `Event`. The `Event` works as the source of the `CommandContext`, and allows commands to know what type of event they're processing from. `CommandContext` is easily adaptable to any message-related event giving lots of freedom to command processing.

Disparser has a few built-in extensions of `CommandContext`. 
- `GuildMessageCommandContext` for processing commands from a `GuildMessageReceivedEvent` event.
- `PrivateMessageCommandContext` for processing commands from a `PrivateMessageReceivedEvent` event.
- `MessageCommandContext` for processing commands from a `MessageReceivedEvent` event which works as a combo of the `GuildMessageReceivedEvent` and `PrivateMessageReceivedEvent` events.

The genericity of `CommandContext` is inclusive because it allows for all commands to not resort to using the same message-based event and provides great freedom to command processing by being easily extensible.

## Commands
`Command` is the core class used in representing a command. `Command` makes use of `CommandContext` by providing an abstract void method called `processCommand()`, which takes in a `CommandContext` of the same type as the generic type parameter for the `Command`.

A `Command` also contains a few other useful things. These include a `AliasesProperty`, a `PermissionsProperty`, an `UnmodifiableSet` of `CommandProperty`s, and an `UnmodifiableList` of `Argument`s.

`Command`s are immutable for respect to thread-safety, as Disparser's designed to allow for concurrency support. Because of this, changeable values about them shouldn't be stored in the `Command` and instead get stored in property maps, which Disparser has built-in support for doing. These property keys in `Command`s also work as annotation processors, and more on that later.

The set of `CommandProperty`s in a `Command` help tell what `CommandProperty`s that `Command` has.
The list of arguments in a `Command` help tell how a `CommandContext` should be created for that `Command` when parsing all the arguments into a list of `ParsedArguments`s.

Below is an example of a simple command that renames a `TextChannel`:
```Java
public final class RenameChannelTestCommand extends Command<GuildMessageCommandContext> {

	public RenameChannelTestCommand() {
		super("rename", TextChannelArgument.get().asOptional(), StringArgument.get());
	}

	@Override
	public void processCommand(GuildMessageCommandContext context) {
		TextChannel channel = context.getParsedResultOrElse(0, context.getChannel());
		channel.getManager().setName(context.getParsedResult(1)).queue();
	}

}
```
## Arguments
Disparser comes with a simple index-based argument system for commands. There are plans to improve this system, such as allowing arguments that can *cleanly* parse multiple objects from one portion of a string. This would pave the way for arguments that can parse a list of things.

An `Argument` is a parameterized interface where `<T>` is the type of the object the `Argument` parses a part of a string from a `Message` into.

The method in an `Argument` that does this is called `parse()`, which takes in a `ArgumentReader` and uses it to read the next argument of a string split from a `Message`. 
This `parse()` method has a return value of a `ParsedArgument` with a parameterized type matching that of the `Argument`'s that acts very similarly to an `Optional`.
This is done since `Arguments` in Disparser can be optional, meaning they don't have to be present in the message for executing the command.

The way an `Argument` is determined to be optional is with the method `isOptional()`.
<br> There is also the `asOptional()` method which converts an `Argument` into an instance of the `Argument`, but optional. </br>

Here is an example of an `Argument` implementation that's built-in into Disparser:
```Java
public final class URLArgument implements Argument<URL> {

	private URLArgument() {
	}

	/**
	 * @return The default instance.
	 */
	public static URLArgument get() {
		return new URLArgument();
	}

	@Override
	public ParsedArgument<URL> parse(ArgumentReader reader) throws Exception {
		String next = reader.nextArgument();
		if (next.startsWith("<") && next.endsWith(">")) {
			next = next.substring(1, next.length() - 1);
		}
		try {
			return ParsedArgument.parse(new URL(next));
		} catch (MalformedURLException e) {
			throw reader.getExceptionProvider().getInvalidURLException().create(next);
		}
	}

}
```
Now that we've gone over the core features in Disparser, we're now going to look over Disparser's other features. These other features pave the way for niche and more advanced features in Discord bots. Some of these include specialized command feedback, concurrency support, command exceptions, annotation processing, and an extensible command properties system.

These features may, of course, not be useful to everyone, but have many capabilities to them and are great for high-quality Discord bots.

The command feedback system is excellent for bots that will be on many servers with different languages. The command feedback system provides a basic framework for localized command messages and is just great for sending command feedback in general.
This system also ties into with the command exception system, as it allows you to make use of built-in exceptions and have them be localized, which becomes especially useful for making use of Disparser's built-in arguments and having the error message handling for them be localized.

More about these features soon:tm:...

# Features
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
* Private Message Support (Already in master branch, no public release yet).
* Localization Support (Already in master branch, no public release yet).
