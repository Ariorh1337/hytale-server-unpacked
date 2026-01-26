/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.util;

import ch.randelshofer.fastdoubleparser.JavaDoubleParser;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.unsafe.UnsafeUtil;
import io.sentry.Sentry;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.NoSuchFileException;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonArray;
import org.bson.BsonBoolean;
import org.bson.BsonDocument;
import org.bson.BsonDouble;
import org.bson.BsonNull;
import org.bson.BsonString;
import org.bson.BsonValue;
import sun.misc.Unsafe;

public class RawJsonReader
implements AutoCloseable {
    public static final ThreadLocal<char[]> READ_BUFFER = ThreadLocal.withInitial(() -> new char[131072]);
    public static final int DEFAULT_CHAR_BUFFER_SIZE = 32768;
    public static final int MIN_CHAR_BUFFER_READ = 16384;
    public static final int BUFFER_GROWTH = 0x100000;
    private static final int UNMARKED = -1;
    private int streamIndex;
    @Nullable
    private Reader in;
    @Nullable
    private char[] buffer;
    private int bufferIndex;
    private int bufferSize;
    private int markIndex = -1;
    private int markLine = -1;
    private int markLineStart = -1;
    private StringBuilder tempSb;
    private int line;
    private int lineStart;
    public static final int ERROR_LINES_BUFFER = 10;

    public RawJsonReader(@Nonnull char[] preFilledBuffer) {
        if (preFilledBuffer == null) {
            throw new IllegalArgumentException("buffer can't be null!");
        }
        this.in = null;
        this.buffer = preFilledBuffer;
        this.bufferIndex = 0;
        this.streamIndex = 0;
        this.bufferSize = preFilledBuffer.length;
    }

    public RawJsonReader(Reader in, @Nonnull char[] buffer) {
        if (buffer == null) {
            throw new IllegalArgumentException("buffer can't be null!");
        }
        this.in = in;
        this.buffer = buffer;
        this.bufferIndex = 0;
        this.streamIndex = 0;
        this.bufferSize = 0;
    }

    public char[] getBuffer() {
        return this.buffer;
    }

    public int getBufferIndex() {
        return this.bufferIndex;
    }

    public int getBufferSize() {
        return this.bufferSize;
    }

    public int getLine() {
        return this.line + 1;
    }

    public int getColumn() {
        return this.bufferIndex - this.lineStart + 1;
    }

    private boolean ensure() throws IOException {
        return this.ensure(1);
    }

    private boolean ensure(int n) throws IOException {
        boolean filled = false;
        while (this.bufferIndex + n > this.bufferSize) {
            if (!this.fill()) {
                throw this.unexpectedEOF();
            }
            filled = true;
        }
        return filled;
    }

    private boolean fill() throws IOException {
        int len;
        int dst;
        if (this.markIndex <= -1) {
            this.streamIndex += this.bufferIndex;
            dst = 0;
            len = this.buffer.length;
        } else {
            int spaceInBuffer = this.buffer.length - this.bufferIndex;
            if (spaceInBuffer > 16384) {
                dst = this.bufferIndex;
                len = spaceInBuffer;
            } else {
                int delta = this.bufferIndex - this.markIndex;
                if (this.markIndex > 16384) {
                    System.arraycopy(this.buffer, this.markIndex, this.buffer, 0, delta);
                } else {
                    int newSize = this.bufferIndex + 0x100000;
                    System.err.println("Reallocate: " + this.buffer.length + " to " + newSize);
                    char[] ncb = new char[newSize];
                    System.arraycopy(this.buffer, this.markIndex, ncb, 0, delta);
                    this.buffer = ncb;
                }
                this.streamIndex += this.markIndex;
                this.markIndex = 0;
                dst = delta;
                this.bufferIndex = this.bufferSize = delta;
                len = this.buffer.length - dst;
            }
        }
        if (this.in == null) {
            return false;
        }
        int n = this.in.read(this.buffer, dst, len);
        if (n > 0) {
            this.bufferSize = dst + n;
            this.bufferIndex = dst;
            return true;
        }
        return false;
    }

    public int peek() throws IOException {
        return this.peek(0);
    }

    public int peek(int n) throws IOException {
        if (this.bufferIndex + n >= this.bufferSize) {
            this.fill();
            if (this.bufferIndex + n >= this.bufferSize) {
                return -1;
            }
        }
        return this.buffer[this.bufferIndex + n];
    }

    public int read() throws IOException {
        char c;
        if (this.bufferIndex >= this.bufferSize) {
            this.fill();
            if (this.bufferIndex >= this.bufferSize) {
                return -1;
            }
        }
        if ((c = this.buffer[this.bufferIndex++]) == '\n') {
            ++this.line;
            this.lineStart = this.bufferIndex;
        }
        return c;
    }

    public long skip(long skip) throws IOException {
        long haveSkipped;
        long charsInBuffer;
        if (skip < 0L) {
            int negativeBufferIndex = -this.bufferIndex;
            if (skip < (long)negativeBufferIndex) {
                this.bufferIndex = 0;
                return negativeBufferIndex;
            }
            this.bufferIndex = (int)((long)this.bufferIndex + skip);
            return skip;
        }
        for (haveSkipped = 0L; haveSkipped < skip; haveSkipped += charsInBuffer) {
            long charsToSkip = skip - haveSkipped;
            charsInBuffer = this.bufferSize - this.bufferIndex;
            if (charsToSkip <= charsInBuffer) {
                this.bufferIndex = (int)((long)this.bufferIndex + charsToSkip);
                return skip;
            }
            this.bufferIndex = this.bufferSize;
            this.fill();
            if (this.bufferIndex < this.bufferSize) continue;
            break;
        }
        return haveSkipped;
    }

    public int findOffset(char value) throws IOException {
        return this.findOffset(0, value);
    }

    public int findOffset(int start, char value) throws IOException {
        while (true) {
            this.ensure();
            char c = this.buffer[this.bufferIndex + start];
            if (c == value) {
                return start;
            }
            ++start;
        }
    }

    public void skipOrThrow(long n) throws IOException {
        long skipped = this.skip(n);
        if (skipped != n) {
            throw new IOException("Failed to skip " + n + " char's!");
        }
    }

    public boolean ready() throws IOException {
        return this.buffer != null && this.bufferIndex < this.bufferSize || this.in.ready();
    }

    public boolean markSupported() {
        return true;
    }

    public void mark(int readAheadLimit) throws IOException {
        this.mark();
    }

    public boolean isMarked() {
        return this.markIndex >= 0;
    }

    public void mark() throws IOException {
        if (this.markIndex >= 0) {
            throw new IOException("mark can't be used while already marked!");
        }
        this.markIndex = this.bufferIndex;
        this.markLine = this.line;
        this.markLineStart = this.lineStart;
    }

    public void unmark() {
        this.markIndex = -1;
        this.markLine = -1;
        this.markLineStart = -1;
    }

    public int getMarkDistance() {
        return this.bufferIndex - this.markIndex;
    }

    public char[] cloneMark() {
        return Arrays.copyOfRange(this.buffer, this.markIndex, this.bufferIndex);
    }

    public void reset() throws IOException {
        if (this.markIndex < 0) {
            throw new IOException("Stream not marked");
        }
        this.bufferIndex = this.markIndex;
        this.markIndex = -1;
        this.line = this.markLine;
        this.lineStart = this.markLineStart;
        this.markLine = -1;
    }

    @Override
    public void close() throws IOException {
        if (this.buffer == null) {
            return;
        }
        try {
            if (this.in != null) {
                this.in.close();
            }
        }
        finally {
            this.in = null;
            this.buffer = null;
        }
    }

    public char[] closeAndTakeBuffer() throws IOException {
        char[] buffer = this.buffer;
        this.close();
        return buffer;
    }

    public boolean peekFor(char consume) throws IOException {
        this.ensure();
        return this.buffer[this.bufferIndex] == consume;
    }

    public boolean tryConsume(char consume) throws IOException {
        this.ensure();
        if (this.buffer[this.bufferIndex] == consume) {
            ++this.bufferIndex;
            if (consume == '\n') {
                ++this.line;
                this.lineStart = this.bufferIndex;
            }
            return true;
        }
        return false;
    }

    public boolean tryConsumeString(@Nonnull String str) throws IOException {
        this.mark();
        if (this.tryConsume('\"') && this.tryConsume(str) && this.tryConsume('\"')) {
            this.unmark();
            return true;
        }
        this.reset();
        return false;
    }

    public boolean tryConsume(@Nonnull String str) throws IOException {
        return this.tryConsume(str, 0);
    }

    public boolean tryConsume(@Nonnull String str, int start) throws IOException {
        while (start < str.length()) {
            this.ensure();
            while (start < str.length() && this.bufferIndex < this.bufferSize) {
                char c = this.buffer[this.bufferIndex];
                if (c != str.charAt(start++)) {
                    return false;
                }
                ++this.bufferIndex;
                if (c != '\n') continue;
                ++this.line;
                this.lineStart = this.bufferIndex;
            }
        }
        return true;
    }

    public int tryConsumeSome(@Nonnull String str, int start) throws IOException {
        while (start < str.length()) {
            this.ensure();
            while (start < str.length() && this.bufferIndex < this.bufferSize) {
                char c = this.buffer[this.bufferIndex];
                if (c != str.charAt(start)) {
                    return start;
                }
                ++start;
                ++this.bufferIndex;
                if (c != '\n') continue;
                ++this.line;
                this.lineStart = this.bufferIndex;
            }
        }
        return start;
    }

    public void expect(char expect) throws IOException {
        this.ensure();
        char read = this.buffer[this.bufferIndex++];
        if (read != expect) {
            throw this.expecting(read, expect);
        }
        if (expect == '\n') {
            ++this.line;
            this.lineStart = this.bufferIndex;
        }
    }

    public void expect(@Nonnull String str, int start) throws IOException {
        this.ensure(str.length() - start);
        while (start < str.length()) {
            char c = this.buffer[this.bufferIndex];
            if (c != str.charAt(start++)) {
                throw this.expecting(c, str, start);
            }
            ++this.bufferIndex;
            if (c != '\n') continue;
            ++this.line;
            this.lineStart = this.bufferIndex;
        }
    }

    public boolean tryConsumeOrExpect(char consume, char expect) throws IOException {
        this.ensure();
        char read = this.buffer[this.bufferIndex];
        if (read == consume) {
            ++this.bufferIndex;
            if (consume == '\n') {
                ++this.line;
                this.lineStart = this.bufferIndex;
            }
            return true;
        }
        if (read == expect) {
            ++this.bufferIndex;
            if (expect == '\n') {
                ++this.line;
                this.lineStart = this.bufferIndex;
            }
            return false;
        }
        throw this.expecting(read, expect);
    }

    public void consumeWhiteSpace() throws IOException {
        block7: {
            block4: while (true) {
                if (this.bufferIndex >= this.bufferSize) {
                    this.fill();
                    if (this.bufferIndex >= this.bufferSize) break block7;
                }
                block5: while (true) {
                    if (this.bufferIndex >= this.bufferSize) continue block4;
                    char ch = this.buffer[this.bufferIndex];
                    switch (ch) {
                        case '\n': {
                            ++this.line;
                            this.lineStart = ++this.bufferIndex;
                            continue block5;
                        }
                        case '\t': 
                        case '\r': 
                        case ' ': {
                            ++this.bufferIndex;
                            continue block5;
                        }
                    }
                    if (!Character.isWhitespace(ch)) break block4;
                    ++this.bufferIndex;
                }
                break;
            }
            return;
        }
    }

    public void consumeIgnoreCase(@Nonnull String str, int start) throws IOException {
        this.ensure(str.length() - start);
        while (start < str.length()) {
            char c = this.buffer[this.bufferIndex];
            if (!RawJsonReader.equalsIgnoreCase(c, str.charAt(start++))) {
                throw this.expecting(c, str, start);
            }
            ++this.bufferIndex;
            if (c != '\n') continue;
            ++this.line;
            this.lineStart = this.bufferIndex;
        }
    }

    @Nonnull
    public String readString() throws IOException {
        this.expect('\"');
        return this.readRemainingString();
    }

    @Nonnull
    public String readRemainingString() throws IOException {
        if (this.tempSb == null) {
            this.tempSb = new StringBuilder(1024);
        }
        block13: while (true) {
            this.ensure();
            block14: while (true) {
                if (this.bufferIndex >= this.bufferSize) continue block13;
                char read = this.buffer[this.bufferIndex++];
                switch (read) {
                    case '\"': {
                        String string = this.tempSb.toString();
                        this.tempSb.setLength(0);
                        return string;
                    }
                    case '\\': {
                        this.ensure();
                        read = this.buffer[this.bufferIndex++];
                        switch (read) {
                            case '\"': 
                            case '/': 
                            case '\\': {
                                this.tempSb.append(read);
                                continue block14;
                            }
                            case 'b': {
                                this.tempSb.append('\b');
                                continue block14;
                            }
                            case 'f': {
                                this.tempSb.append('\f');
                                continue block14;
                            }
                            case 'n': {
                                this.tempSb.append('\n');
                                continue block14;
                            }
                            case 'r': {
                                this.tempSb.append('\r');
                                continue block14;
                            }
                            case 't': {
                                this.tempSb.append('\t');
                                continue block14;
                            }
                            case 'u': {
                                this.ensure(4);
                                read = this.buffer[this.bufferIndex++];
                                int digit = Character.digit(read, 16);
                                if (digit == -1) {
                                    throw this.expectingWhile(read, "HEX Digit 0-F", "reading string");
                                }
                                int hex = digit << 12;
                                if ((digit = Character.digit(read = this.buffer[this.bufferIndex++], 16)) == -1) {
                                    throw this.expectingWhile(read, "HEX Digit 0-F", "reading string");
                                }
                                hex |= digit << 8;
                                if ((digit = Character.digit(read = this.buffer[this.bufferIndex++], 16)) == -1) {
                                    throw this.expectingWhile(read, "HEX Digit 0-F", "reading string");
                                }
                                hex |= digit << 4;
                                if ((digit = Character.digit(read = this.buffer[this.bufferIndex++], 16)) == -1) {
                                    throw this.expectingWhile(read, "HEX Digit 0-F", "reading string");
                                }
                                this.tempSb.appendCodePoint(hex |= digit);
                                continue block14;
                            }
                        }
                        throw this.expecting(read, "escape char");
                    }
                }
                if (Character.isISOControl(read)) {
                    throw this.unexpectedChar(read);
                }
                this.tempSb.append(read);
            }
            break;
        }
    }

    public void skipString() throws IOException {
        this.expect('\"');
        this.skipRemainingString();
    }

    public void skipRemainingString() throws IOException {
        char read;
        block8: while (true) {
            this.ensure();
            block9: while (true) {
                if (this.bufferIndex >= this.bufferSize) continue block8;
                read = this.buffer[this.bufferIndex++];
                switch (read) {
                    case '\"': {
                        return;
                    }
                    case '\\': {
                        this.ensure();
                        read = this.buffer[this.bufferIndex++];
                        switch (read) {
                            case '\"': 
                            case '/': 
                            case '\\': 
                            case 'b': 
                            case 'f': 
                            case 'n': 
                            case 'r': 
                            case 't': {
                                continue block9;
                            }
                            case 'u': {
                                this.ensure(4);
                                read = this.buffer[this.bufferIndex++];
                                int digit = Character.digit(read, 16);
                                if (digit == -1) {
                                    throw this.expectingWhile(read, "HEX Digit 0-F", "skipping string");
                                }
                                if ((digit = Character.digit(read = this.buffer[this.bufferIndex++], 16)) == -1) {
                                    throw this.expectingWhile(read, "HEX Digit 0-F", "skipping string");
                                }
                                if ((digit = Character.digit(read = this.buffer[this.bufferIndex++], 16)) == -1) {
                                    throw this.expectingWhile(read, "HEX Digit 0-F", "skipping string");
                                }
                                if ((digit = Character.digit(read = this.buffer[this.bufferIndex++], 16)) != -1) continue block9;
                                throw this.expectingWhile(read, "HEX Digit 0-F", "skipping string");
                            }
                        }
                        throw this.expecting(read, "escape char");
                    }
                }
                if (Character.isISOControl(read)) break block8;
            }
            break;
        }
        throw this.unexpectedChar(read);
    }

    public long readStringPartAsLong(int count) throws IOException {
        assert (count > 0 && count <= 4);
        if (UnsafeUtil.UNSAFE != null && this.bufferIndex + count <= this.bufferSize) {
            return this.readStringPartAsLongUnsafe(count);
        }
        return this.readStringPartAsLongSlow(count);
    }

    protected long readStringPartAsLongSlow(int count) throws IOException {
        this.ensure(count);
        char c1 = this.buffer[this.bufferIndex++];
        if (count == 1) {
            return c1;
        }
        char c2 = this.buffer[this.bufferIndex++];
        long value = (long)c1 | (long)c2 << 16;
        if (count == 2) {
            return value;
        }
        char c3 = this.buffer[this.bufferIndex++];
        value |= (long)c3 << 32;
        if (count == 3) {
            return value;
        }
        char c4 = this.buffer[this.bufferIndex++];
        return value | (long)c4 << 48;
    }

    protected long readStringPartAsLongUnsafe(int count) throws IOException {
        this.ensure(count);
        int offset = Unsafe.ARRAY_CHAR_BASE_OFFSET + Unsafe.ARRAY_CHAR_INDEX_SCALE * this.bufferIndex;
        long value = UnsafeUtil.UNSAFE.getLong(this.buffer, offset);
        this.bufferIndex += count;
        long mask = count == 4 ? -1L : (1L << count * 16) - 1L;
        return value & mask;
    }

    public boolean readBooleanValue() throws IOException {
        this.ensure(4);
        char read = this.buffer[this.bufferIndex++];
        return switch (read) {
            case 'T', 't' -> {
                this.consumeIgnoreCase("true", 1);
                yield true;
            }
            case 'F', 'f' -> {
                this.consumeIgnoreCase("false", 1);
                yield false;
            }
            default -> throw this.expecting(read, "true' or 'false");
        };
    }

    public void skipBooleanValue() throws IOException {
        this.readBooleanValue();
    }

    @Nullable
    public Void readNullValue() throws IOException {
        this.consumeIgnoreCase("null", 0);
        return null;
    }

    public void skipNullValue() throws IOException {
        this.consumeIgnoreCase("null", 0);
    }

    public double readDoubleValue() throws IOException {
        int start = this.bufferIndex;
        block3: while (true) {
            if (this.bufferIndex >= this.bufferSize) {
                this.fill();
                if (this.bufferIndex >= this.bufferSize) {
                    return JavaDoubleParser.parseDouble(this.buffer, start, this.bufferIndex - start);
                }
            }
            block4: while (true) {
                if (this.bufferIndex >= this.bufferSize) continue block3;
                char read = this.buffer[this.bufferIndex];
                switch (read) {
                    case '+': 
                    case '-': 
                    case '.': 
                    case 'E': 
                    case 'e': {
                        ++this.bufferIndex;
                        continue block4;
                    }
                }
                if (!Character.isDigit(read)) break block3;
                ++this.bufferIndex;
            }
            break;
        }
        return JavaDoubleParser.parseDouble(this.buffer, start, this.bufferIndex - start);
    }

    public void skipDoubleValue() throws IOException {
        block3: while (true) {
            if (this.bufferIndex >= this.bufferSize) {
                this.fill();
                if (this.bufferIndex >= this.bufferSize) {
                    return;
                }
            }
            block4: while (true) {
                if (this.bufferIndex >= this.bufferSize) continue block3;
                char read = this.buffer[this.bufferIndex];
                switch (read) {
                    case '+': 
                    case '-': 
                    case '.': 
                    case 'E': 
                    case 'e': {
                        ++this.bufferIndex;
                        continue block4;
                    }
                }
                if (!Character.isDigit(read)) break block3;
                ++this.bufferIndex;
            }
            break;
        }
    }

    public float readFloatValue() throws IOException {
        return (float)this.readDoubleValue();
    }

    public void skipFloatValue() throws IOException {
        this.skipDoubleValue();
    }

    public long readLongValue() throws IOException {
        return this.readLongValue(10);
    }

    public long readLongValue(int radix) throws IOException {
        if (this.tempSb == null) {
            this.tempSb = new StringBuilder(1024);
        }
        block3: while (true) {
            if (this.bufferIndex >= this.bufferSize) {
                this.fill();
                if (this.bufferIndex >= this.bufferSize) {
                    long value = Long.parseLong(this.tempSb, 0, this.tempSb.length(), radix);
                    this.tempSb.setLength(0);
                    return value;
                }
            }
            block4: while (true) {
                if (this.bufferIndex >= this.bufferSize) continue block3;
                char read = this.buffer[this.bufferIndex];
                switch (read) {
                    case '+': 
                    case '-': 
                    case '.': 
                    case 'E': 
                    case 'e': {
                        this.tempSb.append(read);
                        ++this.bufferIndex;
                        continue block4;
                    }
                }
                if (Character.digit(read, radix) < 0) break block3;
                this.tempSb.append(read);
                ++this.bufferIndex;
            }
            break;
        }
        long value = Long.parseLong(this.tempSb, 0, this.tempSb.length(), radix);
        this.tempSb.setLength(0);
        return value;
    }

    public void skipLongValue() throws IOException {
        this.skipLongValue(10);
    }

    public void skipLongValue(int radix) throws IOException {
        block3: while (true) {
            if (this.bufferIndex >= this.bufferSize) {
                this.fill();
                if (this.bufferIndex >= this.bufferSize) {
                    return;
                }
            }
            block4: while (true) {
                if (this.bufferIndex >= this.bufferSize) continue block3;
                char read = this.buffer[this.bufferIndex];
                switch (read) {
                    case '+': 
                    case '-': 
                    case '.': 
                    case 'E': 
                    case 'e': {
                        ++this.bufferIndex;
                        continue block4;
                    }
                }
                if (Character.digit(read, radix) < 0) break block3;
                ++this.bufferIndex;
            }
            break;
        }
    }

    public int readIntValue() throws IOException {
        return this.readIntValue(10);
    }

    public int readIntValue(int radix) throws IOException {
        return (int)this.readLongValue(radix);
    }

    public byte readByteValue() throws IOException {
        return this.readByteValue(10);
    }

    public byte readByteValue(int radix) throws IOException {
        return (byte)this.readLongValue(radix);
    }

    public void skipIntValue() throws IOException {
        this.skipLongValue();
    }

    public void skipIntValue(int radix) throws IOException {
        this.skipLongValue(radix);
    }

    public void skipObject() throws IOException {
        this.expect('{');
        this.skipObjectContinued();
    }

    public void skipObjectContinued() throws IOException {
        int count = 1;
        block5: while (true) {
            this.ensure();
            while (true) {
                if (this.bufferIndex >= this.bufferSize) continue block5;
                char read = this.buffer[this.bufferIndex++];
                switch (read) {
                    case '\n': {
                        ++this.line;
                        this.lineStart = this.bufferIndex;
                        break;
                    }
                    case '{': {
                        ++count;
                        break;
                    }
                    case '}': {
                        if (--count != 0) break;
                        return;
                    }
                }
            }
            break;
        }
    }

    public void skipArray() throws IOException {
        this.expect('[');
        this.skipArrayContinued();
    }

    public void skipArrayContinued() throws IOException {
        int count = 1;
        block5: while (true) {
            this.ensure();
            while (true) {
                if (this.bufferIndex >= this.bufferSize) continue block5;
                char read = this.buffer[this.bufferIndex++];
                switch (read) {
                    case '\n': {
                        ++this.line;
                        this.lineStart = this.bufferIndex;
                        break;
                    }
                    case '[': {
                        ++count;
                        break;
                    }
                    case ']': {
                        if (--count != 0) break;
                        return;
                    }
                }
            }
            break;
        }
    }

    public void skipValue() throws IOException {
        this.ensure();
        char read = this.buffer[this.bufferIndex];
        switch (read) {
            case '\"': {
                this.skipString();
                break;
            }
            case 'N': 
            case 'n': {
                this.skipNullValue();
                break;
            }
            case 'T': 
            case 't': {
                this.consumeIgnoreCase("true", 0);
                break;
            }
            case 'F': 
            case 'f': {
                this.consumeIgnoreCase("false", 0);
                break;
            }
            case '{': {
                this.skipObject();
                break;
            }
            case '[': {
                this.skipArray();
                break;
            }
            case '+': 
            case '-': {
                this.skipDoubleValue();
                break;
            }
            default: {
                if (Character.isDigit(read)) {
                    this.skipDoubleValue();
                    break;
                }
                throw this.unexpectedChar(read);
            }
        }
    }

    @Nonnull
    private IOException unexpectedEOF() {
        return new IOException("Unexpected EOF!");
    }

    @Nonnull
    private IOException unexpectedChar(char read) {
        return new IOException("Unexpected character: " + Integer.toHexString(read) + ", '" + read + "'!");
    }

    @Nonnull
    private IOException expecting(char read, char expect) {
        return new IOException("Unexpected character: " + Integer.toHexString(read) + ", '" + read + "' expected '" + expect + "'!");
    }

    @Nonnull
    private IOException expecting(char read, String expected) {
        return new IOException("Unexpected character: " + Integer.toHexString(read) + ", '" + read + "' expected '" + expected + "'!");
    }

    @Nonnull
    private IOException expectingWhile(char read, String expected, String reason) {
        return new IOException("Unexpected character: " + Integer.toHexString(read) + ", '" + read + "' expected '" + expected + "' while " + reason + "!");
    }

    @Nonnull
    private IOException expecting(char read, @Nonnull String expected, int index) {
        return new IOException("Unexpected character: " + Integer.toHexString(read) + ", '" + read + "' when consuming string '" + expected + "' expected '" + expected.substring(index - 1) + "'!");
    }

    @Nonnull
    public String toString() {
        int lineNumber;
        if (this.buffer == null) {
            return "Closed RawJsonReader";
        }
        StringBuilder s = new StringBuilder("Index: ").append(this.streamIndex + this.bufferIndex).append(", StreamIndex: ").append(this.streamIndex).append(", BufferIndex: ").append(this.bufferIndex).append(", BufferSize: ").append(this.bufferSize).append(", Line: ").append(this.line).append(", MarkIndex: ").append(this.markIndex).append(", MarkLine: ").append(this.markLine).append('\n');
        int lineStart = this.findLineStart(this.bufferIndex);
        for (lineNumber = this.line; lineStart > 0 && lineNumber > this.line - 10; --lineNumber) {
            lineStart = this.findLineStart(lineStart);
        }
        while (lineNumber < this.line) {
            lineStart = this.appendLine(s, lineStart, lineNumber);
            ++lineNumber;
        }
        lineStart = this.appendProblemLine(s, lineStart, this.line);
        while (lineStart < this.bufferSize && lineNumber < this.line + 10) {
            lineStart = this.appendLine(s, lineStart, lineNumber);
            ++lineNumber;
        }
        if (this.in == null) {
            return "Buffer RawJsonReader: " + String.valueOf(s);
        }
        return "Streamed RawJsonReader: " + String.valueOf(s);
    }

    private int findLineStart(int index) {
        --index;
        while (index > 0 && this.buffer[index] != '\n') {
            --index;
        }
        return index;
    }

    private int appendLine(@Nonnull StringBuilder sb, int index, int lineNumber) {
        int lineStart = index + 1;
        ++index;
        while (index < this.bufferSize && this.buffer[index] != '\n') {
            ++index;
        }
        sb.append("L").append(String.format("%3s", lineNumber)).append('|').append(this.buffer, lineStart, index - lineStart).append('\n');
        return index;
    }

    private int appendProblemLine(@Nonnull StringBuilder sb, int index, int lineNumber) {
        int lineStart = ++index;
        while (index < this.bufferSize && this.buffer[index] != '\n') {
            ++index;
        }
        sb.append("L").append(String.format("%3s", lineNumber)).append('>').append(this.buffer, lineStart, index - lineStart).append('\n');
        sb.append("    |");
        sb.append("-".repeat(Math.max(0, this.bufferIndex - lineStart - 1)));
        sb.append('^').append('\n');
        return index;
    }

    @Nonnull
    public static RawJsonReader fromRawString(String str) {
        return RawJsonReader.fromJsonString("\"" + str + "\"");
    }

    @Nonnull
    public static RawJsonReader fromJsonString(@Nonnull String str) {
        return RawJsonReader.fromBuffer(str.toCharArray());
    }

    @Nonnull
    public static RawJsonReader fromPath(@Nonnull Path path, @Nonnull char[] buffer) throws IOException {
        return new RawJsonReader(new InputStreamReader(Files.newInputStream(path, new OpenOption[0]), StandardCharsets.UTF_8), buffer);
    }

    @Nonnull
    public static RawJsonReader fromBuffer(@Nonnull char[] buffer) {
        return new RawJsonReader(buffer);
    }

    public static boolean equalsIgnoreCase(char c1, char c2) {
        char u2;
        if (c1 == c2) {
            return true;
        }
        char u1 = Character.toUpperCase(c1);
        return u1 == (u2 = Character.toUpperCase(c2)) || Character.toLowerCase(u1) == Character.toLowerCase(u2);
    }

    @Deprecated
    public static BsonDocument readBsonDocument(@Nonnull RawJsonReader reader) throws IOException {
        reader.expect('{');
        StringBuilder sb = new StringBuilder("{");
        RawJsonReader.readBsonDocument0(reader, sb);
        return BsonDocument.parse(sb.toString());
    }

    private static void readBsonDocument0(@Nonnull RawJsonReader reader, @Nonnull StringBuilder sb) throws IOException {
        int read;
        int count = 1;
        while ((read = reader.read()) != -1) {
            sb.append((char)read);
            switch (read) {
                case 123: {
                    ++count;
                    break;
                }
                case 125: {
                    if (--count != 0) break;
                    return;
                }
                case 91: {
                    RawJsonReader.readBsonArray0(reader, sb);
                    break;
                }
                case 10: {
                    ++reader.line;
                    reader.lineStart = reader.bufferIndex;
                }
            }
        }
        throw reader.unexpectedEOF();
    }

    @Deprecated
    public static BsonArray readBsonArray(@Nonnull RawJsonReader reader) throws IOException {
        reader.expect('[');
        StringBuilder sb = new StringBuilder("[");
        RawJsonReader.readBsonArray0(reader, sb);
        return BsonArray.parse(sb.toString());
    }

    private static void readBsonArray0(@Nonnull RawJsonReader reader, @Nonnull StringBuilder sb) throws IOException {
        int read;
        int count = 1;
        while ((read = reader.read()) != -1) {
            sb.append((char)read);
            switch (read) {
                case 91: {
                    ++count;
                    break;
                }
                case 93: {
                    if (--count != 0) break;
                    return;
                }
                case 123: {
                    RawJsonReader.readBsonDocument0(reader, sb);
                    break;
                }
                case 10: {
                    ++reader.line;
                    reader.lineStart = reader.bufferIndex;
                }
            }
        }
        throw reader.unexpectedEOF();
    }

    @Deprecated
    public static BsonValue readBsonValue(@Nonnull RawJsonReader reader) throws IOException {
        int read = reader.peek();
        if (read == -1) {
            throw reader.unexpectedEOF();
        }
        return switch (read) {
            case 34 -> new BsonString(reader.readString());
            case 78, 110 -> {
                reader.skipNullValue();
                yield BsonNull.VALUE;
            }
            case 70, 84, 102, 116 -> {
                if (reader.readBooleanValue()) {
                    yield BsonBoolean.TRUE;
                }
                yield BsonBoolean.FALSE;
            }
            case 123 -> RawJsonReader.readBsonDocument(reader);
            case 91 -> RawJsonReader.readBsonArray(reader);
            case 43, 45 -> new BsonDouble(reader.readDoubleValue());
            default -> {
                if (Character.isDigit(read)) {
                    yield new BsonDouble(reader.readDoubleValue());
                }
                throw reader.unexpectedChar((char)read);
            }
        };
    }

    public static boolean seekToKey(@Nonnull RawJsonReader reader, @Nonnull String search) throws IOException {
        reader.consumeWhiteSpace();
        reader.expect('{');
        reader.consumeWhiteSpace();
        if (reader.tryConsume('}')) {
            return false;
        }
        while (true) {
            reader.expect('\"');
            if (reader.tryConsume(search) && reader.tryConsume('\"')) {
                reader.consumeWhiteSpace();
                reader.expect(':');
                reader.consumeWhiteSpace();
                return true;
            }
            reader.skipRemainingString();
            reader.consumeWhiteSpace();
            reader.expect(':');
            reader.consumeWhiteSpace();
            reader.skipValue();
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect('}', ',')) {
                return false;
            }
            reader.consumeWhiteSpace();
        }
    }

    @Nullable
    public static String seekToKeyFromObjectStart(@Nonnull RawJsonReader reader, @Nonnull String search1, @Nonnull String search2) throws IOException {
        reader.consumeWhiteSpace();
        reader.expect('{');
        reader.consumeWhiteSpace();
        if (reader.tryConsume('}')) {
            return null;
        }
        while (true) {
            reader.expect('\"');
            int search1Index = reader.tryConsumeSome(search1, 0);
            if (search1Index == search1.length() && reader.tryConsume('\"')) {
                reader.consumeWhiteSpace();
                reader.expect(':');
                reader.consumeWhiteSpace();
                return search1;
            }
            if (reader.tryConsume(search2, search1Index) && reader.tryConsume('\"')) {
                reader.consumeWhiteSpace();
                reader.expect(':');
                reader.consumeWhiteSpace();
                return search2;
            }
            reader.skipRemainingString();
            reader.consumeWhiteSpace();
            reader.expect(':');
            reader.consumeWhiteSpace();
            reader.skipValue();
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect('}', ',')) {
                return null;
            }
            reader.consumeWhiteSpace();
        }
    }

    @Nullable
    public static String seekToKeyFromObjectContinued(@Nonnull RawJsonReader reader, @Nonnull String search1, @Nonnull String search2) throws IOException {
        reader.consumeWhiteSpace();
        if (reader.tryConsumeOrExpect('}', ',')) {
            return null;
        }
        reader.consumeWhiteSpace();
        while (true) {
            reader.expect('\"');
            int search1Index = reader.tryConsumeSome(search1, 0);
            if (search1Index == search1.length() && reader.tryConsume('\"')) {
                reader.consumeWhiteSpace();
                reader.expect(':');
                reader.consumeWhiteSpace();
                return search1;
            }
            if (reader.tryConsume(search2, search1Index) && reader.tryConsume('\"')) {
                reader.consumeWhiteSpace();
                reader.expect(':');
                reader.consumeWhiteSpace();
                return search2;
            }
            reader.skipRemainingString();
            reader.consumeWhiteSpace();
            reader.expect(':');
            reader.consumeWhiteSpace();
            reader.skipValue();
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect('}', ',')) {
                return null;
            }
            reader.consumeWhiteSpace();
        }
    }

    public static void validateBsonDocument(@Nonnull RawJsonReader reader) throws IOException {
        reader.expect('{');
        reader.consumeWhiteSpace();
        if (reader.tryConsume('}')) {
            return;
        }
        while (true) {
            reader.skipString();
            reader.consumeWhiteSpace();
            reader.expect(':');
            reader.consumeWhiteSpace();
            RawJsonReader.validateBsonValue(reader);
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect('}', ',')) {
                return;
            }
            reader.consumeWhiteSpace();
        }
    }

    public static void validateBsonArray(@Nonnull RawJsonReader reader) throws IOException {
        reader.expect('[');
        reader.consumeWhiteSpace();
        if (reader.tryConsume(']')) {
            return;
        }
        while (true) {
            RawJsonReader.validateBsonValue(reader);
            reader.consumeWhiteSpace();
            if (reader.tryConsumeOrExpect(']', ',')) {
                return;
            }
            reader.consumeWhiteSpace();
        }
    }

    public static void validateBsonValue(@Nonnull RawJsonReader reader) throws IOException {
        int read = reader.peek();
        if (read == -1) {
            throw reader.unexpectedEOF();
        }
        switch (read) {
            case 34: {
                reader.skipString();
                break;
            }
            case 78: 
            case 110: {
                reader.skipNullValue();
                break;
            }
            case 70: 
            case 84: 
            case 102: 
            case 116: {
                reader.readBooleanValue();
                break;
            }
            case 123: {
                RawJsonReader.validateBsonDocument(reader);
                break;
            }
            case 91: {
                RawJsonReader.validateBsonArray(reader);
                break;
            }
            case 43: 
            case 45: {
                reader.readDoubleValue();
                break;
            }
            default: {
                if (Character.isDigit(read)) {
                    reader.readDoubleValue();
                    return;
                }
                throw reader.unexpectedChar((char)read);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public static <T> T readSync(@Nonnull Path path, @Nonnull Codec<T> codec, @Nonnull HytaleLogger logger) throws IOException {
        char[] buffer = READ_BUFFER.get();
        RawJsonReader reader = RawJsonReader.fromPath(path, buffer);
        try {
            ExtraInfo extraInfo = ExtraInfo.THREAD_LOCAL.get();
            T value = codec.decodeJson(reader, extraInfo);
            extraInfo.getValidationResults().logOrThrowValidatorExceptions(logger);
            T t = value;
            return t;
        }
        finally {
            char[] newBuffer = reader.closeAndTakeBuffer();
            if (newBuffer.length > buffer.length) {
                READ_BUFFER.set(newBuffer);
            }
        }
    }

    @Nullable
    public static <T> T readSyncWithBak(@Nonnull Path path, @Nonnull Codec<T> codec, @Nonnull HytaleLogger logger) {
        try {
            return RawJsonReader.readSync(path, codec, logger);
        }
        catch (IOException e) {
            Path backupPath = path.resolveSibling(String.valueOf(path.getFileName()) + ".bak");
            if (e instanceof NoSuchFileException && !Files.exists(backupPath, new LinkOption[0])) {
                return null;
            }
            if (Sentry.isEnabled()) {
                Sentry.captureException(e);
            }
            ((HytaleLogger.Api)logger.at(Level.SEVERE).withCause(e)).log("Failed to load from primary file %s, trying backup file", path);
            try {
                T value = RawJsonReader.readSync(backupPath, codec, logger);
                logger.at(Level.WARNING).log("Loaded from backup file %s after primary file %s failed to load", (Object)backupPath, (Object)path);
                return value;
            }
            catch (NoSuchFileException e1) {
                return null;
            }
            catch (IOException e1) {
                ((HytaleLogger.Api)logger.at(Level.WARNING).withCause(e)).log("Failed to load from both %s and backup file %s", (Object)path, (Object)backupPath);
                return null;
            }
        }
    }
}

