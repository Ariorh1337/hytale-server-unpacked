/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.RsaSsaPkcs1;
import com.google.crypto.tink.proto.RsaSsaPkcs1ParamsOrBuilder;
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

public final class RsaSsaPkcs1Params
extends GeneratedMessage
implements RsaSsaPkcs1ParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int HASH_TYPE_FIELD_NUMBER = 1;
    private int hashType_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final RsaSsaPkcs1Params DEFAULT_INSTANCE;
    private static final Parser<RsaSsaPkcs1Params> PARSER;

    private RsaSsaPkcs1Params(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private RsaSsaPkcs1Params() {
        this.hashType_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1Params_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1Params_fieldAccessorTable.ensureFieldAccessorsInitialized(RsaSsaPkcs1Params.class, Builder.class);
    }

    @Override
    public int getHashTypeValue() {
        return this.hashType_;
    }

    @Override
    public HashType getHashType() {
        HashType result = HashType.forNumber(this.hashType_);
        return result == null ? HashType.UNRECOGNIZED : result;
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
        if (this.hashType_ != HashType.UNKNOWN_HASH.getNumber()) {
            output.writeEnum(1, this.hashType_);
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
        if (this.hashType_ != HashType.UNKNOWN_HASH.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.hashType_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RsaSsaPkcs1Params)) {
            return super.equals(obj);
        }
        RsaSsaPkcs1Params other = (RsaSsaPkcs1Params)obj;
        if (this.hashType_ != other.hashType_) {
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
        hash = 19 * hash + RsaSsaPkcs1Params.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.hashType_;
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static RsaSsaPkcs1Params parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPkcs1Params parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPkcs1Params parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPkcs1Params parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPkcs1Params parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPkcs1Params parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPkcs1Params parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static RsaSsaPkcs1Params parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static RsaSsaPkcs1Params parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static RsaSsaPkcs1Params parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static RsaSsaPkcs1Params parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static RsaSsaPkcs1Params parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return RsaSsaPkcs1Params.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(RsaSsaPkcs1Params prototype) {
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

    public static RsaSsaPkcs1Params getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<RsaSsaPkcs1Params> parser() {
        return PARSER;
    }

    public Parser<RsaSsaPkcs1Params> getParserForType() {
        return PARSER;
    }

    @Override
    public RsaSsaPkcs1Params getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", RsaSsaPkcs1Params.class.getName());
        DEFAULT_INSTANCE = new RsaSsaPkcs1Params();
        PARSER = new AbstractParser<RsaSsaPkcs1Params>(){

            @Override
            public RsaSsaPkcs1Params parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = RsaSsaPkcs1Params.newBuilder();
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
    implements RsaSsaPkcs1ParamsOrBuilder {
        private int bitField0_;
        private int hashType_ = 0;

        public static final Descriptors.Descriptor getDescriptor() {
            return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1Params_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1Params_fieldAccessorTable.ensureFieldAccessorsInitialized(RsaSsaPkcs1Params.class, Builder.class);
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
            this.hashType_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1Params_descriptor;
        }

        @Override
        public RsaSsaPkcs1Params getDefaultInstanceForType() {
            return RsaSsaPkcs1Params.getDefaultInstance();
        }

        @Override
        public RsaSsaPkcs1Params build() {
            RsaSsaPkcs1Params result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public RsaSsaPkcs1Params buildPartial() {
            RsaSsaPkcs1Params result = new RsaSsaPkcs1Params(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(RsaSsaPkcs1Params result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.hashType_ = this.hashType_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof RsaSsaPkcs1Params) {
                return this.mergeFrom((RsaSsaPkcs1Params)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(RsaSsaPkcs1Params other) {
            if (other == RsaSsaPkcs1Params.getDefaultInstance()) {
                return this;
            }
            if (other.hashType_ != 0) {
                this.setHashTypeValue(other.getHashTypeValue());
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
                            this.hashType_ = input.readEnum();
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
        public int getHashTypeValue() {
            return this.hashType_;
        }

        public Builder setHashTypeValue(int value) {
            this.hashType_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        @Override
        public HashType getHashType() {
            HashType result = HashType.forNumber(this.hashType_);
            return result == null ? HashType.UNRECOGNIZED : result;
        }

        public Builder setHashType(HashType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 1;
            this.hashType_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearHashType() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.hashType_ = 0;
            this.onChanged();
            return this;
        }
    }
}

