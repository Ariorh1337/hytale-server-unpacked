/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCtrHmacAead;
import com.google.crypto.tink.proto.AesCtrHmacAeadKeyFormatOrBuilder;
import com.google.crypto.tink.proto.AesCtrKeyFormat;
import com.google.crypto.tink.proto.AesCtrKeyFormatOrBuilder;
import com.google.crypto.tink.proto.HmacKeyFormat;
import com.google.crypto.tink.proto.HmacKeyFormatOrBuilder;
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

public final class AesCtrHmacAeadKeyFormat
extends GeneratedMessage
implements AesCtrHmacAeadKeyFormatOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int AES_CTR_KEY_FORMAT_FIELD_NUMBER = 1;
    private AesCtrKeyFormat aesCtrKeyFormat_;
    public static final int HMAC_KEY_FORMAT_FIELD_NUMBER = 2;
    private HmacKeyFormat hmacKeyFormat_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final AesCtrHmacAeadKeyFormat DEFAULT_INSTANCE;
    private static final Parser<AesCtrHmacAeadKeyFormat> PARSER;

    private AesCtrHmacAeadKeyFormat(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private AesCtrHmacAeadKeyFormat() {
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKeyFormat_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(AesCtrHmacAeadKeyFormat.class, Builder.class);
    }

    @Override
    public boolean hasAesCtrKeyFormat() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public AesCtrKeyFormat getAesCtrKeyFormat() {
        return this.aesCtrKeyFormat_ == null ? AesCtrKeyFormat.getDefaultInstance() : this.aesCtrKeyFormat_;
    }

    @Override
    public AesCtrKeyFormatOrBuilder getAesCtrKeyFormatOrBuilder() {
        return this.aesCtrKeyFormat_ == null ? AesCtrKeyFormat.getDefaultInstance() : this.aesCtrKeyFormat_;
    }

    @Override
    public boolean hasHmacKeyFormat() {
        return (this.bitField0_ & 2) != 0;
    }

    @Override
    public HmacKeyFormat getHmacKeyFormat() {
        return this.hmacKeyFormat_ == null ? HmacKeyFormat.getDefaultInstance() : this.hmacKeyFormat_;
    }

    @Override
    public HmacKeyFormatOrBuilder getHmacKeyFormatOrBuilder() {
        return this.hmacKeyFormat_ == null ? HmacKeyFormat.getDefaultInstance() : this.hmacKeyFormat_;
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
            output.writeMessage(1, this.getAesCtrKeyFormat());
        }
        if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(2, this.getHmacKeyFormat());
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
            size += CodedOutputStream.computeMessageSize(1, this.getAesCtrKeyFormat());
        }
        if ((this.bitField0_ & 2) != 0) {
            size += CodedOutputStream.computeMessageSize(2, this.getHmacKeyFormat());
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AesCtrHmacAeadKeyFormat)) {
            return super.equals(obj);
        }
        AesCtrHmacAeadKeyFormat other = (AesCtrHmacAeadKeyFormat)obj;
        if (this.hasAesCtrKeyFormat() != other.hasAesCtrKeyFormat()) {
            return false;
        }
        if (this.hasAesCtrKeyFormat() && !this.getAesCtrKeyFormat().equals(other.getAesCtrKeyFormat())) {
            return false;
        }
        if (this.hasHmacKeyFormat() != other.hasHmacKeyFormat()) {
            return false;
        }
        if (this.hasHmacKeyFormat() && !this.getHmacKeyFormat().equals(other.getHmacKeyFormat())) {
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
        hash = 19 * hash + AesCtrHmacAeadKeyFormat.getDescriptor().hashCode();
        if (this.hasAesCtrKeyFormat()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getAesCtrKeyFormat().hashCode();
        }
        if (this.hasHmacKeyFormat()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getHmacKeyFormat().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesCtrHmacAeadKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static AesCtrHmacAeadKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesCtrHmacAeadKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return AesCtrHmacAeadKeyFormat.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(AesCtrHmacAeadKeyFormat prototype) {
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

    public static AesCtrHmacAeadKeyFormat getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<AesCtrHmacAeadKeyFormat> parser() {
        return PARSER;
    }

    public Parser<AesCtrHmacAeadKeyFormat> getParserForType() {
        return PARSER;
    }

    @Override
    public AesCtrHmacAeadKeyFormat getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCtrHmacAeadKeyFormat.class.getName());
        DEFAULT_INSTANCE = new AesCtrHmacAeadKeyFormat();
        PARSER = new AbstractParser<AesCtrHmacAeadKeyFormat>(){

            @Override
            public AesCtrHmacAeadKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = AesCtrHmacAeadKeyFormat.newBuilder();
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
    implements AesCtrHmacAeadKeyFormatOrBuilder {
        private int bitField0_;
        private AesCtrKeyFormat aesCtrKeyFormat_;
        private SingleFieldBuilder<AesCtrKeyFormat, AesCtrKeyFormat.Builder, AesCtrKeyFormatOrBuilder> aesCtrKeyFormatBuilder_;
        private HmacKeyFormat hmacKeyFormat_;
        private SingleFieldBuilder<HmacKeyFormat, HmacKeyFormat.Builder, HmacKeyFormatOrBuilder> hmacKeyFormatBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKeyFormat_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(AesCtrHmacAeadKeyFormat.class, Builder.class);
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
                this.internalGetAesCtrKeyFormatFieldBuilder();
                this.internalGetHmacKeyFormatFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.aesCtrKeyFormat_ = null;
            if (this.aesCtrKeyFormatBuilder_ != null) {
                this.aesCtrKeyFormatBuilder_.dispose();
                this.aesCtrKeyFormatBuilder_ = null;
            }
            this.hmacKeyFormat_ = null;
            if (this.hmacKeyFormatBuilder_ != null) {
                this.hmacKeyFormatBuilder_.dispose();
                this.hmacKeyFormatBuilder_ = null;
            }
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return AesCtrHmacAead.internal_static_google_crypto_tink_AesCtrHmacAeadKeyFormat_descriptor;
        }

        @Override
        public AesCtrHmacAeadKeyFormat getDefaultInstanceForType() {
            return AesCtrHmacAeadKeyFormat.getDefaultInstance();
        }

        @Override
        public AesCtrHmacAeadKeyFormat build() {
            AesCtrHmacAeadKeyFormat result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public AesCtrHmacAeadKeyFormat buildPartial() {
            AesCtrHmacAeadKeyFormat result = new AesCtrHmacAeadKeyFormat(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(AesCtrHmacAeadKeyFormat result) {
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
                result.aesCtrKeyFormat_ = this.aesCtrKeyFormatBuilder_ == null ? this.aesCtrKeyFormat_ : this.aesCtrKeyFormatBuilder_.build();
                to_bitField0_ |= 1;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.hmacKeyFormat_ = this.hmacKeyFormatBuilder_ == null ? this.hmacKeyFormat_ : this.hmacKeyFormatBuilder_.build();
                to_bitField0_ |= 2;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof AesCtrHmacAeadKeyFormat) {
                return this.mergeFrom((AesCtrHmacAeadKeyFormat)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(AesCtrHmacAeadKeyFormat other) {
            if (other == AesCtrHmacAeadKeyFormat.getDefaultInstance()) {
                return this;
            }
            if (other.hasAesCtrKeyFormat()) {
                this.mergeAesCtrKeyFormat(other.getAesCtrKeyFormat());
            }
            if (other.hasHmacKeyFormat()) {
                this.mergeHmacKeyFormat(other.getHmacKeyFormat());
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
                        case 10: {
                            input.readMessage(this.internalGetAesCtrKeyFormatFieldBuilder().getBuilder(), extensionRegistry);
                            this.bitField0_ |= 1;
                            continue block10;
                        }
                        case 18: {
                            input.readMessage(this.internalGetHmacKeyFormatFieldBuilder().getBuilder(), extensionRegistry);
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
        public boolean hasAesCtrKeyFormat() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public AesCtrKeyFormat getAesCtrKeyFormat() {
            if (this.aesCtrKeyFormatBuilder_ == null) {
                return this.aesCtrKeyFormat_ == null ? AesCtrKeyFormat.getDefaultInstance() : this.aesCtrKeyFormat_;
            }
            return this.aesCtrKeyFormatBuilder_.getMessage();
        }

        public Builder setAesCtrKeyFormat(AesCtrKeyFormat value) {
            if (this.aesCtrKeyFormatBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.aesCtrKeyFormat_ = value;
            } else {
                this.aesCtrKeyFormatBuilder_.setMessage(value);
            }
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder setAesCtrKeyFormat(AesCtrKeyFormat.Builder builderForValue) {
            if (this.aesCtrKeyFormatBuilder_ == null) {
                this.aesCtrKeyFormat_ = builderForValue.build();
            } else {
                this.aesCtrKeyFormatBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder mergeAesCtrKeyFormat(AesCtrKeyFormat value) {
            if (this.aesCtrKeyFormatBuilder_ == null) {
                if ((this.bitField0_ & 1) != 0 && this.aesCtrKeyFormat_ != null && this.aesCtrKeyFormat_ != AesCtrKeyFormat.getDefaultInstance()) {
                    this.getAesCtrKeyFormatBuilder().mergeFrom(value);
                } else {
                    this.aesCtrKeyFormat_ = value;
                }
            } else {
                this.aesCtrKeyFormatBuilder_.mergeFrom(value);
            }
            if (this.aesCtrKeyFormat_ != null) {
                this.bitField0_ |= 1;
                this.onChanged();
            }
            return this;
        }

        public Builder clearAesCtrKeyFormat() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.aesCtrKeyFormat_ = null;
            if (this.aesCtrKeyFormatBuilder_ != null) {
                this.aesCtrKeyFormatBuilder_.dispose();
                this.aesCtrKeyFormatBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public AesCtrKeyFormat.Builder getAesCtrKeyFormatBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.internalGetAesCtrKeyFormatFieldBuilder().getBuilder();
        }

        @Override
        public AesCtrKeyFormatOrBuilder getAesCtrKeyFormatOrBuilder() {
            if (this.aesCtrKeyFormatBuilder_ != null) {
                return this.aesCtrKeyFormatBuilder_.getMessageOrBuilder();
            }
            return this.aesCtrKeyFormat_ == null ? AesCtrKeyFormat.getDefaultInstance() : this.aesCtrKeyFormat_;
        }

        private SingleFieldBuilder<AesCtrKeyFormat, AesCtrKeyFormat.Builder, AesCtrKeyFormatOrBuilder> internalGetAesCtrKeyFormatFieldBuilder() {
            if (this.aesCtrKeyFormatBuilder_ == null) {
                this.aesCtrKeyFormatBuilder_ = new SingleFieldBuilder(this.getAesCtrKeyFormat(), this.getParentForChildren(), this.isClean());
                this.aesCtrKeyFormat_ = null;
            }
            return this.aesCtrKeyFormatBuilder_;
        }

        @Override
        public boolean hasHmacKeyFormat() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public HmacKeyFormat getHmacKeyFormat() {
            if (this.hmacKeyFormatBuilder_ == null) {
                return this.hmacKeyFormat_ == null ? HmacKeyFormat.getDefaultInstance() : this.hmacKeyFormat_;
            }
            return this.hmacKeyFormatBuilder_.getMessage();
        }

        public Builder setHmacKeyFormat(HmacKeyFormat value) {
            if (this.hmacKeyFormatBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.hmacKeyFormat_ = value;
            } else {
                this.hmacKeyFormatBuilder_.setMessage(value);
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder setHmacKeyFormat(HmacKeyFormat.Builder builderForValue) {
            if (this.hmacKeyFormatBuilder_ == null) {
                this.hmacKeyFormat_ = builderForValue.build();
            } else {
                this.hmacKeyFormatBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder mergeHmacKeyFormat(HmacKeyFormat value) {
            if (this.hmacKeyFormatBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0 && this.hmacKeyFormat_ != null && this.hmacKeyFormat_ != HmacKeyFormat.getDefaultInstance()) {
                    this.getHmacKeyFormatBuilder().mergeFrom(value);
                } else {
                    this.hmacKeyFormat_ = value;
                }
            } else {
                this.hmacKeyFormatBuilder_.mergeFrom(value);
            }
            if (this.hmacKeyFormat_ != null) {
                this.bitField0_ |= 2;
                this.onChanged();
            }
            return this;
        }

        public Builder clearHmacKeyFormat() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.hmacKeyFormat_ = null;
            if (this.hmacKeyFormatBuilder_ != null) {
                this.hmacKeyFormatBuilder_.dispose();
                this.hmacKeyFormatBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public HmacKeyFormat.Builder getHmacKeyFormatBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.internalGetHmacKeyFormatFieldBuilder().getBuilder();
        }

        @Override
        public HmacKeyFormatOrBuilder getHmacKeyFormatOrBuilder() {
            if (this.hmacKeyFormatBuilder_ != null) {
                return this.hmacKeyFormatBuilder_.getMessageOrBuilder();
            }
            return this.hmacKeyFormat_ == null ? HmacKeyFormat.getDefaultInstance() : this.hmacKeyFormat_;
        }

        private SingleFieldBuilder<HmacKeyFormat, HmacKeyFormat.Builder, HmacKeyFormatOrBuilder> internalGetHmacKeyFormatFieldBuilder() {
            if (this.hmacKeyFormatBuilder_ == null) {
                this.hmacKeyFormatBuilder_ = new SingleFieldBuilder(this.getHmacKeyFormat(), this.getParentForChildren(), this.isClean());
                this.hmacKeyFormat_ = null;
            }
            return this.hmacKeyFormatBuilder_;
        }
    }
}

