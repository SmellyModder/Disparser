package net.smelly.disparser.commands;

import net.dv8tion.jda.api.Permission;
import net.smelly.disparser.annotations.Aliases;
import net.smelly.disparser.annotations.Context;
import net.smelly.disparser.annotations.Permissions;
import net.smelly.disparser.context.GuildMessageCommandContext;
import net.smelly.disparser.context.MessageCommandContext;

public final class Commands {
	@Context(MessageCommandContext.class)
	@Aliases(value = {"optional", "test_optional"}, mergeAliases = true)
	public static final OptionalTestCommand OPTIONAL_TEST = new OptionalTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = {"test_complex", "multi"}, mergeAliases = true)
	public static final ComplexTestCommand COMPLEX_TEST = new ComplexTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "enum_test", mergeAliases = true)
	public static final EnumTestCommand ENUM_TEST = new EnumTestCommand();

	@Context(GuildMessageCommandContext.class)
	@Permissions(Permission.ADMINISTRATOR)
	@Aliases(value = "rename_test", mergeAliases = true)
	public static final RenameChannelTestCommand RENAME_CHANNEL_TEST = new RenameChannelTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "pfp", mergeAliases = true)
	public static final UserProfileTestCommand USER_PROFILE_TEST = new UserProfileTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "number_test", mergeAliases = true)
	public static final NumberTestCommand NUMBER_TEST = new NumberTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "color_test", mergeAliases = true)
	public static final ColorTestCommand COLOR_TEST = new ColorTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "url_test", mergeAliases = true)
	public static final URLTestCommand URL_TEST = new URLTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "emoji_test", mergeAliases = true)
	public static final EmojiTestCommand EMOJI_TEST_COMMAND = new EmojiTestCommand();

	@Context(GuildMessageCommandContext.class)
	@Aliases(value = "role_test", mergeAliases = true)
	public static final RoleTestCommand ROLE_TEST_COMMAND = new RoleTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "sum_test", mergeAliases = true)
	public static final BigSumTestCommand SUM_TEST_COMMAND = new BigSumTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "boolean_test", mergeAliases = true)
	public static final BooleanTestCommand BOOLEAN_TEST_COMMAND = new BooleanTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "ping_test", mergeAliases = true)
	public static final PingTestCommand PING_TEST_COMMAND = new PingTestCommand();

	@Context(MessageCommandContext.class)
	@Aliases(value = "normal_test", mergeAliases = true)
	public static final TestCommand TEST_COMMAND = new TestCommand();
}