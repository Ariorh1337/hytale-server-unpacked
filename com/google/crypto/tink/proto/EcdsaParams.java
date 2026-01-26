/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Ecdsa;
import com.google.crypto.tink.proto.EcdsaParamsOrBuilder;
import com.google.crypto.tink.proto.EcdsaSignatureEncoding;
import com.google.crypto.tink.proto.EllipticCurveType;
import com.google.crypto.tink.proto.HashType;
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

public final class EcdsaParams
extends GeneratedMessage
implements EcdsaParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int HASH_TYPE_FIELD_NUMBER = 1;
    private int hashType_ = 0;
    public static final int CURVE_FIELD_NUMBER = 2;
    private int curve_ = 0;
    public static final int ENCODING_FIELD_NUMBER = 3;
    private int encoding_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final EcdsaParams DEFAULT_INSTANCE;
    private static final Parser<EcdsaParams> PARSER;

    private EcdsaParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private EcdsaParams() {
        this.hashType_ = 0;
        this.curve_ = 0;
        this.encoding_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_fieldAccessorTable.ensureFieldAccessorsInitialized(EcdsaParams.class, Builder.class);
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
    public int getCurveValue() {
        return this.curve_;
    }

    @Override
    public EllipticCurveType getCurve() {
        EllipticCurveType result = EllipticCurveType.forNumber(this.curve_);
        return result == null ? EllipticCurveType.UNRECOGNIZED : result;
    }

    @Override
    public int getEncodingValue() {
        return this.encoding_;
    }

    @Override
    public EcdsaSignatureEncoding getEncoding() {
        EcdsaSignatureEncoding result = EcdsaSignatureEncoding.forNumber(this.encoding_);
        return result == null ? EcdsaSignatureEncoding.UNRECOGNIZED : result;
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
        if (this.curve_ != EllipticCurveType.UNKNOWN_CURVE.getNumber()) {
            output.writeEnum(2, this.curve_);
        }
        if (this.encoding_ != EcdsaSignatureEncoding.UNKNOWN_ENCODING.getNumber()) {
            output.writeEnum(3, this.encoding_);
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
        if (this.curve_ != EllipticCurveType.UNKNOWN_CURVE.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.curve_);
        }
        if (this.encoding_ != EcdsaSignatureEncoding.UNKNOWN_ENCODING.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.encoding_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof EcdsaParams)) {
            return super.equals(obj);
        }
        EcdsaParams other = (EcdsaParams)obj;
        if (this.hashType_ != other.hashType_) {
            return false;
        }
        if (this.curve_ != other.curve_) {
            return false;
        }
        if (this.encoding_ != other.encoding_) {
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
        hash = 19 * hash + EcdsaParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.hashType_;
        hash = 37 * hash + 2;
        hash = 53 * hash + this.curve_;
        hash = 37 * hash + 3;
        hash = 53 * hash + this.encoding_;
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static EcdsaParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EcdsaParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EcdsaParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EcdsaParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EcdsaParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EcdsaParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EcdsaParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static EcdsaParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static EcdsaParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static EcdsaParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static EcdsaParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static EcdsaParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return EcdsaParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(EcdsaParams prototype) {
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

    public static EcdsaParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<EcdsaParams> parser() {
        return PARSER;
    }

    public Parser<EcdsaParams> getParserForType() {
        return PARSER;
    }

    @Override
    public EcdsaParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EcdsaParams.class.getName());
        DEFAULT_INSTANCE = new EcdsaParams();
        PARSER = new AbstractParser<EcdsaParams>(){

            @Override
            public EcdsaParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = EcdsaParams.newBuilder();
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
    implements EcdsaParamsOrBuilder {
        private int bitField0_;
        private int hashType_ = 0;
        private int curve_ = 0;
        private int encoding_ = 0;

        public static final Descriptors.Descriptor getDescriptor() {
            return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_fieldAccessorTable.ensureFieldAccessorsInitialized(EcdsaParams.class, Builder.class);
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
            this.curve_ = 0;
            this.encoding_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Ecdsa.internal_static_google_crypto_tink_EcdsaParams_descriptor;
        }

        @Override
        public EcdsaParams getDefaultInstanceForType() {
            return EcdsaParams.getDefaultInstance();
        }

        @Override
        public EcdsaParams build() {
            EcdsaParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public EcdsaParams buildPartial() {
            EcdsaParams result = new EcdsaParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(EcdsaParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.hashType_ = this.hashType_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.curve_ = this.curve_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.encoding_ = this.encoding_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof EcdsaParams) {
                return this.mergeFrom((EcdsaParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(EcdsaParams other) {
            if (other == EcdsaParams.getDefaultInstance()) {
                return this;
            }
            if (other.hashType_ != 0) {
                this.setHashTypeValue(other.getHashTypeValue());
            }
            if (other.curve_ != 0) {
                this.setCurveValue(other.getCurveValue());
            }
            if (other.encoding_ != 0) {
                this.setEncodingValue(other.getEncodingValue());
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
                        case 8: {
                            this.hashType_ = input.readEnum();
                            this.bitField0_ |= 1;
                            continue block11;
                        }
                        case 16: {
                            this.curve_ = input.readEnum();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 24: {
                            this.encoding_ = input.readEnum();
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

        @Override
        public int getCurveValue() {
            return this.curve_;
        }

        public Builder setCurveValue(int value) {
            this.curve_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        @Override
        public EllipticCurveType getCurve() {
            EllipticCurveType result = EllipticCurveType.forNumber(this.curve_);
            return result == null ? EllipticCurveType.UNRECOGNIZED : result;
        }

        public Builder setCurve(EllipticCurveType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 2;
            this.curve_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearCurve() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.curve_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getEncodingValue() {
            return this.encoding_;
        }

        public Builder setEncodingValue(int value) {
            this.encoding_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        @Override
        public EcdsaSignatureEncoding getEncoding() {
            EcdsaSignatureEncoding result = EcdsaSignatureEncoding.forNumber(this.encoding_);
            return result == null ? EcdsaSignatureEncoding.UNRECOGNIZED : result;
        }

        public Builder setEncoding(EcdsaSignatureEncoding value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 4;
            this.encoding_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearEncoding() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.encoding_ = 0;
            this.onChanged();
            return this;
        }
    }
}

