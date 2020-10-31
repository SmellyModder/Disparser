package net.smelly.disparser.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.smelly.disparser.Command;
import net.smelly.disparser.arguments.java.IntegerArgument;
import net.smelly.disparser.context.MessageCommandContext;
import net.smelly.disparser.feedback.FeedbackHandler;

import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Threading test.
 */
public final class BigSumTestCommand extends Command<MessageCommandContext> {
	private final AtomicInteger sumNumber = new AtomicInteger();
	private final ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

	public BigSumTestCommand() {
		super("sum", IntegerArgument.getMin(100000));
	}

	@Override
	public void processCommand(MessageCommandContext context) throws Exception {
		FeedbackHandler feedbackHandler = context.getFeedbackHandler();
		feedbackHandler.sendFeedback(channel -> "Queueing random sum, this may take some time to complete!");
		int sumNumber = this.sumNumber.incrementAndGet();
		this.queue.add(sumNumber);
		int sequences = context.getParsedResult(0);
		int sum = 0;
		Random random = ThreadLocalRandom.current();
		for (int i = 0; i < sequences; i++) {
			sum += random.nextInt(10);
		}
		while (true) {
			if (!this.queue.contains(sumNumber - 1)) {
				this.queue.remove(sumNumber);
				break;
			}
		}
		EmbedBuilder embedBuilder = this.getBaseEmbedBuilder();
		embedBuilder.appendDescription("Sum #" + sumNumber + " " + sum);
		embedBuilder.appendDescription("\nI'm from thread " + Thread.currentThread().getName() + "!");
		feedbackHandler.sendFeedback(embedBuilder.build());
	}

	private EmbedBuilder getBaseEmbedBuilder() {
		return new EmbedBuilder().setTitle("Sum Results: ").setColor(7506394);
	}
}
