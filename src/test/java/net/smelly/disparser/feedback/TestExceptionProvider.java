package net.smelly.disparser.feedback;

import net.smelly.disparser.feedback.exceptions.DisparserExceptionProvider;
import net.smelly.disparser.feedback.exceptions.DynamicCommandExceptionCreator;

public final class TestExceptionProvider extends DisparserExceptionProvider {
	public static final TestExceptionProvider INSTANCE = new TestExceptionProvider();
	private static final DynamicCommandExceptionCreator<String> COLOR_EXCEPTION = DynamicCommandExceptionCreator.createInstance(color -> {
		return new TestTranslatableCommandMessage("command.exception.color", color);
	});

	private TestExceptionProvider() {}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidColorException() {
		return COLOR_EXCEPTION;
	}
}
