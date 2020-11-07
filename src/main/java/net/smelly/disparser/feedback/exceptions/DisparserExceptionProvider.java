package net.smelly.disparser.feedback.exceptions;

import net.dv8tion.jda.api.Permission;
import net.smelly.disparser.feedback.FormattedCommandMessage;
import net.smelly.disparser.util.MessageUtil;

import javax.annotation.concurrent.ThreadSafe;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * This class is an implementation class of {@link BuiltInExceptionProvider} for all the built-in {@link CommandExceptionCreator}s used in creating exceptions for the built-in Disparser arguments.
 * All of these fields are used internally in Disparser.
 *
 * @author Luke Tonon
 * @see BuiltInExceptionProvider
 */
@ThreadSafe
public class DisparserExceptionProvider implements BuiltInExceptionProvider {
	public static final BuiltInExceptionProviderBuilder BUILDER = channel -> new DisparserExceptionProvider();

	private static final DynamicCommandExceptionCreator<Set<Permission>> PERMISSIONS_EXCEPTION = DynamicCommandExceptionCreator.createInstance((permissions) -> new FormattedCommandMessage("You do not have permission to run this command! Required Permissions: %s", permissions));
	private static final SimpleCommandExceptionCreator NO_ARGUMENTS_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "No arguments are present!");
	private static final SimpleCommandExceptionCreator MISSING_ARGUMENT_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "An argument is missing");
	private static final SimpleCommandExceptionCreator MISSING_ARGUMENTS_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "Multiple arguments are missing, view this command's arguments!");
	private static final DynamicCommandExceptionCreator<Integer> SPECIFIC_MISSING_ARGUMENT_EXCEPTION = DynamicCommandExceptionCreator.createInstance((arg) -> new FormattedCommandMessage("%s argument is missing", arg + MessageUtil.getOrdinalForInteger(arg)));
	private static final DynamicCommandExceptionCreator<List<Integer>> SPECIFIC_MISSING_ARGUMENTS_EXCEPTION = DynamicCommandExceptionCreator.createInstance((missingArgs) -> {
		List<String> ordinals = new ArrayList<>(missingArgs.size());
		for (int arg : missingArgs) {
			ordinals.add(arg + MessageUtil.getOrdinalForInteger(arg));
		}
		return channel -> MessageUtil.createFormattedSentenceOfCollection(ordinals) + " arguments are missing";
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_INTEGER_EXCEPTION = DynamicCommandExceptionCreator.createInstance(integer -> {
		return new FormattedCommandMessage("`%s` is not a valid integer!", integer);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_LONG_EXCEPTION = DynamicCommandExceptionCreator.createInstance(along -> {
		return new FormattedCommandMessage("`%s` is not a valid long", along);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_CHAR_EXCEPTION = DynamicCommandExceptionCreator.createInstance(character -> {
		return new FormattedCommandMessage("`%s` is not a valid char!", character);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_SHORT_EXCEPTION = DynamicCommandExceptionCreator.createInstance(ashort -> {
		return new FormattedCommandMessage("`%s` is not a valid short!", ashort);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_BYTE_EXCEPTION = DynamicCommandExceptionCreator.createInstance(abyte -> {
		return new FormattedCommandMessage("`%s` is not a valid byte!", abyte);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_FLOAT_EXCEPTION = DynamicCommandExceptionCreator.createInstance(afloat -> {
		return new FormattedCommandMessage("`%s` is not a valid float!", afloat);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_DOUBLE_EXCEPTION = DynamicCommandExceptionCreator.createInstance(adouble -> {
		return new FormattedCommandMessage("`%s` is not a valid double!", adouble);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_ENUM_EXCEPTION = DynamicCommandExceptionCreator.createInstance(type -> {
		return new FormattedCommandMessage("`%s` is not a valid type!", type);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_NUMBER_EXCEPTION = DynamicCommandExceptionCreator.createInstance(number -> {
		return new FormattedCommandMessage("`%s` is not a valid number!", number);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_COLOR_EXCEPTION = DynamicCommandExceptionCreator.createInstance(number -> {
		return new FormattedCommandMessage("`%s` is not a valid color value!", number);
	});

	private static final BiDynamicCommandExceptionCreator<String, Integer> NOT_ENOUGH_CHARS_EXCEPTION = BiDynamicCommandExceptionCreator.createInstance((string, length) -> {
		return new FormattedCommandMessage("`%s` doesn't meet the minimum character length of %o", string, length);
	});

	private static final BiDynamicCommandExceptionCreator<String, Integer> TOO_MANY_CHARS_EXCEPTION = BiDynamicCommandExceptionCreator.createInstance((string, length) -> {
		return new FormattedCommandMessage("`%s` exceeds the max character length of %o", string, length);
	});

	private static final BiDynamicCommandExceptionCreator<Number, Number> VALUE_TOO_HIGH_EXCEPTION = BiDynamicCommandExceptionCreator.createInstance((value, max) -> {
		return new FormattedCommandMessage("Value (`%1$s`) cannot be greater than %2$s", value, max);
	});

	private static final BiDynamicCommandExceptionCreator<Number, Number> VALUE_TOO_LOW_EXCEPTION = BiDynamicCommandExceptionCreator.createInstance((value, min) -> {
		return new FormattedCommandMessage("Value (`%1$s`) cannot be lower than %2$s", value, min);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_URL_EXCEPTION = DynamicCommandExceptionCreator.createInstance((url) -> {
		return new FormattedCommandMessage("`%s` is not a valid URL", url);
	});

	private static final DynamicCommandExceptionCreator<String> INVALID_EMOJI_EXCEPTION = DynamicCommandExceptionCreator.createInstance((mention) -> {
		return new FormattedCommandMessage("`%s` is not a valid emoji", mention);
	});

	private static final SimpleCommandExceptionCreator CUSTOM_EMOJI_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "Custom emojis cannot be used!");

	private static final DynamicCommandExceptionCreator<String> NO_VALID_EMOJIS_EXCEPTION = DynamicCommandExceptionCreator.createInstance((string) -> {
		return new FormattedCommandMessage("No allowed/valid emojis were found in `%s`", string);
	});

	private static final BiDynamicCommandExceptionCreator<String, Integer> TOO_MANY_EMOJIS_EXCEPTION = BiDynamicCommandExceptionCreator.createInstance((string, max) -> {
		return new FormattedCommandMessage("`%1$s` contains more than %2$s emojis", string, max);
	});

	private static final BiDynamicCommandExceptionCreator<String, Integer> NOT_ENOUGH_EMOJIS_EXCEPTION = BiDynamicCommandExceptionCreator.createInstance((string, max) -> {
		return new FormattedCommandMessage("`%1$s` contains less than %2$s emojis", string, max);
	});

	private static final SimpleCommandExceptionCreator MENTION_CHANNEL_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "Text Channel in mention could not be found");

	private static final DynamicCommandExceptionCreator<Long> CHANNEL_NOT_FOUND_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("Channel with id `%d` could not be found", id);
	}));

	private static final DynamicCommandExceptionCreator<String> INVALID_CHANNEL_ID_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("`%s` is not a valid channel id", id);
	}));

	private static final DynamicCommandExceptionCreator<Long> USER_NOT_FOUND_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("User with id `%d` could not be found", id);
	}));

	private static final SimpleCommandExceptionCreator MENTION_USER_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "Member in mention could not be found");

	private static final DynamicCommandExceptionCreator<String> INVALID_USER_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("`%s` is not a valid member id or valid user mention", id);
	}));

	private static final DynamicCommandExceptionCreator<Long> WEBHOOK_NOT_FOUND_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("Webhook with id `%d` could not be found", id);
	}));

	private static final DynamicCommandExceptionCreator<String> INVALID_WEBHOOK_ID_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("`%s` is not a valid webhook id", id);
	}));

	private static final DynamicCommandExceptionCreator<Long> GUILD_NOT_FOUND_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("Guild with id `%d` could not be found", id);
	}));

	private static final DynamicCommandExceptionCreator<String> INVALID_GUILD_ID_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("`%s` is not a valid guild id", id);
	}));

	private static final DynamicCommandExceptionCreator<Long> ROLE_NOT_FOUND_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("Role with id `%d` could not be found", id);
	}));

	private static final DynamicCommandExceptionCreator<String> INVALID_ROLE_ID_EXCEPTION = DynamicCommandExceptionCreator.createInstance((id -> {
		return new FormattedCommandMessage("`%s` is not a valid role id", id);
	}));

	private static final SimpleCommandExceptionCreator MENTION_ROLE_NOT_FOUND_EXCEPTION = new SimpleCommandExceptionCreator(channel -> "Role in mention could not be found");

	@Override
	public DynamicCommandExceptionCreator<Set<Permission>> getMissingPermissionsException() {
		return PERMISSIONS_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionCreator getNoArgumentsException() {
		return NO_ARGUMENTS_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionCreator getMissingArgumentException() {
		return MISSING_ARGUMENT_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionCreator getMissingArgumentsException() {
		return MISSING_ARGUMENTS_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<Integer> getSpecificMissingArgumentException() {
		return SPECIFIC_MISSING_ARGUMENT_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<List<Integer>> getSpecificMissingArgumentsException() {
		return SPECIFIC_MISSING_ARGUMENTS_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidIntegerException() {
		return INVALID_INTEGER_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidLongException() {
		return INVALID_LONG_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidCharException() {
		return INVALID_CHAR_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidShortException() {
		return INVALID_SHORT_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidByteException() {
		return INVALID_BYTE_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidFloatException() {
		return INVALID_FLOAT_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidDoubleException() {
		return INVALID_DOUBLE_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidEnumException() {
		return INVALID_ENUM_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidNumberException() {
		return INVALID_NUMBER_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidColorException() {
		return INVALID_COLOR_EXCEPTION;
	}

	@Override
	public BiDynamicCommandExceptionCreator<String, Integer> getTooLowStringLengthException() {
		return NOT_ENOUGH_CHARS_EXCEPTION;
	}

	@Override
	public BiDynamicCommandExceptionCreator<String, Integer> getTooHighStringLengthException() {
		return TOO_MANY_CHARS_EXCEPTION;
	}

	@Override
	public BiDynamicCommandExceptionCreator<Number, Number> getValueTooHighException() {
		return VALUE_TOO_HIGH_EXCEPTION;
	}

	@Override
	public BiDynamicCommandExceptionCreator<Number, Number> getValueTooLowException() {
		return VALUE_TOO_LOW_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidURLException() {
		return INVALID_URL_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidEmojiException() {
		return INVALID_EMOJI_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionCreator getCustomEmojiException() {
		return CUSTOM_EMOJI_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getNoValidEmojisException() {
		return NO_VALID_EMOJIS_EXCEPTION;
	}

	@Override
	public BiDynamicCommandExceptionCreator<String, Integer> getTooManyEmojisException() {
		return TOO_MANY_EMOJIS_EXCEPTION;
	}

	@Override
	public BiDynamicCommandExceptionCreator<String, Integer> getNotEnoughEmojisException() {
		return NOT_ENOUGH_EMOJIS_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionCreator getMentionChannelNotFoundException() {
		return MENTION_CHANNEL_NOT_FOUND_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<Long> getChannelNotFoundException() {
		return CHANNEL_NOT_FOUND_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidChannelIdException() {
		return INVALID_CHANNEL_ID_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<Long> getUserNotFoundException() {
		return USER_NOT_FOUND_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionCreator getMentionUserNotFoundException() {
		return MENTION_USER_NOT_FOUND_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidUserException() {
		return INVALID_USER_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<Long> getWebhookNotFoundException() {
		return WEBHOOK_NOT_FOUND_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidWebhookIdException() {
		return INVALID_WEBHOOK_ID_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<Long> getGuildNotFoundException() {
		return GUILD_NOT_FOUND_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidGuildIdException() {
		return INVALID_GUILD_ID_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<Long> getRoleNotFoundException() {
		return ROLE_NOT_FOUND_EXCEPTION;
	}

	@Override
	public DynamicCommandExceptionCreator<String> getInvalidRoleIdException() {
		return INVALID_ROLE_ID_EXCEPTION;
	}

	@Override
	public SimpleCommandExceptionCreator getMentionRoleNotFoundException() {
		return MENTION_ROLE_NOT_FOUND_EXCEPTION;
	}
}
