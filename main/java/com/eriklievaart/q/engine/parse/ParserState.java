package com.eriklievaart.q.engine.parse;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

import com.eriklievaart.toolkit.lang.api.concurrent.Immutable;

/**
 * This class represents the different ParserStates that ShellParser can go through. When the parser starts it is in the
 * new state and a command that will be executed has to be specified. if so done, it transitions to the configure state.
 * In the configure state flags and arguments to the command can be added. The configure state ends when there are no
 * more tokens, or if a swallow token is encountered. In the last case, the parser would move to the swallow state. In
 * the swallow state, the only thing that can be accepted is a single raw token which is required. Strictly speaking
 * there is an additional FINISHED state which the parser moves to after receiving the raw token and parsing completes
 * successfully when an EOL token (there is no such TokenType) is received in the configure or finished state. These
 * last few rules are implicit.
 *
 * @author Erik Lievaart
 */
@Immutable
class ParserState {

	static final ParserState SWALLOW = new ParserState(EnumSet.of(TokenType.RAW), Collections.EMPTY_MAP);
	static final ParserState CONFIGURE = new ParserState(
			EnumSet.of(TokenType.FLAG, TokenType.VARIABLE, TokenType.STRING, TokenType.SWALLOW),
			map(TokenType.SWALLOW, SWALLOW));
	static final ParserState NEW = new ParserState(EnumSet.of(TokenType.COMMAND), map(TokenType.COMMAND, CONFIGURE));

	private final Set<TokenType> validTokens;
	private final Map<TokenType, ParserState> transitions;

	private ParserState(final EnumSet<TokenType> validTokens, final Map<TokenType, ParserState> transitions) {
		this.validTokens = Collections.unmodifiableSet(validTokens);
		this.transitions = transitions;
	}

	Set<TokenType> getValidTokens() {
		return validTokens;
	}

	Map<TokenType, ParserState> transitions() {
		return transitions;
	}

	String tokenString() {
		return validTokens.toString();
	}

	private static Map<TokenType, ParserState> map(final TokenType type, final ParserState state) {
		Map<TokenType, ParserState> map = new EnumMap<>(TokenType.class);
		map.put(type, state);
		return Collections.unmodifiableMap(map);
	}
}