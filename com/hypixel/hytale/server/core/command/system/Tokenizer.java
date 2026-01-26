/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.command.system;

import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.command.system.ParseResult;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Tokenizer {
    public static final char MULTI_ARG_SEPARATOR_CHAR = ',';
    public static final char MULTI_ARG_BEGIN_CHAR = '[';
    public static final char MULTI_ARG_END_CHAR = ']';
    public static final String MULTI_ARG_SEPARATOR = String.valueOf(',');
    public static final String MULTI_ARG_BEGIN = String.valueOf('[');
    public static final String MULTI_ARG_END = String.valueOf(']');
    private static final Message MESSAGE_COMMANDS_PARSING_ERROR_UNBALANCED_QUOTES = Message.translation("server.commands.parsing.error.unbalancedQuotes");

    @Nullable
    public static List<String> parseArguments(@Nonnull String input, @Nonnull ParseResult parseResult) {
        ObjectArrayList<String> parsedTokens = new ObjectArrayList<String>();
        String[] firstSplit = input.split(Pattern.quote(" "), 2);
        parsedTokens.add(firstSplit[0]);
        if (firstSplit.length == 1) {
            return parsedTokens;
        }
        Object argsStr = firstSplit[1];
        int quote = 0;
        int tokenStart = 0;
        boolean inList = false;
        for (int i = 0; i < ((String)argsStr).length(); ++i) {
            char c = ((String)argsStr).charAt(i);
            boolean extractToken = false;
            block0 : switch (c) {
                case '\\': {
                    if (((String)argsStr).length() <= i + 1) {
                        parseResult.fail(Message.translation("server.commands.parsing.error.invalidEscape").param("index", i + 1).param("input", input));
                        return null;
                    }
                    char nextCharacter = ((String)argsStr).charAt(i + 1);
                    switch (nextCharacter) {
                        case '\"': 
                        case '\'': 
                        case ',': 
                        case '[': 
                        case '\\': 
                        case ']': {
                            argsStr = ((String)argsStr).substring(0, i) + ((String)argsStr).substring(i + 1);
                            ++i;
                            break block0;
                        }
                    }
                    parseResult.fail(Message.translation("server.commands.parsing.error.invalidEscapeForSymbol").param("symbol", nextCharacter).param("index", i + 1).param("input", input).param("command", input));
                    return null;
                }
                case ' ': {
                    if (quote != 0) break;
                    if (tokenStart < i) {
                        parsedTokens.add(((String)argsStr).substring(tokenStart, i));
                    }
                    tokenStart = i + 1;
                    break;
                }
                case '\"': {
                    if (quote == 0) {
                        quote = 34;
                        break;
                    }
                    if (quote != 34) break;
                    quote = 0;
                    String extraction = ((String)argsStr).substring(tokenStart, i + 1);
                    if (!extraction.isEmpty()) {
                        parsedTokens.add(extraction);
                    }
                    tokenStart = i + 1;
                    break;
                }
                case '\'': {
                    if (quote == 0) {
                        quote = 39;
                        break;
                    }
                    if (quote != 39) break;
                    quote = 0;
                    String extraction = ((String)argsStr).substring(tokenStart, i + 1);
                    if (!extraction.isEmpty()) {
                        parsedTokens.add(extraction);
                    }
                    tokenStart = i + 1;
                    break;
                }
                case '[': {
                    if (quote != 0) break;
                    if (inList) {
                        parseResult.fail(Message.translation("server.commands.parsing.error.cannotBeginListInsideList").param("index", i));
                        return null;
                    }
                    inList = true;
                    tokenStart = i;
                    extractToken = true;
                    break;
                }
                case ']': {
                    if (quote != 0) break;
                    if (!inList) {
                        parseResult.fail(Message.translation("server.commands.parsing.error.cannotEndListWithoutStarting").param("index", i));
                        return null;
                    }
                    String extraction = ((String)argsStr).substring(tokenStart, i);
                    if (!extraction.isEmpty()) {
                        parsedTokens.add(extraction);
                    }
                    tokenStart = i;
                    inList = false;
                    extractToken = true;
                    break;
                }
                case ',': {
                    if (quote != 0) break;
                    String extraction = ((String)argsStr).substring(tokenStart, i);
                    if (!extraction.isEmpty()) {
                        parsedTokens.add(extraction);
                    }
                    tokenStart = i;
                    extractToken = true;
                }
            }
            if (extractToken) {
                parsedTokens.add(((String)argsStr).substring(tokenStart, i + 1));
                tokenStart = i + 1;
            }
            if (tokenStart <= ((String)argsStr).length()) continue;
            tokenStart = ((String)argsStr).length();
            break;
        }
        if (quote != 0) {
            parseResult.fail(MESSAGE_COMMANDS_PARSING_ERROR_UNBALANCED_QUOTES);
            return null;
        }
        if (tokenStart != ((String)argsStr).length()) {
            parsedTokens.add(((String)argsStr).substring(tokenStart));
        }
        return parsedTokens;
    }
}

