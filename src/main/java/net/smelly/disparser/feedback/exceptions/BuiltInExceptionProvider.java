package net.smelly.disparser.feedback.exceptions;

import net.dv8tion.jda.api.Permission;

import javax.annotation.concurrent.ThreadSafe;
import java.util.Set;

/**
 * An interface holding getters for all the built-in Disparser {@link CommandExceptionCreator}s.
 * <p>Implement this only on classes that will need to override all getters for the built-in exceptions. In other words, just extend {@link DisparserExceptionProvider} when you don't need to override all these methods. </p>
 *
 * @author Luke Tonon
 * @see DisparserExceptionProvider
 * @see BuiltInExceptionProviderBuilder
 */
@ThreadSafe
public interface BuiltInExceptionProvider {
	DynamicCommandExceptionCreator<Set<Permission>> getMissingPermissionsException();

	SimpleCommandExceptionCreator getNoArgumentsException();

	DynamicCommandExceptionCreator<String> getMissingArgumentException();

	BiDynamicCommandExceptionCreator<Integer, Integer> getMissingArgumentsException();

	DynamicCommandExceptionCreator<String> getInvalidIntegerException();

	DynamicCommandExceptionCreator<String> getInvalidLongException();

	DynamicCommandExceptionCreator<String> getInvalidCharException();

	DynamicCommandExceptionCreator<String> getInvalidShortException();

	DynamicCommandExceptionCreator<String> getInvalidByteException();

	DynamicCommandExceptionCreator<String> getInvalidFloatException();

	DynamicCommandExceptionCreator<String> getInvalidDoubleException();

	DynamicCommandExceptionCreator<String> getInvalidEnumException();

	DynamicCommandExceptionCreator<String> getInvalidNumberException();

	DynamicCommandExceptionCreator<String> getInvalidColorException();

	BiDynamicCommandExceptionCreator<String, Integer> getTooLowStringLengthException();

	BiDynamicCommandExceptionCreator<String, Integer> getTooHighStringLengthException();

	BiDynamicCommandExceptionCreator<Number, Number> getValueTooHighException();

	BiDynamicCommandExceptionCreator<Number, Number> getValueTooLowException();

	DynamicCommandExceptionCreator<String> getInvalidURLException();

	DynamicCommandExceptionCreator<String> getInvalidEmojiException();

	SimpleCommandExceptionCreator getCustomEmojiException();

	DynamicCommandExceptionCreator<String> getNoValidEmojisException();

	BiDynamicCommandExceptionCreator<String, Integer> getTooManyEmojisException();

	BiDynamicCommandExceptionCreator<String, Integer> getNotEnoughEmojisException();

	SimpleCommandExceptionCreator getMentionChannelNotFoundException();

	DynamicCommandExceptionCreator<Long> getChannelNotFoundException();

	DynamicCommandExceptionCreator<String> getInvalidChannelIdException();

	DynamicCommandExceptionCreator<Long> getUserNotFoundException();

	SimpleCommandExceptionCreator getMentionUserNotFoundException();

	DynamicCommandExceptionCreator<String> getInvalidUserException();

	DynamicCommandExceptionCreator<Long> getWebhookNotFoundException();

	DynamicCommandExceptionCreator<String> getInvalidWebhookIdException();

	DynamicCommandExceptionCreator<Long> getGuildNotFoundException();

	DynamicCommandExceptionCreator<String> getInvalidGuildIdException();

	DynamicCommandExceptionCreator<Long> getRoleNotFoundException();

	DynamicCommandExceptionCreator<String> getInvalidRoleIdException();

	SimpleCommandExceptionCreator getMentionRoleNotFoundException();
}
