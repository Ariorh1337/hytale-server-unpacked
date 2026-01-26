/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Reader;
import com.google.protobuf.WireFormat;
import com.google.protobuf.Writer;
import java.io.IOException;

@CheckReturnValue
abstract class UnknownFieldSchema<T, B> {
    static final int DEFAULT_RECURSION_LIMIT = 100;
    private static volatile int recursionLimit = 100;

    UnknownFieldSchema() {
    }

    abstract boolean shouldDiscardUnknownFields(Reader var1);

    abstract void addVarint(B var1, int var2, long var3);

    abstract void addFixed32(B var1, int var2, int var3);

    abstract void addFixed64(B var1, int var2, long var3);

    abstract void addLengthDelimited(B var1, int var2, ByteString var3);

    abstract void addGroup(B var1, int var2, T var3);

    abstract B newBuilder();

    abstract T toImmutable(B var1);

    abstract void setToMessage(Object var1, T var2);

    abstract T getFromMessage(Object var1);

    abstract B getBuilderFromMessage(Object var1);

    abstract void setBuilderToMessage(Object var1, B var2);

    abstract void makeImmutable(Object var1);

    final boolean mergeOneFieldFrom(B unknownFields, Reader reader, int currentDepth) throws IOException {
        int tag = reader.getTag();
        int fieldNumber = WireFormat.getTagFieldNumber(tag);
        switch (WireFormat.getTagWireType(tag)) {
            case 0: {
                this.addVarint(unknownFields, fieldNumber, reader.readInt64());
                return true;
            }
            case 5: {
                this.addFixed32(unknownFields, fieldNumber, reader.readFixed32());
                return true;
            }
            case 1: {
                this.addFixed64(unknownFields, fieldNumber, reader.readFixed64());
                return true;
            }
            case 2: {
                this.addLengthDelimited(unknownFields, fieldNumber, reader.readBytes());
                return true;
            }
            case 3: {
                B subFields = this.newBuilder();
                int endGroupTag = WireFormat.makeTag(fieldNumber, 4);
                if (++currentDepth >= recursionLimit) {
                    throw InvalidProtocolBufferException.recursionLimitExceeded();
                }
                this.mergeFrom(subFields, reader, currentDepth);
                --currentDepth;
                if (endGroupTag != reader.getTag()) {
                    throw InvalidProtocolBufferException.invalidEndTag();
                }
                this.addGroup(unknownFields, fieldNumber, this.toImmutable(subFields));
                return true;
            }
            case 4: {
                if (currentDepth == 0) {
                    throw InvalidProtocolBufferException.invalidEndTag();
                }
                return false;
            }
        }
        throw InvalidProtocolBufferException.invalidWireType();
    }

    private final void mergeFrom(B unknownFields, Reader reader, int currentDepth) throws IOException {
        while (reader.getFieldNumber() != Integer.MAX_VALUE && this.mergeOneFieldFrom(unknownFields, reader, currentDepth)) {
        }
    }

    abstract void writeTo(T var1, Writer var2) throws IOException;

    abstract void writeAsMessageSetTo(T var1, Writer var2) throws IOException;

    abstract T merge(T var1, T var2);

    abstract int getSerializedSizeAsMessageSet(T var1);

    abstract int getSerializedSize(T var1);

    public void setRecursionLimit(int limit) {
        recursionLimit = limit;
    }
}

