/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.RsaSsaPkcs1;
import com.google.crypto.tink.proto.RsaSsaPkcs1KeyFormatOrBuilder;
import com.google.crypto.tink.proto.RsaSsaPkcs1Params;
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
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class RsaSsaPkcs1KeyFormat
extends GeneratedMessage
implements RsaSsaPkcs1KeyFormatOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int PARAMS_FIELD_NUMBER = 1;
    private RsaSsaPkcs1Params params_;
    public static final int MODULUS_SIZE_IN_BITS_FIELD_NUMBER = 2;
    private int modulusSizeInBits_ = 0;
    public static final int PUBLIC_EXPONENT_FIELD_NUMBER = 3;
    private ByteString publicExponent_ = ByteString.EMPTY;
    private byte memoizedIsInitialized = (byte)-1;
    private static final RsaSsaPkcs1KeyFormat DEFAULT_INSTANCE;
    private static final Parser<RsaSsaPkcs1KeyFormat> PARSER;

    private RsaSsaPkcs1KeyFormat(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private RsaSsaPkcs1KeyFormat() {
        this.publicExponent_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1KeyFormat_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1KeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(RsaSsaPkcs1KeyFormat.class, Builder.class);
    }

    @Override
    public boolean hasParams() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public RsaSsaPkcs1Params getParams() {
        return this.params_ == null ? RsaSsaPkcs1Params.getDefaultInstance() : this.params_;
    }

    @Override
    public RsaSsaPkcs1ParamsOrBuilder getParamsOrBuilder() {
        return this.params_ == null ? RsaSsaPkcs1Params.getDefaultInstance() : this.params_;
    }

    @Override
    public int getModulusSizeInBits() {
        return this.modulusSizeInBits_;
    }

    @Override
    public ByteString getPublicExponent() {
        return this.publicExponent_;
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
        if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getParams());
        }
        if (this.modulusSizeInBits_ != 0) {
            output.writeUInt32(2, this.modulusSizeInBits_);
        }
        if (!this.publicExponent_.isEmpty()) {
            output.writeBytes(3, this.publicExponent_);
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
        if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(1, this.getParams());
        }
        if (this.modulusSizeInBits_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.modulusSizeInBits_);
        }
        if (!this.publicExponent_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.publicExponent_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RsaSsaPkcs1KeyFormat)) {
            return super.equals(obj);
        }
        RsaSsaPkcs1KeyFormat other = (RsaSsaPkcs1KeyFormat)obj;
        if (this.hasParams() != other.hasParams()) {
            return false;
        }
        if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
        }
        if (this.getModulusSizeInBits() != other.getModulusSizeInBits()) {
            return false;
        }
        if (!this.getPublicExponent().equals(other.getPublicExponent())) {
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
        hash = 19 * hash + RsaSsaPkcs1KeyFormat.getDescriptor().hashCode();
        if (this.hasParams()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getParams().hashCode();
        }
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getModulusSizeInBits();
        hash = 37 * hash + 3;
        hash = 53 * hash + this.getPublicExponent().hashCode();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static RsaSsaPkcs1KeyFormat parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static RsaSsaPkcs1KeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static RsaSsaPkcs1KeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return RsaSsaPkcs1KeyFormat.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(RsaSsaPkcs1KeyFormat prototype) {
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

    public static RsaSsaPkcs1KeyFormat getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<RsaSsaPkcs1KeyFormat> parser() {
        return PARSER;
    }

    public Parser<RsaSsaPkcs1KeyFormat> getParserForType() {
        return PARSER;
    }

    @Override
    public RsaSsaPkcs1KeyFormat getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", RsaSsaPkcs1KeyFormat.class.getName());
        DEFAULT_INSTANCE = new RsaSsaPkcs1KeyFormat();
        PARSER = new AbstractParser<RsaSsaPkcs1KeyFormat>(){

            @Override
            public RsaSsaPkcs1KeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = RsaSsaPkcs1KeyFormat.newBuilder();
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
    implements RsaSsaPkcs1KeyFormatOrBuilder {
        private int bitField0_;
        private RsaSsaPkcs1Params params_;
        private SingleFieldBuilder<RsaSsaPkcs1Params, RsaSsaPkcs1Params.Builder, RsaSsaPkcs1ParamsOrBuilder> paramsBuilder_;
        private int modulusSizeInBits_;
        private ByteString publicExponent_ = ByteString.EMPTY;

        public static final Descriptors.Descriptor getDescriptor() {
            return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1KeyFormat_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1KeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(RsaSsaPkcs1KeyFormat.class, Builder.class);
        }

        private Builder() {
            this.maybeForceBuilderInitialization();
        }

        private Builder(AbstractMessage.BuilderParent parent) {
            super(parent);
            this.maybeForceBuilderInitialization();
        }

        private void maybeForceBuilderInitialization() {
            if (alwaysUseFieldBuilders) {
                this.internalGetParamsFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.params_ = null;
            if (this.paramsBuilder_ != null) {
                this.paramsBuilder_.dispose();
                this.paramsBuilder_ = null;
            }
            this.modulusSizeInBits_ = 0;
            this.publicExponent_ = ByteString.EMPTY;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return RsaSsaPkcs1.internal_static_google_crypto_tink_RsaSsaPkcs1KeyFormat_descriptor;
        }

        @Override
        public RsaSsaPkcs1KeyFormat getDefaultInstanceForType() {
            return RsaSsaPkcs1KeyFormat.getDefaultInstance();
        }

        @Override
        public RsaSsaPkcs1KeyFormat build() {
            RsaSsaPkcs1KeyFormat result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public RsaSsaPkcs1KeyFormat buildPartial() {
            RsaSsaPkcs1KeyFormat result = new RsaSsaPkcs1KeyFormat(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(RsaSsaPkcs1KeyFormat result) {
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
                result.params_ = this.paramsBuilder_ == null ? this.params_ : this.paramsBuilder_.build();
                to_bitField0_ |= 1;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.modulusSizeInBits_ = this.modulusSizeInBits_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.publicExponent_ = this.publicExponent_;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof RsaSsaPkcs1KeyFormat) {
                return this.mergeFrom((RsaSsaPkcs1KeyFormat)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(RsaSsaPkcs1KeyFormat other) {
            if (other == RsaSsaPkcs1KeyFormat.getDefaultInstance()) {
                return this;
            }
            if (other.hasParams()) {
                this.mergeParams(other.getParams());
            }
            if (other.getModulusSizeInBits() != 0) {
                this.setModulusSizeInBits(other.getModulusSizeInBits());
            }
            if (!other.getPublicExponent().isEmpty()) {
                this.setPublicExponent(other.getPublicExponent());
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
                block11: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block11;
                        }
                        case 10: {
                            input.readMessage(this.internalGetParamsFieldBuilder().getBuilder(), extensionRegistry);
                            this.bitField0_ |= 1;
                            continue block11;
                        }
                        case 16: {
                            this.modulusSizeInBits_ = input.readUInt32();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 26: {
                            this.publicExponent_ = input.readBytes();
                            this.bitField0_ |= 4;
                            continue block11;
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
        public boolean hasParams() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public RsaSsaPkcs1Params getParams() {
            if (this.paramsBuilder_ == null) {
                return this.params_ == null ? RsaSsaPkcs1Params.getDefaultInstance() : this.params_;
            }
            return this.paramsBuilder_.getMessage();
        }

        public Builder setParams(RsaSsaPkcs1Params value) {
            if (this.paramsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.params_ = value;
            } else {
                this.paramsBuilder_.setMessage(value);
            }
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder setParams(RsaSsaPkcs1Params.Builder builderForValue) {
            if (this.paramsBuilder_ == null) {
                this.params_ = builderForValue.build();
            } else {
                this.paramsBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder mergeParams(RsaSsaPkcs1Params value) {
            if (this.paramsBuilder_ == null) {
                if ((this.bitField0_ & 1) != 0 && this.params_ != null && this.params_ != RsaSsaPkcs1Params.getDefaultInstance()) {
                    this.getParamsBuilder().mergeFrom(value);
                } else {
                    this.params_ = value;
                }
            } else {
                this.paramsBuilder_.mergeFrom(value);
            }
            if (this.params_ != null) {
                this.bitField0_ |= 1;
                this.onChanged();
            }
            return this;
        }

        public Builder clearParams() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.params_ = null;
            if (this.paramsBuilder_ != null) {
                this.paramsBuilder_.dispose();
                this.paramsBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public RsaSsaPkcs1Params.Builder getParamsBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.internalGetParamsFieldBuilder().getBuilder();
        }

        @Override
        public RsaSsaPkcs1ParamsOrBuilder getParamsOrBuilder() {
            if (this.paramsBuilder_ != null) {
                return this.paramsBuilder_.getMessageOrBuilder();
            }
            return this.params_ == null ? RsaSsaPkcs1Params.getDefaultInstance() : this.params_;
        }

        private SingleFieldBuilder<RsaSsaPkcs1Params, RsaSsaPkcs1Params.Builder, RsaSsaPkcs1ParamsOrBuilder> internalGetParamsFieldBuilder() {
            if (this.paramsBuilder_ == null) {
                this.paramsBuilder_ = new SingleFieldBuilder(this.getParams(), this.getParentForChildren(), this.isClean());
                this.params_ = null;
            }
            return this.paramsBuilder_;
        }

        @Override
        public int getModulusSizeInBits() {
            return this.modulusSizeInBits_;
        }

        public Builder setModulusSizeInBits(int value) {
            this.modulusSizeInBits_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearModulusSizeInBits() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.modulusSizeInBits_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public ByteString getPublicExponent() {
            return this.publicExponent_;
        }

        public Builder setPublicExponent(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.publicExponent_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder clearPublicExponent() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.publicExponent_ = RsaSsaPkcs1KeyFormat.getDefaultInstance().getPublicExponent();
            this.onChanged();
            return this;
        }
    }
}

