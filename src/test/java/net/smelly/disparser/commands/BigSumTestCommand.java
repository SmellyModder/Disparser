package net.smelly.disparser.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.smelly.disparser.Command;
import net.smelly.disparser.ConfiguredArgument;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.context.tree.ArgumentNode;
import net.smelly.disparser.context.tree.RootNode;
import net.smelly.disparser.feedback.FeedbackHandler;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Threading test.
 */
public final class BigSumTestCommand extends Command<MessageReceivedEvent, MessageCommandContext> {
	private static final RootNode<MessageReceivedEvent, MessageCommandContext> NODE = RootNode.Builder.create(MessageCommandContext.class)
		.then(
			ArgumentNode.Builder.create(ConfiguredArgument.named(IntegerArgument.getMin(100000), channel -> "iterations"), MessageCommandContext.class)
				.consumes(BigSumTestCommand::processSum)
		).build();
	private static final AtomicInteger SUM_NUMBER = new AtomicInteger();
	private static final ConcurrentLinkedQueue<Integer> QUEUE = new ConcurrentLinkedQueue<>();

	public BigSumTestCommand() {
		super("sum", NODE);
	}

	private static void processSum(MessageCommandContext context) {
		FeedbackHandler feedbackHandler = context.getFeedbackHandler();
		feedbackHandler.sendFeedback(channel -> "Queueing random sum, this may take some time to complete!");
		int sumNumber = SUM_NUMBER.incrementAndGet();
		QUEUE.add(sumNumber);
		int sequences = context.getParsedResult(0);
		int sum = 0;
		Random random = ThreadLocalRandom.current();
		for (int i = 0; i < sequences; i++) {
			sum += random.nextInt(10);
		}
		while (true) {
			if (!QUEUE.contains(sumNumber - 1)) {
				QUEUE.remove(sumNumber);
				break;
			}
		}
		EmbedBuilder embedBuilder = getBaseEmbedBuilder();
		embedBuilder.appendDescription("Sum #" + sumNumber + " " + sum);
		embedBuilder.appendDescription("\nI'm from thread " + Thread.currentThread().getName() + "!");
		feedbackHandler.sendFeedback(embedBuilder.build());
	}

	private static EmbedBuilder getBaseEmbedBuilder() {
		return new EmbedBuilder().setTitle("Sum Results: ").setColor(7506394);
	}
}
