/*
 * Decompiled with CFR 0.152.
 */
package org.bson.json;

import java.io.Reader;
import org.bson.BsonRegularExpression;
import org.bson.json.JsonBuffer;
import org.bson.json.JsonParseException;
import org.bson.json.JsonStreamBuffer;
import org.bson.json.JsonStringBuffer;
import org.bson.json.JsonToken;
import org.bson.json.JsonTokenType;

class JsonScanner {
    private final JsonBuffer buffer;

    JsonScanner(JsonBuffer buffer) {
        this.buffer = buffer;
    }

    JsonScanner(String json) {
        this(new JsonStringBuffer(json));
    }

    JsonScanner(Reader reader) {
        this(new JsonStreamBuffer(reader));
    }

    public void reset(int markPos) {
        this.buffer.reset(markPos);
    }

    public int mark() {
        return this.buffer.mark();
    }

    public void discard(int markPos) {
        this.buffer.discard(markPos);
    }

    public JsonToken nextToken() {
        int c = this.buffer.read();
        while (c != -1 && Character.isWhitespace(c)) {
            c = this.buffer.read();
        }
        if (c == -1) {
            return new JsonToken(JsonTokenType.END_OF_FILE, "<eof>");
        }
        switch (c) {
            case 123: {
                return new JsonToken(JsonTokenType.BEGIN_OBJECT, "{");
            }
            case 125: {
                return new JsonToken(JsonTokenType.END_OBJECT, "}");
            }
            case 91: {
                return new JsonToken(JsonTokenType.BEGIN_ARRAY, "[");
            }
            case 93: {
                return new JsonToken(JsonTokenType.END_ARRAY, "]");
            }
            case 40: {
                return new JsonToken(JsonTokenType.LEFT_PAREN, "(");
            }
            case 41: {
                return new JsonToken(JsonTokenType.RIGHT_PAREN, ")");
            }
            case 58: {
                return new JsonToken(JsonTokenType.COLON, ":");
            }
            case 44: {
                return new JsonToken(JsonTokenType.COMMA, ",");
            }
            case 34: 
            case 39: {
                return this.scanString((char)c);
            }
            case 47: {
                return this.scanRegularExpression();
            }
        }
        if (c == 45 || Character.isDigit(c)) {
            return this.scanNumber((char)c);
        }
        if (c == 36 || c == 95 || Character.isLetter(c)) {
            return this.scanUnquotedString((char)c);
        }
        int position = this.buffer.getPosition();
        this.buffer.unread(c);
        throw new JsonParseException("Invalid JSON input. Position: %d. Character: '%c'.", position, c);
    }

    private JsonToken scanRegularExpression() {
        StringBuilder patternBuilder = new StringBuilder();
        StringBuilder optionsBuilder = new StringBuilder();
        RegularExpressionState state = RegularExpressionState.IN_PATTERN;
        block21: while (true) {
            int c = this.buffer.read();
            block0 : switch (state) {
                case IN_PATTERN: {
                    switch (c) {
                        case -1: {
                            state = RegularExpressionState.INVALID;
                            break block0;
                        }
                        case 47: {
                            state = RegularExpressionState.IN_OPTIONS;
                            break block0;
                        }
                        case 92: {
                            state = RegularExpressionState.IN_ESCAPE_SEQUENCE;
                            break block0;
                        }
                    }
                    state = RegularExpressionState.IN_PATTERN;
                    break;
                }
                case IN_ESCAPE_SEQUENCE: {
                    state = RegularExpressionState.IN_PATTERN;
                    break;
                }
                case IN_OPTIONS: {
                    switch (c) {
                        case 105: 
                        case 109: 
                        case 115: 
                        case 120: {
                            state = RegularExpressionState.IN_OPTIONS;
                            break block0;
                        }
                        case -1: 
                        case 41: 
                        case 44: 
                        case 93: 
                        case 125: {
                            state = RegularExpressionState.DONE;
                            break block0;
                        }
                    }
                    if (Character.isWhitespace(c)) {
                        state = RegularExpressionState.DONE;
                        break;
                    }
                    state = RegularExpressionState.INVALID;
                    break;
                }
            }
            switch (state) {
                case DONE: {
                    this.buffer.unread(c);
                    BsonRegularExpression regex = new BsonRegularExpression(patternBuilder.toString(), optionsBuilder.toString());
                    return new JsonToken(JsonTokenType.REGULAR_EXPRESSION, regex);
                }
                case INVALID: {
                    throw new JsonParseException("Invalid JSON regular expression. Position: %d.", this.buffer.getPosition());
                }
            }
            switch (state) {
                case IN_OPTIONS: {
                    if (c == 47) continue block21;
                    optionsBuilder.append((char)c);
                    continue block21;
                }
            }
            patternBuilder.append((char)c);
        }
    }

