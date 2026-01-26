/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CanIgnoreReturnValue;
import com.google.protobuf.CheckReturnValue;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLiteOrBuilder;
import com.google.protobuf.Parser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

@CheckReturnValue
public interface MessageLite
extends MessageLiteOrBuilder {
    public void writeTo(CodedOutputStream var1) throws IOException;

    public int getSerializedSize();

    public Parser<? extends MessageLite> getParserForType();

    public ByteString toByteString();

    public byte[] toByteArray();

    public void writeTo(OutputStream var1) throws IOException;

    public void writeDelimitedTo(OutputStream var1) throws IOException;

    public Builder newBuilderForType();

    public Builder toBuilder();

    public static interface Builder
    extends MessageLiteOrBuilder,
    Cloneable {
        @CanIgnoreReturnValue
        public Builder clear();

        public MessageLite build();

        public MessageLite buildPartial();

        public Builder clone();

        @CanIgnoreReturnValue
        public Builder mergeFrom(CodedInputStream var1) throws IOException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(CodedInputStream var1, ExtensionRegistryLite var2) throws IOException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(ByteString var1) throws InvalidProtocolBufferException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(ByteString var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(byte[] var1) throws InvalidProtocolBufferException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(byte[] var1, int var2, int var3) throws InvalidProtocolBufferException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(byte[] var1, ExtensionRegistryLite var2) throws InvalidProtocolBufferException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(byte[] var1, int var2, int var3, ExtensionRegistryLite var4) throws InvalidProtocolBufferException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(InputStream var1) throws IOException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException;

        @CanIgnoreReturnValue
        public Builder mergeFrom(MessageLite var1);

        public boolean mergeDelimitedFrom(InputStream var1) throws IOException;

        public boolean mergeDelimitedFrom(InputStream var1, ExtensionRegistryLite var2) throws IOException;
    }
}

