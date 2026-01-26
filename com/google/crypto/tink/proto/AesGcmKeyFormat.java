/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesGcm;
import com.google.crypto.tink.proto.AesGcmKeyFormatOrBuilder;
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

public final class AesGcmKeyFormat
extends GeneratedMessage
implements AesGcmKeyFormatOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int KEY_SIZE_FIELD_NUMBER = 2;
    private int keySize_ = 0;
    public static final int VERSION_FIELD_NUMBER = 3;
    private int version_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final AesGcmKeyFormat DEFAULT_INSTANCE;
    private static final Parser<AesGcmKeyFormat> PARSER;

    private AesGcmKeyFormat(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private AesGcmKeyFormat() {
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return AesGcm.internal_static_google_crypto_tink_AesGcmKeyFormat_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return AesGcm.internal_static_google_crypto_tink_AesGcmKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(AesGcmKeyFormat.class, Builder.class);
    }

    @Override
    public int getKeySize() {
        return this.keySize_;
    }

    @Override
    public int getVersion() {
        return this.version_;
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
        if (this.keySize_ != 0) {
            output.writeUInt32(2, this.keySize_);
        }
        if (this.version_ != 0) {
            output.writeUInt32(3, this.version_);
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
        if (this.keySize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.keySize_);
        }
        if (this.version_ != 0) {
            size += CodedOutputStream.computeUInt32Size(3, this.version_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AesGcmKeyFormat)) {
            return super.equals(obj);
        }
        AesGcmKeyFormat other = (AesGcmKeyFormat)obj;
        if (this.getKeySize() != other.getKeySize()) {
            return false;
        }
        if (this.getVersion() != other.getVersion()) {
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
        hash = 19 * hash + AesGcmKeyFormat.getDescriptor().hashCode();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getKeySize();
        hash = 37 * hash + 3;
        hash = 53 * hash + this.getVersion();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static AesGcmKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesGcmKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesGcmKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesGcmKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesGcmKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesGcmKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesGcmKeyFormat parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesGcmKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesGcmKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static AesGcmKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesGcmKeyFormat parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesGcmKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return AesGcmKeyFormat.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(AesGcmKeyFormat prototype) {
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

    public static AesGcmKeyFormat getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<AesGcmKeyFormat> parser() {
        return PARSER;
    }

    public Parser<AesGcmKeyFormat> getParserForType() {
        return PARSER;
    }

    @Override
    public AesGcmKeyFormat getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesGcmKeyFormat.class.getName());
        DEFAULT_INSTANCE = new AesGcmKeyFormat();
        PARSER = new AbstractParser<AesGcmKeyFormat>(){

            @Override
            public AesGcmKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = AesGcmKeyFormat.newBuilder();
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
    implements AesGcmKeyFormatOrBuilder {
        private int bitField0_;
        private int keySize_;
        private int version_;

        public static final Descriptors.Descriptor getDescriptor() {
            return AesGcm.internal_static_google_crypto_tink_AesGcmKeyFormat_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return AesGcm.internal_static_google_crypto_tink_AesGcmKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(AesGcmKeyFormat.class, Builder.class);
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
            this.keySize_ = 0;
            this.version_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return AesGcm.internal_static_google_crypto_tink_AesGcmKeyFormat_descriptor;
        }

        @Override
        public AesGcmKeyFormat getDefaultInstanceForType() {
            return AesGcmKeyFormat.getDefaultInstance();
        }

        @Override
        public AesGcmKeyFormat build() {
            AesGcmKeyFormat result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public AesGcmKeyFormat buildPartial() {
            AesGcmKeyFormat result = new AesGcmKeyFormat(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(AesGcmKeyFormat result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.keySize_ = this.keySize_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.version_ = this.version_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof AesGcmKeyFormat) {
                return this.mergeFrom((AesGcmKeyFormat)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(AesGcmKeyFormat other) {
            if (other == AesGcmKeyFormat.getDefaultInstance()) {
                return this;
            }
            if (other.getKeySize() != 0) {
                this.setKeySize(other.getKeySize());
            }
            if (other.getVersion() != 0) {
                this.setVersion(other.getVersion());
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
                        case 16: {
                            this.keySize_ = input.readUInt32();
                            this.bitField0_ |= 1;
                            continue block10;
                        }
                        case 24: {
                            this.version_ = input.readUInt32();
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
        public int getKeySize() {
            return this.keySize_;
        }

        public Builder setKeySize(int value) {
            this.keySize_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearKeySize() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.keySize_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getVersion() {
            return this.version_;
        }

        public Builder setVersion(int value) {
            this.version_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearVersion() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.version_ = 0;
            this.onChanged();
            return this;
        }
    }
}

