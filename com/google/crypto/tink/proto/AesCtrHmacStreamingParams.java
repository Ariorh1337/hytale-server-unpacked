/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCtrHmacStreaming;
import com.google.crypto.tink.proto.AesCtrHmacStreamingParamsOrBuilder;
import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.HmacParams;
import com.google.crypto.tink.proto.HmacParamsOrBuilder;
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

public final class AesCtrHmacStreamingParams
extends GeneratedMessage
implements AesCtrHmacStreamingParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int CIPHERTEXT_SEGMENT_SIZE_FIELD_NUMBER = 1;
    private int ciphertextSegmentSize_ = 0;
    public static final int DERIVED_KEY_SIZE_FIELD_NUMBER = 2;
    private int derivedKeySize_ = 0;
    public static final int HKDF_HASH_TYPE_FIELD_NUMBER = 3;
    private int hkdfHashType_ = 0;
    public static final int HMAC_PARAMS_FIELD_NUMBER = 4;
    private HmacParams hmacParams_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final AesCtrHmacStreamingParams DEFAULT_INSTANCE;
    private static final Parser<AesCtrHmacStreamingParams> PARSER;

    private AesCtrHmacStreamingParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private AesCtrHmacStreamingParams() {
        this.hkdfHashType_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_fieldAccessorTable.ensureFieldAccessorsInitialized(AesCtrHmacStreamingParams.class, Builder.class);
    }

    @Override
    public int getCiphertextSegmentSize() {
        return this.ciphertextSegmentSize_;
    }

    @Override
    public int getDerivedKeySize() {
        return this.derivedKeySize_;
    }

    @Override
    public int getHkdfHashTypeValue() {
        return this.hkdfHashType_;
    }

    @Override
    public HashType getHkdfHashType() {
        HashType result = HashType.forNumber(this.hkdfHashType_);
        return result == null ? HashType.UNRECOGNIZED : result;
    }

    @Override
    public boolean hasHmacParams() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public HmacParams getHmacParams() {
        return this.hmacParams_ == null ? HmacParams.getDefaultInstance() : this.hmacParams_;
    }

    @Override
    public HmacParamsOrBuilder getHmacParamsOrBuilder() {
        return this.hmacParams_ == null ? HmacParams.getDefaultInstance() : this.hmacParams_;
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
        if (this.ciphertextSegmentSize_ != 0) {
            output.writeUInt32(1, this.ciphertextSegmentSize_);
        }
        if (this.derivedKeySize_ != 0) {
            output.writeUInt32(2, this.derivedKeySize_);
        }
        if (this.hkdfHashType_ != HashType.UNKNOWN_HASH.getNumber()) {
            output.writeEnum(3, this.hkdfHashType_);
        }
        if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(4, this.getHmacParams());
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
        if (this.ciphertextSegmentSize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.ciphertextSegmentSize_);
        }
        if (this.derivedKeySize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.derivedKeySize_);
        }
        if (this.hkdfHashType_ != HashType.UNKNOWN_HASH.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.hkdfHashType_);
        }
        if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(4, this.getHmacParams());
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AesCtrHmacStreamingParams)) {
            return super.equals(obj);
        }
        AesCtrHmacStreamingParams other = (AesCtrHmacStreamingParams)obj;
        if (this.getCiphertextSegmentSize() != other.getCiphertextSegmentSize()) {
            return false;
        }
        if (this.getDerivedKeySize() != other.getDerivedKeySize()) {
            return false;
        }
        if (this.hkdfHashType_ != other.hkdfHashType_) {
            return false;
        }
        if (this.hasHmacParams() != other.hasHmacParams()) {
            return false;
        }
        if (this.hasHmacParams() && !this.getHmacParams().equals(other.getHmacParams())) {
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
        hash = 19 * hash + AesCtrHmacStreamingParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getCiphertextSegmentSize();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getDerivedKeySize();
        hash = 37 * hash + 3;
        hash = 53 * hash + this.hkdfHashType_;
        if (this.hasHmacParams()) {
            hash = 37 * hash + 4;
            hash = 53 * hash + this.getHmacParams().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static AesCtrHmacStreamingParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrHmacStreamingParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrHmacStreamingParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrHmacStreamingParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrHmacStreamingParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrHmacStreamingParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrHmacStreamingParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesCtrHmacStreamingParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesCtrHmacStreamingParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static AesCtrHmacStreamingParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesCtrHmacStreamingParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesCtrHmacStreamingParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return AesCtrHmacStreamingParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(AesCtrHmacStreamingParams prototype) {
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

    public static AesCtrHmacStreamingParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<AesCtrHmacStreamingParams> parser() {
        return PARSER;
    }

    public Parser<AesCtrHmacStreamingParams> getParserForType() {
        return PARSER;
    }

    @Override
    public AesCtrHmacStreamingParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCtrHmacStreamingParams.class.getName());
        DEFAULT_INSTANCE = new AesCtrHmacStreamingParams();
        PARSER = new AbstractParser<AesCtrHmacStreamingParams>(){

            @Override
            public AesCtrHmacStreamingParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = AesCtrHmacStreamingParams.newBuilder();
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
    implements AesCtrHmacStreamingParamsOrBuilder {
        private int bitField0_;
        private int ciphertextSegmentSize_;
        private int derivedKeySize_;
        private int hkdfHashType_ = 0;
        private HmacParams hmacParams_;
        private SingleFieldBuilder<HmacParams, HmacParams.Builder, HmacParamsOrBuilder> hmacParamsBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_fieldAccessorTable.ensureFieldAccessorsInitialized(AesCtrHmacStreamingParams.class, Builder.class);
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
                this.internalGetHmacParamsFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.ciphertextSegmentSize_ = 0;
            this.derivedKeySize_ = 0;
            this.hkdfHashType_ = 0;
            this.hmacParams_ = null;
            if (this.hmacParamsBuilder_ != null) {
                this.hmacParamsBuilder_.dispose();
                this.hmacParamsBuilder_ = null;
            }
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return AesCtrHmacStreaming.internal_static_google_crypto_tink_AesCtrHmacStreamingParams_descriptor;
        }

        @Override
        public AesCtrHmacStreamingParams getDefaultInstanceForType() {
            return AesCtrHmacStreamingParams.getDefaultInstance();
        }

        @Override
        public AesCtrHmacStreamingParams build() {
            AesCtrHmacStreamingParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public AesCtrHmacStreamingParams buildPartial() {
            AesCtrHmacStreamingParams result = new AesCtrHmacStreamingParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(AesCtrHmacStreamingParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.ciphertextSegmentSize_ = this.ciphertextSegmentSize_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.derivedKeySize_ = this.derivedKeySize_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.hkdfHashType_ = this.hkdfHashType_;
            }
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 8) != 0) {
                result.hmacParams_ = this.hmacParamsBuilder_ == null ? this.hmacParams_ : this.hmacParamsBuilder_.build();
                to_bitField0_ |= 1;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof AesCtrHmacStreamingParams) {
                return this.mergeFrom((AesCtrHmacStreamingParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(AesCtrHmacStreamingParams other) {
            if (other == AesCtrHmacStreamingParams.getDefaultInstance()) {
                return this;
            }
            if (other.getCiphertextSegmentSize() != 0) {
                this.setCiphertextSegmentSize(other.getCiphertextSegmentSize());
            }
            if (other.getDerivedKeySize() != 0) {
                this.setDerivedKeySize(other.getDerivedKeySize());
            }
            if (other.hkdfHashType_ != 0) {
                this.setHkdfHashTypeValue(other.getHkdfHashTypeValue());
            }
            if (other.hasHmacParams()) {
                this.mergeHmacParams(other.getHmacParams());
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
                block12: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block12;
                        }
                        case 8: {
                            this.ciphertextSegmentSize_ = input.readUInt32();
                            this.bitField0_ |= 1;
                            continue block12;
                        }
                        case 16: {
                            this.derivedKeySize_ = input.readUInt32();
                            this.bitField0_ |= 2;
                            continue block12;
                        }
                        case 24: {
                            this.hkdfHashType_ = input.readEnum();
                            this.bitField0_ |= 4;
                            continue block12;
                        }
                        case 34: {
                            input.readMessage(this.internalGetHmacParamsFieldBuilder().getBuilder(), extensionRegistry);
                            this.bitField0_ |= 8;
                            continue block12;
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
        public int getCiphertextSegmentSize() {
            return this.ciphertextSegmentSize_;
        }

        public Builder setCiphertextSegmentSize(int value) {
            this.ciphertextSegmentSize_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearCiphertextSegmentSize() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.ciphertextSegmentSize_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getDerivedKeySize() {
            return this.derivedKeySize_;
        }

        public Builder setDerivedKeySize(int value) {
            this.derivedKeySize_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearDerivedKeySize() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.derivedKeySize_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getHkdfHashTypeValue() {
            return this.hkdfHashType_;
        }

        public Builder setHkdfHashTypeValue(int value) {
            this.hkdfHashType_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        @Override
        public HashType getHkdfHashType() {
            HashType result = HashType.forNumber(this.hkdfHashType_);
            return result == null ? HashType.UNRECOGNIZED : result;
        }

        public Builder setHkdfHashType(HashType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 4;
            this.hkdfHashType_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearHkdfHashType() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.hkdfHashType_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public boolean hasHmacParams() {
            return (this.bitField0_ & 8) != 0;
        }

        @Override
        public HmacParams getHmacParams() {
            if (this.hmacParamsBuilder_ == null) {
                return this.hmacParams_ == null ? HmacParams.getDefaultInstance() : this.hmacParams_;
            }
            return this.hmacParamsBuilder_.getMessage();
        }

        public Builder setHmacParams(HmacParams value) {
            if (this.hmacParamsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.hmacParams_ = value;
            } else {
                this.hmacParamsBuilder_.setMessage(value);
            }
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
        }

        public Builder setHmacParams(HmacParams.Builder builderForValue) {
            if (this.hmacParamsBuilder_ == null) {
                this.hmacParams_ = builderForValue.build();
            } else {
                this.hmacParamsBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
        }

        public Builder mergeHmacParams(HmacParams value) {
            if (this.hmacParamsBuilder_ == null) {
                if ((this.bitField0_ & 8) != 0 && this.hmacParams_ != null && this.hmacParams_ != HmacParams.getDefaultInstance()) {
                    this.getHmacParamsBuilder().mergeFrom(value);
                } else {
                    this.hmacParams_ = value;
                }
            } else {
                this.hmacParamsBuilder_.mergeFrom(value);
            }
            if (this.hmacParams_ != null) {
                this.bitField0_ |= 8;
                this.onChanged();
            }
            return this;
        }

        public Builder clearHmacParams() {
            this.bitField0_ &= 0xFFFFFFF7;
            this.hmacParams_ = null;
            if (this.hmacParamsBuilder_ != null) {
                this.hmacParamsBuilder_.dispose();
                this.hmacParamsBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public HmacParams.Builder getHmacParamsBuilder() {
            this.bitField0_ |= 8;
            this.onChanged();
            return this.internalGetHmacParamsFieldBuilder().getBuilder();
        }

        @Override
        public HmacParamsOrBuilder getHmacParamsOrBuilder() {
            if (this.hmacParamsBuilder_ != null) {
                return this.hmacParamsBuilder_.getMessageOrBuilder();
            }
            return this.hmacParams_ == null ? HmacParams.getDefaultInstance() : this.hmacParams_;
        }

        private SingleFieldBuilder<HmacParams, HmacParams.Builder, HmacParamsOrBuilder> internalGetHmacParamsFieldBuilder() {
            if (this.hmacParamsBuilder_ == null) {
                this.hmacParamsBuilder_ = new SingleFieldBuilder(this.getHmacParams(), this.getParentForChildren(), this.isClean());
                this.hmacParams_ = null;
            }
            return this.hmacParamsBuilder_;
        }
    }
}

