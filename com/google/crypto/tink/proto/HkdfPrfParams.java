/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.HkdfPrf;
import com.google.crypto.tink.proto.HkdfPrfParamsOrBuilder;
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

public final class HkdfPrfParams
extends GeneratedMessage
implements HkdfPrfParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int HASH_FIELD_NUMBER = 1;
    private int hash_ = 0;
    public static final int SALT_FIELD_NUMBER = 2;
    private ByteString salt_ = ByteString.EMPTY;
    private byte memoizedIsInitialized = (byte)-1;
    private static final HkdfPrfParams DEFAULT_INSTANCE;
    private static final Parser<HkdfPrfParams> PARSER;

    private HkdfPrfParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private HkdfPrfParams() {
        this.hash_ = 0;
        this.salt_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return HkdfPrf.internal_static_google_crypto_tink_HkdfPrfParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return HkdfPrf.internal_static_google_crypto_tink_HkdfPrfParams_fieldAccessorTable.ensureFieldAccessorsInitialized(HkdfPrfParams.class, Builder.class);
    }

    @Override
    public int getHashValue() {
        return this.hash_;
    }

    @Override
    public HashType getHash() {
        HashType result = HashType.forNumber(this.hash_);
        return result == null ? HashType.UNRECOGNIZED : result;
    }

    @Override
    public ByteString getSalt() {
        return this.salt_;
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
        if (this.hash_ != HashType.UNKNOWN_HASH.getNumber()) {
            output.writeEnum(1, this.hash_);
        }
        if (!this.salt_.isEmpty()) {
            output.writeBytes(2, this.salt_);
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
        if (this.hash_ != HashType.UNKNOWN_HASH.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.hash_);
        }
        if (!this.salt_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(2, this.salt_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HkdfPrfParams)) {
            return super.equals(obj);
        }
        HkdfPrfParams other = (HkdfPrfParams)obj;
        if (this.hash_ != other.hash_) {
            return false;
        }
        if (!this.getSalt().equals(other.getSalt())) {
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
        hash = 19 * hash + HkdfPrfParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.hash_;
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getSalt().hashCode();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static HkdfPrfParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HkdfPrfParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HkdfPrfParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HkdfPrfParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HkdfPrfParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HkdfPrfParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HkdfPrfParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static HkdfPrfParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static HkdfPrfParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static HkdfPrfParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static HkdfPrfParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static HkdfPrfParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return HkdfPrfParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(HkdfPrfParams prototype) {
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

    public static HkdfPrfParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<HkdfPrfParams> parser() {
        return PARSER;
    }

    public Parser<HkdfPrfParams> getParserForType() {
        return PARSER;
    }

    @Override
    public HkdfPrfParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HkdfPrfParams.class.getName());
        DEFAULT_INSTANCE = new HkdfPrfParams();
        PARSER = new AbstractParser<HkdfPrfParams>(){

            @Override
            public HkdfPrfParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = HkdfPrfParams.newBuilder();
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
    implements HkdfPrfParamsOrBuilder {
        private int bitField0_;
        private int hash_ = 0;
        private ByteString salt_ = ByteString.EMPTY;

        public static final Descriptors.Descriptor getDescriptor() {
            return HkdfPrf.internal_static_google_crypto_tink_HkdfPrfParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return HkdfPrf.internal_static_google_crypto_tink_HkdfPrfParams_fieldAccessorTable.ensureFieldAccessorsInitialized(HkdfPrfParams.class, Builder.class);
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
            this.hash_ = 0;
            this.salt_ = ByteString.EMPTY;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return HkdfPrf.internal_static_google_crypto_tink_HkdfPrfParams_descriptor;
        }

        @Override
        public HkdfPrfParams getDefaultInstanceForType() {
            return HkdfPrfParams.getDefaultInstance();
        }

        @Override
        public HkdfPrfParams build() {
            HkdfPrfParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public HkdfPrfParams buildPartial() {
            HkdfPrfParams result = new HkdfPrfParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(HkdfPrfParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.hash_ = this.hash_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.salt_ = this.salt_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof HkdfPrfParams) {
                return this.mergeFrom((HkdfPrfParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(HkdfPrfParams other) {
            if (other == HkdfPrfParams.getDefaultInstance()) {
                return this;
            }
            if (other.hash_ != 0) {
                this.setHashValue(other.getHashValue());
            }
            if (!other.getSalt().isEmpty()) {
                this.setSalt(other.getSalt());
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
                            this.hash_ = input.readEnum();
                            this.bitField0_ |= 1;
                            continue block10;
                        }
                        case 18: {
                            this.salt_ = input.readBytes();
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
        public int getHashValue() {
            return this.hash_;
        }

        public Builder setHashValue(int value) {
            this.hash_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        @Override
        public HashType getHash() {
            HashType result = HashType.forNumber(this.hash_);
            return result == null ? HashType.UNRECOGNIZED : result;
        }

        public Builder setHash(HashType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 1;
            this.hash_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearHash() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.hash_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public ByteString getSalt() {
            return this.salt_;
        }

        public Builder setSalt(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.salt_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearSalt() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.salt_ = HkdfPrfParams.getDefaultInstance().getSalt();
            this.onChanged();
            return this;
        }
    }
}

