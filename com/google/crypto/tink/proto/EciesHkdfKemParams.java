/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EciesAeadHkdf;
import com.google.crypto.tink.proto.EciesHkdfKemParamsOrBuilder;
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

public final class EciesHkdfKemParams
extends GeneratedMessage
implements EciesHkdfKemParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int CURVE_TYPE_FIELD_NUMBER = 1;
    private int curveType_ = 0;
    public static final int HKDF_HASH_TYPE_FIELD_NUMBER = 2;
    private int hkdfHashType_ = 0;
    public static final int HKDF_SALT_FIELD_NUMBER = 11;
    private ByteString hkdfSalt_ = ByteString.EMPTY;
    private byte memoizedIsInitialized = (byte)-1;
    private static final EciesHkdfKemParams DEFAULT_INSTANCE;
    private static final Parser<EciesHkdfKemParams> PARSER;

    private EciesHkdfKemParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private EciesHkdfKemParams() {
        this.curveType_ = 0;
        this.hkdfHashType_ = 0;
        this.hkdfSalt_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return EciesAeadHkdf.internal_static_google_crypto_tink_EciesHkdfKemParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return EciesAeadHkdf.internal_static_google_crypto_tink_EciesHkdfKemParams_fieldAccessorTable.ensureFieldAccessorsInitialized(EciesHkdfKemParams.class, Builder.class);
    }

    @Override
    public int getCurveTypeValue() {
        return this.curveType_;
    }

    @Override
    public EllipticCurveType getCurveType() {
        EllipticCurveType result = EllipticCurveType.forNumber(this.curveType_);
        return result == null ? EllipticCurveType.UNRECOGNIZED : result;
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
    public ByteString getHkdfSalt() {
        return this.hkdfSalt_;
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
        if (this.curveType_ != EllipticCurveType.UNKNOWN_CURVE.getNumber()) {
            output.writeEnum(1, this.curveType_);
        }
        if (this.hkdfHashType_ != HashType.UNKNOWN_HASH.getNumber()) {
            output.writeEnum(2, this.hkdfHashType_);
        }
        if (!this.hkdfSalt_.isEmpty()) {
            output.writeBytes(11, this.hkdfSalt_);
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
        if (this.curveType_ != EllipticCurveType.UNKNOWN_CURVE.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.curveType_);
        }
        if (this.hkdfHashType_ != HashType.UNKNOWN_HASH.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.hkdfHashType_);
        }
        if (!this.hkdfSalt_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(11, this.hkdfSalt_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof EciesHkdfKemParams)) {
            return super.equals(obj);
        }
        EciesHkdfKemParams other = (EciesHkdfKemParams)obj;
        if (this.curveType_ != other.curveType_) {
            return false;
        }
        if (this.hkdfHashType_ != other.hkdfHashType_) {
            return false;
        }
        if (!this.getHkdfSalt().equals(other.getHkdfSalt())) {
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
        hash = 19 * hash + EciesHkdfKemParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.curveType_;
        hash = 37 * hash + 2;
        hash = 53 * hash + this.hkdfHashType_;
        hash = 37 * hash + 11;
        hash = 53 * hash + this.getHkdfSalt().hashCode();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static EciesHkdfKemParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EciesHkdfKemParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EciesHkdfKemParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EciesHkdfKemParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EciesHkdfKemParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EciesHkdfKemParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EciesHkdfKemParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static EciesHkdfKemParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static EciesHkdfKemParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static EciesHkdfKemParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static EciesHkdfKemParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static EciesHkdfKemParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return EciesHkdfKemParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(EciesHkdfKemParams prototype) {
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

    public static EciesHkdfKemParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<EciesHkdfKemParams> parser() {
        return PARSER;
    }

    public Parser<EciesHkdfKemParams> getParserForType() {
        return PARSER;
    }

    @Override
    public EciesHkdfKemParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EciesHkdfKemParams.class.getName());
        DEFAULT_INSTANCE = new EciesHkdfKemParams();
        PARSER = new AbstractParser<EciesHkdfKemParams>(){

            @Override
            public EciesHkdfKemParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = EciesHkdfKemParams.newBuilder();
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
    implements EciesHkdfKemParamsOrBuilder {
        private int bitField0_;
        private int curveType_ = 0;
        private int hkdfHashType_ = 0;
        private ByteString hkdfSalt_ = ByteString.EMPTY;

        public static final Descriptors.Descriptor getDescriptor() {
            return EciesAeadHkdf.internal_static_google_crypto_tink_EciesHkdfKemParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return EciesAeadHkdf.internal_static_google_crypto_tink_EciesHkdfKemParams_fieldAccessorTable.ensureFieldAccessorsInitialized(EciesHkdfKemParams.class, Builder.class);
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
            this.curveType_ = 0;
            this.hkdfHashType_ = 0;
            this.hkdfSalt_ = ByteString.EMPTY;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return EciesAeadHkdf.internal_static_google_crypto_tink_EciesHkdfKemParams_descriptor;
        }

        @Override
        public EciesHkdfKemParams getDefaultInstanceForType() {
            return EciesHkdfKemParams.getDefaultInstance();
        }

        @Override
        public EciesHkdfKemParams build() {
            EciesHkdfKemParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public EciesHkdfKemParams buildPartial() {
            EciesHkdfKemParams result = new EciesHkdfKemParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(EciesHkdfKemParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.curveType_ = this.curveType_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.hkdfHashType_ = this.hkdfHashType_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.hkdfSalt_ = this.hkdfSalt_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof EciesHkdfKemParams) {
                return this.mergeFrom((EciesHkdfKemParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(EciesHkdfKemParams other) {
            if (other == EciesHkdfKemParams.getDefaultInstance()) {
                return this;
            }
            if (other.curveType_ != 0) {
                this.setCurveTypeValue(other.getCurveTypeValue());
            }
            if (other.hkdfHashType_ != 0) {
                this.setHkdfHashTypeValue(other.getHkdfHashTypeValue());
            }
            if (!other.getHkdfSalt().isEmpty()) {
                this.setHkdfSalt(other.getHkdfSalt());
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
                            this.curveType_ = input.readEnum();
                            this.bitField0_ |= 1;
                            continue block11;
                        }
                        case 16: {
                            this.hkdfHashType_ = input.readEnum();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 90: {
                            this.hkdfSalt_ = input.readBytes();
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
        public int getCurveTypeValue() {
            return this.curveType_;
        }

        public Builder setCurveTypeValue(int value) {
            this.curveType_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        @Override
        public EllipticCurveType getCurveType() {
            EllipticCurveType result = EllipticCurveType.forNumber(this.curveType_);
            return result == null ? EllipticCurveType.UNRECOGNIZED : result;
        }

        public Builder setCurveType(EllipticCurveType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 1;
            this.curveType_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearCurveType() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.curveType_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getHkdfHashTypeValue() {
            return this.hkdfHashType_;
        }

        public Builder setHkdfHashTypeValue(int value) {
            this.hkdfHashType_ = value;
            this.bitField0_ |= 2;
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
            this.bitField0_ |= 2;
            this.hkdfHashType_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearHkdfHashType() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.hkdfHashType_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public ByteString getHkdfSalt() {
            return this.hkdfSalt_;
        }

        public Builder setHkdfSalt(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.hkdfSalt_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder clearHkdfSalt() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.hkdfSalt_ = EciesHkdfKemParams.getDefaultInstance().getHkdfSalt();
            this.onChanged();
            return this;
        }
    }
}

