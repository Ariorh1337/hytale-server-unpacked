/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.SlhDsa;
import com.google.crypto.tink.proto.SlhDsaHashType;
import com.google.crypto.tink.proto.SlhDsaParamsOrBuilder;
import com.google.crypto.tink.proto.SlhDsaSignatureType;
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

public final class SlhDsaParams
extends GeneratedMessage
implements SlhDsaParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int KEY_SIZE_FIELD_NUMBER = 1;
    private int keySize_ = 0;
    public static final int HASH_TYPE_FIELD_NUMBER = 2;
    private int hashType_ = 0;
    public static final int SIG_TYPE_FIELD_NUMBER = 3;
    private int sigType_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final SlhDsaParams DEFAULT_INSTANCE;
    private static final Parser<SlhDsaParams> PARSER;

    private SlhDsaParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private SlhDsaParams() {
        this.hashType_ = 0;
        this.sigType_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return SlhDsa.internal_static_google_crypto_tink_SlhDsaParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return SlhDsa.internal_static_google_crypto_tink_SlhDsaParams_fieldAccessorTable.ensureFieldAccessorsInitialized(SlhDsaParams.class, Builder.class);
    }

    @Override
    public int getKeySize() {
        return this.keySize_;
    }

    @Override
    public int getHashTypeValue() {
        return this.hashType_;
    }

    @Override
    public SlhDsaHashType getHashType() {
        SlhDsaHashType result = SlhDsaHashType.forNumber(this.hashType_);
        return result == null ? SlhDsaHashType.UNRECOGNIZED : result;
    }

    @Override
    public int getSigTypeValue() {
        return this.sigType_;
    }

    @Override
    public SlhDsaSignatureType getSigType() {
        SlhDsaSignatureType result = SlhDsaSignatureType.forNumber(this.sigType_);
        return result == null ? SlhDsaSignatureType.UNRECOGNIZED : result;
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
            output.writeInt32(1, this.keySize_);
        }
        if (this.hashType_ != SlhDsaHashType.SLH_DSA_HASH_TYPE_UNSPECIFIED.getNumber()) {
            output.writeEnum(2, this.hashType_);
        }
        if (this.sigType_ != SlhDsaSignatureType.SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED.getNumber()) {
            output.writeEnum(3, this.sigType_);
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
            size += CodedOutputStream.computeInt32Size(1, this.keySize_);
        }
        if (this.hashType_ != SlhDsaHashType.SLH_DSA_HASH_TYPE_UNSPECIFIED.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.hashType_);
        }
        if (this.sigType_ != SlhDsaSignatureType.SLH_DSA_SIGNATURE_TYPE_UNSPECIFIED.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.sigType_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SlhDsaParams)) {
            return super.equals(obj);
        }
        SlhDsaParams other = (SlhDsaParams)obj;
        if (this.getKeySize() != other.getKeySize()) {
            return false;
        }
        if (this.hashType_ != other.hashType_) {
            return false;
        }
        if (this.sigType_ != other.sigType_) {
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
        hash = 19 * hash + SlhDsaParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getKeySize();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.hashType_;
        hash = 37 * hash + 3;
        hash = 53 * hash + this.sigType_;
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static SlhDsaParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static SlhDsaParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static SlhDsaParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static SlhDsaParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static SlhDsaParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static SlhDsaParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static SlhDsaParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static SlhDsaParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static SlhDsaParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static SlhDsaParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static SlhDsaParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static SlhDsaParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return SlhDsaParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(SlhDsaParams prototype) {
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

    public static SlhDsaParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<SlhDsaParams> parser() {
        return PARSER;
    }

    public Parser<SlhDsaParams> getParserForType() {
        return PARSER;
    }

    @Override
    public SlhDsaParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", SlhDsaParams.class.getName());
        DEFAULT_INSTANCE = new SlhDsaParams();
        PARSER = new AbstractParser<SlhDsaParams>(){

            @Override
            public SlhDsaParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = SlhDsaParams.newBuilder();
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
    implements SlhDsaParamsOrBuilder {
        private int bitField0_;
        private int keySize_;
        private int hashType_ = 0;
        private int sigType_ = 0;

        public static final Descriptors.Descriptor getDescriptor() {
            return SlhDsa.internal_static_google_crypto_tink_SlhDsaParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return SlhDsa.internal_static_google_crypto_tink_SlhDsaParams_fieldAccessorTable.ensureFieldAccessorsInitialized(SlhDsaParams.class, Builder.class);
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
            this.hashType_ = 0;
            this.sigType_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return SlhDsa.internal_static_google_crypto_tink_SlhDsaParams_descriptor;
        }

        @Override
        public SlhDsaParams getDefaultInstanceForType() {
            return SlhDsaParams.getDefaultInstance();
        }

        @Override
        public SlhDsaParams build() {
            SlhDsaParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public SlhDsaParams buildPartial() {
            SlhDsaParams result = new SlhDsaParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(SlhDsaParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.keySize_ = this.keySize_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.hashType_ = this.hashType_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.sigType_ = this.sigType_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof SlhDsaParams) {
                return this.mergeFrom((SlhDsaParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(SlhDsaParams other) {
            if (other == SlhDsaParams.getDefaultInstance()) {
                return this;
            }
            if (other.getKeySize() != 0) {
                this.setKeySize(other.getKeySize());
            }
            if (other.hashType_ != 0) {
                this.setHashTypeValue(other.getHashTypeValue());
            }
            if (other.sigType_ != 0) {
                this.setSigTypeValue(other.getSigTypeValue());
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
                            this.keySize_ = input.readInt32();
                            this.bitField0_ |= 1;
                            continue block11;
                        }
                        case 16: {
                            this.hashType_ = input.readEnum();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 24: {
                            this.sigType_ = input.readEnum();
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
        public int getHashTypeValue() {
            return this.hashType_;
        }

        public Builder setHashTypeValue(int value) {
            this.hashType_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        @Override
        public SlhDsaHashType getHashType() {
            SlhDsaHashType result = SlhDsaHashType.forNumber(this.hashType_);
            return result == null ? SlhDsaHashType.UNRECOGNIZED : result;
        }

        public Builder setHashType(SlhDsaHashType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 2;
            this.hashType_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearHashType() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.hashType_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getSigTypeValue() {
            return this.sigType_;
        }

        public Builder setSigTypeValue(int value) {
            this.sigType_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        @Override
        public SlhDsaSignatureType getSigType() {
            SlhDsaSignatureType result = SlhDsaSignatureType.forNumber(this.sigType_);
            return result == null ? SlhDsaSignatureType.UNRECOGNIZED : result;
        }

        public Builder setSigType(SlhDsaSignatureType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 4;
            this.sigType_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearSigType() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.sigType_ = 0;
            this.onChanged();
            return this;
        }
    }
}

