package disparser;

public class TestCommand extends Command {

	public TestCommand() {
		super("test");
	}

	@Override
	public void processCommand(CommandContext context) {
		context.getEvent().getChannel().sendMessage("This is a Test!").queue();
	}

}