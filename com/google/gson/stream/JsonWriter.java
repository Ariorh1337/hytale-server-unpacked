/*
 * Decompiled with CFR 0.152.
 */
package com.google.gson.stream;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.gson.FormattingStyle;
import com.google.gson.Strictness;
import java.io.Closeable;
import java.io.Flushable;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

public class JsonWriter
implements Closeable,
Flushable {
    private static final Pattern VALID_JSON_NUMBER_PATTERN = Pattern.compile("-?(?:0|[1-9][0-9]*)(?:\\.[0-9]+)?(?:[eE][-+]?[0-9]+)?");
    private static final String[] REPLACEMENT_CHARS = new String[128];
    private static final String[] HTML_SAFE_REPLACEMENT_CHARS;
    private final Writer out;
    private int[] stack = new int[32];
    private int stackSize = 0;
    private FormattingStyle formattingStyle;
    private String formattedColon;
    private String formattedComma;
    private boolean usesEmptyNewlineAndIndent;
    private Strictness strictness;
    private boolean htmlSafe;
    private String deferredName;
    private boolean serializeNulls;

    public JsonWriter(Writer out) {
        this.push(6);
        this.strictness = Strictness.LEGACY_STRICT;
        this.serializeNulls = true;
        this.out = Objects.requireNonNull(out, "out == null");
        this.setFormattingStyle(FormattingStyle.COMPACT);
    }

    public final void setIndent(String indent) {
        if (indent.isEmpty()) {
            this.setFormattingStyle(FormattingStyle.COMPACT);
        } else {
            this.setFormattingStyle(FormattingStyle.PRETTY.withIndent(indent));
        }
    }

    public final void setFormattingStyle(FormattingStyle formattingStyle) {
        this.formattingStyle = Objects.requireNonNull(formattingStyle);
        this.formattedComma = ",";
        if (this.formattingStyle.usesSpaceAfterSeparators()) {
            this.formattedColon = ": ";
            if (this.formattingStyle.getNewline().isEmpty()) {
                this.formattedComma = ", ";
            }
        } else {
            this.formattedColon = ":";
        }
        this.usesEmptyNewlineAndIndent = this.formattingStyle.getNewline().isEmpty() && this.formattingStyle.getIndent().isEmpty();
    }

    public final FormattingStyle getFormattingStyle() {
        return this.formattingStyle;
    }

    @Deprecated
    public final void setLenient(boolean lenient) {
        this.setStrictness(lenient ? Strictness.LENIENT : Strictness.LEGACY_STRICT);
    }

    public boolean isLenient() {
        return this.strictness == Strictness.LENIENT;
    }

    public final void setStrictness(Strictness strictness) {
        this.strictness = Objects.requireNonNull(strictness);
    }

    public final Strictness getStrictness() {
        return this.strictness;
    }

    public final void setHtmlSafe(boolean htmlSafe) {
        this.htmlSafe = htmlSafe;
    }

    public final boolean isHtmlSafe() {
        return this.htmlSafe;
    }

    public final void setSerializeNulls(boolean serializeNulls) {
        this.serializeNulls = serializeNulls;
    }

    public final boolean getSerializeNulls() {
        return this.serializeNulls;
    }

    @CanIgnoreReturnValue
    public JsonWriter beginArray() throws IOException {
        this.writeDeferredName();
        return this.openScope(1, '[');
    }

    @CanIgnoreReturnValue
    public JsonWriter endArray() throws IOException {
        return this.closeScope(1, 2, ']');
    }

    @CanIgnoreReturnValue
    public JsonWriter beginObject() throws IOException {
        this.writeDeferredName();
        return this.openScope(3, '{');
    }

    @CanIgnoreReturnValue
    public JsonWriter endObject() throws IOException {
        return this.closeScope(3, 5, '}');
    }

    @CanIgnoreReturnValue
    private JsonWriter openScope(int empty, char openBracket) throws IOException {
        this.beforeValue();
        this.push(empty);
        this.out.write(openBracket);
        return this;
    }

    @CanIgnoreReturnValue
    private JsonWriter closeScope(int empty, int nonempty, char closeBracket) throws IOException {
        int context = this.peek();
        if (context != nonempty && context != empty) {
            throw new IllegalStateException("Nesting problem.");
        }
        if (this.deferredName != null) {
            throw new IllegalStateException("Dangling name: " + this.deferredName);
        }
        --this.stackSize;
        if (context == nonempty) {
            this.newline();
        }
        this.out.write(closeBracket);
        return this;
    }

    private void push(int newTop) {
        if (this.stackSize == this.stack.length) {
            this.stack = Arrays.copyOf(this.stack, this.stackSize * 2);
        }
        this.stack[this.stackSize++] = newTop;
    }

    private int peek() {
        if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        return this.stack[this.stackSize - 1];
    }

    private void replaceTop(int topOfStack) {
        this.stack[this.stackSize - 1] = topOfStack;
    }

    @CanIgnoreReturnValue
    public JsonWriter name(String name) throws IOException {
        Objects.requireNonNull(name, "name == null");
        if (this.deferredName != null) {
            throw new IllegalStateException("Already wrote a name, expecting a value.");
        }
        int context = this.peek();
        if (context != 3 && context != 5) {
            throw new IllegalStateException("Please begin an object before writing a name.");
        }
        this.deferredName = name;
        return this;
    }

    private void writeDeferredName() throws IOException {
        if (this.deferredName != null) {
            this.beforeName();
            this.string(this.deferredName);
            this.deferredName = null;
        }
    }

    @CanIgnoreReturnValue
    public JsonWriter value(String value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.string(value);
        return this;
    }

    @CanIgnoreReturnValue
    public JsonWriter value(boolean value) throws IOException {
        this.writeDeferredName();
        this.beforeValue();
        this.out.write(value ? "true" : "false");
        return this;
    }

    @CanIgnoreReturnValue
    public JsonWriter value(Boolean value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.out.write(value != false ? "true" : "false");
        return this;
    }

    @CanIgnoreReturnValue
    public JsonWriter value(float value) throws IOException {
        this.writeDeferredName();
        if (this.strictness != Strictness.LENIENT && (Float.isNaN(value) || Float.isInfinite(value))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        this.beforeValue();
        this.out.append(Float.toString(value));
        return this;
    }

    @CanIgnoreReturnValue
    public JsonWriter value(double value) throws IOException {
        this.writeDeferredName();
        if (this.strictness != Strictness.LENIENT && (Double.isNaN(value) || Double.isInfinite(value))) {
            throw new IllegalArgumentException("Numeric values must be finite, but was " + value);
        }
        this.beforeValue();
        this.out.append(Double.toString(value));
        return this;
    }

    @CanIgnoreReturnValue
    public JsonWriter value(long value) throws IOException {
        this.writeDeferredName();
        this.beforeValue();
        this.out.write(Long.toString(value));
        return this;
    }

    @CanIgnoreReturnValue
    public JsonWriter value(Number value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        String string = value.toString();
        Class<?> numberClass = value.getClass();
        if (!JsonWriter.alwaysCreatesValidJsonNumber(numberClass)) {
            if (string.equals("-Infinity") || string.equals("Infinity") || string.equals("NaN")) {
                if (this.strictness != Strictness.LENIENT) {
                    throw new IllegalArgumentException("Numeric values must be finite, but was " + string);
                }
            } else if (numberClass != Float.class && numberClass != Double.class && !VALID_JSON_NUMBER_PATTERN.matcher(string).matches()) {
                throw new IllegalArgumentException("String created by " + numberClass + " is not a valid JSON number: " + string);
            }
        }
        this.beforeValue();
        this.out.append(string);
        return this;
    }

    @CanIgnoreReturnValue
    public JsonWriter nullValue() throws IOException {
        if (this.deferredName != null) {
            if (this.serializeNulls) {
                this.writeDeferredName();
            } else {
                this.deferredName = null;
                return this;
            }
        }
        this.beforeValue();
        this.out.write("null");
        return this;
    }

    @CanIgnoreReturnValue
    public JsonWriter jsonValue(String value) throws IOException {
        if (value == null) {
            return this.nullValue();
        }
        this.writeDeferredName();
        this.beforeValue();
        this.out.append(value);
        return this;
    }

    @Override
    public void flush() throws IOException {
        if (this.stackSize == 0) {
            throw new IllegalStateException("JsonWriter is closed.");
        }
        this.out.flush();
    }

    @Override
    public void close() throws IOException {
        this.out.close();
        int size = this.stackSize;
        if (size > 1 || size == 1 && this.stack[size - 1] != 7) {
            throw new IOException("Incomplete document");
        }
        this.stackSize = 0;
    }

    private static boolean alwaysCreatesValidJsonNumber(Class<? extends Number> c) {
        return c == Integer.class || c == Long.class || c == Byte.class || c == Short.class || c == BigDecimal.class || c == BigInteger.class || c == AtomicInteger.class || c == AtomicLong.class;
    }

    private void string(String value) throws IOException {
        String[] replacements = this.htmlSafe ? HTML_SAFE_REPLACEMENT_CHARS : REPLACEMENT_CHARS;
        this.out.write(34);
        int last = 0;
        int length = value.length();
        for (int i = 0; i < length; ++i) {
            String replacement;
            char c = value.charAt(i);
            if (c < '\u0080') {
                replacement = replacements[c];
                if (replacement == null) {
                    continue;
                }
            } else if (c == '\u2028') {
                replacement = "\\u2028";
            } else {
                if (c != '\u2029') continue;
                replacement = "\\u2029";
            }
            if (last < i) {
                this.out.write(value, last, i - last);
            }
            this.out.write(replacement);
            last = i + 1;
        }
        if (last < length) {
            this.out.write(value, last, length - last);
        }
        this.out.write(34);
    }

    private void newline() throws IOException {
        if (this.usesEmptyNewlineAndIndent) {
            return;
        }
        this.out.write(this.formattingStyle.getNewline());
        int size = this.stackSize;
        for (int i = 1; i < size; ++i) {
            this.out.write(this.formattingStyle.getIndent());
        }
    }

    private void beforeName() throws IOException {
        int context = this.peek();
        if (context == 5) {
            this.out.write(this.formattedComma);
        } else if (context != 3) {
            throw new IllegalStateException("Nesting problem.");
        }
        this.newline();
        this.replaceTop(4);
    }

    private void beforeValue() throws IOException {
        switch (this.peek()) {
            case 7: {
                if (this.strictness != Strictness.LENIENT) {
                    throw new IllegalStateException("JSON must have only one top-level value.");
                }
            }
            case 6: {
                this.replaceTop(7);
                break;
            }
            case 1: {
                this.replaceTop(2);
                this.newline();
                break;
            }
            case 2: {
                this.out.append(this.formattedComma);
                this.newline();
                break;
            }
            case 4: {
                this.out.append(this.formattedColon);
                this.replaceTop(5);
                break;
            }
            default: {
                throw new IllegalStateException("Nesting problem.");
            }
        }
    }

    static {
        for (int i = 0; i <= 31; ++i) {
            JsonWriter.REPLACEMENT_CHARS[i] = String.format("\\u%04x", i);
        }
        JsonWriter.REPLACEMENT_CHARS[34] = "\\\"";
        JsonWriter.REPLACEMENT_CHARS[92] = "\\\\";
        JsonWriter.REPLACEMENT_CHARS[9] = "\\t";
        JsonWriter.REPLACEMENT_CHARS[8] = "\\b";
        JsonWriter.REPLACEMENT_CHARS[10] = "\\n";
        JsonWriter.REPLACEMENT_CHARS[13] = "\\r";
        JsonWriter.REPLACEMENT_CHARS[12] = "\\f";
        HTML_SAFE_REPLACEMENT_CHARS = (String[])REPLACEMENT_CHARS.clone();
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[60] = "\\u003c";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[62] = "\\u003e";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[38] = "\\u0026";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[61] = "\\u003d";
        JsonWriter.HTML_SAFE_REPLACEMENT_CHARS[39] = "\\u0027";
    }
}

