package net.smelly.disparser.commands;

import net.smelly.disparser.annotations.Aliases;
import net.smelly.disparser.annotations.Permissions;
import net.dv8tion.jda.api.Permission;

public class Commands {
	@Aliases(value = {"optional", "test_optional"}, mergeAliases = true)
	public static final OptionalTestCommand OPTIONAL_TEST = new OptionalTestCommand();
	
	@Aliases(value = {"test_complex", "multi"}, mergeAliases = true)
	public static final ComplexTestCommand COMPLEX_TEST = new ComplexTestCommand();
	
	@Aliases(value = {"enum_test"}, mergeAliases = true)
	public static final EnumTestCommand ENUM_TEST = new EnumTestCommand();
	
	@Permissions(Permission.ADMINISTRATOR)
	@Aliases(value = {"rename_test"}, mergeAliases = true)
	public static final RenameChannelTestCommand RENAME_CHANNEL_TEST = new RenameChannelTestCommand();
	
	@Aliases(value = {"pfp"}, mergeAliases = true)
	public static final UserProfileTestCommand USER_PROFILE_TEST = new UserProfileTestCommand();

	@Aliases(value = {"number_test"}, mergeAliases = true)
	public static final NumberTestCommand NUMBER_TEST = new NumberTestCommand();

	@Aliases(value = {"color_test"}, mergeAliases = true)
	public static final ColorTestCommand COLOR_TEST = new ColorTestCommand();
	
	@Aliases(value = {"normal_test"}, mergeAliases = true)
	public static final TestCommand TEST_COMMAND = new TestCommand();
}