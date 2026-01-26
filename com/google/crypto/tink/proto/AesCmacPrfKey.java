/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCmacPrf;
import com.google.crypto.tink.proto.AesCmacPrfKeyOrBuilder;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class AesCmacPrfKey
extends GeneratedMessage
implements AesCmacPrfKeyOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_ = 0;
    public static final int KEY_VALUE_FIELD_NUMBER = 2;
    private ByteString keyValue_ = ByteString.EMPTY;
    private byte memoizedIsInitialized = (byte)-1;
    private static final AesCmacPrfKey DEFAULT_INSTANCE;
    private static final Parser<AesCmacPrfKey> PARSER;

    private AesCmacPrfKey(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private AesCmacPrfKey() {
        this.keyValue_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return AesCmacPrf.internal_static_google_crypto_tink_AesCmacPrfKey_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return AesCmacPrf.internal_static_google_crypto_tink_AesCmacPrfKey_fieldAccessorTable.ensureFieldAccessorsInitialized(AesCmacPrfKey.class, Builder.class);
    }

    @Override
    public int getVersion() {
        return this.version_;
    }

    @Override
    public ByteString getKeyValue() {
        return this.keyValue_;
    }

    @Override
    public final boolean isInitialized() {
        byte isInitialized = this.memoizedIsInitialized;
        if (isInitialized == 1) {
            return true;
        }
        if (isInitialized == 0) {
            return false;
        }
        this.memoizedIsInitialized = 1;
        return true;
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        if (this.version_ != 0) {
            output.writeUInt32(1, this.version_);
        }
        if (!this.keyValue_.isEmpty()) {
            output.writeBytes(2, this.keyValue_);
        }
        this.getUnknownFields().writeTo(output);
    }

    @Override
    public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1) {
            return size;
        }
        size = 0;
        if (this.version_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.version_);
        }
        if (!this.keyValue_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(2, this.keyValue_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AesCmacPrfKey)) {
            return super.equals(obj);
        }
        AesCmacPrfKey other = (AesCmacPrfKey)obj;
        if (this.getVersion() != other.getVersion()) {
            return false;
        }
        if (!this.getKeyValue().equals(other.getKeyValue())) {
            return false;
        }
        return this.getUnknownFields().equals(other.getUnknownFields());
    }

    @Override
    public int hashCode() {
        if (this.memoizedHashCode != 0) {
            return this.memoizedHashCode;
        }
        int hash = 41;
        hash = 19 * hash + AesCmacPrfKey.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getVersion();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getKeyValue().hashCode();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static AesCmacPrfKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCmacPrfKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCmacPrfKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCmacPrfKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCmacPrfKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCmacPrfKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCmacPrfKey parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesCmacPrfKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesCmacPrfKey parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static AesCmacPrfKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesCmacPrfKey parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesCmacPrfKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return AesCmacPrfKey.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(AesCmacPrfKey prototype) {
        return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }

    @Override
    public Builder toBuilder() {
        return this == DEFAULT_INSTANCE ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(AbstractMessage.BuilderParent parent) {
        Builder builder = new Builder(parent);
        return builder;
    }

    public static AesCmacPrfKey getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<AesCmacPrfKey> parser() {
        return PARSER;
    }

    public Parser<AesCmacPrfKey> getParserForType() {
        return PARSER;
    }

    @Override
    public AesCmacPrfKey getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCmacPrfKey.class.getName());
        DEFAULT_INSTANCE = new AesCmacPrfKey();
        PARSER = new AbstractParser<AesCmacPrfKey>(){

            @Override
            public AesCmacPrfKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = AesCmacPrfKey.newBuilder();
                try {
                    builder.mergeFrom(input, extensionRegistry);
                }
                catch (InvalidProtocolBufferException e) {
                    throw e.setUnfinishedMessage(builder.buildPartial());
                }
                catch (UninitializedMessageException e) {
                    throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
                }
                catch (IOException e) {
                    throw new InvalidProtocolBufferException(e).setUnfinishedMessage(builder.buildPartial());
                }
                return builder.buildPartial();
            }
        };
    }

    public static final class Builder
    extends GeneratedMessage.Builder<Builder>
    implements AesCmacPrfKeyOrBuilder {
        private int bitField0_;
        private int version_;
        private ByteString keyValue_ = ByteString.EMPTY;

        public static final Descriptors.Descriptor getDescriptor() {
            return AesCmacPrf.internal_static_google_crypto_tink_AesCmacPrfKey_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return AesCmacPrf.internal_static_google_crypto_tink_AesCmacPrfKey_fieldAccessorTable.ensureFieldAccessorsInitialized(AesCmacPrfKey.class, Builder.class);
        }

        private Builder() {
        }

        private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.version_ = 0;
            this.keyValue_ = ByteString.EMPTY;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return AesCmacPrf.internal_static_google_crypto_tink_AesCmacPrfKey_descriptor;
        }

        @Override
        public AesCmacPrfKey getDefaultInstanceForType() {
            return AesCmacPrfKey.getDefaultInstance();
        }

        @Override
        public AesCmacPrfKey build() {
            AesCmacPrfKey result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public AesCmacPrfKey buildPartial() {
            AesCmacPrfKey result = new AesCmacPrfKey(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(AesCmacPrfKey result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.version_ = this.version_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.keyValue_ = this.keyValue_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof AesCmacPrfKey) {
                return this.mergeFrom((AesCmacPrfKey)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(AesCmacPrfKey other) {
            if (other == AesCmacPrfKey.getDefaultInstance()) {
                return this;
            }
            if (other.getVersion() != 0) {
                this.setVersion(other.getVersion());
            }
            if (!other.getKeyValue().isEmpty()) {
                this.setKeyValue(other.getKeyValue());
            }
            this.mergeUnknownFields(other.getUnknownFields());
            this.onChanged();
            return this;
        }

        @Override
        public final boolean isInitialized() {
            return true;
        }

        @Override
        public Builder mergeFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            if (extensionRegistry == null) {
                throw new NullPointerException();
            }
            try {
                boolean done = false;
                block10: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block10;
                        }
                        case 8: {
                            this.version_ = input.readUInt32();
                            this.bitField0_ |= 1;
                            continue block10;
                        }
                        case 18: {
                            this.keyValue_ = input.readBytes();
                            this.bitField0_ |= 2;
                            continue block10;
                        }
                    }
                    if (super.parseUnknownField(input, extensionRegistry, tag)) continue;
                    done = true;
                }
            }
            catch (InvalidProtocolBufferException e) {
                throw e.unwrapIOException();
            }
            finally {
                this.onChanged();
            }
            return this;
        }

        @Override
        public int getVersion() {
            return this.version_;
        }

        public Builder setVersion(int value) {
            this.version_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearVersion() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.version_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public ByteString getKeyValue() {
            return this.keyValue_;
        }

        public Builder setKeyValue(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.keyValue_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearKeyValue() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.keyValue_ = AesCmacPrfKey.getDefaultInstance().getKeyValue();
            this.onChanged();
            return this;
        }
    }
}

