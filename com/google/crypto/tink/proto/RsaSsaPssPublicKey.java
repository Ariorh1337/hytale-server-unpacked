/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.RsaSsaPss;
import com.google.crypto.tink.proto.RsaSsaPssParams;
import com.google.crypto.tink.proto.RsaSsaPssParamsOrBuilder;
import com.google.crypto.tink.proto.RsaSsaPssPublicKeyOrBuilder;
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

public final class RsaSsaPssPublicKey
extends GeneratedMessage
implements RsaSsaPssPublicKeyOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_ = 0;
    public static final int PARAMS_FIELD_NUMBER = 2;
    private RsaSsaPssParams params_;
    public static final int N_FIELD_NUMBER = 3;
    private ByteString n_ = ByteString.EMPTY;
    public static final int E_FIELD_NUMBER = 4;
    private ByteString e_ = ByteString.EMPTY;
    private byte memoizedIsInitialized = (byte)-1;
    private static final RsaSsaPssPublicKey DEFAULT_INSTANCE;
    private static final Parser<RsaSsaPssPublicKey> PARSER;

    private RsaSsaPssPublicKey(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private RsaSsaPssPublicKey() {
        this.n_ = ByteString.EMPTY;
        this.e_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_fieldAccessorTable.ensureFieldAccessorsInitialized(RsaSsaPssPublicKey.class, Builder.class);
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
    public RsaSsaPssParams getParams() {
        return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
    }

    @Override
    public RsaSsaPssParamsOrBuilder getParamsOrBuilder() {
        return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
    }

    @Override
    public ByteString getN() {
        return this.n_;
    }

    @Override
    public ByteString getE() {
        return this.e_;
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
        if (!this.n_.isEmpty()) {
            output.writeBytes(3, this.n_);
        }
        if (!this.e_.isEmpty()) {
            output.writeBytes(4, this.e_);
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
        if (!this.n_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.n_);
        }
        if (!this.e_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(4, this.e_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RsaSsaPssPublicKey)) {
            return super.equals(obj);
        }
        RsaSsaPssPublicKey other = (RsaSsaPssPublicKey)obj;
        if (this.getVersion() != other.getVersion()) {
            return false;
        }
        if (this.hasParams() != other.hasParams()) {
            return false;
        }
        if (this.hasParams() && !this.getParams().equals(other.getParams())) {
            return false;
        }
        if (!this.getN().equals(other.getN())) {
            return false;
        }
        if (!this.getE().equals(other.getE())) {
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
        hash = 19 * hash + RsaSsaPssPublicKey.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getVersion();
        if (this.hasParams()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getParams().hashCode();
        }
        hash = 37 * hash + 3;
        hash = 53 * hash + this.getN().hashCode();
        hash = 37 * hash + 4;
        hash = 53 * hash + this.getE().hashCode();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static RsaSsaPssPublicKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPssPublicKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPssPublicKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPssPublicKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPssPublicKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RsaSsaPssPublicKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RsaSsaPssPublicKey parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static RsaSsaPssPublicKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static RsaSsaPssPublicKey parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static RsaSsaPssPublicKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static RsaSsaPssPublicKey parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static RsaSsaPssPublicKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return RsaSsaPssPublicKey.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(RsaSsaPssPublicKey prototype) {
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

    public static RsaSsaPssPublicKey getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<RsaSsaPssPublicKey> parser() {
        return PARSER;
    }

    public Parser<RsaSsaPssPublicKey> getParserForType() {
        return PARSER;
    }

    @Override
    public RsaSsaPssPublicKey getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", RsaSsaPssPublicKey.class.getName());
        DEFAULT_INSTANCE = new RsaSsaPssPublicKey();
        PARSER = new AbstractParser<RsaSsaPssPublicKey>(){

            @Override
            public RsaSsaPssPublicKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = RsaSsaPssPublicKey.newBuilder();
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
    implements RsaSsaPssPublicKeyOrBuilder {
        private int bitField0_;
        private int version_;
        private RsaSsaPssParams params_;
        private SingleFieldBuilder<RsaSsaPssParams, RsaSsaPssParams.Builder, RsaSsaPssParamsOrBuilder> paramsBuilder_;
        private ByteString n_ = ByteString.EMPTY;
        private ByteString e_ = ByteString.EMPTY;

        public static final Descriptors.Descriptor getDescriptor() {
            return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_fieldAccessorTable.ensureFieldAccessorsInitialized(RsaSsaPssPublicKey.class, Builder.class);
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
            this.n_ = ByteString.EMPTY;
            this.e_ = ByteString.EMPTY;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return RsaSsaPss.internal_static_google_crypto_tink_RsaSsaPssPublicKey_descriptor;
        }

        @Override
        public RsaSsaPssPublicKey getDefaultInstanceForType() {
            return RsaSsaPssPublicKey.getDefaultInstance();
        }

        @Override
        public RsaSsaPssPublicKey build() {
            RsaSsaPssPublicKey result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public RsaSsaPssPublicKey buildPartial() {
            RsaSsaPssPublicKey result = new RsaSsaPssPublicKey(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(RsaSsaPssPublicKey result) {
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
                result.n_ = this.n_;
            }
            if ((from_bitField0_ & 8) != 0) {
                result.e_ = this.e_;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof RsaSsaPssPublicKey) {
                return this.mergeFrom((RsaSsaPssPublicKey)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(RsaSsaPssPublicKey other) {
            if (other == RsaSsaPssPublicKey.getDefaultInstance()) {
                return this;
            }
            if (other.getVersion() != 0) {
                this.setVersion(other.getVersion());
            }
            if (other.hasParams()) {
                this.mergeParams(other.getParams());
            }
            if (!other.getN().isEmpty()) {
                this.setN(other.getN());
            }
            if (!other.getE().isEmpty()) {
                this.setE(other.getE());
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
                block12: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block12;
                        }
                        case 8: {
                            this.version_ = input.readUInt32();
                            this.bitField0_ |= 1;
                            continue block12;
                        }
                        case 18: {
                            input.readMessage(this.internalGetParamsFieldBuilder().getBuilder(), extensionRegistry);
                            this.bitField0_ |= 2;
                            continue block12;
                        }
                        case 26: {
                            this.n_ = input.readBytes();
                            this.bitField0_ |= 4;
                            continue block12;
                        }
                        case 34: {
                            this.e_ = input.readBytes();
                            this.bitField0_ |= 8;
                            continue block12;
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
        public RsaSsaPssParams getParams() {
            if (this.paramsBuilder_ == null) {
                return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
            }
            return this.paramsBuilder_.getMessage();
        }

        public Builder setParams(RsaSsaPssParams value) {
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

        public Builder setParams(RsaSsaPssParams.Builder builderForValue) {
            if (this.paramsBuilder_ == null) {
                this.params_ = builderForValue.build();
            } else {
                this.paramsBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder mergeParams(RsaSsaPssParams value) {
            if (this.paramsBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0 && this.params_ != null && this.params_ != RsaSsaPssParams.getDefaultInstance()) {
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

        public RsaSsaPssParams.Builder getParamsBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.internalGetParamsFieldBuilder().getBuilder();
        }

        @Override
        public RsaSsaPssParamsOrBuilder getParamsOrBuilder() {
            if (this.paramsBuilder_ != null) {
                return this.paramsBuilder_.getMessageOrBuilder();
            }
            return this.params_ == null ? RsaSsaPssParams.getDefaultInstance() : this.params_;
        }

        private SingleFieldBuilder<RsaSsaPssParams, RsaSsaPssParams.Builder, RsaSsaPssParamsOrBuilder> internalGetParamsFieldBuilder() {
            if (this.paramsBuilder_ == null) {
                this.paramsBuilder_ = new SingleFieldBuilder(this.getParams(), this.getParentForChildren(), this.isClean());
                this.params_ = null;
            }
            return this.paramsBuilder_;
        }

        @Override
        public ByteString getN() {
            return this.n_;
        }

        public Builder setN(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.n_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder clearN() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.n_ = RsaSsaPssPublicKey.getDefaultInstance().getN();
            this.onChanged();
            return this;
        }

        @Override
        public ByteString getE() {
            return this.e_;
        }

        public Builder setE(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.e_ = value;
            this.bitField0_ |= 8;
            this.onChanged();
            return this;
        }

        public Builder clearE() {
            this.bitField0_ &= 0xFFFFFFF7;
            this.e_ = RsaSsaPssPublicKey.getDefaultInstance().getE();
            this.onChanged();
            return this;
        }
    }
}

