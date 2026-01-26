/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.EcPointFormat;
import com.google.crypto.tink.proto.EciesAeadDemParams;
import com.google.crypto.tink.proto.EciesAeadDemParamsOrBuilder;
import com.google.crypto.tink.proto.EciesAeadHkdf;
import com.google.crypto.tink.proto.EciesAeadHkdfParamsOrBuilder;
import com.google.crypto.tink.proto.EciesHkdfKemParams;
import com.google.crypto.tink.proto.EciesHkdfKemParamsOrBuilder;
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

public final class EciesAeadHkdfParams
extends GeneratedMessage
implements EciesAeadHkdfParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int KEM_PARAMS_FIELD_NUMBER = 1;
    private EciesHkdfKemParams kemParams_;
    public static final int DEM_PARAMS_FIELD_NUMBER = 2;
    private EciesAeadDemParams demParams_;
    public static final int EC_POINT_FORMAT_FIELD_NUMBER = 3;
    private int ecPointFormat_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final EciesAeadHkdfParams DEFAULT_INSTANCE;
    private static final Parser<EciesAeadHkdfParams> PARSER;

    private EciesAeadHkdfParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private EciesAeadHkdfParams() {
        this.ecPointFormat_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfParams_fieldAccessorTable.ensureFieldAccessorsInitialized(EciesAeadHkdfParams.class, Builder.class);
    }

    @Override
    public boolean hasKemParams() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public EciesHkdfKemParams getKemParams() {
        return this.kemParams_ == null ? EciesHkdfKemParams.getDefaultInstance() : this.kemParams_;
    }

    @Override
    public EciesHkdfKemParamsOrBuilder getKemParamsOrBuilder() {
        return this.kemParams_ == null ? EciesHkdfKemParams.getDefaultInstance() : this.kemParams_;
    }

    @Override
    public boolean hasDemParams() {
        return (this.bitField0_ & 2) != 0;
    }

    @Override
    public EciesAeadDemParams getDemParams() {
        return this.demParams_ == null ? EciesAeadDemParams.getDefaultInstance() : this.demParams_;
    }

    @Override
    public EciesAeadDemParamsOrBuilder getDemParamsOrBuilder() {
        return this.demParams_ == null ? EciesAeadDemParams.getDefaultInstance() : this.demParams_;
    }

    @Override
    public int getEcPointFormatValue() {
        return this.ecPointFormat_;
    }

    @Override
    public EcPointFormat getEcPointFormat() {
        EcPointFormat result = EcPointFormat.forNumber(this.ecPointFormat_);
        return result == null ? EcPointFormat.UNRECOGNIZED : result;
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
            output.writeMessage(1, this.getKemParams());
        }
        if ((this.bitField0_ & 2) != 0) {
            output.writeMessage(2, this.getDemParams());
        }
        if (this.ecPointFormat_ != EcPointFormat.UNKNOWN_FORMAT.getNumber()) {
            output.writeEnum(3, this.ecPointFormat_);
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
            size += CodedOutputStream.computeMessageSize(1, this.getKemParams());
        }
        if ((this.bitField0_ & 2) != 0) {
            size += CodedOutputStream.computeMessageSize(2, this.getDemParams());
        }
        if (this.ecPointFormat_ != EcPointFormat.UNKNOWN_FORMAT.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.ecPointFormat_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof EciesAeadHkdfParams)) {
            return super.equals(obj);
        }
        EciesAeadHkdfParams other = (EciesAeadHkdfParams)obj;
        if (this.hasKemParams() != other.hasKemParams()) {
            return false;
        }
        if (this.hasKemParams() && !this.getKemParams().equals(other.getKemParams())) {
            return false;
        }
        if (this.hasDemParams() != other.hasDemParams()) {
            return false;
        }
        if (this.hasDemParams() && !this.getDemParams().equals(other.getDemParams())) {
            return false;
        }
        if (this.ecPointFormat_ != other.ecPointFormat_) {
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
        hash = 19 * hash + EciesAeadHkdfParams.getDescriptor().hashCode();
        if (this.hasKemParams()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getKemParams().hashCode();
        }
        if (this.hasDemParams()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getDemParams().hashCode();
        }
        hash = 37 * hash + 3;
        hash = 53 * hash + this.ecPointFormat_;
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static EciesAeadHkdfParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EciesAeadHkdfParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EciesAeadHkdfParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EciesAeadHkdfParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EciesAeadHkdfParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static EciesAeadHkdfParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static EciesAeadHkdfParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static EciesAeadHkdfParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static EciesAeadHkdfParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static EciesAeadHkdfParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static EciesAeadHkdfParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static EciesAeadHkdfParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return EciesAeadHkdfParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(EciesAeadHkdfParams prototype) {
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

    public static EciesAeadHkdfParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<EciesAeadHkdfParams> parser() {
        return PARSER;
    }

    public Parser<EciesAeadHkdfParams> getParserForType() {
        return PARSER;
    }

    @Override
    public EciesAeadHkdfParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", EciesAeadHkdfParams.class.getName());
        DEFAULT_INSTANCE = new EciesAeadHkdfParams();
        PARSER = new AbstractParser<EciesAeadHkdfParams>(){

            @Override
            public EciesAeadHkdfParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = EciesAeadHkdfParams.newBuilder();
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
    implements EciesAeadHkdfParamsOrBuilder {
        private int bitField0_;
        private EciesHkdfKemParams kemParams_;
        private SingleFieldBuilder<EciesHkdfKemParams, EciesHkdfKemParams.Builder, EciesHkdfKemParamsOrBuilder> kemParamsBuilder_;
        private EciesAeadDemParams demParams_;
        private SingleFieldBuilder<EciesAeadDemParams, EciesAeadDemParams.Builder, EciesAeadDemParamsOrBuilder> demParamsBuilder_;
        private int ecPointFormat_ = 0;

        public static final Descriptors.Descriptor getDescriptor() {
            return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfParams_fieldAccessorTable.ensureFieldAccessorsInitialized(EciesAeadHkdfParams.class, Builder.class);
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
                this.internalGetKemParamsFieldBuilder();
                this.internalGetDemParamsFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.kemParams_ = null;
            if (this.kemParamsBuilder_ != null) {
                this.kemParamsBuilder_.dispose();
                this.kemParamsBuilder_ = null;
            }
            this.demParams_ = null;
            if (this.demParamsBuilder_ != null) {
                this.demParamsBuilder_.dispose();
                this.demParamsBuilder_ = null;
            }
            this.ecPointFormat_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return EciesAeadHkdf.internal_static_google_crypto_tink_EciesAeadHkdfParams_descriptor;
        }

        @Override
        public EciesAeadHkdfParams getDefaultInstanceForType() {
            return EciesAeadHkdfParams.getDefaultInstance();
        }

        @Override
        public EciesAeadHkdfParams build() {
            EciesAeadHkdfParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public EciesAeadHkdfParams buildPartial() {
            EciesAeadHkdfParams result = new EciesAeadHkdfParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(EciesAeadHkdfParams result) {
            int from_bitField0_ = this.bitField0_;
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 1) != 0) {
                result.kemParams_ = this.kemParamsBuilder_ == null ? this.kemParams_ : this.kemParamsBuilder_.build();
                to_bitField0_ |= 1;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.demParams_ = this.demParamsBuilder_ == null ? this.demParams_ : this.demParamsBuilder_.build();
                to_bitField0_ |= 2;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.ecPointFormat_ = this.ecPointFormat_;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof EciesAeadHkdfParams) {
                return this.mergeFrom((EciesAeadHkdfParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(EciesAeadHkdfParams other) {
            if (other == EciesAeadHkdfParams.getDefaultInstance()) {
                return this;
            }
            if (other.hasKemParams()) {
                this.mergeKemParams(other.getKemParams());
            }
            if (other.hasDemParams()) {
                this.mergeDemParams(other.getDemParams());
            }
            if (other.ecPointFormat_ != 0) {
                this.setEcPointFormatValue(other.getEcPointFormatValue());
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
                            input.readMessage(this.internalGetKemParamsFieldBuilder().getBuilder(), extensionRegistry);
                            this.bitField0_ |= 1;
                            continue block11;
                        }
                        case 18: {
                            input.readMessage(this.internalGetDemParamsFieldBuilder().getBuilder(), extensionRegistry);
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 24: {
                            this.ecPointFormat_ = input.readEnum();
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
        public boolean hasKemParams() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public EciesHkdfKemParams getKemParams() {
            if (this.kemParamsBuilder_ == null) {
                return this.kemParams_ == null ? EciesHkdfKemParams.getDefaultInstance() : this.kemParams_;
            }
            return this.kemParamsBuilder_.getMessage();
        }

        public Builder setKemParams(EciesHkdfKemParams value) {
            if (this.kemParamsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.kemParams_ = value;
            } else {
                this.kemParamsBuilder_.setMessage(value);
            }
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder setKemParams(EciesHkdfKemParams.Builder builderForValue) {
            if (this.kemParamsBuilder_ == null) {
                this.kemParams_ = builderForValue.build();
            } else {
                this.kemParamsBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder mergeKemParams(EciesHkdfKemParams value) {
            if (this.kemParamsBuilder_ == null) {
                if ((this.bitField0_ & 1) != 0 && this.kemParams_ != null && this.kemParams_ != EciesHkdfKemParams.getDefaultInstance()) {
                    this.getKemParamsBuilder().mergeFrom(value);
                } else {
                    this.kemParams_ = value;
                }
            } else {
                this.kemParamsBuilder_.mergeFrom(value);
            }
            if (this.kemParams_ != null) {
                this.bitField0_ |= 1;
                this.onChanged();
            }
            return this;
        }

        public Builder clearKemParams() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.kemParams_ = null;
            if (this.kemParamsBuilder_ != null) {
                this.kemParamsBuilder_.dispose();
                this.kemParamsBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public EciesHkdfKemParams.Builder getKemParamsBuilder() {
            this.bitField0_ |= 1;
            this.onChanged();
            return this.internalGetKemParamsFieldBuilder().getBuilder();
        }

        @Override
        public EciesHkdfKemParamsOrBuilder getKemParamsOrBuilder() {
            if (this.kemParamsBuilder_ != null) {
                return this.kemParamsBuilder_.getMessageOrBuilder();
            }
            return this.kemParams_ == null ? EciesHkdfKemParams.getDefaultInstance() : this.kemParams_;
        }

        private SingleFieldBuilder<EciesHkdfKemParams, EciesHkdfKemParams.Builder, EciesHkdfKemParamsOrBuilder> internalGetKemParamsFieldBuilder() {
            if (this.kemParamsBuilder_ == null) {
                this.kemParamsBuilder_ = new SingleFieldBuilder(this.getKemParams(), this.getParentForChildren(), this.isClean());
                this.kemParams_ = null;
            }
            return this.kemParamsBuilder_;
        }

        @Override
        public boolean hasDemParams() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public EciesAeadDemParams getDemParams() {
            if (this.demParamsBuilder_ == null) {
                return this.demParams_ == null ? EciesAeadDemParams.getDefaultInstance() : this.demParams_;
            }
            return this.demParamsBuilder_.getMessage();
        }

        public Builder setDemParams(EciesAeadDemParams value) {
            if (this.demParamsBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.demParams_ = value;
            } else {
                this.demParamsBuilder_.setMessage(value);
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder setDemParams(EciesAeadDemParams.Builder builderForValue) {
            if (this.demParamsBuilder_ == null) {
                this.demParams_ = builderForValue.build();
            } else {
                this.demParamsBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder mergeDemParams(EciesAeadDemParams value) {
            if (this.demParamsBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0 && this.demParams_ != null && this.demParams_ != EciesAeadDemParams.getDefaultInstance()) {
                    this.getDemParamsBuilder().mergeFrom(value);
                } else {
                    this.demParams_ = value;
                }
            } else {
                this.demParamsBuilder_.mergeFrom(value);
            }
            if (this.demParams_ != null) {
                this.bitField0_ |= 2;
                this.onChanged();
            }
            return this;
        }

        public Builder clearDemParams() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.demParams_ = null;
            if (this.demParamsBuilder_ != null) {
                this.demParamsBuilder_.dispose();
                this.demParamsBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public EciesAeadDemParams.Builder getDemParamsBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.internalGetDemParamsFieldBuilder().getBuilder();
        }

        @Override
        public EciesAeadDemParamsOrBuilder getDemParamsOrBuilder() {
            if (this.demParamsBuilder_ != null) {
                return this.demParamsBuilder_.getMessageOrBuilder();
            }
            return this.demParams_ == null ? EciesAeadDemParams.getDefaultInstance() : this.demParams_;
        }

        private SingleFieldBuilder<EciesAeadDemParams, EciesAeadDemParams.Builder, EciesAeadDemParamsOrBuilder> internalGetDemParamsFieldBuilder() {
            if (this.demParamsBuilder_ == null) {
                this.demParamsBuilder_ = new SingleFieldBuilder(this.getDemParams(), this.getParentForChildren(), this.isClean());
                this.demParams_ = null;
            }
            return this.demParamsBuilder_;
        }

        @Override
        public int getEcPointFormatValue() {
            return this.ecPointFormat_;
        }

        public Builder setEcPointFormatValue(int value) {
            this.ecPointFormat_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        @Override
        public EcPointFormat getEcPointFormat() {
            EcPointFormat result = EcPointFormat.forNumber(this.ecPointFormat_);
            return result == null ? EcPointFormat.UNRECOGNIZED : result;
        }

        public Builder setEcPointFormat(EcPointFormat value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 4;
            this.ecPointFormat_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearEcPointFormat() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.ecPointFormat_ = 0;
            this.onChanged();
            return this;
        }
    }
}

