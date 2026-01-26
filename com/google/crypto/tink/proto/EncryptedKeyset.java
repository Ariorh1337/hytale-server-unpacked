/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EncryptedKeysetOrBuilder;
import com.google.crypto.tink.proto.KeysetInfo;
import com.google.crypto.tink.proto.KeysetInfoOrBuilder;
import com.google.crypto.tink.proto.Tink;
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

public final class EncryptedKeyset
extends GeneratedMessage
implements EncryptedKeysetOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int ENCRYPTED_KEYSET_FIELD_NUMBER = 2;
    private ByteString encryptedKeyset_ = ByteString.EMPTY;
    public static final int KEYSET_INFO_FIELD_NUMBER = 3;
    private KeysetInfo keysetInfo_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final EncryptedKeyset DEFAULT_INSTANCE;
    private static final Parser<EncryptedKeyset> PARSER;

    private EncryptedKeyset(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private EncryptedKeyset() {
        this.encryptedKeyset_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Tink.internal_static_google_crypto_tink_EncryptedKeyset_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Tink.internal_static_google_crypto_tink_EncryptedKeyset_fieldAccessorTable.ensureFieldAccessorsInitialized(EncryptedKeyset.class, Builder.class);
    }

    @Override
    public ByteString getEncryptedKeyset() {
        return this.encryptedKeyset_;
    }

    @Override
    public boolean hasKeysetInfo() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public KeysetInfo getKeysetInfo() {
        return this.keysetInfo_ == null ? KeysetInfo.getDefaultInstance() : this.keysetInfo_;
    }

    @Override
    public KeysetInfoOrBuilder getKeysetInfoOrBuilder() {
        return this.keysetInfo_ == null ? KeysetInfo.getDefaultInstance() : this.keysetInfo_;
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
        if (!this.encryptedKeyset_.isEmpty()) {
            output.writeBytes(2, this.encryptedKeyset_);
        }
        if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(3, this.getKeysetInfo());
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
        if (!this.encryptedKeyset_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(2, this.encryptedKeyset_);
        }
        if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(3, this.getKeysetInfo());
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof EncryptedKeyset)) {
            return super.equals(obj);
        }
        EncryptedKeyset other = (EncryptedKeyset)obj;
        if (!this.getEncryptedKeyset().equals(other.getEncryptedKeyset())) {
            return false;
        }
        if (this.hasKeysetInfo() != other.hasKeysetInfo()) {
            return false;
        }
        if (this.hasKeysetInfo() && !this.getKeysetInfo().equals(other.getKeysetInfo())) {
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
        hash = 19 * hash + EncryptedKeyset.getDescriptor().hashCode();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getEncryptedKeyset().hashCode();
        if (this.hasKeysetInfo()) {
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getKeysetInfo().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static EncryptedKeyset parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EncryptedKeyset parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EncryptedKeyset parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EncryptedKeyset parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EncryptedKeyset parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EncryptedKeyset parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EncryptedKeyset parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static EncryptedKeyset parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static EncryptedKeyset parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static EncryptedKeyset parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static EncryptedKeyset parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static EncryptedKeyset parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return EncryptedKeyset.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(EncryptedKeyset prototype) {
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

    public static EncryptedKeyset getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<EncryptedKeyset> parser() {
        return PARSER;
    }

    public Parser<EncryptedKeyset> getParserForType() {
        return PARSER;
    }

    @Override
    public EncryptedKeyset getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EncryptedKeyset.class.getName());
        DEFAULT_INSTANCE = new EncryptedKeyset();
        PARSER = new AbstractParser<EncryptedKeyset>(){

            @Override
            public EncryptedKeyset parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = EncryptedKeyset.newBuilder();
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
    implements EncryptedKeysetOrBuilder {
        private int bitField0_;
        private ByteString encryptedKeyset_ = ByteString.EMPTY;
        private KeysetInfo keysetInfo_;
        private SingleFieldBuilder<KeysetInfo, KeysetInfo.Builder, KeysetInfoOrBuilder> keysetInfoBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return Tink.internal_static_google_crypto_tink_EncryptedKeyset_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Tink.internal_static_google_crypto_tink_EncryptedKeyset_fieldAccessorTable.ensureFieldAccessorsInitialized(EncryptedKeyset.class, Builder.class);
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
                this.internalGetKeysetInfoFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.encryptedKeyset_ = ByteString.EMPTY;
            this.keysetInfo_ = null;
            if (this.keysetInfoBuilder_ != null) {
                this.keysetInfoBuilder_.dispose();
                this.keysetInfoBuilder_ = null;
            }
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Tink.internal_static_google_crypto_tink_EncryptedKeyset_descriptor;
        }

        @Override
        public EncryptedKeyset getDefaultInstanceForType() {
            return EncryptedKeyset.getDefaultInstance();
        }

        @Override
        public EncryptedKeyset build() {
            EncryptedKeyset result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public EncryptedKeyset buildPartial() {
            EncryptedKeyset result = new EncryptedKeyset(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(EncryptedKeyset result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.encryptedKeyset_ = this.encryptedKeyset_;
            }
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 2) != 0) {
                result.keysetInfo_ = this.keysetInfoBuilder_ == null ? this.keysetInfo_ : this.keysetInfoBuilder_.build();
                to_bitField0_ |= 1;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof EncryptedKeyset) {
                return this.mergeFrom((EncryptedKeyset)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(EncryptedKeyset other) {
            if (other == EncryptedKeyset.getDefaultInstance()) {
                return this;
            }
            if (!other.getEncryptedKeyset().isEmpty()) {
                this.setEncryptedKeyset(other.getEncryptedKeyset());
            }
            if (other.hasKeysetInfo()) {
                this.mergeKeysetInfo(other.getKeysetInfo());
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
                        case 18: {
                            this.encryptedKeyset_ = input.readBytes();
                            this.bitField0_ |= 1;
                            continue block10;
                        }
                        case 26: {
                            input.readMessage(this.internalGetKeysetInfoFieldBuilder().getBuilder(), extensionRegistry);
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
        public ByteString getEncryptedKeyset() {
            return this.encryptedKeyset_;
        }

        public Builder setEncryptedKeyset(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.encryptedKeyset_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearEncryptedKeyset() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.encryptedKeyset_ = EncryptedKeyset.getDefaultInstance().getEncryptedKeyset();
            this.onChanged();
            return this;
        }

        @Override
        public boolean hasKeysetInfo() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public KeysetInfo getKeysetInfo() {
            if (this.keysetInfoBuilder_ == null) {
                return this.keysetInfo_ == null ? KeysetInfo.getDefaultInstance() : this.keysetInfo_;
            }
            return this.keysetInfoBuilder_.getMessage();
        }

        public Builder setKeysetInfo(KeysetInfo value) {
            if (this.keysetInfoBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.keysetInfo_ = value;
            } else {
                this.keysetInfoBuilder_.setMessage(value);
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder setKeysetInfo(KeysetInfo.Builder builderForValue) {
            if (this.keysetInfoBuilder_ == null) {
                this.keysetInfo_ = builderForValue.build();
            } else {
                this.keysetInfoBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder mergeKeysetInfo(KeysetInfo value) {
            if (this.keysetInfoBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0 && this.keysetInfo_ != null && this.keysetInfo_ != KeysetInfo.getDefaultInstance()) {
                    this.getKeysetInfoBuilder().mergeFrom(value);
                } else {
                    this.keysetInfo_ = value;
                }
            } else {
                this.keysetInfoBuilder_.mergeFrom(value);
            }
            if (this.keysetInfo_ != null) {
                this.bitField0_ |= 2;
                this.onChanged();
            }
            return this;
        }

        public Builder clearKeysetInfo() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.keysetInfo_ = null;
            if (this.keysetInfoBuilder_ != null) {
                this.keysetInfoBuilder_.dispose();
                this.keysetInfoBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public KeysetInfo.Builder getKeysetInfoBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.internalGetKeysetInfoFieldBuilder().getBuilder();
        }

        @Override
        public KeysetInfoOrBuilder getKeysetInfoOrBuilder() {
            if (this.keysetInfoBuilder_ != null) {
                return this.keysetInfoBuilder_.getMessageOrBuilder();
            }
            return this.keysetInfo_ == null ? KeysetInfo.getDefaultInstance() : this.keysetInfo_;
        }

        private SingleFieldBuilder<KeysetInfo, KeysetInfo.Builder, KeysetInfoOrBuilder> internalGetKeysetInfoFieldBuilder() {
            if (this.keysetInfoBuilder_ == null) {
                this.keysetInfoBuilder_ = new SingleFieldBuilder(this.getKeysetInfo(), this.getParentForChildren(), this.isClean());
                this.keysetInfo_ = null;
            }
            return this.keysetInfoBuilder_;
        }
    }
}

