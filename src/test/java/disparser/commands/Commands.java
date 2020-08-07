package disparser.commands;

import disparser.annotations.Aliases;

public class Commands {
	@Aliases(value = {"optional", "test_optional"}, mergeAliases = true)
	public static final OptionalTestCommand OPTIONAL_TEST = new OptionalTestCommand();
	
	@Aliases(value = {"test_complex", "multi"}, mergeAliases = true)
	public static final ComplexTestCommand COMPLEX_TEST = new ComplexTestCommand();
	
	@Aliases(value = {"enum_test"}, mergeAliases = true)
	public static final EnumTestCommand ENUM_TEST = new EnumTestCommand();
	
	@Aliases(value = {"normal_test"}, mergeAliases = true)
	public static final TestCommand TEST_COMMAND = new TestCommand();
}