package net.smelly.disparser.feedback;

import net.dv8tion.jda.api.entities.TextChannel;

import javax.annotation.Nullable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public final class TestTranslatableCommandMessage implements CommandMessage {
	private static final ConcurrentHashMap<Long, Language> MAP = new ConcurrentHashMap<>();

	static {
		MAP.put(735963087152349326L, Language.ENGLISH_UK);
	}

	private final String translatableString;
	private final String invalidColor;

	public TestTranslatableCommandMessage(String translatableString, String invalidColor) {
		this.translatableString = translatableString;
		this.invalidColor = invalidColor;
	}

	@Override
	public String getMessage(@Nullable TextChannel channel) {
		return MAP.getOrDefault(channel != null ? channel.getGuild().getIdLong() : 0L, Language.ENGLISH_US).translator.apply(this.translatableString) + this.invalidColor;
	}

	//Example, translations really shouldn't be done like this...
	enum Language {
		ENGLISH_US(s -> s.equals("command.exception.color") ? "Invalid Color: " : s),
		ENGLISH_UK(s -> s.equals("command.exception.color") ? "Invalid Colour: " : s);

		private final Function<String, String> translator;

		Language(Function<String, String> translator) {
			this.translator = translator;
		}
	}
}
