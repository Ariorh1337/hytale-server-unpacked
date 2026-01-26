/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.reader.ParsedLine;
import org.jline.reader.SyntaxError;

public interface Parser {
    public static final String REGEX_VARIABLE = "[a-zA-Z_]+[a-zA-Z0-9_-]*";
    public static final String REGEX_COMMAND = "[:]?[a-zA-Z]+[a-zA-Z0-9_-]*";

    public ParsedLine parse(String var1, int var2, ParseContext var3) throws SyntaxError;

    default public ParsedLine parse(String line, int cursor) throws SyntaxError {
        return this.parse(line, cursor, ParseContext.UNSPECIFIED);
    }

    default public boolean isEscapeChar(char ch) {
        return ch == '\\';
    }

    default public boolean validCommandName(String name) {
        return name != null && name.matches(REGEX_COMMAND);
    }

    default public boolean validVariableName(String name) {
        return name != null && name.matches(REGEX_VARIABLE);
    }

    default public String getCommand(String line) {
        String out;
        Pattern patternCommand = Pattern.compile("^\\s*[a-zA-Z_]+[a-zA-Z0-9_-]*=([:]?[a-zA-Z]+[a-zA-Z0-9_-]*)(\\s+|$)");
        Matcher matcher = patternCommand.matcher(line);
        if (matcher.find()) {
            out = matcher.group(1);
        } else {
            out = line.trim().split("\\s+")[0];
            if (!out.matches(REGEX_COMMAND)) {
                out = "";
            }
        }
        return out;
    }

    default public String getVariable(String line) {
        String out = null;
        Pattern patternCommand = Pattern.compile("^\\s*([a-zA-Z_]+[a-zA-Z0-9_-]*)\\s*=[^=~].*");
        Matcher matcher = patternCommand.matcher(line);
        if (matcher.find()) {
            out = matcher.group(1);
        }
        return out;
    }

    public static enum ParseContext {
        UNSPECIFIED,
        ACCEPT_LINE,
        SPLIT_LINE,
        COMPLETE,
        SECONDARY_PROMPT;

    }
}

