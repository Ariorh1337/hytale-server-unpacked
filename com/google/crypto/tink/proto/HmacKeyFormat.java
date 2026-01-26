/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Hmac;
import com.google.crypto.tink.proto.HmacKeyFormatOrBuilder;
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

public final class HmacKeyFormat
extends GeneratedMessage
implements HmacKeyFormatOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int PARAMS_FIELD_NUMBER = 1;
    private HmacParams params_;
    public static final int KEY_SIZE_FIELD_NUMBER = 2;
    private int keySize_ = 0;
    public static final int VERSION_FIELD_NUMBER = 3;
    private int version_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final HmacKeyFormat DEFAULT_INSTANCE;
    private static final Parser<HmacKeyFormat> PARSER;

    private HmacKeyFormat(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private HmacKeyFormat() {
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Hmac.internal_static_google_crypto_tink_HmacKeyFormat_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Hmac.internal_static_google_crypto_tink_HmacKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(HmacKeyFormat.class, Builder.class);
    }

    @Override
    public boolean hasParams() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public HmacParams getParams() {
        return this.params_ == null ? HmacParams.getDefaultInstance() : this.params_;
    }

    @Override
    public HmacParamsOrBuilder getParamsOrBuilder() {
        return this.params_ == null ? HmacParams.getDefaultInstance() : this.params_;
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
        if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(1, this.getParams());
        }
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
        if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(1, this.getParams());
        }
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
        if (!(obj instanceof HmacKeyFormat)) {
            return super.equals(obj);
        }
        HmacKeyFormat other = (HmacKeyFormat)obj;
        if (this.hasParams() != other.hasParams()) {
            return false;
        }
        if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
        }
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
        hash = 19 * hash + HmacKeyFormat.getDescriptor().hashCode();
        if (this.hasParams()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getParams().hashCode();
        }
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getKeySize();
        hash = 37 * hash + 3;
        hash = 53 * hash + this.getVersion();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static HmacKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HmacKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HmacKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HmacKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HmacKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HmacKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HmacKeyFormat parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static HmacKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static HmacKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static HmacKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static HmacKeyFormat parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static HmacKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return HmacKeyFormat.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(HmacKeyFormat prototype) {
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

    public static HmacKeyFormat getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<HmacKeyFormat> parser() {
        return PARSER;
    }

    public Parser<HmacKeyFormat> getParserForType() {
        return PARSER;
    }

    @Override
    public HmacKeyFormat getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HmacKeyFormat.class.getName());
        DEFAULT_INSTANCE = new HmacKeyFormat();
        PARSER = new AbstractParser<HmacKeyFormat>(){

            @Override
            public HmacKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = HmacKeyFormat.newBuilder();
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
    implements HmacKeyFormatOrBuilder {
        private int bitField0_;
        private HmacParams params_;
        private SingleFieldBuilder<HmacParams, HmacParams.Builder, HmacParamsOrBuilder> paramsBuilder_;
        private int keySize_;
        private int version_;

        public static final Descriptors.Descriptor getDescriptor() {
            return Hmac.internal_static_google_crypto_tink_HmacKeyFormat_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Hmac.internal_static_google_crypto_tink_HmacKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(HmacKeyFormat.class, Builder.class);
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
            this.keySize_ = 0;
            this.version_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Hmac.internal_static_google_crypto_tink_HmacKeyFormat_descriptor;
        }

        @Override
        public HmacKeyFormat getDefaultInstanceForType() {
            return HmacKeyFormat.getDefaultInstance();
        }

        @Override
        public HmacKeyFormat build() {
            HmacKeyFormat result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public HmacKeyFormat buildPartial() {
            HmacKeyFormat result = new HmacKeyFormat(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(HmacKeyFormat result) {
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
                result.params_ = this.paramsBuilder_ == null ? this.params_ : this.paramsBuilder_.build();
                to_bitField0_ |= 1;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.keySize_ = this.keySize_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.version_ = this.version_;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof HmacKeyFormat) {
                return this.mergeFrom((HmacKeyFormat)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(HmacKeyFormat other) {
            if (other == HmacKeyFormat.getDefaultInstance()) {
                return this;
            }
            if (other.hasParams()) {
                this.mergeParams(other.getParams());
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
                            this.keySize_ = input.readUInt32();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 24: {
                            this.version_ = input.readUInt32();
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
        public HmacParams getParams() {
            if (this.paramsBuilder_ == null) {
                return this.params_ == null ? HmacParams.getDefaultInstance() : this.params_;
            }
            return this.paramsBuilder_.getMessage();
        }

        public Builder setParams(HmacParams value) {
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

        public Builder setParams(HmacParams.Builder builderForValue) {
            if (this.paramsBuilder_ == null) {
                this.params_ = builderForValue.build();
            } else {
                this.paramsBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder mergeParams(HmacParams value) {
            if (this.paramsBuilder_ == null) {
                if ((this.bitField0_ & 1) != 0 && this.params_ != null && this.params_ != HmacParams.getDefaultInstance()) {
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

        public HmacParams.Builder getParamsBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.internalGetParamsFieldBuilder().getBuilder();
        }

        @Override
        public HmacParamsOrBuilder getParamsOrBuilder() {
            if (this.paramsBuilder_ != null) {
                return this.paramsBuilder_.getMessageOrBuilder();
            }
            return this.params_ == null ? HmacParams.getDefaultInstance() : this.params_;
        }

        private SingleFieldBuilder<HmacParams, HmacParams.Builder, HmacParamsOrBuilder> internalGetParamsFieldBuilder() {
            if (this.paramsBuilder_ == null) {
                this.paramsBuilder_ = new SingleFieldBuilder(this.getParams(), this.getParentForChildren(), this.isClean());
                this.params_ = null;
            }
            return this.paramsBuilder_;
        }

        @Override
        public int getKeySize() {
            return this.keySize_;
        }

        public Builder setKeySize(int value) {
            this.keySize_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearKeySize() {
            this.bitField0_ &= 0xFFFFFFFD;
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
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder clearVersion() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.version_ = 0;
            this.onChanged();
            return this;
        }
    }
}

