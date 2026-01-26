/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jline.reader.CompletingParsedLine;
import org.jline.reader.EOFError;
import org.jline.reader.ParsedLine;
import org.jline.reader.Parser;

public class DefaultParser
implements Parser {
    private char[] quoteChars = new char[]{'\'', '\"'};
    private char[] escapeChars = new char[]{'\\'};
    private boolean eofOnUnclosedQuote;
    private boolean eofOnEscapedNewLine;
    private char[] openingBrackets = null;
    private char[] closingBrackets = null;
    private String[] lineCommentDelims = null;
    private BlockCommentDelims blockCommentDelims = null;
    private String regexVariable = "[a-zA-Z_]+[a-zA-Z0-9_-]*((\\.|\\['|\\[\"|\\[)[a-zA-Z0-9_-]*(|']|\"]|]))?";
    private String regexCommand = "[:]?[a-zA-Z]+[a-zA-Z0-9_-]*";
    private int commandGroup = 4;

    public DefaultParser lineCommentDelims(String[] lineCommentDelims) {
        this.lineCommentDelims = lineCommentDelims;
        return this;
    }

    public DefaultParser blockCommentDelims(BlockCommentDelims blockCommentDelims) {
        this.blockCommentDelims = blockCommentDelims;
        return this;
    }

    public DefaultParser quoteChars(char[] chars) {
        this.quoteChars = chars;
        return this;
    }

    public DefaultParser escapeChars(char[] chars) {
        this.escapeChars = chars;
        return this;
    }

    public DefaultParser eofOnUnclosedQuote(boolean eofOnUnclosedQuote) {
        this.eofOnUnclosedQuote = eofOnUnclosedQuote;
        return this;
    }

    public DefaultParser eofOnUnclosedBracket(Bracket ... brackets) {
        this.setEofOnUnclosedBracket(brackets);
        return this;
    }

    public DefaultParser eofOnEscapedNewLine(boolean eofOnEscapedNewLine) {
        this.eofOnEscapedNewLine = eofOnEscapedNewLine;
        return this;
    }

    public DefaultParser regexVariable(String regexVariable) {
        this.regexVariable = regexVariable;
        return this;
    }

    public DefaultParser regexCommand(String regexCommand) {
        this.regexCommand = regexCommand;
        return this;
    }

    public DefaultParser commandGroup(int commandGroup) {
        this.commandGroup = commandGroup;
        return this;
    }

    public void setQuoteChars(char[] chars) {
        this.quoteChars = chars;
    }

    public char[] getQuoteChars() {
        return this.quoteChars;
    }

    public void setEscapeChars(char[] chars) {
        this.escapeChars = chars;
    }

    public char[] getEscapeChars() {
        return this.escapeChars;
    }

    public void setLineCommentDelims(String[] lineCommentDelims) {
        this.lineCommentDelims = lineCommentDelims;
    }

    public String[] getLineCommentDelims() {
        return this.lineCommentDelims;
    }

    public void setBlockCommentDelims(BlockCommentDelims blockCommentDelims) {
        this.blockCommentDelims = blockCommentDelims;
    }

    public BlockCommentDelims getBlockCommentDelims() {
        return this.blockCommentDelims;
    }

    public void setEofOnUnclosedQuote(boolean eofOnUnclosedQuote) {
        this.eofOnUnclosedQuote = eofOnUnclosedQuote;
    }

    public boolean isEofOnUnclosedQuote() {
        return this.eofOnUnclosedQuote;
    }

    public void setEofOnEscapedNewLine(boolean eofOnEscapedNewLine) {
        this.eofOnEscapedNewLine = eofOnEscapedNewLine;
    }

    public boolean isEofOnEscapedNewLine() {
        return this.eofOnEscapedNewLine;
    }

    public void setEofOnUnclosedBracket(Bracket ... brackets) {
        if (brackets == null) {
            this.openingBrackets = null;
            this.closingBrackets = null;
        } else {
            HashSet<Bracket> bs = new HashSet<Bracket>(Arrays.asList(brackets));
            this.openingBrackets = new char[bs.size()];
            this.closingBrackets = new char[bs.size()];
            int i = 0;
            for (Bracket b : bs) {
                switch (b.ordinal()) {
                    case 0: {
                        this.openingBrackets[i] = 40;
                        this.closingBrackets[i] = 41;
                        break;
                    }
                    case 1: {
                        this.openingBrackets[i] = 123;
                        this.closingBrackets[i] = 125;
                        break;
                    }
                    case 2: {
                        this.openingBrackets[i] = 91;
                        this.closingBrackets[i] = 93;
                        break;
                    }
                    case 3: {
                        this.openingBrackets[i] = 60;
                        this.closingBrackets[i] = 62;
                    }
                }
                ++i;
            }
        }
    }

    public void setRegexVariable(String regexVariable) {
        this.regexVariable = regexVariable;
    }

    public void setRegexCommand(String regexCommand) {
        this.regexCommand = regexCommand;
    }

    public void setCommandGroup(int commandGroup) {
        this.commandGroup = commandGroup;
    }

    @Override
    public boolean validCommandName(String name) {
        return name != null && name.matches(this.regexCommand);
    }

    @Override
    public boolean validVariableName(String name) {
        return name != null && this.regexVariable != null && name.matches(this.regexVariable);
    }

    @Override
    public String getCommand(String line) {
        boolean checkCommandOnly;
        String out = "";
        boolean bl = checkCommandOnly = this.regexVariable == null;
        if (!checkCommandOnly) {
            Pattern patternCommand = Pattern.compile("^\\s*" + this.regexVariable + "=(" + this.regexCommand + ")(\\s+|$)");
            Matcher matcher = patternCommand.matcher(line);
            if (matcher.find()) {
                out = matcher.group(this.commandGroup);
            } else {
                checkCommandOnly = true;
            }
        }
        if (checkCommandOnly && !(out = line.trim().split("\\s+")[0]).matches(this.regexCommand)) {
            out = "";
        }
        return out;
    }

    @Override
    public String getVariable(String line) {
        Pattern patternCommand;
        Matcher matcher;
        String out = null;
        if (this.regexVariable != null && (matcher = (patternCommand = Pattern.compile("^\\s*(" + this.regexVariable + ")\\s*=[^=~].*")).matcher(line)).find()) {
            out = matcher.group(1);
        }
        return out;
    }

    @Override
    public ParsedLine parse(String line, int cursor, Parser.ParseContext context) {
        LinkedList<String> words = new LinkedList<String>();
        StringBuilder current = new StringBuilder();
        int wordCursor = -1;
        int wordIndex = -1;
        int quoteStart = -1;
        int rawWordCursor = -1;
        int rawWordLength = -1;
        int rawWordStart = 0;
        BracketChecker bracketChecker = new BracketChecker(cursor);
        boolean quotedWord = false;
        boolean lineCommented = false;
        boolean blockCommented = false;
        boolean blockCommentInRightOrder = true;
        String blockCommentEnd = this.blockCommentDelims == null ? null : this.blockCommentDelims.end;
        String blockCommentStart = this.blockCommentDelims == null ? null : this.blockCommentDelims.start;
        for (int i = 0; line != null && i < line.length(); ++i) {
            if (i == cursor) {
                wordIndex = words.size();
                wordCursor = current.length();
                rawWordCursor = i - rawWordStart;
            }
            if (quoteStart < 0 && this.isQuoteChar(line, i) && !lineCommented && !blockCommented) {
                quoteStart = i;
                if (current.length() == 0) {
                    quotedWord = true;
                    if (context != Parser.ParseContext.SPLIT_LINE) continue;
                    current.append(line.charAt(i));
                    continue;
                }
                current.append(line.charAt(i));
                continue;
            }
            if (quoteStart >= 0 && line.charAt(quoteStart) == line.charAt(i) && !this.isEscaped(line, i)) {
                if (!quotedWord || context == Parser.ParseContext.SPLIT_LINE) {
                    current.append(line.charAt(i));
                } else if (rawWordCursor >= 0 && rawWordLength < 0) {
                    rawWordLength = i - rawWordStart + 1;
                }
                quoteStart = -1;
                quotedWord = false;
                continue;
            }
            if (quoteStart < 0 && this.isDelimiter(line, i)) {
                if (lineCommented) {
                    if (!this.isCommentDelim(line, i, System.lineSeparator())) continue;
                    lineCommented = false;
                    continue;
                }
                if (blockCommented) {
                    if (!this.isCommentDelim(line, i, blockCommentEnd)) continue;
                    blockCommented = false;
                    continue;
                }
                rawWordLength = this.handleDelimiterAndGetRawWordLength(current, words, rawWordStart, rawWordCursor, rawWordLength, i);
                rawWordStart = i + 1;
                continue;
            }
            if (quoteStart < 0 && !blockCommented && (lineCommented || this.isLineCommentStarted(line, i))) {
                lineCommented = true;
                continue;
            }
            if (quoteStart < 0 && !lineCommented && (blockCommented || this.isCommentDelim(line, i, blockCommentStart))) {
                if (blockCommented) {
                    if (blockCommentEnd == null || !this.isCommentDelim(line, i, blockCommentEnd)) continue;
                    blockCommented = false;
                    i += blockCommentEnd.length() - 1;
                    continue;
                }
                blockCommented = true;
                rawWordLength = this.handleDelimiterAndGetRawWordLength(current, words, rawWordStart, rawWordCursor, rawWordLength, i);
                rawWordStart = (i += blockCommentStart == null ? 0 : blockCommentStart.length() - 1) + 1;
                continue;
            }
            if (quoteStart < 0 && !lineCommented && this.isCommentDelim(line, i, blockCommentEnd)) {
                current.append(line.charAt(i));
                blockCommentInRightOrder = false;
                continue;
            }
            if (!this.isEscapeChar(line, i)) {
                current.append(line.charAt(i));
                if (quoteStart >= 0) continue;
                bracketChecker.check(line, i);
                continue;
            }
            if (context != Parser.ParseContext.SPLIT_LINE) continue;
            current.append(line.charAt(i));
        }
        if (current.length() > 0 || cursor == line.length()) {
            words.add(current.toString());
            if (rawWordCursor >= 0 && rawWordLength < 0) {
                rawWordLength = line.length() - rawWordStart;
            }
        }
        if (cursor == line.length()) {
            wordIndex = words.size() - 1;
            wordCursor = ((String)words.get(words.size() - 1)).length();
            rawWordLength = rawWordCursor = cursor - rawWordStart;
        }
        if (context != Parser.ParseContext.COMPLETE && context != Parser.ParseContext.SPLIT_LINE) {
            if (this.eofOnEscapedNewLine && this.isEscapeChar(line, line.length() - 1)) {
                throw new EOFError(-1, -1, "Escaped new line", "newline");
            }
            if (this.eofOnUnclosedQuote && quoteStart >= 0) {
                throw new EOFError(-1, -1, "Missing closing quote", line.charAt(quoteStart) == '\'' ? "quote" : "dquote");
            }
            if (blockCommented) {
                throw new EOFError(-1, -1, "Missing closing block comment delimiter", "add: " + blockCommentEnd);
            }
            if (!blockCommentInRightOrder) {
                throw new EOFError(-1, -1, "Missing opening block comment delimiter", "missing: " + blockCommentStart);
            }
            if (bracketChecker.isClosingBracketMissing() || bracketChecker.isOpeningBracketMissing()) {
                String message = null;
                String missing = null;
                if (bracketChecker.isClosingBracketMissing()) {
                    message = "Missing closing brackets";
                    missing = "add: " + bracketChecker.getMissingClosingBrackets();
                } else {
                    message = "Missing opening bracket";
                    missing = "missing: " + bracketChecker.getMissingOpeningBracket();
                }
                throw new EOFError(-1, -1, message, missing, bracketChecker.getOpenBrackets(), bracketChecker.getNextClosingBracket());
            }
        }
        String openingQuote = quotedWord ? line.substring(quoteStart, quoteStart + 1) : null;
        return new ArgumentList(line, words, wordIndex, wordCursor, cursor, openingQuote, rawWordCursor, rawWordLength);
    }

    public boolean isDelimiter(CharSequence buffer, int pos) {
        return !this.isQuoted(buffer, pos) && !this.isEscaped(buffer, pos) && this.isDelimiterChar(buffer, pos);
    }

    private int handleDelimiterAndGetRawWordLength(StringBuilder current, List<String> words, int rawWordStart, int rawWordCursor, int rawWordLength, int pos) {
        if (current.length() > 0) {
            words.add(current.toString());
            current.setLength(0);
            if (rawWordCursor >= 0 && rawWordLength < 0) {
                return pos - rawWordStart;
            }
        }
        return rawWordLength;
    }

    public boolean isQuoted(CharSequence buffer, int pos) {
        return false;
    }

    public boolean isQuoteChar(CharSequence buffer, int pos) {
        if (pos < 0) {
            return false;
        }
        if (this.quoteChars != null) {
            for (char e : this.quoteChars) {
                if (e != buffer.charAt(pos)) continue;
                return !this.isEscaped(buffer, pos);
            }
        }
        return false;
    }

    private boolean isCommentDelim(CharSequence buffer, int pos, String pattern) {
        int length;
        if (pos < 0) {
            return false;
        }
        if (pattern != null && (length = pattern.length()) <= buffer.length() - pos) {
            for (int i = 0; i < length; ++i) {
                if (pattern.charAt(i) == buffer.charAt(pos + i)) continue;
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean isLineCommentStarted(CharSequence buffer, int pos) {
        if (this.lineCommentDelims != null) {
            for (String comment : this.lineCommentDelims) {
                if (!this.isCommentDelim(buffer, pos, comment)) continue;
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isEscapeChar(char ch) {
        if (this.escapeChars != null) {
            for (char e : this.escapeChars) {
                if (e != ch) continue;
                return true;
            }
        }
        return false;
    }

    public boolean isEscapeChar(CharSequence buffer, int pos) {
        if (pos < 0) {
            return false;
        }
        char ch = buffer.charAt(pos);
        return this.isEscapeChar(ch) && !this.isEscaped(buffer, pos);
    }

    public boolean isEscaped(CharSequence buffer, int pos) {
        if (pos <= 0) {
            return false;
        }
        return this.isEscapeChar(buffer, pos - 1);
    }

    public boolean isDelimiterChar(CharSequence buffer, int pos) {
        return Character.isWhitespace(buffer.charAt(pos));
    }

    private boolean isRawEscapeChar(char key) {
        if (this.escapeChars != null) {
            for (char e : this.escapeChars) {
                if (e != key) continue;
                return true;
            }
        }
        return false;
    }

    private boolean isRawQuoteChar(char key) {
        if (this.quoteChars != null) {
            for (char e : this.quoteChars) {
                if (e != key) continue;
                return true;
            }
        }
        return false;
    }

    public static class BlockCommentDelims {
        private final String start;
        private final String end;

        public BlockCommentDelims(String start, String end) {
            if (start == null || end == null || start.isEmpty() || end.isEmpty() || start.equals(end)) {
                throw new IllegalArgumentException("Bad block comment delimiter!");
            }
            this.start = start;
            this.end = end;
        }

        public String getStart() {
            return this.start;
        }

        public String getEnd() {
            return this.end;
        }
    }

    public static enum Bracket {
        ROUND,
        CURLY,
        SQUARE,
        ANGLE;

    }

    private class BracketChecker {
        private int missingOpeningBracket = -1;
        private List<Integer> nested = new ArrayList<Integer>();
        private int openBrackets = 0;
        private int cursor;
        private String nextClosingBracket;

        public BracketChecker(int cursor) {
            this.cursor = cursor;
        }

        public void check(CharSequence buffer, int pos) {
            if (DefaultParser.this.openingBrackets == null || pos < 0) {
                return;
            }
            int bid = this.bracketId(DefaultParser.this.openingBrackets, buffer, pos);
            if (bid >= 0) {
                this.nested.add(bid);
            } else {
                bid = this.bracketId(DefaultParser.this.closingBrackets, buffer, pos);
                if (bid >= 0) {
                    if (!this.nested.isEmpty() && bid == this.nested.get(this.nested.size() - 1)) {
                        this.nested.remove(this.nested.size() - 1);
                    } else {
                        this.missingOpeningBracket = bid;
                    }
                }
            }
            if (this.cursor > pos) {
                this.openBrackets = this.nested.size();
                if (this.nested.size() > 0) {
                    this.nextClosingBracket = String.valueOf(DefaultParser.this.closingBrackets[this.nested.get(this.nested.size() - 1)]);
                }
            }
        }

        public boolean isOpeningBracketMissing() {
            return this.missingOpeningBracket != -1;
        }

        public String getMissingOpeningBracket() {
            if (!this.isOpeningBracketMissing()) {
                return null;
            }
            return Character.toString(DefaultParser.this.openingBrackets[this.missingOpeningBracket]);
        }

        public boolean isClosingBracketMissing() {
            return !this.nested.isEmpty();
        }

        public String getMissingClosingBrackets() {
            if (!this.isClosingBracketMissing()) {
                return null;
            }
            StringBuilder out = new StringBuilder();
            for (int i = this.nested.size() - 1; i > -1; --i) {
                out.append(DefaultParser.this.closingBrackets[this.nested.get(i)]);
            }
            return out.toString();
        }

        public int getOpenBrackets() {
            return this.openBrackets;
        }

        public String getNextClosingBracket() {
            return this.nested.size() == 2 ? this.nextClosingBracket : null;
        }

        private int bracketId(char[] brackets, CharSequence buffer, int pos) {
            for (int i = 0; i < brackets.length; ++i) {
                if (buffer.charAt(pos) != brackets[i]) continue;
                return i;
            }
            return -1;
        }
    }

    public class ArgumentList
    implements ParsedLine,
    CompletingParsedLine {
        private final String line;
        private final List<String> words;
        private final int wordIndex;
        private final int wordCursor;
        private final int cursor;
        private final String openingQuote;
        private final int rawWordCursor;
        private final int rawWordLength;

        @Deprecated
        public ArgumentList(String line, List<String> words, int wordIndex, int wordCursor, int cursor) {
            this(line, words, wordIndex, wordCursor, cursor, null, wordCursor, words.get(wordIndex).length());
        }

        public ArgumentList(String line, List<String> words, int wordIndex, int wordCursor, int cursor, String openingQuote, int rawWordCursor, int rawWordLength) {
            this.line = line;
            this.words = Collections.unmodifiableList(Objects.requireNonNull(words));
            this.wordIndex = wordIndex;
            this.wordCursor = wordCursor;
            this.cursor = cursor;
            this.openingQuote = openingQuote;
            this.rawWordCursor = rawWordCursor;
            this.rawWordLength = rawWordLength;
        }

        @Override
        public int wordIndex() {
            return this.wordIndex;
        }

        @Override
        public String word() {
            if (this.wordIndex < 0 || this.wordIndex >= this.words.size()) {
                return "";
            }
            return this.words.get(this.wordIndex);
        }

        @Override
        public int wordCursor() {
            return this.wordCursor;
        }

        @Override
        public List<String> words() {
            return this.words;
        }

        @Override
        public int cursor() {
            return this.cursor;
        }

        @Override
        public String line() {
            return this.line;
        }

        @Override
        public CharSequence escape(CharSequence candidate, boolean complete) {
            int i2;
            StringBuilder sb = new StringBuilder(candidate);
            String quote = this.openingQuote;
            boolean middleQuotes = false;
            if (this.openingQuote == null) {
                for (i2 = 0; i2 < sb.length(); ++i2) {
                    if (!DefaultParser.this.isQuoteChar(sb, i2)) continue;
                    middleQuotes = true;
                    break;
                }
            }
            if (DefaultParser.this.escapeChars != null) {
                if (DefaultParser.this.escapeChars.length > 0) {
                    Predicate<Integer> needToBeEscaped = this.openingQuote != null ? i -> DefaultParser.this.isRawEscapeChar(sb.charAt((int)i)) || String.valueOf(sb.charAt((int)i)).equals(this.openingQuote) : (middleQuotes ? i -> DefaultParser.this.isRawEscapeChar(sb.charAt((int)i)) : i -> DefaultParser.this.isDelimiterChar(sb, (int)i) || DefaultParser.this.isRawEscapeChar(sb.charAt((int)i)) || DefaultParser.this.isRawQuoteChar(sb.charAt((int)i)));
                    for (i2 = 0; i2 < sb.length(); ++i2) {
                        if (!needToBeEscaped.test(i2)) continue;
                        sb.insert(i2++, DefaultParser.this.escapeChars[0]);
                    }
                }
            } else if (this.openingQuote == null && !middleQuotes) {
                for (i2 = 0; i2 < sb.length(); ++i2) {
                    if (!DefaultParser.this.isDelimiterChar(sb, i2)) continue;
                    quote = "'";
                    break;
                }
            }
            if (quote != null) {
                sb.insert(0, quote);
                if (complete) {
                    sb.append(quote);
                }
            }
            return sb;
        }

        @Override
        public int rawWordCursor() {
            return this.rawWordCursor;
        }

        @Override
        public int rawWordLength() {
            return this.rawWordLength;
        }
    }
}

