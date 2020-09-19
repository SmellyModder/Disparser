package disparser.commands;

import disparser.Command;
import disparser.CommandContext;
import disparser.arguments.primitive.ColorArgument;
import net.dv8tion.jda.api.EmbedBuilder;

public final class ColorTestCommand extends Command {

	public ColorTestCommand() {
		super("color", ColorArgument.get());
	}

	@Override
	public void processCommand(CommandContext context) throws Exception {
		context.getFeedbackHandler().sendFeedback(
				new EmbedBuilder().addBlankField(false).setColor(context.getParsedResult(0)).build()
		);
	}

}
