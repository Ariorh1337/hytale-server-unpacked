/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.util.expression.compile;

import com.hypixel.hytale.server.npc.util.expression.compile.Lexer;
import com.hypixel.hytale.server.npc.util.expression.compile.LexerContext;
import com.hypixel.hytale.server.npc.util.expression.compile.Token;
import com.hypixel.hytale.server.npc.util.expression.compile.TokenFlags;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Parser {
    public static final String MISMATCHED_CLOSING_BRACKET = "Mismatched closing bracket";
    public static final String TOO_MANY_OPERANDS = "Too many operands";
    public static final String NOT_ENOUGH_OPERANDS = "Not enough operands";
    public static final String EXPECTED_UNARY_OPERATOR = "Expected unary operator";
    public static final String EXPECTED_BINARY_OPERATOR = "Expected binary operator";
    public static final String MISSING_CLOSING_BRACKET = "Missing closing bracket";
    public static final String ILLEGAL_USE_OF_ARGUMENT_LIST = "Illegal use of argument list";
    private Lexer<Token> lexer;
    private LexerContext<Token> context;
    @Nonnull
    private Deque<ParsedToken> operatorStack = new ArrayDeque<ParsedToken>();
    @Nonnull
    private Deque<ParsedToken> bracketStack = new ArrayDeque<ParsedToken>();

    public Parser(Lexer<Token> lexer) {
        this.lexer = lexer;
        this.context = new LexerContext();
    }

    @Nonnull
    private ParsedToken nextToken() throws ParseException {
        return ParsedToken.fromLexer(this.lexer, this.context);
    }

    public void parse(@Nonnull String expression, @Nonnull ParsedTokenConsumer tokenConsumer) throws ParseException {
        this.operatorStack.clear();
        this.bracketStack.clear();
        this.bracketStack.push(new ParsedToken(Token.END));
        this.context.init(expression);
        ParsedToken parsedToken = this.nextToken();
        Token token = parsedToken.token;
        Token lastToken = null;
        ParsedToken bracket = this.bracketStack.peek();
        while (!token.isEndToken()) {
            if (token.isOperand()) {
                tokenConsumer.pushOperand(parsedToken);
                ++bracket.operandCount;
            } else if (token.isOpenBracket()) {
                if (token == Token.OPEN_BRACKET) {
                    if (lastToken == Token.IDENTIFIER) {
                        parsedToken.isTuple = true;
                        parsedToken.isFunctionCall = true;
                    }
                } else if (token.isOpenTuple()) {
                    parsedToken.isTuple = true;
                    parsedToken.isFunctionCall = false;
                }
                this.operatorStack.push(parsedToken);
                this.bracketStack.push(parsedToken);
                bracket = this.bracketStack.peek();
            } else if (token.isCloseBracket()) {
                int deltaArity;
                Token otherBracket = token.getMatchingBracket();
                if (bracket.token != otherBracket) {
                    throw new ParseException(MISMATCHED_CLOSING_BRACKET, parsedToken.tokenPosition);
                }
                ParsedToken first = this.operatorStack.pop();
                while (!first.token.isOpenBracket()) {
                    bracket.operandCount = this.adjustOperandCount(first, bracket.operandCount);
                    tokenConsumer.processOperator(first);
                    first = this.operatorStack.pop();
                }
                this.validateOperandCount(bracket);
                if (bracket.isFunctionCall) {
                    bracket.tupleLength += bracket.operandCount;
                    tokenConsumer.processFunction(bracket.tupleLength);
                    deltaArity = 0;
                } else if (bracket.isTuple) {
                    bracket.tupleLength += bracket.operandCount;
                    tokenConsumer.processTuple(bracket, bracket.tupleLength);
                    deltaArity = 1;
                } else {
                    deltaArity = 1;
                }
                this.bracketStack.pop();
                bracket = this.bracketStack.peek();
                bracket.operandCount += deltaArity;
            } else if (token.isList()) {
                if (!bracket.isTuple) {
                    throw new ParseException(ILLEGAL_USE_OF_ARGUMENT_LIST, parsedToken.tokenPosition);
                }
                ParsedToken first = this.peekOperator();
                while (!first.token.isOpenBracket()) {
                    bracket.operandCount = this.adjustOperandCount(first, bracket.operandCount);
                    tokenConsumer.processOperator(first);
                    this.operatorStack.pop();
                    first = this.peekOperator();
                }
                this.validateOperandCount(bracket);
                ++bracket.tupleLength;
                bracket.operandCount = 0;
            } else if (token.isOperator()) {
                boolean mustBeUnary;
                boolean bl = mustBeUnary = lastToken == null || lastToken.containsAnyFlag(EnumSet.of(TokenFlags.OPERATOR, TokenFlags.LIST, TokenFlags.OPENING_BRACKET));
                if (token.canBeUnary() && mustBeUnary) {
                    parsedToken.token = token = token.getUnaryVariant();
                } else {
                    if (mustBeUnary && !token.isUnary()) {
                        throw new ParseException(EXPECTED_UNARY_OPERATOR, parsedToken.tokenPosition);
                    }
                    if (token.isUnary() && !mustBeUnary) {
                        throw new ParseException(EXPECTED_BINARY_OPERATOR, parsedToken.tokenPosition);
                    }
                }
                ParsedToken stackToken = this.peekOperator();
                while (this.hasLowerPrecedence(token, stackToken)) {
                    bracket.operandCount = this.adjustOperandCount(stackToken, bracket.operandCount);
                    tokenConsumer.processOperator(stackToken);
                    this.operatorStack.pop();
                    stackToken = this.peekOperator();
                }
                this.operatorStack.push(parsedToken);
            } else {
                throw new RuntimeException("Internal parser error: " + String.valueOf(token));
            }
            lastToken = token;
            parsedToken = this.nextToken();
            token = parsedToken.token;
        }
        if (bracket.token != Token.END) {
            throw new ParseException(MISSING_CLOSING_BRACKET, bracket.tokenPosition);
        }
        while (!this.operatorStack.isEmpty()) {
            parsedToken = this.operatorStack.pop();
            bracket.operandCount = this.adjustOperandCount(parsedToken, bracket.operandCount);
            tokenConsumer.processOperator(parsedToken);
        }
        this.validateOperandCount(bracket);
        tokenConsumer.done();
    }

    @Nullable
    public ParsedToken peekOperator() {
        return this.operatorStack.isEmpty() ? null : this.operatorStack.peek();
    }

    private void validateOperandCount(@Nonnull ParsedToken bracket) throws ParseException {
        if (bracket.isTuple && bracket.tupleLength == 0 && bracket.operandCount == 0) {
            return;
        }
        if (bracket.operandCount <= 0) {
            throw new ParseException(NOT_ENOUGH_OPERANDS, 0);
        }
        if (bracket.operandCount > 1) {
            throw new ParseException(TOO_MANY_OPERANDS, 0);
        }
    }

    private int adjustOperandCount(@Nonnull ParsedToken parsedToken, int operandCount) throws ParseException {
        int requiredOperands = this.arity(parsedToken.token);
        if (operandCount < requiredOperands) {
            throw new ParseException(NOT_ENOUGH_OPERANDS, parsedToken.tokenPosition);
        }
        return operandCount - requiredOperands + 1;
    }

    private boolean hasLowerPrecedence(@Nonnull Token token, @Nullable ParsedToken stackToken) {
        int stackTokenPrecedence;
        if (stackToken == null || stackToken.token.isList() || stackToken.token.isOpenBracket()) {
            return false;
        }
        int tokenPrecedence = token.getPrecedence();
        return tokenPrecedence == (stackTokenPrecedence = stackToken.token.getPrecedence()) ? !token.isRightToLeft() : tokenPrecedence < stackTokenPrecedence;
    }

    private int arity(@Nonnull Token operator) {
        if (!operator.isOperator()) {
            throw new RuntimeException("Arity only possible with operators");
        }
        return operator.isUnary() ? 1 : 2;
    }

    public static class ParsedToken {
        @Nullable
        public Token token;
        @Nullable
        public String tokenString;
        public double tokenNumber;
        public int tokenPosition;
        public int operandCount;
        public boolean isTuple;
        public boolean isFunctionCall;
        public int tupleLength;

        public ParsedToken(@Nonnull LexerContext<Token> context) {
            this(context.getToken());
            this.tokenString = context.getTokenString();
            this.tokenNumber = context.getTokenNumber();
            this.tokenPosition = context.getTokenPosition();
        }

        public ParsedToken(Token token) {
            this.token = token;
            this.tokenString = null;
            this.tokenNumber = 0.0;
            this.tokenPosition = 0;
            this.operandCount = 0;
            this.isTuple = false;
            this.isFunctionCall = false;
            this.tupleLength = 0;
        }

        @Nonnull
        static ParsedToken fromLexer(@Nonnull Lexer<Token> lexer, @Nonnull LexerContext<Token> context) throws ParseException {
            lexer.nextToken(context);
            return new ParsedToken(context);
        }
    }

    public static interface ParsedTokenConsumer {
        public void pushOperand(ParsedToken var1);

        public void processOperator(ParsedToken var1) throws ParseException;

        public void processFunction(int var1) throws ParseException;

        public void processTuple(ParsedToken var1, int var2);

        public void done();
    }
}

