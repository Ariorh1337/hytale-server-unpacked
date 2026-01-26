/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.AesCtr;
import com.google.crypto.tink.proto.AesCtrKeyOrBuilder;
import com.google.crypto.tink.proto.AesCtrParams;
import com.google.crypto.tink.proto.AesCtrParamsOrBuilder;
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

public final class AesCtrKey
extends GeneratedMessage
implements AesCtrKeyOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_ = 0;
    public static final int PARAMS_FIELD_NUMBER = 2;
    private AesCtrParams params_;
    public static final int KEY_VALUE_FIELD_NUMBER = 3;
    private ByteString keyValue_ = ByteString.EMPTY;
    private byte memoizedIsInitialized = (byte)-1;
    private static final AesCtrKey DEFAULT_INSTANCE;
    private static final Parser<AesCtrKey> PARSER;

    private AesCtrKey(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private AesCtrKey() {
        this.keyValue_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return AesCtr.internal_static_google_crypto_tink_AesCtrKey_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return AesCtr.internal_static_google_crypto_tink_AesCtrKey_fieldAccessorTable.ensureFieldAccessorsInitialized(AesCtrKey.class, Builder.class);
    }

    @Override
    public int getVersion() {
        return this.version_;
    }

    @Override
    public boolean hasParams() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public AesCtrParams getParams() {
        return this.params_ == null ? AesCtrParams.getDefaultInstance() : this.params_;
    }

    @Override
    public AesCtrParamsOrBuilder getParamsOrBuilder() {
        return this.params_ == null ? AesCtrParams.getDefaultInstance() : this.params_;
    }

    @Override
    public ByteString getKeyValue() {
        return this.keyValue_;
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
        if (this.version_ != 0) {
            output.writeUInt32(1, this.version_);
        }
        if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(2, this.getParams());
        }
        if (!this.keyValue_.isEmpty()) {
            output.writeBytes(3, this.keyValue_);
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
        if (this.version_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.version_);
        }
        if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(2, this.getParams());
        }
        if (!this.keyValue_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.keyValue_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof AesCtrKey)) {
            return super.equals(obj);
        }
        AesCtrKey other = (AesCtrKey)obj;
        if (this.getVersion() != other.getVersion()) {
            return false;
        }
        if (this.hasParams() != other.hasParams()) {
            return false;
        }
        if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
        }
        if (!this.getKeyValue().equals(other.getKeyValue())) {
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
        hash = 19 * hash + AesCtrKey.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getVersion();
        if (this.hasParams()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getParams().hashCode();
        }
        hash = 37 * hash + 3;
        hash = 53 * hash + this.getKeyValue().hashCode();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static AesCtrKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static AesCtrKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static AesCtrKey parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesCtrKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesCtrKey parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static AesCtrKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static AesCtrKey parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static AesCtrKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return AesCtrKey.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(AesCtrKey prototype) {
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

    public static AesCtrKey getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<AesCtrKey> parser() {
        return PARSER;
    }

    public Parser<AesCtrKey> getParserForType() {
        return PARSER;
    }

    @Override
    public AesCtrKey getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", AesCtrKey.class.getName());
        DEFAULT_INSTANCE = new AesCtrKey();
        PARSER = new AbstractParser<AesCtrKey>(){

            @Override
            public AesCtrKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = AesCtrKey.newBuilder();
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
    implements AesCtrKeyOrBuilder {
        private int bitField0_;
        private int version_;
        private AesCtrParams params_;
        private SingleFieldBuilder<AesCtrParams, AesCtrParams.Builder, AesCtrParamsOrBuilder> paramsBuilder_;
        private ByteString keyValue_ = ByteString.EMPTY;

        public static final Descriptors.Descriptor getDescriptor() {
            return AesCtr.internal_static_google_crypto_tink_AesCtrKey_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return AesCtr.internal_static_google_crypto_tink_AesCtrKey_fieldAccessorTable.ensureFieldAccessorsInitialized(AesCtrKey.class, Builder.class);
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
            this.version_ = 0;
            this.params_ = null;
            if (this.paramsBuilder_ != null) {
                this.paramsBuilder_.dispose();
                this.paramsBuilder_ = null;
            }
            this.keyValue_ = ByteString.EMPTY;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return AesCtr.internal_static_google_crypto_tink_AesCtrKey_descriptor;
        }

        @Override
        public AesCtrKey getDefaultInstanceForType() {
            return AesCtrKey.getDefaultInstance();
        }

        @Override
        public AesCtrKey build() {
            AesCtrKey result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public AesCtrKey buildPartial() {
            AesCtrKey result = new AesCtrKey(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(AesCtrKey result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.version_ = this.version_;
            }
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 2) != 0) {
                result.params_ = this.paramsBuilder_ == null ? this.params_ : this.paramsBuilder_.build();
                to_bitField0_ |= 1;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.keyValue_ = this.keyValue_;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof AesCtrKey) {
                return this.mergeFrom((AesCtrKey)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(AesCtrKey other) {
            if (other == AesCtrKey.getDefaultInstance()) {
                return this;
            }
            if (other.getVersion() != 0) {
                this.setVersion(other.getVersion());
            }
            if (other.hasParams()) {
                this.mergeParams(other.getParams());
            }
            if (!other.getKeyValue().isEmpty()) {
                this.setKeyValue(other.getKeyValue());
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
                            this.version_ = input.readUInt32();
                            this.bitField0_ |= 1;
                            continue block11;
                        }
                        case 18: {
                            input.readMessage(this.internalGetParamsFieldBuilder().getBuilder(), extensionRegistry);
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 26: {
                            this.keyValue_ = input.readBytes();
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
        public int getVersion() {
            return this.version_;
        }

        public Builder setVersion(int value) {
            this.version_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearVersion() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.version_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public boolean hasParams() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public AesCtrParams getParams() {
            if (this.paramsBuilder_ == null) {
                return this.params_ == null ? AesCtrParams.getDefaultInstance() : this.params_;
            }
            return this.paramsBuilder_.getMessage();
        }

        public Builder setParams(AesCtrParams value) {
            if (this.paramsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.params_ = value;
            } else {
                this.paramsBuilder_.setMessage(value);
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder setParams(AesCtrParams.Builder builderForValue) {
            if (this.paramsBuilder_ == null) {
                this.params_ = builderForValue.build();
            } else {
                this.paramsBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder mergeParams(AesCtrParams value) {
            if (this.paramsBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0 && this.params_ != null && this.params_ != AesCtrParams.getDefaultInstance()) {
                    this.getParamsBuilder().mergeFrom(value);
                } else {
                    this.params_ = value;
                }
            } else {
                this.paramsBuilder_.mergeFrom(value);
            }
            if (this.params_ != null) {
                this.bitField0_ |= 2;
                this.onChanged();
            }
            return this;
        }

        public Builder clearParams() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.params_ = null;
            if (this.paramsBuilder_ != null) {
                this.paramsBuilder_.dispose();
                this.paramsBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public AesCtrParams.Builder getParamsBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.internalGetParamsFieldBuilder().getBuilder();
        }

        @Override
        public AesCtrParamsOrBuilder getParamsOrBuilder() {
            if (this.paramsBuilder_ != null) {
                return this.paramsBuilder_.getMessageOrBuilder();
            }
            return this.params_ == null ? AesCtrParams.getDefaultInstance() : this.params_;
        }

        private SingleFieldBuilder<AesCtrParams, AesCtrParams.Builder, AesCtrParamsOrBuilder> internalGetParamsFieldBuilder() {
            if (this.paramsBuilder_ == null) {
                this.paramsBuilder_ = new SingleFieldBuilder(this.getParams(), this.getParentForChildren(), this.isClean());
                this.params_ = null;
            }
            return this.paramsBuilder_;
        }

        @Override
        public ByteString getKeyValue() {
            return this.keyValue_;
        }

        public Builder setKeyValue(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.keyValue_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder clearKeyValue() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.keyValue_ = AesCtrKey.getDefaultInstance().getKeyValue();
            this.onChanged();
            return this;
        }
    }
}

