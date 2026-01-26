/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util.expression.compile;

import com.hypixel.hytale.server.npc.util.expression.compile.LexerContext;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.text.ParseException;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Lexer<Token extends Supplier<String>> {
    public static final String UNTERMINATED_STRING = "Unterminated string";
    public static final String INVALID_NUMBER_FORMAT = "Invalid number format";
    public static final String INVALID_CHARACTER_IN_EXPRESSION = "Invalid character in expression :";
    private final Token tokenEnd;
    private final Token tokenIdent;
    private final Token tokenString;
    private final Token tokenNumber;
    private final CharacterSequenceMatcher<Token> characterSequenceMatcher;

    public Lexer(Token tokenEnd, Token tokenIdent, Token tokenString, Token tokenNumber, @Nonnull Stream<Token> operators) {
        this.tokenEnd = tokenEnd;
        this.tokenIdent = tokenIdent;
        this.tokenString = tokenString;
        this.tokenNumber = tokenNumber;
        this.characterSequenceMatcher = new CharacterSequenceMatcher();
        operators.forEach(token -> this.characterSequenceMatcher.addToken((Supplier)token, (String)token.get()));
    }

    public Token nextToken(@Nonnull LexerContext<Token> context) throws ParseException {
        context.resetToken();
        if (!context.eatWhiteSpace()) {
            return (Token)((Supplier)context.setToken((Supplier)this.tokenEnd));
        }
        char ch = context.currentChar();
        if (Character.isLetter(ch) || ch == '_') {
            context.parseIdent(ch);
            return (Token)((Supplier)context.setToken((Supplier)this.tokenIdent));
        }
        if (context.isNumber(ch)) {
            context.parseNumber(ch);
            return (Token)((Supplier)context.setToken((Supplier)this.tokenNumber));
        }
        if (ch == '\"' || ch == '\'') {
            context.parseString(ch);
            return (Token)((Supplier)context.setToken((Supplier)this.tokenString));
        }
        CharacterSequenceMatcher<Token> lastTerminal = null;
        int lastValidPosition = context.getPosition();
        for (CharacterSequenceMatcher<Token> matcher = this.characterSequenceMatcher.matchLetter(ch); matcher != null; matcher = matcher.matchLetter(ch)) {
            if (matcher.token != null) {
                lastValidPosition = context.getPosition();
                lastTerminal = matcher;
            }
            ch = context.addTokenCharacter(ch);
            if (!context.haveChar()) break;
        }
        if (lastTerminal != null) {
            context.adjustPosition(lastValidPosition + 1);
            return (Token)context.setToken((Supplier)lastTerminal.token);
        }
        throw new ParseException(INVALID_CHARACTER_IN_EXPRESSION + ch, context.getTokenPosition());
    }

    protected static class CharacterSequenceMatcher<Token> {
        @Nullable
        public Token token = null;
        public char letter;
        @Nullable
        public List<CharacterSequenceMatcher<Token>> children;

        public CharacterSequenceMatcher() {
            this.letter = '\u0000';
            this.children = null;
        }

        public CharacterSequenceMatcher(char letter) {
            this.letter = letter;
            this.children = null;
        }

        protected void addToken(Token token, int depth, @Nonnull String text, int maxDepth) {
            int index;
            char ch = text.charAt(depth);
            if (this.children == null) {
                this.children = new ObjectArrayList<CharacterSequenceMatcher<Token>>();
                this.append(token, depth, text, maxDepth, ch);
                return;
            }
            int size = this.children.size();
            for (index = 0; index < size && this.children.get((int)index).letter < ch; ++index) {
            }
            if (index == size) {
                this.append(token, depth, text, maxDepth, ch);
            } else {
                CharacterSequenceMatcher<Token> child = this.children.get(index);
                if (child.letter == ch) {
                    if (depth == maxDepth) {
                        if (child.token != null) {
                            throw new RuntimeException("Duplicate operator " + text);
                        }
                        child.token = token;
                    } else {
                        child.addToken(token, depth + 1, text, maxDepth);
                    }
                } else {
                    CharacterSequenceMatcher<Token> lookup = new CharacterSequenceMatcher<Token>(ch);
                    this.children.add(index, lookup);
                    this.addTail(token, depth, text, maxDepth, lookup);
                }
            }
        }

        protected void addToken(Token token, @Nonnull String text) {
            this.addToken(token, 0, text, text.length() - 1);
        }

        private void append(Token token, int depth, @Nonnull String text, int maxDepth, char ch) {
            CharacterSequenceMatcher<Token> lookup = new CharacterSequenceMatcher<Token>(ch);
            this.children.add(lookup);
            this.addTail(token, depth, text, maxDepth, lookup);
        }

        private void addTail(Token token, int depth, @Nonnull String text, int maxDepth, @Nonnull CharacterSequenceMatcher<Token> lookup) {
            if (depth == maxDepth) {
                lookup.token = token;
            } else {
                lookup.addToken(token, depth + 1, text, maxDepth);
            }
        }

        @Nullable
        protected CharacterSequenceMatcher<Token> matchLetter(char ch) {
            if (this.children != null) {
                int size = this.children.size();
                for (int index = 0; index < size; ++index) {
                    CharacterSequenceMatcher<Token> characterSequenceMatcher = this.children.get(index);
                    char letter = characterSequenceMatcher.letter;
                    if (letter == ch) {
                        return characterSequenceMatcher;
                    }
                    if (letter <= ch) continue;
                    return null;
                }
            }
            return null;
        }
    }
}

