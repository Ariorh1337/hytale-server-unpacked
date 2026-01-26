/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.HashType;
import com.google.crypto.tink.proto.Hmac;
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
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class HmacParams
extends GeneratedMessage
implements HmacParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int HASH_FIELD_NUMBER = 1;
    private int hash_ = 0;
    public static final int TAG_SIZE_FIELD_NUMBER = 2;
    private int tagSize_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final HmacParams DEFAULT_INSTANCE;
    private static final Parser<HmacParams> PARSER;

    private HmacParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private HmacParams() {
        this.hash_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Hmac.internal_static_google_crypto_tink_HmacParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Hmac.internal_static_google_crypto_tink_HmacParams_fieldAccessorTable.ensureFieldAccessorsInitialized(HmacParams.class, Builder.class);
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
    public int getTagSize() {
        return this.tagSize_;
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
        if (this.tagSize_ != 0) {
            output.writeUInt32(2, this.tagSize_);
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
        if (this.tagSize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(2, this.tagSize_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HmacParams)) {
            return super.equals(obj);
        }
        HmacParams other = (HmacParams)obj;
        if (this.hash_ != other.hash_) {
            return false;
        }
        if (this.getTagSize() != other.getTagSize()) {
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
        hash = 19 * hash + HmacParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.hash_;
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getTagSize();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static HmacParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HmacParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HmacParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HmacParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HmacParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HmacParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HmacParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static HmacParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static HmacParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static HmacParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static HmacParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static HmacParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return HmacParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(HmacParams prototype) {
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

    public static HmacParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<HmacParams> parser() {
        return PARSER;
    }

    public Parser<HmacParams> getParserForType() {
        return PARSER;
    }

    @Override
    public HmacParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HmacParams.class.getName());
        DEFAULT_INSTANCE = new HmacParams();
        PARSER = new AbstractParser<HmacParams>(){

            @Override
            public HmacParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = HmacParams.newBuilder();
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
    implements HmacParamsOrBuilder {
        private int bitField0_;
        private int hash_ = 0;
        private int tagSize_;

        public static final Descriptors.Descriptor getDescriptor() {
            return Hmac.internal_static_google_crypto_tink_HmacParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Hmac.internal_static_google_crypto_tink_HmacParams_fieldAccessorTable.ensureFieldAccessorsInitialized(HmacParams.class, Builder.class);
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
            this.tagSize_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Hmac.internal_static_google_crypto_tink_HmacParams_descriptor;
        }

        @Override
        public HmacParams getDefaultInstanceForType() {
            return HmacParams.getDefaultInstance();
        }

        @Override
        public HmacParams build() {
            HmacParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public HmacParams buildPartial() {
            HmacParams result = new HmacParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(HmacParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.hash_ = this.hash_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.tagSize_ = this.tagSize_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof HmacParams) {
                return this.mergeFrom((HmacParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(HmacParams other) {
            if (other == HmacParams.getDefaultInstance()) {
                return this;
            }
            if (other.hash_ != 0) {
                this.setHashValue(other.getHashValue());
            }
            if (other.getTagSize() != 0) {
                this.setTagSize(other.getTagSize());
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
                        case 16: {
                            this.tagSize_ = input.readUInt32();
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
        public int getTagSize() {
            return this.tagSize_;
        }

        public Builder setTagSize(int value) {
            this.tagSize_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearTagSize() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.tagSize_ = 0;
            this.onChanged();
            return this;
        }
    }
}

