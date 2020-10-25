package net.smelly.disparser.feedback.exceptions;

import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.concurrent.ThreadSafe;

/**
 * An interface used in constructing a new {@link BuiltInExceptionProvider} for a specified {@link TextChannel}.
 *
 * @author Luke Tonon
 * @see BuiltInExceptionProvider
 * @see DisparserExceptionProvider
 * @see DisparserExceptionProvider#BUILDER
 */
@ThreadSafe
@FunctionalInterface
public interface BuiltInExceptionProviderBuilder {
	/**
	 * Builds a {@link BuiltInExceptionProvider} for a {@link TextChannel}.
	 *
	 * @param channel The channel to build the {@link BuiltInExceptionProvider} for.
	 * @return The constructed {@link BuiltInExceptionProvider} for the specified {@link TextChannel}.
	 */
	BuiltInExceptionProvider build(TextChannel channel);
}
