/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.IterableByteBufferInputStream;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.google.protobuf.UnsafeUtil;
import com.google.protobuf.Utf8;
import com.google.protobuf.WireFormat;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public abstract class CodedInputStream {
    private static final int DEFAULT_BUFFER_SIZE = 4096;
    private static final int DEFAULT_SIZE_LIMIT = Integer.MAX_VALUE;
    private static volatile int defaultRecursionLimit = 100;
    int messageDepth;
    int groupDepth;
    int recursionLimit = defaultRecursionLimit;
    int sizeLimit = Integer.MAX_VALUE;
    Object wrapper;
    private boolean shouldDiscardUnknownFields = false;

    public static CodedInputStream newInstance(InputStream input) {
        return CodedInputStream.newInstance(input, 4096);
    }

    public static CodedInputStream newInstance(InputStream input, int bufferSize) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize must be > 0");
        }
        if (input == null) {
            return CodedInputStream.newInstance(Internal.EMPTY_BYTE_ARRAY);
        }
        return new StreamDecoder(input, bufferSize);
    }

    public static CodedInputStream newInstance(Iterable<ByteBuffer> input) {
        if (!UnsafeDirectNioDecoder.isSupported()) {
            return CodedInputStream.newInstance(new IterableByteBufferInputStream(input));
        }
        return CodedInputStream.newInstance(input, false);
    }

    static CodedInputStream newInstance(Iterable<ByteBuffer> bufs, boolean bufferIsImmutable) {
        int flag = 0;
        int totalSize = 0;
        for (ByteBuffer buf : bufs) {
            totalSize += buf.remaining();
            if (buf.hasArray()) {
                flag |= 1;
                continue;
            }
            if (buf.isDirect()) {
                flag |= 2;
                continue;
            }
            flag |= 4;
        }
        if (flag == 2) {
            return new IterableDirectByteBufferDecoder(bufs, totalSize, bufferIsImmutable);
        }
        return CodedInputStream.newInstance(new IterableByteBufferInputStream(bufs));
    }

    public static CodedInputStream newInstance(byte[] buf) {
        return CodedInputStream.newInstance(buf, 0, buf.length);
    }

    public static CodedInputStream newInstance(byte[] buf, int off, int len) {
        return CodedInputStream.newInstance(buf, off, len, false);
    }

    static CodedInputStream newInstance(byte[] buf, int off, int len, boolean bufferIsImmutable) {
        ArrayDecoder result = new ArrayDecoder(buf, off, len, bufferIsImmutable);
        try {
            result.pushLimit(len);
        }
        catch (InvalidProtocolBufferException ex) {
            throw new IllegalArgumentException(ex);
        }
        return result;
    }

    public static CodedInputStream newInstance(ByteBuffer buf) {
        return CodedInputStream.newInstance(buf, false);
    }

    static CodedInputStream newInstance(ByteBuffer buf, boolean bufferIsImmutable) {
        if (buf.hasArray()) {
            return CodedInputStream.newInstance(buf.array(), buf.arrayOffset() + buf.position(), buf.remaining(), bufferIsImmutable);
        }
        if (buf.isDirect() && UnsafeDirectNioDecoder.isSupported()) {
            return new UnsafeDirectNioDecoder(buf, bufferIsImmutable);
        }
        byte[] buffer = new byte[buf.remaining()];
        buf.duplicate().get(buffer);
        return CodedInputStream.newInstance(buffer, 0, buffer.length, true);
    }

    public void checkRecursionLimit() throws InvalidProtocolBufferException {
        if (this.messageDepth + this.groupDepth >= this.recursionLimit) {
            throw InvalidProtocolBufferException.recursionLimitExceeded();
        }
    }

    public void checkValidEndTag() throws InvalidProtocolBufferException {
        if (this.groupDepth == 0) {
            this.checkLastTagWas(0);
        }
    }

    private CodedInputStream() {
    }

    public abstract int readTag() throws IOException;

    public abstract void checkLastTagWas(int var1) throws InvalidProtocolBufferException;

    public abstract int getLastTag();

    public abstract boolean skipField(int var1) throws IOException;

    @Deprecated
    public abstract boolean skipField(int var1, CodedOutputStream var2) throws IOException;

    public void skipMessage() throws IOException {
        boolean fieldSkipped;
        do {
            int tag;
            if ((tag = this.readTag()) == 0) {
                return;
            }
            this.checkRecursionLimit();
            ++this.groupDepth;
            fieldSkipped = this.skipField(tag);
            --this.groupDepth;
        } while (fieldSkipped);
    }

    public void skipMessage(CodedOutputStream output) throws IOException {
        boolean fieldSkipped;
        do {
            int tag;
            if ((tag = this.readTag()) == 0) {
                return;
            }
            this.checkRecursionLimit();
            ++this.groupDepth;
            fieldSkipped = this.skipField(tag, output);
            --this.groupDepth;
        } while (fieldSkipped);
    }

    public abstract double readDouble() throws IOException;

    public abstract float readFloat() throws IOException;

    public abstract long readUInt64() throws IOException;

    public abstract long readInt64() throws IOException;

    public abstract int readInt32() throws IOException;

    public abstract long readFixed64() throws IOException;

    public abstract int readFixed32() throws IOException;

    public abstract boolean readBool() throws IOException;

    public abstract String readString() throws IOException;

    Object readString(WireFormat.Utf8Validation utf8Validation) throws IOException {
        switch (utf8Validation) {
            case LOOSE: {
                return this.readString();
            }
            case STRICT: {
                return this.readStringRequireUtf8();
            }
            case LAZY: {
                return this.readBytes();
            }
        }
        throw new IllegalStateException("Unknown UTF8 validation: " + (Object)((Object)utf8Validation));
    }

    Object readPrimitiveField(WireFormat.FieldType type, WireFormat.Utf8Validation utf8Validation) throws IOException {
        switch (type) {
            case DOUBLE: {
                return this.readDouble();
            }
            case FLOAT: {
                return Float.valueOf(this.readFloat());
            }
            case INT64: {
                return this.readInt64();
            }
            case UINT64: {
                return this.readUInt64();
            }
            case INT32: {
                return this.readInt32();
            }
            case FIXED64: {
                return this.readFixed64();
            }
            case FIXED32: {
                return this.readFixed32();
            }
            case BOOL: {
                return this.readBool();
            }
            case BYTES: {
                return this.readBytes();
            }
            case UINT32: {
                return this.readUInt32();
            }
            case SFIXED32: {
                return this.readSFixed32();
            }
            case SFIXED64: {
                return this.readSFixed64();
            }
            case SINT32: {
                return this.readSInt32();
            }
            case SINT64: {
                return this.readSInt64();
            }
            case STRING: {
                return this.readString(utf8Validation);
            }
            case GROUP: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle nested groups.");
            }
            case MESSAGE: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle embedded messages.");
            }
            case ENUM: {
                throw new IllegalArgumentException("readPrimitiveField() cannot handle enums.");
            }
        }
        throw new IllegalStateException("Unknown field type: " + (Object)((Object)type));
    }

    public abstract String readStringRequireUtf8() throws IOException;

    public abstract void readGroup(int var1, MessageLite.Builder var2, ExtensionRegistryLite var3) throws IOException;

    public abstract <T extends MessageLite> T readGroup(int var1, Parser<T> var2, ExtensionRegistryLite var3) throws IOException;

    @Deprecated
    public abstract void readUnknownGroup(int var1, MessageLite.Builder var2) throws IOException;

    public abstract void readMessage(MessageLite.Builder var1, ExtensionRegistryLite var2) throws IOException;

    public abstract <T extends MessageLite> T readMessage(Parser<T> var1, ExtensionRegistryLite var2) throws IOException;

    public abstract ByteString readBytes() throws IOException;

    public abstract byte[] readByteArray() throws IOException;

    public abstract ByteBuffer readByteBuffer() throws IOException;

    public abstract int readUInt32() throws IOException;

    public abstract int readEnum() throws IOException;

    public abstract int readSFixed32() throws IOException;

    public abstract long readSFixed64() throws IOException;

    public abstract int readSInt32() throws IOException;

    public abstract long readSInt64() throws IOException;

    public abstract int readRawVarint32() throws IOException;

    public abstract long readRawVarint64() throws IOException;

    abstract long readRawVarint64SlowPath() throws IOException;

    public abstract int readRawLittleEndian32() throws IOException;

    public abstract long readRawLittleEndian64() throws IOException;

    public abstract void enableAliasing(boolean var1);

    public final int setRecursionLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Recursion limit cannot be negative: " + limit);
        }
        int oldLimit = this.recursionLimit;
        this.recursionLimit = limit;
        return oldLimit;
    }

    public final int setSizeLimit(int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Size limit cannot be negative: " + limit);
        }
        int oldLimit = this.sizeLimit;
        this.sizeLimit = limit;
        return oldLimit;
    }

    final void discardUnknownFields() {
        this.shouldDiscardUnknownFields = true;
    }

    final void unsetDiscardUnknownFields() {
        this.shouldDiscardUnknownFields = false;
    }

    final boolean shouldDiscardUnknownFields() {
        return this.shouldDiscardUnknownFields;
    }

    public abstract void resetSizeCounter();

    public abstract int pushLimit(int var1) throws InvalidProtocolBufferException;

    public abstract void popLimit(int var1);

    public abstract int getBytesUntilLimit();

    public abstract boolean isAtEnd() throws IOException;

    public abstract int getTotalBytesRead();

    public abstract byte readRawByte() throws IOException;

    public abstract byte[] readRawBytes(int var1) throws IOException;

    public abstract void skipRawBytes(int var1) throws IOException;

    public static int decodeZigZag32(int n) {
        return n >>> 1 ^ -(n & 1);
    }

    public static long decodeZigZag64(long n) {
        return n >>> 1 ^ -(n & 1L);
    }

    public static int readRawVarint32(int firstByte, InputStream input) throws IOException {
        int b;
        int offset;
        if ((firstByte & 0x80) == 0) {
            return firstByte;
        }
        int result = firstByte & 0x7F;
        for (offset = 7; offset < 32; offset += 7) {
            b = input.read();
            if (b == -1) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            result |= (b & 0x7F) << offset;
            if ((b & 0x80) != 0) continue;
            return result;
        }
        while (offset < 64) {
            b = input.read();
            if (b == -1) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            if ((b & 0x80) == 0) {
                return result;
            }
            offset += 7;
        }
        throw InvalidProtocolBufferException.malformedVarint();
    }

    static int readRawVarint32(InputStream input) throws IOException {
        int firstByte = input.read();
        if (firstByte == -1) {
            throw InvalidProtocolBufferException.truncatedMessage();
        }
        return CodedInputStream.readRawVarint32(firstByte, input);
    }

    private static final class StreamDecoder
    extends CodedInputStream {
        private final InputStream input;
        private final byte[] buffer;
        private int bufferSize;
        private int bufferSizeAfterLimit;
        private int pos;
        private int lastTag;
        private int totalBytesRetired;
        private int currentLimit = Integer.MAX_VALUE;
        private RefillCallback refillCallback = null;

        private StreamDecoder(InputStream input, int bufferSize) {
            Internal.checkNotNull(input, "input");
            this.input = input;
            this.buffer = new byte[bufferSize];
            this.bufferSize = 0;
            this.pos = 0;
            this.totalBytesRetired = 0;
        }

        private static int read(InputStream input, byte[] data, int offset, int length) throws IOException {
            try {
                return input.read(data, offset, length);
            }
            catch (InvalidProtocolBufferException e) {
                e.setThrownFromInputStream();
                throw e;
            }
        }

        private static long skip(InputStream input, long length) throws IOException {
            try {
                return input.skip(length);
            }
            catch (InvalidProtocolBufferException e) {
                e.setThrownFromInputStream();
                throw e;
            }
        }

        private static int available(InputStream input) throws IOException {
            try {
                return input.available();
            }
            catch (InvalidProtocolBufferException e) {
                e.setThrownFromInputStream();
                throw e;
            }
        }

        @Override
        public int readTag() throws IOException {
            if (this.isAtEnd()) {
                this.lastTag = 0;
                return 0;
            }
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }

        @Override
        public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
            if (this.lastTag != value) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        }

        @Override
        public int getLastTag() {
            return this.lastTag;
        }

        @Override
        public boolean skipField(int tag) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    this.skipRawVarint();
                    return true;
                }
                case 1: {
                    this.skipRawBytes(8);
                    return true;
                }
                case 2: {
                    this.skipRawBytes(this.readRawVarint32());
                    return true;
                }
                case 3: {
                    this.skipMessage();
                    this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                    return true;
                }
                case 4: {
                    this.checkValidEndTag();
                    return false;
                }
                case 5: {
                    this.skipRawBytes(4);
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public boolean skipField(int tag, CodedOutputStream output) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    long value = this.readInt64();
                    output.writeUInt32NoTag(tag);
                    output.writeUInt64NoTag(value);
                    return true;
                }
                case 1: {
                    long value = this.readRawLittleEndian64();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed64NoTag(value);
                    return true;
                }
                case 2: {
                    ByteString value = this.readBytes();
                    output.writeUInt32NoTag(tag);
                    output.writeBytesNoTag(value);
                    return true;
                }
                case 3: {
                    output.writeUInt32NoTag(tag);
                    this.skipMessage(output);
                    int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
                    this.checkLastTagWas(endtag);
                    output.writeUInt32NoTag(endtag);
                    return true;
                }
                case 4: {
                    this.checkValidEndTag();
                    return false;
                }
                case 5: {
                    int value = this.readRawLittleEndian32();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed32NoTag(value);
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(this.readRawLittleEndian64());
        }

        @Override
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(this.readRawLittleEndian32());
        }

        @Override
        public long readUInt64() throws IOException {
            return this.readRawVarint64();
        }

        @Override
        public long readInt64() throws IOException {
            return this.readRawVarint64();
        }

        @Override
        public int readInt32() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public long readFixed64() throws IOException {
            return this.readRawLittleEndian64();
        }

        @Override
        public int readFixed32() throws IOException {
            return this.readRawLittleEndian32();
        }

        @Override
        public boolean readBool() throws IOException {
            return this.readRawVarint64() != 0L;
        }

        @Override
        public String readString() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.bufferSize - this.pos) {
                String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
                this.pos += size;
                return result;
            }
            if (size == 0) {
                return "";
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if (size <= this.bufferSize) {
                this.refillBuffer(size);
                String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
                this.pos += size;
                return result;
            }
            return new String(this.readRawBytesSlowPath(size, false), Internal.UTF_8);
        }

        @Override
        public String readStringRequireUtf8() throws IOException {
            int tempPos;
            byte[] bytes;
            int oldPos;
            int size = this.readRawVarint32();
            if (size <= this.bufferSize - (oldPos = this.pos) && size > 0) {
                bytes = this.buffer;
                this.pos = oldPos + size;
                tempPos = oldPos;
            } else {
                if (size == 0) {
                    return "";
                }
                if (size < 0) {
                    throw InvalidProtocolBufferException.negativeSize();
                }
                if (size <= this.bufferSize) {
                    this.refillBuffer(size);
                    bytes = this.buffer;
                    tempPos = 0;
                    this.pos = tempPos + size;
                } else {
                    bytes = this.readRawBytesSlowPath(size, false);
                    tempPos = 0;
                }
            }
            return Utf8.decodeUtf8(bytes, tempPos, size);
        }

        @Override
        public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.checkRecursionLimit();
            ++this.groupDepth;
            builder.mergeFrom(this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.groupDepth;
        }

        @Override
        public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.checkRecursionLimit();
            ++this.groupDepth;
            MessageLite result = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.groupDepth;
            return (T)result;
        }

        @Override
        @Deprecated
        public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
            this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override
        public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = this.readRawVarint32();
            this.checkRecursionLimit();
            int oldLimit = this.pushLimit(length);
            ++this.messageDepth;
            builder.mergeFrom(this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.messageDepth;
            if (this.getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.popLimit(oldLimit);
        }

        @Override
        public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = this.readRawVarint32();
            this.checkRecursionLimit();
            int oldLimit = this.pushLimit(length);
            ++this.messageDepth;
            MessageLite result = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.messageDepth;
            if (this.getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.popLimit(oldLimit);
            return (T)result;
        }

        @Override
        public ByteString readBytes() throws IOException {
            int size = this.readRawVarint32();
            if (size <= this.bufferSize - this.pos && size > 0) {
                ByteString result = ByteString.copyFrom(this.buffer, this.pos, size);
                this.pos += size;
                return result;
            }
            if (size == 0) {
                return ByteString.EMPTY;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            return this.readBytesSlowPath(size);
        }

        @Override
        public byte[] readByteArray() throws IOException {
            int size = this.readRawVarint32();
            if (size <= this.bufferSize - this.pos && size > 0) {
                byte[] result = Arrays.copyOfRange(this.buffer, this.pos, this.pos + size);
                this.pos += size;
                return result;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            return this.readRawBytesSlowPath(size, false);
        }

        @Override
        public ByteBuffer readByteBuffer() throws IOException {
            int size = this.readRawVarint32();
            if (size <= this.bufferSize - this.pos && size > 0) {
                ByteBuffer result = ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
                this.pos += size;
                return result;
            }
            if (size == 0) {
                return Internal.EMPTY_BYTE_BUFFER;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            return ByteBuffer.wrap(this.readRawBytesSlowPath(size, true));
        }

        @Override
        public int readUInt32() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public int readEnum() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public int readSFixed32() throws IOException {
            return this.readRawLittleEndian32();
        }

        @Override
        public long readSFixed64() throws IOException {
            return this.readRawLittleEndian64();
        }

        @Override
        public int readSInt32() throws IOException {
            return StreamDecoder.decodeZigZag32(this.readRawVarint32());
        }

        @Override
        public long readSInt64() throws IOException {
            return StreamDecoder.decodeZigZag64(this.readRawVarint64());
        }

        @Override
        public int readRawVarint32() throws IOException {
            block4: {
                int x;
                int tempPos;
                block6: {
                    byte[] buffer;
                    block8: {
                        block7: {
                            block5: {
                                tempPos = this.pos;
                                if (this.bufferSize == tempPos) break block4;
                                buffer = this.buffer;
                                if ((x = buffer[tempPos++]) >= 0) {
                                    this.pos = tempPos;
                                    return x;
                                }
                                if (this.bufferSize - tempPos < 9) break block4;
                                if ((x ^= buffer[tempPos++] << 7) >= 0) break block5;
                                x ^= 0xFFFFFF80;
                                break block6;
                            }
                            if ((x ^= buffer[tempPos++] << 14) < 0) break block7;
                            x ^= 0x3F80;
                            break block6;
                        }
                        if ((x ^= buffer[tempPos++] << 21) >= 0) break block8;
                        x ^= 0xFFE03F80;
                        break block6;
                    }
                    byte y = buffer[tempPos++];
                    x ^= y << 28;
                    x ^= 0xFE03F80;
                    if (y < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0) break block4;
                }
                this.pos = tempPos;
                return x;
            }
            return (int)this.readRawVarint64SlowPath();
        }

        private void skipRawVarint() throws IOException {
            if (this.bufferSize - this.pos >= 10) {
                this.skipRawVarintFastPath();
            } else {
                this.skipRawVarintSlowPath();
            }
        }

        private void skipRawVarintFastPath() throws IOException {
            for (int i = 0; i < 10; ++i) {
                if (this.buffer[this.pos++] < 0) continue;
                return;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private void skipRawVarintSlowPath() throws IOException {
            for (int i = 0; i < 10; ++i) {
                if (this.readRawByte() < 0) continue;
                return;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override
        public long readRawVarint64() throws IOException {
            block4: {
                long x;
                int tempPos;
                block6: {
                    byte[] buffer;
                    block12: {
                        block11: {
                            block10: {
                                block9: {
                                    int y;
                                    block8: {
                                        block7: {
                                            block5: {
                                                tempPos = this.pos;
                                                if (this.bufferSize == tempPos) break block4;
                                                buffer = this.buffer;
                                                if ((y = buffer[tempPos++]) >= 0) {
                                                    this.pos = tempPos;
                                                    return y;
                                                }
                                                if (this.bufferSize - tempPos < 9) break block4;
                                                if ((y ^= buffer[tempPos++] << 7) >= 0) break block5;
                                                x = y ^ 0xFFFFFF80;
                                                break block6;
                                            }
                                            if ((y ^= buffer[tempPos++] << 14) < 0) break block7;
                                            x = y ^ 0x3F80;
                                            break block6;
                                        }
                                        if ((y ^= buffer[tempPos++] << 21) >= 0) break block8;
                                        x = y ^ 0xFFE03F80;
                                        break block6;
                                    }
                                    if ((x = (long)y ^ (long)buffer[tempPos++] << 28) < 0L) break block9;
                                    x ^= 0xFE03F80L;
                                    break block6;
                                }
                                if ((x ^= (long)buffer[tempPos++] << 35) >= 0L) break block10;
                                x ^= 0xFFFFFFF80FE03F80L;
                                break block6;
                            }
                            if ((x ^= (long)buffer[tempPos++] << 42) < 0L) break block11;
                            x ^= 0x3F80FE03F80L;
                            break block6;
                        }
                        if ((x ^= (long)buffer[tempPos++] << 49) >= 0L) break block12;
                        x ^= 0xFFFE03F80FE03F80L;
                        break block6;
                    }
                    x ^= (long)buffer[tempPos++] << 56;
                    if ((x ^= 0xFE03F80FE03F80L) < 0L && (long)buffer[tempPos++] < 0L) break block4;
                }
                this.pos = tempPos;
                return x;
            }
            return this.readRawVarint64SlowPath();
        }

        @Override
        long readRawVarint64SlowPath() throws IOException {
            long result = 0L;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = this.readRawByte();
                result |= (long)(b & 0x7F) << shift;
                if ((b & 0x80) != 0) continue;
                return result;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override
        public int readRawLittleEndian32() throws IOException {
            int tempPos = this.pos;
            if (this.bufferSize - tempPos < 4) {
                this.refillBuffer(4);
                tempPos = this.pos;
            }
            byte[] buffer = this.buffer;
            this.pos = tempPos + 4;
            return buffer[tempPos] & 0xFF | (buffer[tempPos + 1] & 0xFF) << 8 | (buffer[tempPos + 2] & 0xFF) << 16 | (buffer[tempPos + 3] & 0xFF) << 24;
        }

        @Override
        public long readRawLittleEndian64() throws IOException {
            int tempPos = this.pos;
            if (this.bufferSize - tempPos < 8) {
                this.refillBuffer(8);
                tempPos = this.pos;
            }
            byte[] buffer = this.buffer;
            this.pos = tempPos + 8;
            return (long)buffer[tempPos] & 0xFFL | ((long)buffer[tempPos + 1] & 0xFFL) << 8 | ((long)buffer[tempPos + 2] & 0xFFL) << 16 | ((long)buffer[tempPos + 3] & 0xFFL) << 24 | ((long)buffer[tempPos + 4] & 0xFFL) << 32 | ((long)buffer[tempPos + 5] & 0xFFL) << 40 | ((long)buffer[tempPos + 6] & 0xFFL) << 48 | ((long)buffer[tempPos + 7] & 0xFFL) << 56;
        }

        @Override
        public void enableAliasing(boolean enabled) {
        }

        @Override
        public void resetSizeCounter() {
            this.totalBytesRetired = -this.pos;
        }

        @Override
        public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
            if (byteLimit < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if ((byteLimit += this.totalBytesRetired + this.pos) < 0) {
                throw InvalidProtocolBufferException.sizeLimitExceeded();
            }
            int oldLimit = this.currentLimit;
            if (byteLimit > oldLimit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.currentLimit = byteLimit;
            this.recomputeBufferSizeAfterLimit();
            return oldLimit;
        }

        private void recomputeBufferSizeAfterLimit() {
            this.bufferSize += this.bufferSizeAfterLimit;
            int bufferEnd = this.totalBytesRetired + this.bufferSize;
            if (bufferEnd > this.currentLimit) {
                this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
                this.bufferSize -= this.bufferSizeAfterLimit;
            } else {
                this.bufferSizeAfterLimit = 0;
            }
        }

        @Override
        public void popLimit(int oldLimit) {
            this.currentLimit = oldLimit;
            this.recomputeBufferSizeAfterLimit();
        }

        @Override
        public int getBytesUntilLimit() {
            if (this.currentLimit == Integer.MAX_VALUE) {
                return -1;
            }
            int currentAbsolutePosition = this.totalBytesRetired + this.pos;
            return this.currentLimit - currentAbsolutePosition;
        }

        @Override
        public boolean isAtEnd() throws IOException {
            return this.pos == this.bufferSize && !this.tryRefillBuffer(1);
        }

        @Override
        public int getTotalBytesRead() {
            return this.totalBytesRetired + this.pos;
        }

        private void refillBuffer(int n) throws IOException {
            if (!this.tryRefillBuffer(n)) {
                if (n > this.sizeLimit - this.totalBytesRetired - this.pos) {
                    throw InvalidProtocolBufferException.sizeLimitExceeded();
                }
                throw InvalidProtocolBufferException.truncatedMessage();
            }
        }

        private boolean tryRefillBuffer(int n) throws IOException {
            int bytesRead;
            int tempPos;
            if (this.pos + n <= this.bufferSize) {
                throw new IllegalStateException("refillBuffer() called when " + n + " bytes were already available in buffer");
            }
            if (n > this.sizeLimit - this.totalBytesRetired - this.pos) {
                return false;
            }
            if (this.totalBytesRetired + this.pos + n > this.currentLimit) {
                return false;
            }
            if (this.refillCallback != null) {
                this.refillCallback.onRefill();
            }
            if ((tempPos = this.pos) > 0) {
                if (this.bufferSize > tempPos) {
                    System.arraycopy(this.buffer, tempPos, this.buffer, 0, this.bufferSize - tempPos);
                }
                this.totalBytesRetired += tempPos;
                this.bufferSize -= tempPos;
                this.pos = 0;
            }
            if ((bytesRead = StreamDecoder.read(this.input, this.buffer, this.bufferSize, Math.min(this.buffer.length - this.bufferSize, this.sizeLimit - this.totalBytesRetired - this.bufferSize))) == 0 || bytesRead < -1 || bytesRead > this.buffer.length) {
                throw new IllegalStateException(this.input.getClass() + "#read(byte[]) returned invalid result: " + bytesRead + "\nThe InputStream implementation is buggy.");
            }
            if (bytesRead > 0) {
                this.bufferSize += bytesRead;
                this.recomputeBufferSizeAfterLimit();
                return this.bufferSize >= n || this.tryRefillBuffer(n);
            }
            return false;
        }

        @Override
        public byte readRawByte() throws IOException {
            if (this.pos == this.bufferSize) {
                this.refillBuffer(1);
            }
            return this.buffer[this.pos++];
        }

        @Override
        public byte[] readRawBytes(int size) throws IOException {
            int tempPos = this.pos;
            if (size <= this.bufferSize - tempPos && size > 0) {
                this.pos = tempPos + size;
                return Arrays.copyOfRange(this.buffer, tempPos, tempPos + size);
            }
            return this.readRawBytesSlowPath(size, false);
        }

        private byte[] readRawBytesSlowPath(int size, boolean ensureNoLeakedReferences) throws IOException {
            byte[] result = this.readRawBytesSlowPathOneChunk(size);
            if (result != null) {
                return ensureNoLeakedReferences ? (byte[])result.clone() : result;
            }
            int originalBufferPos = this.pos;
            int bufferedBytes = this.bufferSize - this.pos;
            this.totalBytesRetired += this.bufferSize;
            this.pos = 0;
            this.bufferSize = 0;
            int sizeLeft = size - bufferedBytes;
            List<byte[]> chunks = this.readRawBytesSlowPathRemainingChunks(sizeLeft);
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
            int tempPos = bufferedBytes;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
                tempPos += chunk.length;
            }
            return bytes;
        }

        private byte[] readRawBytesSlowPathOneChunk(int size) throws IOException {
            if (size == 0) {
                return Internal.EMPTY_BYTE_ARRAY;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            int currentMessageSize = this.totalBytesRetired + this.pos + size;
            if (currentMessageSize - this.sizeLimit > 0) {
                throw InvalidProtocolBufferException.sizeLimitExceeded();
            }
            if (currentMessageSize > this.currentLimit) {
                this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            int bufferedBytes = this.bufferSize - this.pos;
            int sizeLeft = size - bufferedBytes;
            if (sizeLeft < 4096 || sizeLeft <= StreamDecoder.available(this.input)) {
                int n;
                byte[] bytes = new byte[size];
                System.arraycopy(this.buffer, this.pos, bytes, 0, bufferedBytes);
                this.totalBytesRetired += this.bufferSize;
                this.pos = 0;
                this.bufferSize = 0;
                for (int tempPos = bufferedBytes; tempPos < bytes.length; tempPos += n) {
                    n = StreamDecoder.read(this.input, bytes, tempPos, size - tempPos);
                    if (n == -1) {
                        throw InvalidProtocolBufferException.truncatedMessage();
                    }
                    this.totalBytesRetired += n;
                }
                return bytes;
            }
            return null;
        }

        private List<byte[]> readRawBytesSlowPathRemainingChunks(int sizeLeft) throws IOException {
            ArrayList<byte[]> chunks = new ArrayList<byte[]>();
            while (sizeLeft > 0) {
                int n;
                byte[] chunk = new byte[Math.min(sizeLeft, 4096)];
                for (int tempPos = 0; tempPos < chunk.length; tempPos += n) {
                    n = this.input.read(chunk, tempPos, chunk.length - tempPos);
                    if (n == -1) {
                        throw InvalidProtocolBufferException.truncatedMessage();
                    }
                    this.totalBytesRetired += n;
                }
                sizeLeft -= chunk.length;
                chunks.add(chunk);
            }
            return chunks;
        }

        private ByteString readBytesSlowPath(int size) throws IOException {
            byte[] result = this.readRawBytesSlowPathOneChunk(size);
            if (result != null) {
                return ByteString.copyFrom(result);
            }
            int originalBufferPos = this.pos;
            int bufferedBytes = this.bufferSize - this.pos;
            this.totalBytesRetired += this.bufferSize;
            this.pos = 0;
            this.bufferSize = 0;
            int sizeLeft = size - bufferedBytes;
            List<byte[]> chunks = this.readRawBytesSlowPathRemainingChunks(sizeLeft);
            byte[] bytes = new byte[size];
            System.arraycopy(this.buffer, originalBufferPos, bytes, 0, bufferedBytes);
            int tempPos = bufferedBytes;
            for (byte[] chunk : chunks) {
                System.arraycopy(chunk, 0, bytes, tempPos, chunk.length);
                tempPos += chunk.length;
            }
            return ByteString.wrap(bytes);
        }

        @Override
        public void skipRawBytes(int size) throws IOException {
            if (size <= this.bufferSize - this.pos && size >= 0) {
                this.pos += size;
            } else {
                this.skipRawBytesSlowPath(size);
            }
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        private void skipRawBytesSlowPath(int size) throws IOException {
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if (this.totalBytesRetired + this.pos + size > this.currentLimit) {
                this.skipRawBytes(this.currentLimit - this.totalBytesRetired - this.pos);
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            int totalSkipped = 0;
            if (this.refillCallback == null) {
                this.totalBytesRetired += this.pos;
                this.bufferSize = 0;
                this.pos = 0;
                try {
                    long skipped;
                    for (totalSkipped = this.bufferSize - this.pos; totalSkipped < size; totalSkipped += (int)skipped) {
                        int toSkip = size - totalSkipped;
                        skipped = StreamDecoder.skip(this.input, toSkip);
                        if (skipped < 0L || skipped > (long)toSkip) {
                            throw new IllegalStateException(this.input.getClass() + "#skip returned invalid result: " + skipped + "\nThe InputStream implementation is buggy.");
                        }
                        if (skipped != 0L) continue;
                        break;
                    }
                }
                finally {
                    this.totalBytesRetired += totalSkipped;
                    this.recomputeBufferSizeAfterLimit();
                }
            }
            if (totalSkipped < size) {
                int tempPos = this.bufferSize - this.pos;
                this.pos = this.bufferSize;
                this.refillBuffer(1);
                while (size - tempPos > this.bufferSize) {
                    tempPos += this.bufferSize;
                    this.pos = this.bufferSize;
                    this.refillBuffer(1);
                }
                this.pos = size - tempPos;
            }
        }

        private static interface RefillCallback {
            public void onRefill();
        }

        private class SkippedDataSink
        implements RefillCallback {
            private int lastPos;
            private ByteArrayOutputStream byteArrayStream;

            private SkippedDataSink() {
                this.lastPos = StreamDecoder.this.pos;
            }

            @Override
            public void onRefill() {
                if (this.byteArrayStream == null) {
                    this.byteArrayStream = new ByteArrayOutputStream();
                }
                this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
                this.lastPos = 0;
            }

            ByteBuffer getSkippedData() {
                if (this.byteArrayStream == null) {
                    return ByteBuffer.wrap(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos - this.lastPos);
                }
                this.byteArrayStream.write(StreamDecoder.this.buffer, this.lastPos, StreamDecoder.this.pos);
                return ByteBuffer.wrap(this.byteArrayStream.toByteArray());
            }
        }
    }

    private static final class UnsafeDirectNioDecoder
    extends CodedInputStream {
        private final ByteBuffer buffer;
        private final boolean immutable;
        private final long address;
        private long limit;
        private long pos;
        private long startPos;
        private int bufferSizeAfterLimit;
        private int lastTag;
        private boolean enableAliasing;
        private int currentLimit = Integer.MAX_VALUE;

        static boolean isSupported() {
            return UnsafeUtil.hasUnsafeByteBufferOperations();
        }

        private UnsafeDirectNioDecoder(ByteBuffer buffer, boolean immutable) {
            this.buffer = buffer.duplicate();
            this.address = UnsafeUtil.addressOffset(buffer);
            this.limit = this.address + (long)buffer.limit();
            this.startPos = this.pos = this.address + (long)buffer.position();
            this.immutable = immutable;
        }

        @Override
        public int readTag() throws IOException {
            if (this.isAtEnd()) {
                this.lastTag = 0;
                return 0;
            }
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }

        @Override
        public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
            if (this.lastTag != value) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        }

        @Override
        public int getLastTag() {
            return this.lastTag;
        }

        @Override
        public boolean skipField(int tag) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    this.skipRawVarint();
                    return true;
                }
                case 1: {
                    this.skipRawBytes(8);
                    return true;
                }
                case 2: {
                    this.skipRawBytes(this.readRawVarint32());
                    return true;
                }
                case 3: {
                    this.skipMessage();
                    this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                    return true;
                }
                case 4: {
                    this.checkValidEndTag();
                    return false;
                }
                case 5: {
                    this.skipRawBytes(4);
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public boolean skipField(int tag, CodedOutputStream output) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    long value = this.readInt64();
                    output.writeUInt32NoTag(tag);
                    output.writeUInt64NoTag(value);
                    return true;
                }
                case 1: {
                    long value = this.readRawLittleEndian64();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed64NoTag(value);
                    return true;
                }
                case 2: {
                    ByteString value = this.readBytes();
                    output.writeUInt32NoTag(tag);
                    output.writeBytesNoTag(value);
                    return true;
                }
                case 3: {
                    output.writeUInt32NoTag(tag);
                    this.skipMessage(output);
                    int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
                    this.checkLastTagWas(endtag);
                    output.writeUInt32NoTag(endtag);
                    return true;
                }
                case 4: {
                    this.checkValidEndTag();
                    return false;
                }
                case 5: {
                    int value = this.readRawLittleEndian32();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed32NoTag(value);
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(this.readRawLittleEndian64());
        }

        @Override
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(this.readRawLittleEndian32());
        }

        @Override
        public long readUInt64() throws IOException {
            return this.readRawVarint64();
        }

        @Override
        public long readInt64() throws IOException {
            return this.readRawVarint64();
        }

        @Override
        public int readInt32() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public long readFixed64() throws IOException {
            return this.readRawLittleEndian64();
        }

        @Override
        public int readFixed32() throws IOException {
            return this.readRawLittleEndian32();
        }

        @Override
        public boolean readBool() throws IOException {
            return this.readRawVarint64() != 0L;
        }

        @Override
        public String readString() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.remaining()) {
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.pos, bytes, 0L, (long)size);
                String result = new String(bytes, Internal.UTF_8);
                this.pos += (long)size;
                return result;
            }
            if (size == 0) {
                return "";
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public String readStringRequireUtf8() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.remaining()) {
                int bufferPos = this.bufferPos(this.pos);
                String result = Utf8.decodeUtf8(this.buffer, bufferPos, size);
                this.pos += (long)size;
                return result;
            }
            if (size == 0) {
                return "";
            }
            if (size <= 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.checkRecursionLimit();
            ++this.groupDepth;
            builder.mergeFrom(this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.groupDepth;
        }

        @Override
        public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.checkRecursionLimit();
            ++this.groupDepth;
            MessageLite result = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.groupDepth;
            return (T)result;
        }

        @Override
        @Deprecated
        public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
            this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override
        public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = this.readRawVarint32();
            this.checkRecursionLimit();
            int oldLimit = this.pushLimit(length);
            ++this.messageDepth;
            builder.mergeFrom(this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.messageDepth;
            if (this.getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.popLimit(oldLimit);
        }

        @Override
        public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = this.readRawVarint32();
            this.checkRecursionLimit();
            int oldLimit = this.pushLimit(length);
            ++this.messageDepth;
            MessageLite result = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.messageDepth;
            if (this.getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.popLimit(oldLimit);
            return (T)result;
        }

        @Override
        public ByteString readBytes() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.remaining()) {
                if (this.immutable && this.enableAliasing) {
                    ByteBuffer result = this.slice(this.pos, this.pos + (long)size);
                    this.pos += (long)size;
                    return ByteString.wrap(result);
                }
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.pos, bytes, 0L, (long)size);
                this.pos += (long)size;
                return ByteString.wrap(bytes);
            }
            if (size == 0) {
                return ByteString.EMPTY;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public byte[] readByteArray() throws IOException {
            return this.readRawBytes(this.readRawVarint32());
        }

        @Override
        public ByteBuffer readByteBuffer() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.remaining()) {
                if (!this.immutable && this.enableAliasing) {
                    ByteBuffer result = this.slice(this.pos, this.pos + (long)size);
                    this.pos += (long)size;
                    return result;
                }
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.pos, bytes, 0L, (long)size);
                this.pos += (long)size;
                return ByteBuffer.wrap(bytes);
            }
            if (size == 0) {
                return Internal.EMPTY_BYTE_BUFFER;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public int readUInt32() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public int readEnum() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public int readSFixed32() throws IOException {
            return this.readRawLittleEndian32();
        }

        @Override
        public long readSFixed64() throws IOException {
            return this.readRawLittleEndian64();
        }

        @Override
        public int readSInt32() throws IOException {
            return UnsafeDirectNioDecoder.decodeZigZag32(this.readRawVarint32());
        }

        @Override
        public long readSInt64() throws IOException {
            return UnsafeDirectNioDecoder.decodeZigZag64(this.readRawVarint64());
        }

        @Override
        public int readRawVarint32() throws IOException {
            block4: {
                int x;
                long tempPos;
                block6: {
                    block8: {
                        block7: {
                            block5: {
                                tempPos = this.pos;
                                if (this.limit == tempPos) break block4;
                                if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
                                    this.pos = tempPos;
                                    return x;
                                }
                                if (this.limit - tempPos < 9L) break block4;
                                if ((x ^= UnsafeUtil.getByte(tempPos++) << 7) >= 0) break block5;
                                x ^= 0xFFFFFF80;
                                break block6;
                            }
                            if ((x ^= UnsafeUtil.getByte(tempPos++) << 14) < 0) break block7;
                            x ^= 0x3F80;
                            break block6;
                        }
                        if ((x ^= UnsafeUtil.getByte(tempPos++) << 21) >= 0) break block8;
                        x ^= 0xFFE03F80;
                        break block6;
                    }
                    byte y = UnsafeUtil.getByte(tempPos++);
                    x ^= y << 28;
                    x ^= 0xFE03F80;
                    if (y < 0 && UnsafeUtil.getByte(tempPos++) < 0 && UnsafeUtil.getByte(tempPos++) < 0 && UnsafeUtil.getByte(tempPos++) < 0 && UnsafeUtil.getByte(tempPos++) < 0 && UnsafeUtil.getByte(tempPos++) < 0) break block4;
                }
                this.pos = tempPos;
                return x;
            }
            return (int)this.readRawVarint64SlowPath();
        }

        private void skipRawVarint() throws IOException {
            if (this.remaining() >= 10) {
                this.skipRawVarintFastPath();
            } else {
                this.skipRawVarintSlowPath();
            }
        }

        private void skipRawVarintFastPath() throws IOException {
            for (int i = 0; i < 10; ++i) {
                if (UnsafeUtil.getByte(this.pos++) < 0) continue;
                return;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private void skipRawVarintSlowPath() throws IOException {
            for (int i = 0; i < 10; ++i) {
                if (this.readRawByte() < 0) continue;
                return;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override
        public long readRawVarint64() throws IOException {
            block4: {
                long x;
                long tempPos;
                block6: {
                    block12: {
                        block11: {
                            block10: {
                                block9: {
                                    int y;
                                    block8: {
                                        block7: {
                                            block5: {
                                                tempPos = this.pos;
                                                if (this.limit == tempPos) break block4;
                                                if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
                                                    this.pos = tempPos;
                                                    return y;
                                                }
                                                if (this.limit - tempPos < 9L) break block4;
                                                if ((y ^= UnsafeUtil.getByte(tempPos++) << 7) >= 0) break block5;
                                                x = y ^ 0xFFFFFF80;
                                                break block6;
                                            }
                                            if ((y ^= UnsafeUtil.getByte(tempPos++) << 14) < 0) break block7;
                                            x = y ^ 0x3F80;
                                            break block6;
                                        }
                                        if ((y ^= UnsafeUtil.getByte(tempPos++) << 21) >= 0) break block8;
                                        x = y ^ 0xFFE03F80;
                                        break block6;
                                    }
                                    if ((x = (long)y ^ (long)UnsafeUtil.getByte(tempPos++) << 28) < 0L) break block9;
                                    x ^= 0xFE03F80L;
                                    break block6;
                                }
                                if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 35) >= 0L) break block10;
                                x ^= 0xFFFFFFF80FE03F80L;
                                break block6;
                            }
                            if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 42) < 0L) break block11;
                            x ^= 0x3F80FE03F80L;
                            break block6;
                        }
                        if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 49) >= 0L) break block12;
                        x ^= 0xFFFE03F80FE03F80L;
                        break block6;
                    }
                    x ^= (long)UnsafeUtil.getByte(tempPos++) << 56;
                    if ((x ^= 0xFE03F80FE03F80L) < 0L && (long)UnsafeUtil.getByte(tempPos++) < 0L) break block4;
                }
                this.pos = tempPos;
                return x;
            }
            return this.readRawVarint64SlowPath();
        }

        @Override
        long readRawVarint64SlowPath() throws IOException {
            long result = 0L;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = this.readRawByte();
                result |= (long)(b & 0x7F) << shift;
                if ((b & 0x80) != 0) continue;
                return result;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override
        public int readRawLittleEndian32() throws IOException {
            long tempPos = this.pos;
            if (this.limit - tempPos < 4L) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.pos = tempPos + 4L;
            return UnsafeUtil.getByte(tempPos) & 0xFF | (UnsafeUtil.getByte(tempPos + 1L) & 0xFF) << 8 | (UnsafeUtil.getByte(tempPos + 2L) & 0xFF) << 16 | (UnsafeUtil.getByte(tempPos + 3L) & 0xFF) << 24;
        }

        @Override
        public long readRawLittleEndian64() throws IOException {
            long tempPos = this.pos;
            if (this.limit - tempPos < 8L) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.pos = tempPos + 8L;
            return (long)UnsafeUtil.getByte(tempPos) & 0xFFL | ((long)UnsafeUtil.getByte(tempPos + 1L) & 0xFFL) << 8 | ((long)UnsafeUtil.getByte(tempPos + 2L) & 0xFFL) << 16 | ((long)UnsafeUtil.getByte(tempPos + 3L) & 0xFFL) << 24 | ((long)UnsafeUtil.getByte(tempPos + 4L) & 0xFFL) << 32 | ((long)UnsafeUtil.getByte(tempPos + 5L) & 0xFFL) << 40 | ((long)UnsafeUtil.getByte(tempPos + 6L) & 0xFFL) << 48 | ((long)UnsafeUtil.getByte(tempPos + 7L) & 0xFFL) << 56;
        }

        @Override
        public void enableAliasing(boolean enabled) {
            this.enableAliasing = enabled;
        }

        @Override
        public void resetSizeCounter() {
            this.startPos = this.pos;
        }

        @Override
        public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
            int oldLimit;
            if (byteLimit < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if ((byteLimit += this.getTotalBytesRead()) > (oldLimit = this.currentLimit)) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.currentLimit = byteLimit;
            this.recomputeBufferSizeAfterLimit();
            return oldLimit;
        }

        @Override
        public void popLimit(int oldLimit) {
            this.currentLimit = oldLimit;
            this.recomputeBufferSizeAfterLimit();
        }

        @Override
        public int getBytesUntilLimit() {
            if (this.currentLimit == Integer.MAX_VALUE) {
                return -1;
            }
            return this.currentLimit - this.getTotalBytesRead();
        }

        @Override
        public boolean isAtEnd() throws IOException {
            return this.pos == this.limit;
        }

        @Override
        public int getTotalBytesRead() {
            return (int)(this.pos - this.startPos);
        }

        @Override
        public byte readRawByte() throws IOException {
            if (this.pos == this.limit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            return UnsafeUtil.getByte(this.pos++);
        }

        @Override
        public byte[] readRawBytes(int length) throws IOException {
            if (length >= 0 && length <= this.remaining()) {
                byte[] bytes = new byte[length];
                this.slice(this.pos, this.pos + (long)length).get(bytes);
                this.pos += (long)length;
                return bytes;
            }
            if (length <= 0) {
                if (length == 0) {
                    return Internal.EMPTY_BYTE_ARRAY;
                }
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public void skipRawBytes(int length) throws IOException {
            if (length >= 0 && length <= this.remaining()) {
                this.pos += (long)length;
                return;
            }
            if (length < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        private void recomputeBufferSizeAfterLimit() {
            this.limit += (long)this.bufferSizeAfterLimit;
            int bufferEnd = (int)(this.limit - this.startPos);
            if (bufferEnd > this.currentLimit) {
                this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
                this.limit -= (long)this.bufferSizeAfterLimit;
            } else {
                this.bufferSizeAfterLimit = 0;
            }
        }

        private int remaining() {
            return (int)(this.limit - this.pos);
        }

        private int bufferPos(long pos) {
            return (int)(pos - this.address);
        }

        private ByteBuffer slice(long begin, long end) throws IOException {
            int prevPos = this.buffer.position();
            int prevLimit = this.buffer.limit();
            ByteBuffer asBuffer = this.buffer;
            try {
                ((Buffer)asBuffer).position(this.bufferPos(begin));
                ((Buffer)asBuffer).limit(this.bufferPos(end));
                ByteBuffer byteBuffer = this.buffer.slice();
                return byteBuffer;
            }
            catch (IllegalArgumentException e) {
                InvalidProtocolBufferException ex = InvalidProtocolBufferException.truncatedMessage();
                ex.initCause(e);
                throw ex;
            }
            finally {
                ((Buffer)asBuffer).position(prevPos);
                ((Buffer)asBuffer).limit(prevLimit);
            }
        }
    }

    private static final class IterableDirectByteBufferDecoder
    extends CodedInputStream {
        private final Iterable<ByteBuffer> input;
        private final Iterator<ByteBuffer> iterator;
        private ByteBuffer currentByteBuffer;
        private final boolean immutable;
        private boolean enableAliasing;
        private int totalBufferSize;
        private int bufferSizeAfterCurrentLimit;
        private int currentLimit = Integer.MAX_VALUE;
        private int lastTag;
        private int totalBytesRead;
        private int startOffset;
        private long currentByteBufferPos;
        private long currentByteBufferStartPos;
        private long currentAddress;
        private long currentByteBufferLimit;

        private IterableDirectByteBufferDecoder(Iterable<ByteBuffer> inputBufs, int size, boolean immutableFlag) {
            this.totalBufferSize = size;
            this.input = inputBufs;
            this.iterator = this.input.iterator();
            this.immutable = immutableFlag;
            this.totalBytesRead = 0;
            this.startOffset = 0;
            if (size == 0) {
                this.currentByteBuffer = Internal.EMPTY_BYTE_BUFFER;
                this.currentByteBufferPos = 0L;
                this.currentByteBufferStartPos = 0L;
                this.currentByteBufferLimit = 0L;
                this.currentAddress = 0L;
            } else {
                this.tryGetNextByteBuffer();
            }
        }

        private void getNextByteBuffer() throws InvalidProtocolBufferException {
            if (!this.iterator.hasNext()) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.tryGetNextByteBuffer();
        }

        private void tryGetNextByteBuffer() {
            this.currentByteBuffer = this.iterator.next();
            this.totalBytesRead += (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
            this.currentByteBufferStartPos = this.currentByteBufferPos = (long)this.currentByteBuffer.position();
            this.currentByteBufferLimit = this.currentByteBuffer.limit();
            this.currentAddress = UnsafeUtil.addressOffset(this.currentByteBuffer);
            this.currentByteBufferPos += this.currentAddress;
            this.currentByteBufferStartPos += this.currentAddress;
            this.currentByteBufferLimit += this.currentAddress;
        }

        @Override
        public int readTag() throws IOException {
            if (this.isAtEnd()) {
                this.lastTag = 0;
                return 0;
            }
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }

        @Override
        public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
            if (this.lastTag != value) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        }

        @Override
        public int getLastTag() {
            return this.lastTag;
        }

        @Override
        public boolean skipField(int tag) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    this.skipRawVarint();
                    return true;
                }
                case 1: {
                    this.skipRawBytes(8);
                    return true;
                }
                case 2: {
                    this.skipRawBytes(this.readRawVarint32());
                    return true;
                }
                case 3: {
                    this.skipMessage();
                    this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                    return true;
                }
                case 4: {
                    this.checkValidEndTag();
                    return false;
                }
                case 5: {
                    this.skipRawBytes(4);
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public boolean skipField(int tag, CodedOutputStream output) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    long value = this.readInt64();
                    output.writeUInt32NoTag(tag);
                    output.writeUInt64NoTag(value);
                    return true;
                }
                case 1: {
                    long value = this.readRawLittleEndian64();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed64NoTag(value);
                    return true;
                }
                case 2: {
                    ByteString value = this.readBytes();
                    output.writeUInt32NoTag(tag);
                    output.writeBytesNoTag(value);
                    return true;
                }
                case 3: {
                    output.writeUInt32NoTag(tag);
                    this.skipMessage(output);
                    int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
                    this.checkLastTagWas(endtag);
                    output.writeUInt32NoTag(endtag);
                    return true;
                }
                case 4: {
                    this.checkValidEndTag();
                    return false;
                }
                case 5: {
                    int value = this.readRawLittleEndian32();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed32NoTag(value);
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(this.readRawLittleEndian64());
        }

        @Override
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(this.readRawLittleEndian32());
        }

        @Override
        public long readUInt64() throws IOException {
            return this.readRawVarint64();
        }

        @Override
        public long readInt64() throws IOException {
            return this.readRawVarint64();
        }

        @Override
        public int readInt32() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public long readFixed64() throws IOException {
            return this.readRawLittleEndian64();
        }

        @Override
        public int readFixed32() throws IOException {
            return this.readRawLittleEndian32();
        }

        @Override
        public boolean readBool() throws IOException {
            return this.readRawVarint64() != 0L;
        }

        @Override
        public String readString() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && (long)size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, (long)size);
                String result = new String(bytes, Internal.UTF_8);
                this.currentByteBufferPos += (long)size;
                return result;
            }
            if (size > 0 && size <= this.remaining()) {
                byte[] bytes = new byte[size];
                this.readRawBytesTo(bytes, 0, size);
                String result = new String(bytes, Internal.UTF_8);
                return result;
            }
            if (size == 0) {
                return "";
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public String readStringRequireUtf8() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && (long)size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
                int bufferPos = (int)(this.currentByteBufferPos - this.currentByteBufferStartPos);
                String result = Utf8.decodeUtf8(this.currentByteBuffer, bufferPos, size);
                this.currentByteBufferPos += (long)size;
                return result;
            }
            if (size >= 0 && size <= this.remaining()) {
                byte[] bytes = new byte[size];
                this.readRawBytesTo(bytes, 0, size);
                return Utf8.decodeUtf8(bytes, 0, size);
            }
            if (size == 0) {
                return "";
            }
            if (size <= 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.checkRecursionLimit();
            ++this.groupDepth;
            builder.mergeFrom(this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.groupDepth;
        }

        @Override
        public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.checkRecursionLimit();
            ++this.groupDepth;
            MessageLite result = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.groupDepth;
            return (T)result;
        }

        @Override
        @Deprecated
        public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
            this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override
        public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = this.readRawVarint32();
            this.checkRecursionLimit();
            int oldLimit = this.pushLimit(length);
            ++this.messageDepth;
            builder.mergeFrom(this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.messageDepth;
            if (this.getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.popLimit(oldLimit);
        }

        @Override
        public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = this.readRawVarint32();
            this.checkRecursionLimit();
            int oldLimit = this.pushLimit(length);
            ++this.messageDepth;
            MessageLite result = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.messageDepth;
            if (this.getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.popLimit(oldLimit);
            return (T)result;
        }

        @Override
        public ByteString readBytes() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && (long)size <= this.currentByteBufferLimit - this.currentByteBufferPos) {
                if (this.immutable && this.enableAliasing) {
                    int idx = (int)(this.currentByteBufferPos - this.currentAddress);
                    ByteString result = ByteString.wrap(this.slice(idx, idx + size));
                    this.currentByteBufferPos += (long)size;
                    return result;
                }
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, (long)size);
                this.currentByteBufferPos += (long)size;
                return ByteString.wrap(bytes);
            }
            if (size > 0 && size <= this.remaining()) {
                if (this.immutable && this.enableAliasing) {
                    ArrayList<ByteString> byteStrings = new ArrayList<ByteString>();
                    int l = size;
                    while (l > 0) {
                        if (this.currentRemaining() == 0L) {
                            this.getNextByteBuffer();
                        }
                        int bytesToCopy = Math.min(l, (int)this.currentRemaining());
                        int idx = (int)(this.currentByteBufferPos - this.currentAddress);
                        byteStrings.add(ByteString.wrap(this.slice(idx, idx + bytesToCopy)));
                        l -= bytesToCopy;
                        this.currentByteBufferPos += (long)bytesToCopy;
                    }
                    return ByteString.copyFrom(byteStrings);
                }
                byte[] temp = new byte[size];
                this.readRawBytesTo(temp, 0, size);
                return ByteString.wrap(temp);
            }
            if (size == 0) {
                return ByteString.EMPTY;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public byte[] readByteArray() throws IOException {
            return this.readRawBytes(this.readRawVarint32());
        }

        @Override
        public ByteBuffer readByteBuffer() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && (long)size <= this.currentRemaining()) {
                if (!this.immutable && this.enableAliasing) {
                    this.currentByteBufferPos += (long)size;
                    return this.slice((int)(this.currentByteBufferPos - this.currentAddress - (long)size), (int)(this.currentByteBufferPos - this.currentAddress));
                }
                byte[] bytes = new byte[size];
                UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, (long)size);
                this.currentByteBufferPos += (long)size;
                return ByteBuffer.wrap(bytes);
            }
            if (size > 0 && size <= this.remaining()) {
                byte[] temp = new byte[size];
                this.readRawBytesTo(temp, 0, size);
                return ByteBuffer.wrap(temp);
            }
            if (size == 0) {
                return Internal.EMPTY_BYTE_BUFFER;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public int readUInt32() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public int readEnum() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public int readSFixed32() throws IOException {
            return this.readRawLittleEndian32();
        }

        @Override
        public long readSFixed64() throws IOException {
            return this.readRawLittleEndian64();
        }

        @Override
        public int readSInt32() throws IOException {
            return IterableDirectByteBufferDecoder.decodeZigZag32(this.readRawVarint32());
        }

        @Override
        public long readSInt64() throws IOException {
            return IterableDirectByteBufferDecoder.decodeZigZag64(this.readRawVarint64());
        }

        @Override
        public int readRawVarint32() throws IOException {
            block4: {
                int x;
                long tempPos;
                block6: {
                    block8: {
                        block7: {
                            block5: {
                                tempPos = this.currentByteBufferPos;
                                if (this.currentByteBufferLimit == this.currentByteBufferPos) break block4;
                                if ((x = UnsafeUtil.getByte(tempPos++)) >= 0) {
                                    ++this.currentByteBufferPos;
                                    return x;
                                }
                                if (this.currentByteBufferLimit - this.currentByteBufferPos < 10L) break block4;
                                if ((x ^= UnsafeUtil.getByte(tempPos++) << 7) >= 0) break block5;
                                x ^= 0xFFFFFF80;
                                break block6;
                            }
                            if ((x ^= UnsafeUtil.getByte(tempPos++) << 14) < 0) break block7;
                            x ^= 0x3F80;
                            break block6;
                        }
                        if ((x ^= UnsafeUtil.getByte(tempPos++) << 21) >= 0) break block8;
                        x ^= 0xFFE03F80;
                        break block6;
                    }
                    byte y = UnsafeUtil.getByte(tempPos++);
                    x ^= y << 28;
                    x ^= 0xFE03F80;
                    if (y < 0 && UnsafeUtil.getByte(tempPos++) < 0 && UnsafeUtil.getByte(tempPos++) < 0 && UnsafeUtil.getByte(tempPos++) < 0 && UnsafeUtil.getByte(tempPos++) < 0 && UnsafeUtil.getByte(tempPos++) < 0) break block4;
                }
                this.currentByteBufferPos = tempPos;
                return x;
            }
            return (int)this.readRawVarint64SlowPath();
        }

        @Override
        public long readRawVarint64() throws IOException {
            block4: {
                long x;
                long tempPos;
                block6: {
                    block12: {
                        block11: {
                            block10: {
                                block9: {
                                    int y;
                                    block8: {
                                        block7: {
                                            block5: {
                                                tempPos = this.currentByteBufferPos;
                                                if (this.currentByteBufferLimit == this.currentByteBufferPos) break block4;
                                                if ((y = UnsafeUtil.getByte(tempPos++)) >= 0) {
                                                    ++this.currentByteBufferPos;
                                                    return y;
                                                }
                                                if (this.currentByteBufferLimit - this.currentByteBufferPos < 10L) break block4;
                                                if ((y ^= UnsafeUtil.getByte(tempPos++) << 7) >= 0) break block5;
                                                x = y ^ 0xFFFFFF80;
                                                break block6;
                                            }
                                            if ((y ^= UnsafeUtil.getByte(tempPos++) << 14) < 0) break block7;
                                            x = y ^ 0x3F80;
                                            break block6;
                                        }
                                        if ((y ^= UnsafeUtil.getByte(tempPos++) << 21) >= 0) break block8;
                                        x = y ^ 0xFFE03F80;
                                        break block6;
                                    }
                                    if ((x = (long)y ^ (long)UnsafeUtil.getByte(tempPos++) << 28) < 0L) break block9;
                                    x ^= 0xFE03F80L;
                                    break block6;
                                }
                                if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 35) >= 0L) break block10;
                                x ^= 0xFFFFFFF80FE03F80L;
                                break block6;
                            }
                            if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 42) < 0L) break block11;
                            x ^= 0x3F80FE03F80L;
                            break block6;
                        }
                        if ((x ^= (long)UnsafeUtil.getByte(tempPos++) << 49) >= 0L) break block12;
                        x ^= 0xFFFE03F80FE03F80L;
                        break block6;
                    }
                    x ^= (long)UnsafeUtil.getByte(tempPos++) << 56;
                    if ((x ^= 0xFE03F80FE03F80L) < 0L && (long)UnsafeUtil.getByte(tempPos++) < 0L) break block4;
                }
                this.currentByteBufferPos = tempPos;
                return x;
            }
            return this.readRawVarint64SlowPath();
        }

        @Override
        long readRawVarint64SlowPath() throws IOException {
            long result = 0L;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = this.readRawByte();
                result |= (long)(b & 0x7F) << shift;
                if ((b & 0x80) != 0) continue;
                return result;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override
        public int readRawLittleEndian32() throws IOException {
            if (this.currentRemaining() >= 4L) {
                long tempPos = this.currentByteBufferPos;
                this.currentByteBufferPos += 4L;
                return UnsafeUtil.getByte(tempPos) & 0xFF | (UnsafeUtil.getByte(tempPos + 1L) & 0xFF) << 8 | (UnsafeUtil.getByte(tempPos + 2L) & 0xFF) << 16 | (UnsafeUtil.getByte(tempPos + 3L) & 0xFF) << 24;
            }
            return this.readRawByte() & 0xFF | (this.readRawByte() & 0xFF) << 8 | (this.readRawByte() & 0xFF) << 16 | (this.readRawByte() & 0xFF) << 24;
        }

        @Override
        public long readRawLittleEndian64() throws IOException {
            if (this.currentRemaining() >= 8L) {
                long tempPos = this.currentByteBufferPos;
                this.currentByteBufferPos += 8L;
                return (long)UnsafeUtil.getByte(tempPos) & 0xFFL | ((long)UnsafeUtil.getByte(tempPos + 1L) & 0xFFL) << 8 | ((long)UnsafeUtil.getByte(tempPos + 2L) & 0xFFL) << 16 | ((long)UnsafeUtil.getByte(tempPos + 3L) & 0xFFL) << 24 | ((long)UnsafeUtil.getByte(tempPos + 4L) & 0xFFL) << 32 | ((long)UnsafeUtil.getByte(tempPos + 5L) & 0xFFL) << 40 | ((long)UnsafeUtil.getByte(tempPos + 6L) & 0xFFL) << 48 | ((long)UnsafeUtil.getByte(tempPos + 7L) & 0xFFL) << 56;
            }
            return (long)this.readRawByte() & 0xFFL | ((long)this.readRawByte() & 0xFFL) << 8 | ((long)this.readRawByte() & 0xFFL) << 16 | ((long)this.readRawByte() & 0xFFL) << 24 | ((long)this.readRawByte() & 0xFFL) << 32 | ((long)this.readRawByte() & 0xFFL) << 40 | ((long)this.readRawByte() & 0xFFL) << 48 | ((long)this.readRawByte() & 0xFFL) << 56;
        }

        @Override
        public void enableAliasing(boolean enabled) {
            this.enableAliasing = enabled;
        }

        @Override
        public void resetSizeCounter() {
            this.startOffset = (int)((long)this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos);
        }

        @Override
        public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
            int oldLimit;
            if (byteLimit < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if ((byteLimit += this.getTotalBytesRead()) > (oldLimit = this.currentLimit)) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.currentLimit = byteLimit;
            this.recomputeBufferSizeAfterLimit();
            return oldLimit;
        }

        private void recomputeBufferSizeAfterLimit() {
            this.totalBufferSize += this.bufferSizeAfterCurrentLimit;
            int bufferEnd = this.totalBufferSize - this.startOffset;
            if (bufferEnd > this.currentLimit) {
                this.bufferSizeAfterCurrentLimit = bufferEnd - this.currentLimit;
                this.totalBufferSize -= this.bufferSizeAfterCurrentLimit;
            } else {
                this.bufferSizeAfterCurrentLimit = 0;
            }
        }

        @Override
        public void popLimit(int oldLimit) {
            this.currentLimit = oldLimit;
            this.recomputeBufferSizeAfterLimit();
        }

        @Override
        public int getBytesUntilLimit() {
            if (this.currentLimit == Integer.MAX_VALUE) {
                return -1;
            }
            return this.currentLimit - this.getTotalBytesRead();
        }

        @Override
        public boolean isAtEnd() throws IOException {
            return (long)this.totalBytesRead + this.currentByteBufferPos - this.currentByteBufferStartPos == (long)this.totalBufferSize;
        }

        @Override
        public int getTotalBytesRead() {
            return (int)((long)(this.totalBytesRead - this.startOffset) + this.currentByteBufferPos - this.currentByteBufferStartPos);
        }

        @Override
        public byte readRawByte() throws IOException {
            if (this.currentRemaining() == 0L) {
                this.getNextByteBuffer();
            }
            return UnsafeUtil.getByte(this.currentByteBufferPos++);
        }

        @Override
        public byte[] readRawBytes(int length) throws IOException {
            if (length >= 0 && (long)length <= this.currentRemaining()) {
                byte[] bytes = new byte[length];
                UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, 0L, (long)length);
                this.currentByteBufferPos += (long)length;
                return bytes;
            }
            if (length >= 0 && length <= this.remaining()) {
                byte[] bytes = new byte[length];
                this.readRawBytesTo(bytes, 0, length);
                return bytes;
            }
            if (length <= 0) {
                if (length == 0) {
                    return Internal.EMPTY_BYTE_ARRAY;
                }
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        private void readRawBytesTo(byte[] bytes, int offset, int length) throws IOException {
            if (length >= 0 && length <= this.remaining()) {
                int l = length;
                while (l > 0) {
                    if (this.currentRemaining() == 0L) {
                        this.getNextByteBuffer();
                    }
                    int bytesToCopy = Math.min(l, (int)this.currentRemaining());
                    UnsafeUtil.copyMemory(this.currentByteBufferPos, bytes, (long)(length - l + offset), (long)bytesToCopy);
                    l -= bytesToCopy;
                    this.currentByteBufferPos += (long)bytesToCopy;
                }
                return;
            }
            if (length <= 0) {
                if (length == 0) {
                    return;
                }
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public void skipRawBytes(int length) throws IOException {
            if (length >= 0 && (long)length <= (long)(this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos) {
                int l = length;
                while (l > 0) {
                    if (this.currentRemaining() == 0L) {
                        this.getNextByteBuffer();
                    }
                    int rl = Math.min(l, (int)this.currentRemaining());
                    l -= rl;
                    this.currentByteBufferPos += (long)rl;
                }
                return;
            }
            if (length < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        private void skipRawVarint() throws IOException {
            for (int i = 0; i < 10; ++i) {
                if (this.readRawByte() < 0) continue;
                return;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private int remaining() {
            return (int)((long)(this.totalBufferSize - this.totalBytesRead) - this.currentByteBufferPos + this.currentByteBufferStartPos);
        }

        private long currentRemaining() {
            return this.currentByteBufferLimit - this.currentByteBufferPos;
        }

        private ByteBuffer slice(int begin, int end) throws IOException {
            int prevPos = this.currentByteBuffer.position();
            int prevLimit = this.currentByteBuffer.limit();
            ByteBuffer asBuffer = this.currentByteBuffer;
            try {
                ((Buffer)asBuffer).position(begin);
                ((Buffer)asBuffer).limit(end);
                ByteBuffer byteBuffer = this.currentByteBuffer.slice();
                return byteBuffer;
            }
            catch (IllegalArgumentException e) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            finally {
                ((Buffer)asBuffer).position(prevPos);
                ((Buffer)asBuffer).limit(prevLimit);
            }
        }
    }

    private static final class ArrayDecoder
    extends CodedInputStream {
        private final byte[] buffer;
        private final boolean immutable;
        private int limit;
        private int bufferSizeAfterLimit;
        private int pos;
        private int startPos;
        private int lastTag;
        private boolean enableAliasing;
        private int currentLimit = Integer.MAX_VALUE;

        private ArrayDecoder(byte[] buffer, int offset, int len, boolean immutable) {
            this.buffer = buffer;
            this.limit = offset + len;
            this.startPos = this.pos = offset;
            this.immutable = immutable;
        }

        @Override
        public int readTag() throws IOException {
            if (this.isAtEnd()) {
                this.lastTag = 0;
                return 0;
            }
            this.lastTag = this.readRawVarint32();
            if (WireFormat.getTagFieldNumber(this.lastTag) == 0) {
                throw InvalidProtocolBufferException.invalidTag();
            }
            return this.lastTag;
        }

        @Override
        public void checkLastTagWas(int value) throws InvalidProtocolBufferException {
            if (this.lastTag != value) {
                throw InvalidProtocolBufferException.invalidEndTag();
            }
        }

        @Override
        public int getLastTag() {
            return this.lastTag;
        }

        @Override
        public boolean skipField(int tag) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    this.skipRawVarint();
                    return true;
                }
                case 1: {
                    this.skipRawBytes(8);
                    return true;
                }
                case 2: {
                    this.skipRawBytes(this.readRawVarint32());
                    return true;
                }
                case 3: {
                    this.skipMessage();
                    this.checkLastTagWas(WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4));
                    return true;
                }
                case 4: {
                    this.checkValidEndTag();
                    return false;
                }
                case 5: {
                    this.skipRawBytes(4);
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public boolean skipField(int tag, CodedOutputStream output) throws IOException {
            switch (WireFormat.getTagWireType(tag)) {
                case 0: {
                    long value = this.readInt64();
                    output.writeUInt32NoTag(tag);
                    output.writeUInt64NoTag(value);
                    return true;
                }
                case 1: {
                    long value = this.readRawLittleEndian64();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed64NoTag(value);
                    return true;
                }
                case 2: {
                    ByteString value = this.readBytes();
                    output.writeUInt32NoTag(tag);
                    output.writeBytesNoTag(value);
                    return true;
                }
                case 3: {
                    output.writeUInt32NoTag(tag);
                    this.skipMessage(output);
                    int endtag = WireFormat.makeTag(WireFormat.getTagFieldNumber(tag), 4);
                    this.checkLastTagWas(endtag);
                    output.writeUInt32NoTag(endtag);
                    return true;
                }
                case 4: {
                    this.checkValidEndTag();
                    return false;
                }
                case 5: {
                    int value = this.readRawLittleEndian32();
                    output.writeUInt32NoTag(tag);
                    output.writeFixed32NoTag(value);
                    return true;
                }
            }
            throw InvalidProtocolBufferException.invalidWireType();
        }

        @Override
        public double readDouble() throws IOException {
            return Double.longBitsToDouble(this.readRawLittleEndian64());
        }

        @Override
        public float readFloat() throws IOException {
            return Float.intBitsToFloat(this.readRawLittleEndian32());
        }

        @Override
        public long readUInt64() throws IOException {
            return this.readRawVarint64();
        }

        @Override
        public long readInt64() throws IOException {
            return this.readRawVarint64();
        }

        @Override
        public int readInt32() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public long readFixed64() throws IOException {
            return this.readRawLittleEndian64();
        }

        @Override
        public int readFixed32() throws IOException {
            return this.readRawLittleEndian32();
        }

        @Override
        public boolean readBool() throws IOException {
            return this.readRawVarint64() != 0L;
        }

        @Override
        public String readString() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.limit - this.pos) {
                String result = new String(this.buffer, this.pos, size, Internal.UTF_8);
                this.pos += size;
                return result;
            }
            if (size == 0) {
                return "";
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public String readStringRequireUtf8() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.limit - this.pos) {
                String result = Utf8.decodeUtf8(this.buffer, this.pos, size);
                this.pos += size;
                return result;
            }
            if (size == 0) {
                return "";
            }
            if (size <= 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public void readGroup(int fieldNumber, MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.checkRecursionLimit();
            ++this.groupDepth;
            builder.mergeFrom(this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.groupDepth;
        }

        @Override
        public <T extends MessageLite> T readGroup(int fieldNumber, Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            this.checkRecursionLimit();
            ++this.groupDepth;
            MessageLite result = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
            this.checkLastTagWas(WireFormat.makeTag(fieldNumber, 4));
            --this.groupDepth;
            return (T)result;
        }

        @Override
        @Deprecated
        public void readUnknownGroup(int fieldNumber, MessageLite.Builder builder) throws IOException {
            this.readGroup(fieldNumber, builder, ExtensionRegistryLite.getEmptyRegistry());
        }

        @Override
        public void readMessage(MessageLite.Builder builder, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = this.readRawVarint32();
            this.checkRecursionLimit();
            int oldLimit = this.pushLimit(length);
            ++this.messageDepth;
            builder.mergeFrom(this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.messageDepth;
            if (this.getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.popLimit(oldLimit);
        }

        @Override
        public <T extends MessageLite> T readMessage(Parser<T> parser, ExtensionRegistryLite extensionRegistry) throws IOException {
            int length = this.readRawVarint32();
            this.checkRecursionLimit();
            int oldLimit = this.pushLimit(length);
            ++this.messageDepth;
            MessageLite result = (MessageLite)parser.parsePartialFrom(this, extensionRegistry);
            this.checkLastTagWas(0);
            --this.messageDepth;
            if (this.getBytesUntilLimit() != 0) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.popLimit(oldLimit);
            return (T)result;
        }

        @Override
        public ByteString readBytes() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.limit - this.pos) {
                ByteString result = this.immutable && this.enableAliasing ? ByteString.wrap(this.buffer, this.pos, size) : ByteString.copyFrom(this.buffer, this.pos, size);
                this.pos += size;
                return result;
            }
            if (size == 0) {
                return ByteString.EMPTY;
            }
            return ByteString.wrap(this.readRawBytes(size));
        }

        @Override
        public byte[] readByteArray() throws IOException {
            int size = this.readRawVarint32();
            return this.readRawBytes(size);
        }

        @Override
        public ByteBuffer readByteBuffer() throws IOException {
            int size = this.readRawVarint32();
            if (size > 0 && size <= this.limit - this.pos) {
                ByteBuffer result = !this.immutable && this.enableAliasing ? ByteBuffer.wrap(this.buffer, this.pos, size).slice() : ByteBuffer.wrap(Arrays.copyOfRange(this.buffer, this.pos, this.pos + size));
                this.pos += size;
                return result;
            }
            if (size == 0) {
                return Internal.EMPTY_BYTE_BUFFER;
            }
            if (size < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public int readUInt32() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public int readEnum() throws IOException {
            return this.readRawVarint32();
        }

        @Override
        public int readSFixed32() throws IOException {
            return this.readRawLittleEndian32();
        }

        @Override
        public long readSFixed64() throws IOException {
            return this.readRawLittleEndian64();
        }

        @Override
        public int readSInt32() throws IOException {
            return ArrayDecoder.decodeZigZag32(this.readRawVarint32());
        }

        @Override
        public long readSInt64() throws IOException {
            return ArrayDecoder.decodeZigZag64(this.readRawVarint64());
        }

        @Override
        public int readRawVarint32() throws IOException {
            block4: {
                int x;
                int tempPos;
                block6: {
                    byte[] buffer;
                    block8: {
                        block7: {
                            block5: {
                                tempPos = this.pos;
                                if (this.limit == tempPos) break block4;
                                buffer = this.buffer;
                                if ((x = buffer[tempPos++]) >= 0) {
                                    this.pos = tempPos;
                                    return x;
                                }
                                if (this.limit - tempPos < 9) break block4;
                                if ((x ^= buffer[tempPos++] << 7) >= 0) break block5;
                                x ^= 0xFFFFFF80;
                                break block6;
                            }
                            if ((x ^= buffer[tempPos++] << 14) < 0) break block7;
                            x ^= 0x3F80;
                            break block6;
                        }
                        if ((x ^= buffer[tempPos++] << 21) >= 0) break block8;
                        x ^= 0xFFE03F80;
                        break block6;
                    }
                    byte y = buffer[tempPos++];
                    x ^= y << 28;
                    x ^= 0xFE03F80;
                    if (y < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0 && buffer[tempPos++] < 0) break block4;
                }
                this.pos = tempPos;
                return x;
            }
            return (int)this.readRawVarint64SlowPath();
        }

        private void skipRawVarint() throws IOException {
            if (this.limit - this.pos >= 10) {
                this.skipRawVarintFastPath();
            } else {
                this.skipRawVarintSlowPath();
            }
        }

        private void skipRawVarintFastPath() throws IOException {
            for (int i = 0; i < 10; ++i) {
                if (this.buffer[this.pos++] < 0) continue;
                return;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        private void skipRawVarintSlowPath() throws IOException {
            for (int i = 0; i < 10; ++i) {
                if (this.readRawByte() < 0) continue;
                return;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override
        public long readRawVarint64() throws IOException {
            block4: {
                long x;
                int tempPos;
                block6: {
                    byte[] buffer;
                    block12: {
                        block11: {
                            block10: {
                                block9: {
                                    int y;
                                    block8: {
                                        block7: {
                                            block5: {
                                                tempPos = this.pos;
                                                if (this.limit == tempPos) break block4;
                                                buffer = this.buffer;
                                                if ((y = buffer[tempPos++]) >= 0) {
                                                    this.pos = tempPos;
                                                    return y;
                                                }
                                                if (this.limit - tempPos < 9) break block4;
                                                if ((y ^= buffer[tempPos++] << 7) >= 0) break block5;
                                                x = y ^ 0xFFFFFF80;
                                                break block6;
                                            }
                                            if ((y ^= buffer[tempPos++] << 14) < 0) break block7;
                                            x = y ^ 0x3F80;
                                            break block6;
                                        }
                                        if ((y ^= buffer[tempPos++] << 21) >= 0) break block8;
                                        x = y ^ 0xFFE03F80;
                                        break block6;
                                    }
                                    if ((x = (long)y ^ (long)buffer[tempPos++] << 28) < 0L) break block9;
                                    x ^= 0xFE03F80L;
                                    break block6;
                                }
                                if ((x ^= (long)buffer[tempPos++] << 35) >= 0L) break block10;
                                x ^= 0xFFFFFFF80FE03F80L;
                                break block6;
                            }
                            if ((x ^= (long)buffer[tempPos++] << 42) < 0L) break block11;
                            x ^= 0x3F80FE03F80L;
                            break block6;
                        }
                        if ((x ^= (long)buffer[tempPos++] << 49) >= 0L) break block12;
                        x ^= 0xFFFE03F80FE03F80L;
                        break block6;
                    }
                    x ^= (long)buffer[tempPos++] << 56;
                    if ((x ^= 0xFE03F80FE03F80L) < 0L && (long)buffer[tempPos++] < 0L) break block4;
                }
                this.pos = tempPos;
                return x;
            }
            return this.readRawVarint64SlowPath();
        }

        @Override
        long readRawVarint64SlowPath() throws IOException {
            long result = 0L;
            for (int shift = 0; shift < 64; shift += 7) {
                byte b = this.readRawByte();
                result |= (long)(b & 0x7F) << shift;
                if ((b & 0x80) != 0) continue;
                return result;
            }
            throw InvalidProtocolBufferException.malformedVarint();
        }

        @Override
        public int readRawLittleEndian32() throws IOException {
            int tempPos = this.pos;
            if (this.limit - tempPos < 4) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            byte[] buffer = this.buffer;
            this.pos = tempPos + 4;
            return buffer[tempPos] & 0xFF | (buffer[tempPos + 1] & 0xFF) << 8 | (buffer[tempPos + 2] & 0xFF) << 16 | (buffer[tempPos + 3] & 0xFF) << 24;
        }

        @Override
        public long readRawLittleEndian64() throws IOException {
            int tempPos = this.pos;
            if (this.limit - tempPos < 8) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            byte[] buffer = this.buffer;
            this.pos = tempPos + 8;
            return (long)buffer[tempPos] & 0xFFL | ((long)buffer[tempPos + 1] & 0xFFL) << 8 | ((long)buffer[tempPos + 2] & 0xFFL) << 16 | ((long)buffer[tempPos + 3] & 0xFFL) << 24 | ((long)buffer[tempPos + 4] & 0xFFL) << 32 | ((long)buffer[tempPos + 5] & 0xFFL) << 40 | ((long)buffer[tempPos + 6] & 0xFFL) << 48 | ((long)buffer[tempPos + 7] & 0xFFL) << 56;
        }

        @Override
        public void enableAliasing(boolean enabled) {
            this.enableAliasing = enabled;
        }

        @Override
        public void resetSizeCounter() {
            this.startPos = this.pos;
        }

        @Override
        public int pushLimit(int byteLimit) throws InvalidProtocolBufferException {
            if (byteLimit < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            if ((byteLimit += this.getTotalBytesRead()) < 0) {
                throw InvalidProtocolBufferException.sizeLimitExceeded();
            }
            int oldLimit = this.currentLimit;
            if (byteLimit > oldLimit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            this.currentLimit = byteLimit;
            this.recomputeBufferSizeAfterLimit();
            return oldLimit;
        }

        private void recomputeBufferSizeAfterLimit() {
            this.limit += this.bufferSizeAfterLimit;
            int bufferEnd = this.limit - this.startPos;
            if (bufferEnd > this.currentLimit) {
                this.bufferSizeAfterLimit = bufferEnd - this.currentLimit;
                this.limit -= this.bufferSizeAfterLimit;
            } else {
                this.bufferSizeAfterLimit = 0;
            }
        }

        @Override
        public void popLimit(int oldLimit) {
            this.currentLimit = oldLimit;
            this.recomputeBufferSizeAfterLimit();
        }

        @Override
        public int getBytesUntilLimit() {
            if (this.currentLimit == Integer.MAX_VALUE) {
                return -1;
            }
            return this.currentLimit - this.getTotalBytesRead();
        }

        @Override
        public boolean isAtEnd() throws IOException {
            return this.pos == this.limit;
        }

        @Override
        public int getTotalBytesRead() {
            return this.pos - this.startPos;
        }

        @Override
        public byte readRawByte() throws IOException {
            if (this.pos == this.limit) {
                throw InvalidProtocolBufferException.truncatedMessage();
            }
            return this.buffer[this.pos++];
        }

        @Override
        public byte[] readRawBytes(int length) throws IOException {
            if (length > 0 && length <= this.limit - this.pos) {
                int tempPos = this.pos;
                this.pos += length;
                return Arrays.copyOfRange(this.buffer, tempPos, this.pos);
            }
            if (length <= 0) {
                if (length == 0) {
                    return Internal.EMPTY_BYTE_ARRAY;
                }
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }

        @Override
        public void skipRawBytes(int length) throws IOException {
            if (length >= 0 && length <= this.limit - this.pos) {
                this.pos += length;
                return;
            }
            if (length < 0) {
                throw InvalidProtocolBufferException.negativeSize();
            }
            throw InvalidProtocolBufferException.truncatedMessage();
        }
    }
}

