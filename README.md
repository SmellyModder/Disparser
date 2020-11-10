[![](https://cdn.discordapp.com/attachments/667088262287851551/765724389244534825/disparser-1.3.0.PNG)](https://jitpack.io/#SmellyModder/Disparser/1.3.0)
[![Codacy Badge](https://app.codacy.com/project/badge/Grade/7eff67ac4c1d49bfb356ff1028bc9028)](https://www.codacy.com/gh/SmellyModder/Disparser/dashboard?utm_source=github.com&amp;utm_medium=referral&amp;utm_content=SmellyModder/Disparser&amp;utm_campaign=Badge_Grade)

<img align="right" src="https://cdn.discordapp.com/attachments/667088262287851551/740459139141992469/disparser_logo.png" height="200" width="200">

# 📖 About
Disparser is a multi-threaded command parser and executor for JDA, the Java Discord API.
It allows for sufficient and performant usage of commands with multiple aliases, permission requirements, and various arguments.
Disparser tries to not have that many niche things in it as possible but paves the way for people to implement them with ease, offering extensible and efficient command parsing for JDA.
<br> *It is a WIP, so expect new features!* </br>

# 📦 Installing
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

# 🛠️ Usage
This section goes over the basics and foundations of Disparser's features. 
<br> Disparser doesn't include many niche features but provides various built-in systems to make them easily implementable. </br>
For a list of all of Disparser's features and capabilities, go to the Features section.
## CommandContext
At the core of Disparser, there is `CommandContext`, a wrapper class for events in JDA used in command processing.
<br> A `CommandContext` is composed of a few things. </br>
* A `List` of `ParsedArgument`s for getting the parsed arguments for a `Command`.
* A `FeedbackHandler` for sending feedback when processing commands.
* A `BuiltInExceptionProvider` for constructing built-in exceptions when processing commands.
* An `Event` that the context wraps around for commands to process from.

`CommandContext` has a generic type parameter `<E>`, which extends an `Event`. The `Event` works as the source of the `CommandContext`, and allows commands to know what type of event they're processing from. `CommandContext` is easily adaptable to any message-related event giving lots of freedom to command processing.

Disparser has a few built-in extensions of `CommandContext`. 
* `GuildMessageCommandContext` for processing commands from a `GuildMessageReceivedEvent` event.
* `PrivateMessageCommandContext` for processing commands from a `PrivateMessageReceivedEvent` event.
* `MessageCommandContext` for processing commands from a `MessageReceivedEvent` event which works as a combo of the `GuildMessageReceivedEvent` and `PrivateMessageReceivedEvent` events.

The genericity of `CommandContext` is inclusive because it allows for all commands to not resort to using the same message-based event and provides great freedom to command processing by being easily extensible.

## Commands
`Command` is the core class used in representing a command. `Command` makes use of `CommandContext` by providing an abstract void method called `processCommand()`, which takes in a `CommandContext` of the same type as the generic type parameter for the `Command`.

A `Command` also contains a few other useful things. These include an `AliasesProperty`, a `PermissionsProperty`, an `UnmodifiableSet` of `CommandProperty`s, and an `UnmodifiableList` of `Argument`s.

`Command`s are immutable for respect to thread-safety, as Disparser's designed to allow for concurrency support. Because of this, changeable values about them shouldn't be stored in the `Command` and instead get stored in property maps, which Disparser has built-in support for doing. These property keys in `Command`s also work as annotation processors, and more on that later.

The set of `CommandProperty`s in a `Command` help tell what `CommandProperty`s that `Command` has.
The list of arguments in a `Command` help tells how a `CommandContext` should be created for that `Command` when parsing all the arguments into a list of `ParsedArguments`s.

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
**IMPORTANT: This System is planned to be reworked in many ways to allow for more parsing features, such as sub-commands.**

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

## Command Feedback
The command feedback system is excellent for bots that will be on many servers with different languages.
It provides a basic framework for localized command messages and is handy for sending command feedback in general.
This system also ties into with the command exception system, as it allows you to make use of built-in exceptions and have them be localized, which becomes especially useful for making use of Disparser's built-in arguments and having the error message handling for them be localized.

There is also `FeedbackHandlerBuilder`, which is an interface for constructing a new `FeedbackHandler` for a `TextChannel`.
<br>This interface is also a functional interface which means you can do `MyFeedbackHandler::new` for a new `FeedbackHandlerBuilder`.</br>

Here is a basic example of a `FeedbackHandler`:
```Java
public final class TestFeedbackHandler implements FeedbackHandler {
	private final MessageChannel channel;

	public TestFeedbackHandler(MessageChannel channel) {
		this.channel = channel;
	}

	@Override
	public void sendFeedback(CommandMessage message) {
		this.channel.sendMessage(message.getMessage(this.channel)).queue();
	}

	@Override
	public void sendFeedback(MessageEmbed messageEmbed) {
		this.channel.sendMessage(messageEmbed).queue();
	}

	@Override
	public void sendSuccess(CommandMessage message) {
		this.channel.sendMessage(MessageUtil.createSuccessfulMessage(message.getMessage(this.channel))).queue();
	}

	@Override
	public void sendError(Exception exception) {
		if (exception instanceof CommandSyntaxException) {
			this.channel.sendMessage(MessageUtil.createErrorMessage(((CommandSyntaxException) exception).getCommandMessage().getMessage(this.channel))).queue();
		} else {
			this.channel.sendMessage(MessageUtil.createErrorMessage(exception.getMessage())).queue();
		}
	}
}
```

`CommandMessage` is a fundamental interface of the feedback system as it allows for localized command feedback.
<br>It has one method, `getMessage()`, which takes in a nullable `MessageChannel` and returns a string.</br>
The `MessageChannel` parameter allows for strings sent by commands to be localized. This interface is also functional allowing for the creations of new `CommandMessage`s without having to have an implementation class. Although, it is advised you make an implementation class if you plan to have translatable messages.

## Exception System
The exception system is pretty simple, and the title sums it up. This system ties in very much with the feedback system.
<br>The core purpose of the exception system is to allow for a simple, ergonomic, and localizable exception creation system. Disparser makes use of this internally to allow for its built-in arguments' error messages to be localizable, effectively allowing Disparser's built-in arguments to support languages other than English.</br>

Disparser also comes with many built-in `CommandExceptionCreator` implementations. A `CommandExceptionCreator` is an interface with one method, `create()`, that creates an `Exception` matching the generic type of the interface.
<br>For Disparser to have its built-in exceptions work properly for localization, the `BuiltInExceptionProvider` had to be created. This interface is large and has a method for each built-in exception creator used in Disparser's built-in arguments. As previously stated, this interface is pretty big, so only implement this on classes that will be fully localizing all of Disparser's built-in exception creators. If you only need to override a few, you should extend `DisparserExceptionProvider` and override the needed methods there, as that serves as the implementation class for `BuiltInExceptionProvider` </br>

There is one last important feature of the exception system, the `BuiltInExceptionProviderBuilder` interface. This interface constructs a new `BuiltInExceptionProvider` for a `MessageChannel` using its `build()` method. Unlike `FeedbackHandlerBuilder`, this isn't necessary to properly use the object it builds as `FeedbackHandler` is created for a specific `MessageChannel`, meaning you could use the same `BuiltInExceptionProvider` over and over for executing commands and not have issues.

Here is a simple example of a `DisparserExceptionProvider` implementation:
```Java
public final class TestExceptionProvider extends DisparserExceptionProvider {
	private final MessageChannel channel;
	private static final DynamicCommandExceptionCreator<String> COLOR_EXCEPTION = DynamicCommandExceptionCreator.createInstance(color -> {
		return new TestTranslatableCommandMessage("command.exception.color", color);
	});

	public TestExceptionProvider(MessageChannel channel) {
		this.channel = channel;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidColorException() {
		return COLOR_EXCEPTION;
	}
}
```

## CommandMessage
`CommandMessage` has already been talked about above, but this section will go over it a bit more. Simply put, the `CommandMessage` interface is a getter for a string for a nullable `MessageChannel`. There are many forms in which you could implement this interface. Some implementations to highlight are translatable messages, guild-based messages, text channel based messages, user-based messages, and much more.

Here is a rather unclean implementation of `CommandMessage` for translatable messages:
```Java
public final class TestTranslatableCommandMessage implements CommandMessage {
	private static final ConcurrentHashMap<Long, Language> MAP = new ConcurrentHashMap<>();

	static {
		MAP.put(735963087152349326L, Language.ENGLISH_UK);
	}

	private final String translatableString;
	private final String invalidColor;

	public TestTranslatableCommandMessage(String translatableString, String invalidColor) {
		this.translatableString = translatableString;
		this.invalidColor = invalidColor;
	}

	@Override
	public String getMessage(@Nullable MessageChannel channel) {
		return MAP.getOrDefault(channel != null ? channel instanceof TextChannel ? ((TextChannel) channel).getGuild().getIdLong() : 0L : 0L, Language.ENGLISH_US).translator.apply(this.translatableString) + this.invalidColor;
	}

	//Example, translations really shouldn't be done like this...
	enum Language {
		ENGLISH_US(s -> s.equals("command.exception.color") ? "Invalid Color: " : s),
		ENGLISH_UK(s -> s.equals("command.exception.color") ? "Invalid Colour: " : s);

		private final Function<String, String> translator;

		Language(Function<String, String> translator) {
			this.translator = translator;
		}
	}
}
```
This is not a great way to go about handling translations, but this implementation does get the point across. A more robust system should get made to manage things like this if you plan to do translatable messages.

## Command Properties/Annotations
`Command` is an immutable class for thread-safety. Because of this, changable values for them must be stored externally and be thread-safe. Creating a system for managing values  about commands can become a hassle, so Disparser has command properties, which are like keys for a value type in a `Command`. A `CommandProperty` is a generic interface that serves as a key for a value type and a processor for an annotation. Every `CommandProperty` is tied to an annotation, to allow for existing commands' properties be modified without modifying the `Command` itself.

An example of this would be:
```Java
@Aliases(value = {"example"}, mergeAliases = true)
public static final SomeOtherCommand EXAMPLE = new SomeOtherCommand();
```

`CommandProperty` implementations should also be immutable for thread-safety. A `CommandProperty` has one method, `get()`, which gets a value from the property from a nullable annotation. This method allows `CommandProperty`s to not just serve as property keys, but also as annotation processors.
There is also `CommandProperty.Value`, which is an extension of `AtomicReference` used to hold the value of a property.
<br>Disparser has two built-in `CommandProperty` implementations; `AliasesProperty` and `PermissionsProperty`. `AliasesProperty` obviously is a property for aliases and `PermissionsProperty` is obviously a property for permissions.</br>

See `AliasesProperty` and `PermissionsProperty` for good examples of a `CommandProperty` implementation.
Disparser also has a built-in class for managing these properties called `CommandPropertyMap`. `CommandPropertyMap` is generic for a type of `CommandContext`. This class is a thread-safe map class in which the property values for commands get mapped.

## Putting it all together
Now that Disparser's features and capabilities have been gone over, there's one final big question to answer.
<br>How does this all go together?</br>
Putting all these features together and making full use of them is not that difficult. Disparser offers a few built-in classes that make use of all these great features, and it's highly recommended you refer to them when making classes for handling all these features. All of Disparser's built-in handler classes for commands are located in the `net.smelly.disparser.context.handlers.*` package.

The abstract class for a command handler is called `AbstractCommandHandler`. This class is generic, and its typed parameters being `E` for an event type and `C` for a `CommandContext` type for the event. `AbstractCommandHandler` extends `ListenerAdapter` to get utilized as an event listener in JDA. It makes use of all of Disparser's features, making it a great class to extend off for custom command handlers. `AbstractCommandHandler` also makes use of Disparser's thread-safety, offering multi-threaded command processing.

`AbstractCommandHandler` has many capabilities to it, as it makes use of all of Disparser's features as stated already. Because of this, things can become overcomplicated as not everyone wants to make use of all the features. Because of this, there is `AbstractCommandHandlerBuilder`. `AbstractCommandHandlerBuilder` works as an abstract generic builder class for an `AbstractCommandHandler` and comes with fields and methods used in building an `AbstractCommandHandler`.

There are three extensions of `AbstractCommandHandler` built into Disparser, each with their own `AbstractCommandHandlerBuilder`.
<br>`CommandHandler` for `MessageCommandContext`, `GuildCommandHandler` for `GuildMessageCommandContext`, and `PrivateCommandHandler` for `PrivateMessageCommandContext`.</br>

Here is an example of a simple bot using these builders and adding the built `AbstractCommandHandler`s as event listeners:
```Java
public final class TestBot {
	private static JDA BOT;

	public static void main(String[] args) throws LoginException {
		JDABuilder botBuilder = JDABuilder.create(args[0], GatewayIntent.getIntents(GatewayIntent.ALL_INTENTS & ~GatewayIntent.getRaw(GatewayIntent.GUILD_PRESENCES, GatewayIntent.GUILD_MESSAGE_TYPING, GatewayIntent.DIRECT_MESSAGE_TYPING)));
		botBuilder.setStatus(OnlineStatus.ONLINE);
		botBuilder.setActivity(Activity.of(ActivityType.DEFAULT, "Disparsing!"));
		botBuilder.addEventListeners(
			new CommandHandler.Builder()
				.setPrefix("c!")
				.registerCommands(Commands.class)
				.setFeedbackBuilder(TestFeedbackHandler::new)
				.setExceptionProviderBuilder(TestExceptionProvider::new)
				.setExecutorService(Executors.newFixedThreadPool(6, new DisparsingThreadFactory("Test")))
				.build(),
			new GuildCommandHandler.Builder()
				.setPrefix("g!")
				.registerCommands(Commands.ROLE_TEST_COMMAND, Commands.RENAME_CHANNEL_TEST)
				.build()
		);
		BOT = botBuilder.build();
	}
}
```

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

## Upcoming Features
* Compound Arguments. Compound Arguments are arguments that include multiple arguments or parameters in themselves, this allows for more complex use and using a system for parsing objects without needing certain information across many commands.
* A thread-safe infrastructure to make concurrent command processing possible (Already in master branch, no public release yet).
* A command properties system (Already in master branch, no public release yet).
* Private Message Support (Already in master branch, no public release yet).
* Localization Support (Already in master branch, no public release yet).
