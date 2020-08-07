package disparser.arguments;

import disparser.Argument;
import disparser.ArgumentReader;
import disparser.ParsedArgument;
import disparser.arguments.EnumTestArgument.ArgumentEnum;

public class EnumTestArgument implements Argument<ArgumentEnum> {

	@Override
	public ParsedArgument<ArgumentEnum> parse(ArgumentReader reader) {
		return reader.parseNextArgument((message) -> {
			for (ArgumentEnum type : ArgumentEnum.values()) {
				if (type.toString().equalsIgnoreCase(message)) {
					return ParsedArgument.parse(type);
				}
			}
			return ParsedArgument.parseError(message + " is not a valid type");
		});
	}
	
	public enum ArgumentEnum {
		A("Alphabet Soup"),
		B("Bees..."),
		C("Chad");
		
		public final String message;
		
		ArgumentEnum(String message)  {
			this.message = message;
		}
	}
	
}