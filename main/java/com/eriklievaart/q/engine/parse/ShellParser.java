package com.eriklievaart.q.engine.parse;

import java.util.List;

import com.eriklievaart.q.engine.exception.ShellCommandMissingException;
import com.eriklievaart.q.engine.exception.ShellFlagMissingException;
import com.eriklievaart.q.engine.exception.ShellParseException;
import com.eriklievaart.toolkit.lang.api.collection.NewCollection;
import com.eriklievaart.toolkit.lang.api.concurrent.Stateless;
import com.eriklievaart.toolkit.lang.api.str.Str;

/**
 * Parses input lines from the CommandLine. Creates a single ShellCommand Object from an input string.
 *
 * @author Erik Lievaart
 */
@Stateless
public class ShellParser {

	private ParserState state = ParserState.NEW;

	private String command;
	private final List<Character> flags = NewCollection.list();
	private final List<ShellArgument> arguments = NewCollection.list();
	private String swallowed = null;

	private ShellParser() {
	}

	/**
	 * Parse a line of input into a ShellCommand.
	 *
	 * @param input
	 *            line of input conforming to the Q shell.
	 * @return the created ShellCommand object.
	 * @throws ShellParseException
	 *             if the input was not properly formatted.
	 */
	public static ShellCommand parseLine(final String input) throws ShellParseException {
		ShellCommandMissingException.on(Str.isBlank(input), "No command specified!");
		return new ShellParser().parse(input);
	}

	/**
	 * Utility method for parsing a single command line argument.
	 *
	 * @return the ShellArgument parsed.
	 */
	public static ShellArgument parseArgument(final String argument) throws ShellParseException {
		List<Token> tokens = ShellLexer.tokens(argument);
		ShellParseException.on(tokens.size() != 1, "Expected exactly one token, got: %", tokens);

		Token token = tokens.get(0);
		ShellParseException.unless(token.getType().isArgument(), "Expected a command line argument: %", token);
		return extractToken(token.getType(), token.getValue());
	}

	private ShellCommand parse(final String input) throws ShellParseException {

		for (Token token : ShellLexer.tokens(input)) {
			validateToken(token);

			if (token.getType() != TokenType.SWALLOW) {
				addToken(token.getType(), token.getValue());
			}
			ParserState stateChange = state.transitions().get(token.getType());
			state = stateChange == null ? state : stateChange;
		}
		return createShellCommand();
	}

	private void addToken(final TokenType type, final String value) {
		switch (type) {

		case COMMAND:
			command = value.toLowerCase();
			return;

		case FLAG:
			flags.add(value.toLowerCase().charAt(0));
			return;

		case RAW:
			swallowed = value;
			return;

		case STRING:
		case VARIABLE:
			arguments.add(extractToken(type, value));
			return;
		}
		throw new IllegalStateException("Unknown TokenType, this is a programming error: " + Str.quote(type));
	}

	private static ShellArgument extractToken(final TokenType type, final String value) {
		switch (type) {

		case STRING:
			return ShellArgument.string(value);

		case VARIABLE:
			return ShellArgument.variable(value);
		}
		throw new IllegalStateException("Unknown TokenType, this is a programming error: " + Str.quote(type));
	}

	private void validateToken(final Token token) throws ShellParseException {
		if (token.getType() == TokenType.UNRECOGNIZED) {
			ShellFlagMissingException.on(token.getValue().equals("-"), createShellCommand(), "Missing flag for '-'");
			throw new ShellParseException("% expected: %", token.error(), state.tokenString());
		}
		boolean validToken = state.getValidTokens().contains(token.getType());
		ShellParseException.unless(validToken, "Invalid token: % expected: %", token, state.tokenString());
	}

	private ShellCommand createShellCommand() {
		return new ShellCommand(command, flags, arguments, swallowed);
	}

}
