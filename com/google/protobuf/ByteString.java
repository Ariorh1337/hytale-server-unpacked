/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.Android;
import com.google.protobuf.ByteBufferWriter;
import com.google.protobuf.ByteOutput;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.CompileTimeConstant;
import com.google.protobuf.Internal;
import com.google.protobuf.Java8Compatibility;
import com.google.protobuf.RopeByteString;
import com.google.protobuf.TextFormatEscaper;
import com.google.protobuf.Utf8;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.InvalidMarkException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.NoSuchElementException;

@CheckReturnValue
public abstract class ByteString
implements Iterable<Byte>,
Serializable {
    private static final long serialVersionUID = 1L;
    static final int CONCATENATE_BY_COPY_SIZE = 128;
    static final int MIN_READ_FROM_CHUNK_SIZE = 256;
    static final int MAX_READ_FROM_CHUNK_SIZE = 8192;
    public static final ByteString EMPTY = new LiteralByteString(Internal.EMPTY_BYTE_ARRAY);
    private static final ByteArrayCopier byteArrayCopier = Android.isOnAndroidDevice() ? new SystemByteArrayCopier() : new ArraysByteArrayCopier();
    private int hash = 0;
    private static final int UNSIGNED_BYTE_MASK = 255;
    private static final Comparator<ByteString> UNSIGNED_LEXICOGRAPHICAL_COMPARATOR = new Comparator<ByteString>(){

        @Override
        public int compare(ByteString former, ByteString latter) {
            ByteIterator formerBytes = former.iterator();
            ByteIterator latterBytes = latter.iterator();
            while (formerBytes.hasNext() && latterBytes.hasNext()) {
                int result = Integer.compare(ByteString.toInt(formerBytes.nextByte()), ByteString.toInt(latterBytes.nextByte()));
                if (result == 0) continue;
                return result;
            }
            return Integer.compare(former.size(), latter.size());
        }
    };

    ByteString() {
    }

    public abstract byte byteAt(int var1);

    abstract byte internalByteAt(int var1);

    public ByteIterator iterator() {
        return new AbstractByteIterator(){
            private int position = 0;
            private final int limit = ByteString.this.size();

            @Override
            public boolean hasNext() {
                return this.position < this.limit;
            }

            @Override
            public byte nextByte() {
                int currentPos = this.position;
                if (currentPos >= this.limit) {
                    throw new NoSuchElementException();
                }
                this.position = currentPos + 1;
                return ByteString.this.internalByteAt(currentPos);
            }
        };
    }

    public abstract int size();

    public final boolean isEmpty() {
        return this.size() == 0;
    }

    public static final ByteString empty() {
        return EMPTY;
    }

    private static int toInt(byte value) {
        return value & 0xFF;
    }

    private static int hexDigit(char c) {
        if (c >= '0' && c <= '9') {
            return c - 48;
        }
        if (c >= 'A' && c <= 'F') {
            return c - 65 + 10;
        }
        if (c >= 'a' && c <= 'f') {
            return c - 97 + 10;
        }
        return -1;
    }

    private static int extractHexDigit(String hexString, int index) {
        int digit = ByteString.hexDigit(hexString.charAt(index));
        if (digit == -1) {
            throw new NumberFormatException("Invalid hexString " + hexString + " must only contain [0-9a-fA-F] but contained " + hexString.charAt(index) + " at index " + index);
        }
        return digit;
    }

    public static Comparator<ByteString> unsignedLexicographicalComparator() {
        return UNSIGNED_LEXICOGRAPHICAL_COMPARATOR;
    }

    public final ByteString substring(int beginIndex) {
        return this.substring(beginIndex, this.size());
    }

    public abstract ByteString substring(int var1, int var2);

    public final boolean startsWith(ByteString prefix) {
        return this.size() >= prefix.size() && this.substring(0, prefix.size()).equals(prefix);
    }

    public final boolean endsWith(ByteString suffix) {
        return this.size() >= suffix.size() && this.substring(this.size() - suffix.size()).equals(suffix);
    }

    public static ByteString fromHex(@CompileTimeConstant String hexString) {
        if (hexString.length() % 2 != 0) {
            throw new NumberFormatException("Invalid hexString " + hexString + " of length " + hexString.length() + " must be even.");
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < bytes.length; ++i) {
            int d1 = ByteString.extractHexDigit(hexString, 2 * i);
            int d2 = ByteString.extractHexDigit(hexString, 2 * i + 1);
            bytes[i] = (byte)(d1 << 4 | d2);
        }
        return new LiteralByteString(bytes);
    }

    public static ByteString copyFrom(byte[] bytes, int offset, int size) {
        ByteString.checkRange(offset, offset + size, bytes.length);
        return new LiteralByteString(byteArrayCopier.copyFrom(bytes, offset, size));
    }

    public static ByteString copyFrom(byte[] bytes) {
        return ByteString.copyFrom(bytes, 0, bytes.length);
    }

    static ByteString wrap(ByteBuffer buffer) {
        if (buffer.hasArray()) {
            int offset = buffer.arrayOffset();
            return ByteString.wrap(buffer.array(), offset + buffer.position(), buffer.remaining());
        }
        return new NioByteString(buffer);
    }

    static ByteString nioByteString(ByteBuffer buffer) {
        return new NioByteString(buffer);
    }

    static ByteString wrap(byte[] bytes) {
        return new LiteralByteString(bytes);
    }

    static ByteString wrap(byte[] bytes, int offset, int length) {
        return new BoundedByteString(bytes, offset, length);
    }

    public static ByteString copyFrom(ByteBuffer bytes, int size) {
        ByteString.checkRange(0, size, bytes.remaining());
        byte[] copy = new byte[size];
        bytes.get(copy);
        return new LiteralByteString(copy);
    }

    public static ByteString copyFrom(ByteBuffer bytes) {
        return ByteString.copyFrom(bytes, bytes.remaining());
    }

    public static ByteString copyFrom(String text, String charsetName) throws UnsupportedEncodingException {
        return new LiteralByteString(text.getBytes(charsetName));
    }

    public static ByteString copyFrom(String text, Charset charset) {
        return new LiteralByteString(text.getBytes(charset));
    }

    public static ByteString copyFromUtf8(String text) {
        return new LiteralByteString(text.getBytes(Internal.UTF_8));
    }

    public static ByteString readFrom(InputStream streamToDrain) throws IOException {
        return ByteString.readFrom(streamToDrain, 256, 8192);
    }

    public static ByteString readFrom(InputStream streamToDrain, int chunkSize) throws IOException {
        return ByteString.readFrom(streamToDrain, chunkSize, chunkSize);
    }

    public static ByteString readFrom(InputStream streamToDrain, int minChunkSize, int maxChunkSize) throws IOException {
        ByteString chunk;
        ArrayList<ByteString> results = new ArrayList<ByteString>();
        int chunkSize = minChunkSize;
        while ((chunk = ByteString.readChunk(streamToDrain, chunkSize)) != null) {
            results.add(chunk);
            chunkSize = Math.min(chunkSize * 2, maxChunkSize);
        }
        return ByteString.copyFrom(results);
    }

    private static ByteString readChunk(InputStream in, int chunkSize) throws IOException {
        int bytesRead;
        int count;
        byte[] buf = new byte[chunkSize];
        for (bytesRead = 0; bytesRead < chunkSize && (count = in.read(buf, bytesRead, chunkSize - bytesRead)) != -1; bytesRead += count) {
        }
        if (bytesRead == 0) {
            return null;
        }
        return ByteString.copyFrom(buf, 0, bytesRead);
    }

    public final ByteString concat(ByteString other) {
        if (Integer.MAX_VALUE - this.size() < other.size()) {
            throw new IllegalArgumentException("ByteString would be too long: " + this.size() + "+" + other.size());
        }
        return RopeByteString.concatenate(this, other);
    }

    public static ByteString copyFrom(Iterable<ByteString> byteStrings) {
        int size;
        if (!(byteStrings instanceof Collection)) {
            int tempSize = 0;
            Iterator<ByteString> iter = byteStrings.iterator();
            while (iter.hasNext()) {
                iter.next();
                ++tempSize;
            }
            size = tempSize;
        } else {
            size = ((Collection)byteStrings).size();
        }
        if (size == 0) {
            return EMPTY;
        }
        return ByteString.balancedConcat(byteStrings.iterator(), size);
    }

    private static ByteString balancedConcat(Iterator<ByteString> iterator, int length) {
        ByteString result;
        if (length < 1) {
            throw new IllegalArgumentException(String.format("length (%s) must be >= 1", length));
        }
        if (length == 1) {
            result = iterator.next();
        } else {
            int halfLength = length >>> 1;
            ByteString left = ByteString.balancedConcat(iterator, halfLength);
            ByteString right = ByteString.balancedConcat(iterator, length - halfLength);
            result = left.concat(right);
        }
        return result;
    }

    public void copyTo(byte[] target, int offset) {
        this.copyTo(target, 0, offset, this.size());
    }

    @Deprecated
    public final void copyTo(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
        ByteString.checkRange(sourceOffset, sourceOffset + numberToCopy, this.size());
        ByteString.checkRange(targetOffset, targetOffset + numberToCopy, target.length);
        if (numberToCopy > 0) {
            this.copyToInternal(target, sourceOffset, targetOffset, numberToCopy);
        }
    }

    protected abstract void copyToInternal(byte[] var1, int var2, int var3, int var4);

    public abstract void copyTo(ByteBuffer var1);

    public final byte[] toByteArray() {
        int size = this.size();
        if (size == 0) {
            return Internal.EMPTY_BYTE_ARRAY;
        }
        byte[] result = new byte[size];
        this.copyToInternal(result, 0, 0, size);
        return result;
    }

    public abstract void writeTo(OutputStream var1) throws IOException;

    final void writeTo(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
        ByteString.checkRange(sourceOffset, sourceOffset + numberToWrite, this.size());
        if (numberToWrite > 0) {
            this.writeToInternal(out, sourceOffset, numberToWrite);
        }
    }

    abstract void writeToInternal(OutputStream var1, int var2, int var3) throws IOException;

    abstract void writeTo(ByteOutput var1) throws IOException;

    abstract void writeToReverse(ByteOutput var1) throws IOException;

    public abstract ByteBuffer asReadOnlyByteBuffer();

    public abstract List<ByteBuffer> asReadOnlyByteBufferList();

    public final String toString(String charsetName) throws UnsupportedEncodingException {
        try {
            return this.toString(Charset.forName(charsetName));
        }
        catch (UnsupportedCharsetException e) {
            UnsupportedEncodingException exception = new UnsupportedEncodingException(charsetName);
            exception.initCause(e);
            throw exception;
        }
    }

    public final String toString(Charset charset) {
        return this.size() == 0 ? "" : this.toStringInternal(charset);
    }

    protected abstract String toStringInternal(Charset var1);

    public final String toStringUtf8() {
        return this.toString(Internal.UTF_8);
    }

    public abstract boolean isValidUtf8();

    protected abstract int partialIsValidUtf8(int var1, int var2, int var3);

    public abstract boolean equals(Object var1);

    public final int hashCode() {
        int h = this.hash;
        if (h == 0) {
            int size = this.size();
            h = this.partialHash(size, 0, size);
            if (h == 0) {
                h = 1;
            }
            this.hash = h;
        }
        return h;
    }

    public abstract InputStream newInput();

    public abstract CodedInputStream newCodedInput();

    public static Output newOutput(int initialCapacity) {
        return new Output(initialCapacity);
    }

    public static Output newOutput() {
        return new Output(128);
    }

    static CodedBuilder newCodedBuilder(int size) {
        return new CodedBuilder(size);
    }

    protected abstract int getTreeDepth();

    protected abstract boolean isBalanced();

    protected final int peekCachedHashCode() {
        return this.hash;
    }

    protected abstract int partialHash(int var1, int var2, int var3);

    static void checkIndex(int index, int size) {
        if ((index | size - (index + 1)) < 0) {
            if (index < 0) {
                throw new ArrayIndexOutOfBoundsException("Index < 0: " + index);
            }
            throw new ArrayIndexOutOfBoundsException("Index > length: " + index + ", " + size);
        }
    }

    @CanIgnoreReturnValue
    static int checkRange(int startIndex, int endIndex, int size) {
        int length = endIndex - startIndex;
        if ((startIndex | endIndex | length | size - endIndex) < 0) {
            if (startIndex < 0) {
                throw new IndexOutOfBoundsException("Beginning index: " + startIndex + " < 0");
            }
            if (endIndex < startIndex) {
                throw new IndexOutOfBoundsException("Beginning index larger than ending index: " + startIndex + ", " + endIndex);
            }
            throw new IndexOutOfBoundsException("End index: " + endIndex + " >= " + size);
        }
        return length;
    }

    public final String toString() {
        return String.format(Locale.ROOT, "<ByteString@%s size=%d contents=\"%s\">", Integer.toHexString(System.identityHashCode(this)), this.size(), this.truncateAndEscapeForDisplay());
    }

    private String truncateAndEscapeForDisplay() {
        int limit = 50;
        return this.size() <= 50 ? TextFormatEscaper.escapeBytes(this) : TextFormatEscaper.escapeBytes(this.substring(0, 47)) + "...";
    }

    private static class LiteralByteString
    extends LeafByteString {
        private static final long serialVersionUID = 1L;
        protected final byte[] bytes;

        LiteralByteString(byte[] bytes) {
            if (bytes == null) {
                throw new NullPointerException();
            }
            this.bytes = bytes;
        }

        @Override
        public byte byteAt(int index) {
            return this.bytes[index];
        }

        @Override
        byte internalByteAt(int index) {
            return this.bytes[index];
        }

        @Override
        public int size() {
            return this.bytes.length;
        }

        @Override
        public final ByteString substring(int beginIndex, int endIndex) {
            int length = LiteralByteString.checkRange(beginIndex, endIndex, this.size());
            if (length == 0) {
                return EMPTY;
            }
            return new BoundedByteString(this.bytes, this.getOffsetIntoBytes() + beginIndex, length);
        }

        @Override
        protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
            System.arraycopy(this.bytes, sourceOffset, target, targetOffset, numberToCopy);
        }

        @Override
        public final void copyTo(ByteBuffer target) {
            target.put(this.bytes, this.getOffsetIntoBytes(), this.size());
        }

        @Override
        public final ByteBuffer asReadOnlyByteBuffer() {
            return ByteBuffer.wrap(this.bytes, this.getOffsetIntoBytes(), this.size()).asReadOnlyBuffer();
        }

        @Override
        public final List<ByteBuffer> asReadOnlyByteBufferList() {
            return Collections.singletonList(this.asReadOnlyByteBuffer());
        }

        @Override
        public final void writeTo(OutputStream outputStream) throws IOException {
            outputStream.write(this.toByteArray());
        }

        @Override
        final void writeToInternal(OutputStream outputStream, int sourceOffset, int numberToWrite) throws IOException {
            outputStream.write(this.bytes, this.getOffsetIntoBytes() + sourceOffset, numberToWrite);
        }

        @Override
        final void writeTo(ByteOutput output) throws IOException {
            output.writeLazy(this.bytes, this.getOffsetIntoBytes(), this.size());
        }

        @Override
        protected final String toStringInternal(Charset charset) {
            return new String(this.bytes, this.getOffsetIntoBytes(), this.size(), charset);
        }

        @Override
        public final boolean isValidUtf8() {
            int offset = this.getOffsetIntoBytes();
            return Utf8.isValidUtf8(this.bytes, offset, offset + this.size());
        }

        @Override
        protected final int partialIsValidUtf8(int state, int offset, int length) {
            int index = this.getOffsetIntoBytes() + offset;
            return Utf8.partialIsValidUtf8(state, this.bytes, index, index + length);
        }

        @Override
        public final boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof ByteString)) {
                return false;
            }
            if (this.size() != ((ByteString)other).size()) {
                return false;
            }
            if (this.size() == 0) {
                return true;
            }
            if (other instanceof LiteralByteString) {
                LiteralByteString otherAsLiteral = (LiteralByteString)other;
                int thisHash = this.peekCachedHashCode();
                int thatHash = otherAsLiteral.peekCachedHashCode();
                if (thisHash != 0 && thatHash != 0 && thisHash != thatHash) {
                    return false;
                }
                return this.equalsRange((LiteralByteString)other, 0, this.size());
            }
            return other.equals(this);
        }

        @Override
        final boolean equalsRange(ByteString other, int offset, int length) {
            if (length > other.size()) {
                throw new IllegalArgumentException("Length too large: " + length + this.size());
            }
            if (offset + length > other.size()) {
                throw new IllegalArgumentException("Ran off end of other: " + offset + ", " + length + ", " + other.size());
            }
            if (other instanceof LiteralByteString) {
                LiteralByteString lbsOther = (LiteralByteString)other;
                byte[] thisBytes = this.bytes;
                byte[] otherBytes = lbsOther.bytes;
                int thisLimit = this.getOffsetIntoBytes() + length;
                int thisIndex = this.getOffsetIntoBytes();
                int otherIndex = lbsOther.getOffsetIntoBytes() + offset;
                while (thisIndex < thisLimit) {
                    if (thisBytes[thisIndex] != otherBytes[otherIndex]) {
                        return false;
                    }
                    ++thisIndex;
                    ++otherIndex;
                }
                return true;
            }
            return other.substring(offset, offset + length).equals(this.substring(0, length));
        }

        @Override
        protected final int partialHash(int h, int offset, int length) {
            return Internal.partialHash(h, this.bytes, this.getOffsetIntoBytes() + offset, length);
        }

        @Override
        public final InputStream newInput() {
            return new ByteArrayInputStream(this.bytes, this.getOffsetIntoBytes(), this.size());
        }

        @Override
        public final CodedInputStream newCodedInput() {
            return CodedInputStream.newInstance(this.bytes, this.getOffsetIntoBytes(), this.size(), true);
        }

        protected int getOffsetIntoBytes() {
            return 0;
        }
    }

    private static interface ByteArrayCopier {
        public byte[] copyFrom(byte[] var1, int var2, int var3);
    }

    private static final class NioByteString
    extends LeafByteString {
        private final ByteBuffer buffer;

        NioByteString(ByteBuffer buffer) {
            Internal.checkNotNull(buffer, "buffer");
            this.buffer = buffer.slice().order(ByteOrder.nativeOrder());
        }

        private Object writeReplace() {
            return ByteString.copyFrom(this.buffer.slice());
        }

        private void readObject(ObjectInputStream in) throws IOException {
            throw new InvalidObjectException("NioByteString instances are not to be serialized directly");
        }

        @Override
        public byte byteAt(int index) {
            try {
                return this.buffer.get(index);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw e;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException(e.getMessage());
            }
        }

        @Override
        public byte internalByteAt(int index) {
            return this.byteAt(index);
        }

        @Override
        public int size() {
            return this.buffer.remaining();
        }

        @Override
        public ByteString substring(int beginIndex, int endIndex) {
            try {
                ByteBuffer slice = this.slice(beginIndex, endIndex);
                return new NioByteString(slice);
            }
            catch (ArrayIndexOutOfBoundsException e) {
                throw e;
            }
            catch (IndexOutOfBoundsException e) {
                throw new ArrayIndexOutOfBoundsException(e.getMessage());
            }
        }

        @Override
        protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
            ByteBuffer slice = this.buffer.slice();
            Java8Compatibility.position(slice, sourceOffset);
            slice.get(target, targetOffset, numberToCopy);
        }

        @Override
        public void copyTo(ByteBuffer target) {
            target.put(this.buffer.slice());
        }

        @Override
        public void writeTo(OutputStream out) throws IOException {
            out.write(this.toByteArray());
        }

        @Override
        boolean equalsRange(ByteString other, int offset, int length) {
            return this.substring(0, length).equals(other.substring(offset, offset + length));
        }

        @Override
        void writeToInternal(OutputStream out, int sourceOffset, int numberToWrite) throws IOException {
            if (this.buffer.hasArray()) {
                int bufferOffset = this.buffer.arrayOffset() + this.buffer.position() + sourceOffset;
                out.write(this.buffer.array(), bufferOffset, numberToWrite);
                return;
            }
            ByteBufferWriter.write(this.slice(sourceOffset, sourceOffset + numberToWrite), out);
        }

        @Override
        void writeTo(ByteOutput output) throws IOException {
            output.writeLazy(this.buffer.slice());
        }

        @Override
        public ByteBuffer asReadOnlyByteBuffer() {
            return this.buffer.asReadOnlyBuffer();
        }

        @Override
        public List<ByteBuffer> asReadOnlyByteBufferList() {
            return Collections.singletonList(this.asReadOnlyByteBuffer());
        }

        @Override
        protected String toStringInternal(Charset charset) {
            int length;
            int offset;
            byte[] bytes;
            if (this.buffer.hasArray()) {
                bytes = this.buffer.array();
                offset = this.buffer.arrayOffset() + this.buffer.position();
                length = this.buffer.remaining();
            } else {
                bytes = this.toByteArray();
                offset = 0;
                length = bytes.length;
            }
            return new String(bytes, offset, length, charset);
        }

        @Override
        public boolean isValidUtf8() {
            return Utf8.isValidUtf8(this.buffer);
        }

        @Override
        protected int partialIsValidUtf8(int state, int offset, int length) {
            return Utf8.partialIsValidUtf8(state, this.buffer, offset, offset + length);
        }

        @Override
        public boolean equals(Object other) {
            if (other == this) {
                return true;
            }
            if (!(other instanceof ByteString)) {
                return false;
            }
            ByteString otherString = (ByteString)other;
            if (this.size() != otherString.size()) {
                return false;
            }
            if (this.size() == 0) {
                return true;
            }
            if (other instanceof NioByteString) {
                return this.buffer.equals(((NioByteString)other).buffer);
            }
            if (other instanceof RopeByteString) {
                return other.equals(this);
            }
            return this.buffer.equals(otherString.asReadOnlyByteBuffer());
        }

        @Override
        protected int partialHash(int h, int offset, int length) {
            for (int i = offset; i < offset + length; ++i) {
                h = h * 31 + this.buffer.get(i);
            }
            return h;
        }

        @Override
        public InputStream newInput() {
            return new InputStream(){
                private final ByteBuffer buf;
                {
                    this.buf = buffer.slice();
                }

                @Override
                public void mark(int readlimit) {
                    Java8Compatibility.mark(this.buf);
                }

                @Override
                public boolean markSupported() {
                    return true;
                }

                @Override
                public void reset() throws IOException {
                    try {
                        Java8Compatibility.reset(this.buf);
                    }
                    catch (InvalidMarkException e) {
                        throw new IOException(e);
                    }
                }

                @Override
                public int available() throws IOException {
                    return this.buf.remaining();
                }

                @Override
                public int read() throws IOException {
                    if (!this.buf.hasRemaining()) {
                        return -1;
                    }
                    return this.buf.get() & 0xFF;
                }

                @Override
                public int read(byte[] bytes, int off, int len) throws IOException {
                    if (!this.buf.hasRemaining()) {
                        return -1;
                    }
                    len = Math.min(len, this.buf.remaining());
                    this.buf.get(bytes, off, len);
                    return len;
                }
            };
        }

        @Override
        public CodedInputStream newCodedInput() {
            return CodedInputStream.newInstance(this.buffer, true);
        }

        private ByteBuffer slice(int beginIndex, int endIndex) {
            if (beginIndex < this.buffer.position() || endIndex > this.buffer.limit() || beginIndex > endIndex) {
                throw new IllegalArgumentException(String.format("Invalid indices [%d, %d]", beginIndex, endIndex));
            }
            ByteBuffer slice = this.buffer.slice();
            Java8Compatibility.position(slice, beginIndex - this.buffer.position());
            Java8Compatibility.limit(slice, endIndex - this.buffer.position());
            return slice;
        }
    }

    private static final class BoundedByteString
    extends LiteralByteString {
        private final int bytesOffset;
        private final int bytesLength;
        private static final long serialVersionUID = 1L;

        BoundedByteString(byte[] bytes, int offset, int length) {
            super(bytes);
            BoundedByteString.checkRange(offset, offset + length, bytes.length);
            this.bytesOffset = offset;
            this.bytesLength = length;
        }

        @Override
        public byte byteAt(int index) {
            BoundedByteString.checkIndex(index, this.size());
            return this.bytes[this.bytesOffset + index];
        }

        @Override
        byte internalByteAt(int index) {
            return this.bytes[this.bytesOffset + index];
        }

        @Override
        public int size() {
            return this.bytesLength;
        }

        @Override
        protected int getOffsetIntoBytes() {
            return this.bytesOffset;
        }

        @Override
        protected void copyToInternal(byte[] target, int sourceOffset, int targetOffset, int numberToCopy) {
            System.arraycopy(this.bytes, this.getOffsetIntoBytes() + sourceOffset, target, targetOffset, numberToCopy);
        }

        Object writeReplace() {
            return ByteString.wrap(this.toByteArray());
        }

        private void readObject(ObjectInputStream in) throws IOException {
            throw new InvalidObjectException("BoundedByteStream instances are not to be serialized directly");
        }
    }

    public static final class Output
    extends OutputStream {
        private static final byte[] EMPTY_BYTE_ARRAY = new byte[0];
        private final int initialCapacity;
        private final ArrayList<ByteString> flushedBuffers;
        private int flushedBuffersTotalBytes;
        private byte[] buffer;
        private int bufferPos;

        Output(int initialCapacity) {
            if (initialCapacity < 0) {
                throw new IllegalArgumentException("Buffer size < 0");
            }
            this.initialCapacity = initialCapacity;
            this.flushedBuffers = new ArrayList();
            this.buffer = new byte[initialCapacity];
        }

        @Override
        public synchronized void write(int b) {
            if (this.bufferPos == this.buffer.length) {
                this.flushFullBuffer(1);
            }
            this.buffer[this.bufferPos++] = (byte)b;
        }

        @Override
        public synchronized void write(byte[] b, int offset, int length) {
            if (length <= this.buffer.length - this.bufferPos) {
                System.arraycopy(b, offset, this.buffer, this.bufferPos, length);
                this.bufferPos += length;
            } else {
                int copySize = this.buffer.length - this.bufferPos;
                System.arraycopy(b, offset, this.buffer, this.bufferPos, copySize);
                this.flushFullBuffer(length -= copySize);
                System.arraycopy(b, offset += copySize, this.buffer, 0, length);
                this.bufferPos = length;
            }
        }

        public synchronized ByteString toByteString() {
            this.flushLastBuffer();
            return ByteString.copyFrom(this.flushedBuffers);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void writeTo(OutputStream out) throws IOException {
            ByteString[] byteStringArray = this;
            synchronized (this) {
                ByteString[] cachedFlushBuffers = this.flushedBuffers.toArray(new ByteString[0]);
                byte[] cachedBuffer = this.buffer;
                int cachedBufferPos = this.bufferPos;
                // ** MonitorExit[var5_2] (shouldn't be in output)
                for (ByteString byteString : cachedFlushBuffers) {
                    byteString.writeTo(out);
                }
                out.write(Arrays.copyOf(cachedBuffer, cachedBufferPos));
                return;
            }
        }

        public synchronized int size() {
            return this.flushedBuffersTotalBytes + this.bufferPos;
        }

        public synchronized void reset() {
            this.flushedBuffers.clear();
            this.flushedBuffersTotalBytes = 0;
            this.bufferPos = 0;
        }

        public String toString() {
            return String.format("<ByteString.Output@%s size=%d>", Integer.toHexString(System.identityHashCode(this)), this.size());
        }

        private void flushFullBuffer(int minSize) {
            this.flushedBuffers.add(new LiteralByteString(this.buffer));
            this.flushedBuffersTotalBytes += this.buffer.length;
            int newSize = Math.max(this.initialCapacity, Math.max(minSize, this.flushedBuffersTotalBytes >>> 1));
            this.buffer = new byte[newSize];
            this.bufferPos = 0;
        }

        private void flushLastBuffer() {
            if (this.bufferPos < this.buffer.length) {
                if (this.bufferPos > 0) {
                    byte[] bufferCopy = Arrays.copyOf(this.buffer, this.bufferPos);
                    this.flushedBuffers.add(new LiteralByteString(bufferCopy));
                }
            } else {
                this.flushedBuffers.add(new LiteralByteString(this.buffer));
                this.buffer = EMPTY_BYTE_ARRAY;
            }
            this.flushedBuffersTotalBytes += this.bufferPos;
            this.bufferPos = 0;
        }
    }

    static final class CodedBuilder {
        private final CodedOutputStream output;
        private final byte[] buffer;

        private CodedBuilder(int size) {
            this.buffer = new byte[size];
            this.output = CodedOutputStream.newInstance(this.buffer);
        }

        public ByteString build() {
            this.output.checkNoSpaceLeft();
            return new LiteralByteString(this.buffer);
        }

        public CodedOutputStream getCodedOutput() {
            return this.output;
        }
    }

    public static interface ByteIterator
    extends Iterator<Byte> {
        public byte nextByte();
    }

    private static final class SystemByteArrayCopier
    implements ByteArrayCopier {
        private SystemByteArrayCopier() {
        }

        @Override
        public byte[] copyFrom(byte[] bytes, int offset, int size) {
            byte[] copy = new byte[size];
            System.arraycopy(bytes, offset, copy, 0, size);
            return copy;
        }
    }

    private static final class ArraysByteArrayCopier
    implements ByteArrayCopier {
        private ArraysByteArrayCopier() {
        }

        @Override
        public byte[] copyFrom(byte[] bytes, int offset, int size) {
            return Arrays.copyOfRange(bytes, offset, offset + size);
        }
    }

    static abstract class LeafByteString
    extends ByteString {
        private static final long serialVersionUID = 1L;

        @Override
        protected final int getTreeDepth() {
            return 0;
        }

        @Override
        protected final boolean isBalanced() {
            return true;
        }

        @Override
        void writeToReverse(ByteOutput byteOutput) throws IOException {
            this.writeTo(byteOutput);
        }

        abstract boolean equalsRange(ByteString var1, int var2, int var3);

        private LeafByteString() {
        }
    }

    static abstract class AbstractByteIterator
    implements ByteIterator {
        AbstractByteIterator() {
        }

        @Override
        public final Byte next() {
            return this.nextByte();
        }

        @Override
        public final void remove() {
            throw new UnsupportedOperationException();
        }
    }
}