    private JsonToken scanUnquotedString(char firstChar) {
        StringBuilder sb = new StringBuilder();
        sb.append(firstChar);
        int c = this.buffer.read();
        while (c == 36 || c == 95 || Character.isLetterOrDigit(c)) {
            sb.append((char)c);
            c = this.buffer.read();
        }
        this.buffer.unread(c);
        String lexeme = sb.toString();
        return new JsonToken(JsonTokenType.UNQUOTED_STRING, lexeme);
    }

    private JsonToken scanNumber(char firstChar) {
        NumberState state;
        int c = firstChar;
        StringBuilder sb = new StringBuilder();
        sb.append((char)firstChar);
        switch (c) {
            case 45: {
                state = NumberState.SAW_LEADING_MINUS;
                break;
            }
            case 48: {
                state = NumberState.SAW_LEADING_ZERO;
                break;
            }
            default: {
                state = NumberState.SAW_INTEGER_DIGITS;
            }
        }
        JsonTokenType type = JsonTokenType.INT64;
        while (true) {
            c = this.buffer.read();
            block4 : switch (state) {
                case SAW_LEADING_MINUS: {
                    switch (c) {
                        case 48: {
                            state = NumberState.SAW_LEADING_ZERO;
                            break block4;
                        }
                        case 73: {
                            state = NumberState.SAW_MINUS_I;
                            break block4;
                        }
                    }
                    if (Character.isDigit(c)) {
                        state = NumberState.SAW_INTEGER_DIGITS;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
                case SAW_LEADING_ZERO: {
                    switch (c) {
                        case 46: {
                            state = NumberState.SAW_DECIMAL_POINT;
                            break block4;
                        }
                        case 69: 
                        case 101: {
                            state = NumberState.SAW_EXPONENT_LETTER;
                            break block4;
                        }
                        case -1: 
                        case 41: 
                        case 44: 
                        case 93: 
                        case 125: {
                            state = NumberState.DONE;
                            break block4;
                        }
                    }
                    if (Character.isDigit(c)) {
                        state = NumberState.SAW_INTEGER_DIGITS;
                        break;
                    }
                    if (Character.isWhitespace(c)) {
                        state = NumberState.DONE;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
                case SAW_INTEGER_DIGITS: {
                    switch (c) {
                        case 46: {
                            state = NumberState.SAW_DECIMAL_POINT;
                            break block4;
                        }
                        case 69: 
                        case 101: {
                            state = NumberState.SAW_EXPONENT_LETTER;
                            break block4;
                        }
                        case -1: 
                        case 41: 
                        case 44: 
                        case 93: 
                        case 125: {
                            state = NumberState.DONE;
                            break block4;
                        }
                    }
                    if (Character.isDigit(c)) {
                        state = NumberState.SAW_INTEGER_DIGITS;
                        break;
                    }
                    if (Character.isWhitespace(c)) {
                        state = NumberState.DONE;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
                case SAW_DECIMAL_POINT: {
                    type = JsonTokenType.DOUBLE;
                    if (Character.isDigit(c)) {
                        state = NumberState.SAW_FRACTION_DIGITS;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
                case SAW_FRACTION_DIGITS: {
                    switch (c) {
                        case 69: 
                        case 101: {
                            state = NumberState.SAW_EXPONENT_LETTER;
                            break block4;
                        }
                        case -1: 
                        case 41: 
                        case 44: 
                        case 93: 
                        case 125: {
                            state = NumberState.DONE;
                            break block4;
                        }
                    }
                    if (Character.isDigit(c)) {
                        state = NumberState.SAW_FRACTION_DIGITS;
                        break;
                    }
                    if (Character.isWhitespace(c)) {
                        state = NumberState.DONE;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
                case SAW_EXPONENT_LETTER: {
                    type = JsonTokenType.DOUBLE;
                    switch (c) {
                        case 43: 
                        case 45: {
                            state = NumberState.SAW_EXPONENT_SIGN;
                            break block4;
                        }
                    }
                    if (Character.isDigit(c)) {
                        state = NumberState.SAW_EXPONENT_DIGITS;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
                case SAW_EXPONENT_SIGN: {
                    if (Character.isDigit(c)) {
                        state = NumberState.SAW_EXPONENT_DIGITS;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
                case SAW_EXPONENT_DIGITS: {
                    switch (c) {
                        case 41: 
                        case 44: 
                        case 93: 
                        case 125: {
                            state = NumberState.DONE;
                            break block4;
                        }
                    }
                    if (Character.isDigit(c)) {
                        state = NumberState.SAW_EXPONENT_DIGITS;
                        break;
                    }
                    if (Character.isWhitespace(c)) {
                        state = NumberState.DONE;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
                case SAW_MINUS_I: {
                    boolean sawMinusInfinity = true;
                    char[] nfinity = new char[]{'n', 'f', 'i', 'n', 'i', 't', 'y'};
                    for (int i = 0; i < nfinity.length; ++i) {
                        if (c != nfinity[i]) {
                            sawMinusInfinity = false;
                            break;
                        }
                        sb.append((char)c);
                        c = this.buffer.read();
                    }
                    if (sawMinusInfinity) {
                        type = JsonTokenType.DOUBLE;
                        switch (c) {
                            case -1: 
                            case 41: 
                            case 44: 
                            case 93: 
                            case 125: {
                                state = NumberState.DONE;
                                break block4;
                            }
                        }
                        if (Character.isWhitespace(c)) {
                            state = NumberState.DONE;
                            break;
                        }
                        state = NumberState.INVALID;
                        break;
                    }
                    state = NumberState.INVALID;
                    break;
                }
            }
            switch (state) {
                case INVALID: {
                    throw new JsonParseException("Invalid JSON number");
                }
                case DONE: {
                    this.buffer.unread(c);
                    String lexeme = sb.toString();
                    if (type == JsonTokenType.DOUBLE) {
                        return new JsonToken(JsonTokenType.DOUBLE, Double.parseDouble(lexeme));
                    }
                    long value = Long.parseLong(lexeme);
                    if (value < Integer.MIN_VALUE || value > Integer.MAX_VALUE) {
                        return new JsonToken(JsonTokenType.INT64, value);
                    }
                    return new JsonToken(JsonTokenType.INT32, (int)value);
                }
            }
            sb.append((char)c);
        }
    }

    private JsonToken scanString(char quoteCharacter) {
        int c;
        StringBuilder sb = new StringBuilder();
        block15: do {
            c = this.buffer.read();
            block0 : switch (c) {
                case 92: {
                    c = this.buffer.read();
                    switch (c) {
                        case 39: {
                            sb.append('\'');
                            break block0;
                        }
                        case 34: {
                            sb.append('\"');
                            break block0;
                        }
                        case 92: {
                            sb.append('\\');
                            break block0;
                        }
                        case 47: {
                            sb.append('/');
                            break block0;
                        }
                        case 98: {
                            sb.append('\b');
                            break block0;
                        }
                        case 102: {
                            sb.append('\f');
                            break block0;
                        }
                        case 110: {
                            sb.append('\n');
                            break block0;
                        }
                        case 114: {
                            sb.append('\r');
                            break block0;
                        }
                        case 116: {
                            sb.append('\t');
                            break block0;
                        }
                        case 117: {
                            int u1 = this.buffer.read();
                            int u2 = this.buffer.read();
                            int u3 = this.buffer.read();
                            int u4 = this.buffer.read();
                            if (u4 == -1) continue block15;
                            String hex = new String(new char[]{(char)u1, (char)u2, (char)u3, (char)u4});
                            sb.append((char)Integer.parseInt(hex, 16));
                            break block0;
                        }
                        default: {
                            throw new JsonParseException("Invalid escape sequence in JSON string '\\%c'.", c);
                        }
                    }
                }
                default: {
                    if (c == quoteCharacter) {
                        return new JsonToken(JsonTokenType.STRING, sb.toString());
                    }
                    if (c == -1) continue block15;
                    sb.append((char)c);
                }
            }
        } while (c != -1);
        throw new JsonParseException("End of file in JSON string.");
    }

    private static enum RegularExpressionState {
        IN_PATTERN,
        IN_ESCAPE_SEQUENCE,
        IN_OPTIONS,
        DONE,
        INVALID;

    }

    private static enum NumberState {
        SAW_LEADING_MINUS,
        SAW_LEADING_ZERO,
        SAW_INTEGER_DIGITS,
        SAW_DECIMAL_POINT,
        SAW_FRACTION_DIGITS,
        SAW_EXPONENT_LETTER,
        SAW_EXPONENT_SIGN,
        SAW_EXPONENT_DIGITS,
        SAW_MINUS_I,
        DONE,
        INVALID;

    }
}

