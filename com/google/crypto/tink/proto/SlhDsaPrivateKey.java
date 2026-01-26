/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.SlhDsa;
import com.google.crypto.tink.proto.SlhDsaPrivateKeyOrBuilder;
import com.google.crypto.tink.proto.SlhDsaPublicKey;
import com.google.crypto.tink.proto.SlhDsaPublicKeyOrBuilder;
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

public final class SlhDsaPrivateKey
extends GeneratedMessage
implements SlhDsaPrivateKeyOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_ = 0;
    public static final int KEY_VALUE_FIELD_NUMBER = 2;
    private ByteString keyValue_ = ByteString.EMPTY;
    public static final int PUBLIC_KEY_FIELD_NUMBER = 3;
    private SlhDsaPublicKey publicKey_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final SlhDsaPrivateKey DEFAULT_INSTANCE;
    private static final Parser<SlhDsaPrivateKey> PARSER;

    private SlhDsaPrivateKey(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private SlhDsaPrivateKey() {
        this.keyValue_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_fieldAccessorTable.ensureFieldAccessorsInitialized(SlhDsaPrivateKey.class, Builder.class);
    }

    @Override
    public int getVersion() {
        return this.version_;
    }

    @Override
    public ByteString getKeyValue() {
        return this.keyValue_;
    }

    @Override
    public boolean hasPublicKey() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public SlhDsaPublicKey getPublicKey() {
        return this.publicKey_ == null ? SlhDsaPublicKey.getDefaultInstance() : this.publicKey_;
    }

    @Override
    public SlhDsaPublicKeyOrBuilder getPublicKeyOrBuilder() {
        return this.publicKey_ == null ? SlhDsaPublicKey.getDefaultInstance() : this.publicKey_;
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
        if (!this.keyValue_.isEmpty()) {
            output.writeBytes(2, this.keyValue_);
        }
        if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(3, this.getPublicKey());
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
        if (!this.keyValue_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(2, this.keyValue_);
        }
        if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(3, this.getPublicKey());
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof SlhDsaPrivateKey)) {
            return super.equals(obj);
        }
        SlhDsaPrivateKey other = (SlhDsaPrivateKey)obj;
        if (this.getVersion() != other.getVersion()) {
            return false;
        }
        if (!this.getKeyValue().equals(other.getKeyValue())) {
            return false;
        }
        if (this.hasPublicKey() != other.hasPublicKey()) {
            return false;
        }
        if (this.hasPublicKey() && !this.getPublicKey().equals(other.getPublicKey())) {
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
        hash = 19 * hash + SlhDsaPrivateKey.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getVersion();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getKeyValue().hashCode();
        if (this.hasPublicKey()) {
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getPublicKey().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static SlhDsaPrivateKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static SlhDsaPrivateKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static SlhDsaPrivateKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static SlhDsaPrivateKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static SlhDsaPrivateKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static SlhDsaPrivateKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static SlhDsaPrivateKey parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static SlhDsaPrivateKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static SlhDsaPrivateKey parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static SlhDsaPrivateKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static SlhDsaPrivateKey parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static SlhDsaPrivateKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return SlhDsaPrivateKey.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(SlhDsaPrivateKey prototype) {
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

    public static SlhDsaPrivateKey getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<SlhDsaPrivateKey> parser() {
        return PARSER;
    }

    public Parser<SlhDsaPrivateKey> getParserForType() {
        return PARSER;
    }

    @Override
    public SlhDsaPrivateKey getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", SlhDsaPrivateKey.class.getName());
        DEFAULT_INSTANCE = new SlhDsaPrivateKey();
        PARSER = new AbstractParser<SlhDsaPrivateKey>(){

            @Override
            public SlhDsaPrivateKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = SlhDsaPrivateKey.newBuilder();
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
    implements SlhDsaPrivateKeyOrBuilder {
        private int bitField0_;
        private int version_;
        private ByteString keyValue_ = ByteString.EMPTY;
        private SlhDsaPublicKey publicKey_;
        private SingleFieldBuilder<SlhDsaPublicKey, SlhDsaPublicKey.Builder, SlhDsaPublicKeyOrBuilder> publicKeyBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_fieldAccessorTable.ensureFieldAccessorsInitialized(SlhDsaPrivateKey.class, Builder.class);
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
                this.internalGetPublicKeyFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.version_ = 0;
            this.keyValue_ = ByteString.EMPTY;
            this.publicKey_ = null;
            if (this.publicKeyBuilder_ != null) {
                this.publicKeyBuilder_.dispose();
                this.publicKeyBuilder_ = null;
            }
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return SlhDsa.internal_static_google_crypto_tink_SlhDsaPrivateKey_descriptor;
        }

        @Override
        public SlhDsaPrivateKey getDefaultInstanceForType() {
            return SlhDsaPrivateKey.getDefaultInstance();
        }

        @Override
        public SlhDsaPrivateKey build() {
            SlhDsaPrivateKey result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public SlhDsaPrivateKey buildPartial() {
            SlhDsaPrivateKey result = new SlhDsaPrivateKey(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(SlhDsaPrivateKey result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.version_ = this.version_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.keyValue_ = this.keyValue_;
            }
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 4) != 0) {
                result.publicKey_ = this.publicKeyBuilder_ == null ? this.publicKey_ : this.publicKeyBuilder_.build();
                to_bitField0_ |= 1;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof SlhDsaPrivateKey) {
                return this.mergeFrom((SlhDsaPrivateKey)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(SlhDsaPrivateKey other) {
            if (other == SlhDsaPrivateKey.getDefaultInstance()) {
                return this;
            }
            if (other.getVersion() != 0) {
                this.setVersion(other.getVersion());
            }
            if (!other.getKeyValue().isEmpty()) {
                this.setKeyValue(other.getKeyValue());
            }
            if (other.hasPublicKey()) {
                this.mergePublicKey(other.getPublicKey());
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
                            this.keyValue_ = input.readBytes();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 26: {
                            input.readMessage(this.internalGetPublicKeyFieldBuilder().getBuilder(), extensionRegistry);
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
        public ByteString getKeyValue() {
            return this.keyValue_;
        }

        public Builder setKeyValue(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.keyValue_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearKeyValue() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.keyValue_ = SlhDsaPrivateKey.getDefaultInstance().getKeyValue();
            this.onChanged();
            return this;
        }

        @Override
        public boolean hasPublicKey() {
            return (this.bitField0_ & 4) != 0;
        }

        @Override
        public SlhDsaPublicKey getPublicKey() {
            if (this.publicKeyBuilder_ == null) {
                return this.publicKey_ == null ? SlhDsaPublicKey.getDefaultInstance() : this.publicKey_;
            }
            return this.publicKeyBuilder_.getMessage();
        }

        public Builder setPublicKey(SlhDsaPublicKey value) {
            if (this.publicKeyBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.publicKey_ = value;
            } else {
                this.publicKeyBuilder_.setMessage(value);
            }
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder setPublicKey(SlhDsaPublicKey.Builder builderForValue) {
            if (this.publicKeyBuilder_ == null) {
                this.publicKey_ = builderForValue.build();
            } else {
                this.publicKeyBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder mergePublicKey(SlhDsaPublicKey value) {
            if (this.publicKeyBuilder_ == null) {
                if ((this.bitField0_ & 4) != 0 && this.publicKey_ != null && this.publicKey_ != SlhDsaPublicKey.getDefaultInstance()) {
                    this.getPublicKeyBuilder().mergeFrom(value);
                } else {
                    this.publicKey_ = value;
                }
            } else {
                this.publicKeyBuilder_.mergeFrom(value);
            }
            if (this.publicKey_ != null) {
                this.bitField0_ |= 4;
                this.onChanged();
            }
            return this;
        }

        public Builder clearPublicKey() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.publicKey_ = null;
            if (this.publicKeyBuilder_ != null) {
                this.publicKeyBuilder_.dispose();
                this.publicKeyBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public SlhDsaPublicKey.Builder getPublicKeyBuilder() {
            this.bitField0_ |= 4;
            this.onChanged();
            return this.internalGetPublicKeyFieldBuilder().getBuilder();
        }

        @Override
        public SlhDsaPublicKeyOrBuilder getPublicKeyOrBuilder() {
            if (this.publicKeyBuilder_ != null) {
                return this.publicKeyBuilder_.getMessageOrBuilder();
            }
            return this.publicKey_ == null ? SlhDsaPublicKey.getDefaultInstance() : this.publicKey_;
        }

        private SingleFieldBuilder<SlhDsaPublicKey, SlhDsaPublicKey.Builder, SlhDsaPublicKeyOrBuilder> internalGetPublicKeyFieldBuilder() {
            if (this.publicKeyBuilder_ == null) {
                this.publicKeyBuilder_ = new SingleFieldBuilder(this.getPublicKey(), this.getParentForChildren(), this.isClean());
                this.publicKey_ = null;
            }
            return this.publicKeyBuilder_;
        }
    }
}

