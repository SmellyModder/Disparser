package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.TextChannel;
import net.smelly.disparser.feedback.exceptions.DisparserExceptionProvider;
import net.smelly.disparser.feedback.exceptions.DynamicCommandExceptionCreator;

public final class TestExceptionProvider extends DisparserExceptionProvider {
	private final TextChannel channel;
	private static final DynamicCommandExceptionCreator<String> COLOR_EXCEPTION = DynamicCommandExceptionCreator.createInstance(color -> {
		return new TestTranslatableCommandMessage("command.exception.color", color);
	});

	public TestExceptionProvider(TextChannel channel) {
		this.channel = channel;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidColorException() {
		return COLOR_EXCEPTION;
	}
}
