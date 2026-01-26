/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesEax;
import com.google.crypto.tink.proto.AesEaxParamsOrBuilder;
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

public final class AesEaxParams
extends GeneratedMessage
implements AesEaxParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int IV_SIZE_FIELD_NUMBER = 1;
    private int ivSize_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final AesEaxParams DEFAULT_INSTANCE;
    private static final Parser<AesEaxParams> PARSER;

    private AesEaxParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private AesEaxParams() {
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return AesEax.internal_static_google_crypto_tink_AesEaxParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return AesEax.internal_static_google_crypto_tink_AesEaxParams_fieldAccessorTable.ensureFieldAccessorsInitialized(AesEaxParams.class, Builder.class);
    }

    @Override
    public int getIvSize() {
        return this.ivSize_;
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
        if (this.ivSize_ != 0) {
            output.writeUInt32(1, this.ivSize_);
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
        if (this.ivSize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.ivSize_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AesEaxParams)) {
            return super.equals(obj);
        }
        AesEaxParams other = (AesEaxParams)obj;
        if (this.getIvSize() != other.getIvSize()) {
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
        hash = 19 * hash + AesEaxParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getIvSize();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static AesEaxParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesEaxParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesEaxParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesEaxParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesEaxParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesEaxParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesEaxParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesEaxParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesEaxParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static AesEaxParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesEaxParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesEaxParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return AesEaxParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(AesEaxParams prototype) {
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

    public static AesEaxParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<AesEaxParams> parser() {
        return PARSER;
    }

    public Parser<AesEaxParams> getParserForType() {
        return PARSER;
    }

    @Override
    public AesEaxParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesEaxParams.class.getName());
        DEFAULT_INSTANCE = new AesEaxParams();
        PARSER = new AbstractParser<AesEaxParams>(){

            @Override
            public AesEaxParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = AesEaxParams.newBuilder();
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
    implements AesEaxParamsOrBuilder {
        private int bitField0_;
        private int ivSize_;

        public static final Descriptors.Descriptor getDescriptor() {
            return AesEax.internal_static_google_crypto_tink_AesEaxParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return AesEax.internal_static_google_crypto_tink_AesEaxParams_fieldAccessorTable.ensureFieldAccessorsInitialized(AesEaxParams.class, Builder.class);
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
            this.ivSize_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return AesEax.internal_static_google_crypto_tink_AesEaxParams_descriptor;
        }

        @Override
        public AesEaxParams getDefaultInstanceForType() {
            return AesEaxParams.getDefaultInstance();
        }

        @Override
        public AesEaxParams build() {
            AesEaxParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public AesEaxParams buildPartial() {
            AesEaxParams result = new AesEaxParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(AesEaxParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.ivSize_ = this.ivSize_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof AesEaxParams) {
                return this.mergeFrom((AesEaxParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(AesEaxParams other) {
            if (other == AesEaxParams.getDefaultInstance()) {
                return this;
            }
            if (other.getIvSize() != 0) {
                this.setIvSize(other.getIvSize());
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
                block9: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block9;
                        }
                        case 8: {
                            this.ivSize_ = input.readUInt32();
                            this.bitField0_ |= 1;
                            continue block9;
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
        public int getIvSize() {
            return this.ivSize_;
        }

        public Builder setIvSize(int value) {
            this.ivSize_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearIvSize() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.ivSize_ = 0;
            this.onChanged();
            return this;
        }
    }
}

