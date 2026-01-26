/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.parser;

import com.google.common.flogger.parser.MessageBuilder;
import com.google.common.flogger.parser.MessageParser;
import com.google.common.flogger.parser.ParseException;

public abstract class PrintfMessageParser
extends MessageParser {
    private static final String ALLOWED_NEWLINE_PATTERN = "\\n|\\r(?:\\n)?";
    private static final String SYSTEM_NEWLINE = PrintfMessageParser.getSafeSystemNewline();

    static String getSafeSystemNewline() {
        try {
            String unsafeNewline = System.getProperty("line.separator");
            if (unsafeNewline.matches(ALLOWED_NEWLINE_PATTERN)) {
                return unsafeNewline;
            }
        }
        catch (SecurityException securityException) {
            // empty catch block
        }
        return "\n";
    }

    abstract int parsePrintfTerm(MessageBuilder<?> var1, int var2, String var3, int var4, int var5, int var6) throws ParseException;

    @Override
    public final void unescape(StringBuilder out, String message, int start, int end) {
        PrintfMessageParser.unescapePrintf(out, message, start, end);
    }

    @Override
    protected final <T> void parseImpl(MessageBuilder<T> builder) throws ParseException {
        String message = builder.getMessage();
        int lastResolvedIndex = -1;
        int implicitIndex = 0;
        int pos = PrintfMessageParser.nextPrintfTerm(message, 0);
        while (pos >= 0) {
            char c;
            int index;
            int optionsStart;
            int termStart;
            block12: {
                termStart = pos++;
                optionsStart = pos;
                index = 0;
                while (pos < message.length()) {
                    char digit;
                    if ((digit = (char)((c = message.charAt(pos++)) - 48)) < '\n') {
                        if ((index = 10 * index + digit) < 1000000) continue;
                        throw ParseException.withBounds("index too large", message, termStart, pos);
                    }
                    break block12;
                }
                throw ParseException.withStartPosition("unterminated parameter", message, termStart);
            }
            if (c == '$') {
                int indexLen = pos - 1 - optionsStart;
                if (indexLen == 0) {
                    throw ParseException.withBounds("missing index", message, termStart, pos);
                }
                if (message.charAt(optionsStart) == '0') {
                    throw ParseException.withBounds("index has leading zero", message, termStart, pos);
                }
                --index;
                optionsStart = pos;
                if (pos == message.length()) {
                    throw ParseException.withStartPosition("unterminated parameter", message, termStart);
                }
                c = message.charAt(pos++);
            } else if (c == '<') {
                if (lastResolvedIndex == -1) {
                    throw ParseException.withBounds("invalid relative parameter", message, termStart, pos);
                }
                index = lastResolvedIndex;
                optionsStart = pos;
                if (pos == message.length()) {
                    throw ParseException.withStartPosition("unterminated parameter", message, termStart);
                }
                c = message.charAt(pos++);
            } else {
                index = implicitIndex++;
            }
            pos = PrintfMessageParser.findFormatChar(message, termStart, pos - 1);
            pos = this.parsePrintfTerm(builder, index, message, termStart, optionsStart, pos);
            lastResolvedIndex = index;
            pos = PrintfMessageParser.nextPrintfTerm(message, pos);
        }
    }

    static int nextPrintfTerm(String message, int pos) throws ParseException {
        while (pos < message.length()) {
            if (message.charAt(pos++) != '%') continue;
            if (pos < message.length()) {
                char c = message.charAt(pos);
                if (c == '%' || c == 'n') {
                    ++pos;
                    continue;
                }
                return pos - 1;
            }
            throw ParseException.withStartPosition("trailing unquoted '%' character", message, pos - 1);
        }
        return -1;
    }

    private static int findFormatChar(String message, int termStart, int pos) throws ParseException {
        while (pos < message.length()) {
            char c = message.charAt(pos);
            char alpha = (char)((c & 0xFFFFFFDF) - 65);
            if (alpha < '\u001a') {
                return pos;
            }
            ++pos;
        }
        throw ParseException.withStartPosition("unterminated parameter", message, termStart);
    }

    static void unescapePrintf(StringBuilder out, String message, int start, int end) {
        int pos = start;
        while (pos < end) {
            if (message.charAt(pos++) != '%') continue;
            if (pos == end) break;
            char chr = message.charAt(pos);
            if (chr == '%') {
                out.append(message, start, pos);
            } else {
                if (chr != 'n') continue;
                out.append(message, start, pos - 1);
                out.append(SYSTEM_NEWLINE);
            }
            start = ++pos;
        }
        if (start < end) {
            out.append(message, start, end);
        }
    }
}

