/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.parser;

import com.google.common.flogger.parser.MessageBuilder;
import com.google.common.flogger.parser.MessageParser;
import com.google.common.flogger.parser.ParseException;

public abstract class BraceStyleMessageParser
extends MessageParser {
    private static final char BRACE_STYLE_SEPARATOR = ',';

    abstract void parseBraceFormatTerm(MessageBuilder<?> var1, int var2, String var3, int var4, int var5, int var6) throws ParseException;

    @Override
    public final void unescape(StringBuilder out, String message, int start, int end) {
        BraceStyleMessageParser.unescapeBraceFormat(out, message, start, end);
    }

    @Override
    protected final <T> void parseImpl(MessageBuilder<T> builder) throws ParseException {
        String message = builder.getMessage();
        int pos = BraceStyleMessageParser.nextBraceFormatTerm(message, 0);
        while (pos >= 0) {
            int trailingPartStart;
            char c;
            int index;
            int indexStart;
            int termStart;
            block10: {
                termStart = pos++;
                indexStart = termStart + 1;
                index = 0;
                while (pos < message.length()) {
                    char digit;
                    if ((digit = (char)((c = message.charAt(pos++)) - 48)) < '\n') {
                        if ((index = 10 * index + digit) < 1000000) continue;
                        throw ParseException.withBounds("index too large", message, indexStart, pos);
                    }
                    break block10;
                }
                throw ParseException.withStartPosition("unterminated parameter", message, termStart);
            }
            int indexLen = pos - 1 - indexStart;
            if (indexLen == 0) {
                throw ParseException.withBounds("missing index", message, termStart, pos);
            }
            if (message.charAt(indexStart) == '0' && indexLen > 1) {
                throw ParseException.withBounds("index has leading zero", message, indexStart, pos - 1);
            }
            if (c == '}') {
                trailingPartStart = -1;
            } else if (c == ',') {
                trailingPartStart = pos;
                do {
                    if (pos != message.length()) continue;
                    throw ParseException.withStartPosition("unterminated parameter", message, termStart);
                } while (message.charAt(pos++) != '}');
            } else {
                throw ParseException.withBounds("malformed index", message, termStart + 1, pos);
            }
            this.parseBraceFormatTerm(builder, index, message, termStart, trailingPartStart, pos);
            pos = BraceStyleMessageParser.nextBraceFormatTerm(message, pos);
        }
    }

    static int nextBraceFormatTerm(String message, int pos) throws ParseException {
        while (pos < message.length()) {
            char c;
            if ((c = message.charAt(pos++)) == '{') {
                return pos - 1;
            }
            if (c != '\'') continue;
            if (pos == message.length()) {
                throw ParseException.withStartPosition("trailing single quote", message, pos - 1);
            }
            if (message.charAt(pos++) == '\'') continue;
            int quote = pos - 2;
            do {
                if (pos != message.length()) continue;
                throw ParseException.withStartPosition("unmatched single quote", message, quote);
            } while (message.charAt(pos++) != '\'');
        }
        return -1;
    }

    static void unescapeBraceFormat(StringBuilder out, String message, int start, int end) {
        int pos = start;
        boolean isQuoted = false;
        while (pos < end) {
            char c;
            if ((c = message.charAt(pos++)) != '\\' && c != '\'') continue;
            int quoteStart = pos - 1;
            if (c == '\\' && (c = message.charAt(pos++)) != '\'') continue;
            out.append(message, start, quoteStart);
            start = pos;
            if (pos == end) break;
            if (isQuoted) {
                isQuoted = false;
                continue;
            }
            if (message.charAt(pos) != '\'') {
                isQuoted = true;
                continue;
            }
            ++pos;
        }
        if (start < end) {
            out.append(message, start, end);
        }
    }
}

