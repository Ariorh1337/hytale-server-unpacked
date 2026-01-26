/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Hpke;
import com.google.crypto.tink.proto.HpkeAead;
import com.google.crypto.tink.proto.HpkeKdf;
import com.google.crypto.tink.proto.HpkeKem;
import com.google.crypto.tink.proto.HpkeParamsOrBuilder;
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

public final class HpkeParams
extends GeneratedMessage
implements HpkeParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int KEM_FIELD_NUMBER = 1;
    private int kem_ = 0;
    public static final int KDF_FIELD_NUMBER = 2;
    private int kdf_ = 0;
    public static final int AEAD_FIELD_NUMBER = 3;
    private int aead_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final HpkeParams DEFAULT_INSTANCE;
    private static final Parser<HpkeParams> PARSER;

    private HpkeParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private HpkeParams() {
        this.kem_ = 0;
        this.kdf_ = 0;
        this.aead_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Hpke.internal_static_google_crypto_tink_HpkeParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Hpke.internal_static_google_crypto_tink_HpkeParams_fieldAccessorTable.ensureFieldAccessorsInitialized(HpkeParams.class, Builder.class);
    }

    @Override
    public int getKemValue() {
        return this.kem_;
    }

    @Override
    public HpkeKem getKem() {
        HpkeKem result = HpkeKem.forNumber(this.kem_);
        return result == null ? HpkeKem.UNRECOGNIZED : result;
    }

    @Override
    public int getKdfValue() {
        return this.kdf_;
    }

    @Override
    public HpkeKdf getKdf() {
        HpkeKdf result = HpkeKdf.forNumber(this.kdf_);
        return result == null ? HpkeKdf.UNRECOGNIZED : result;
    }

    @Override
    public int getAeadValue() {
        return this.aead_;
    }

    @Override
    public HpkeAead getAead() {
        HpkeAead result = HpkeAead.forNumber(this.aead_);
        return result == null ? HpkeAead.UNRECOGNIZED : result;
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
        if (this.kem_ != HpkeKem.KEM_UNKNOWN.getNumber()) {
            output.writeEnum(1, this.kem_);
        }
        if (this.kdf_ != HpkeKdf.KDF_UNKNOWN.getNumber()) {
            output.writeEnum(2, this.kdf_);
        }
        if (this.aead_ != HpkeAead.AEAD_UNKNOWN.getNumber()) {
            output.writeEnum(3, this.aead_);
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
        if (this.kem_ != HpkeKem.KEM_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.kem_);
        }
        if (this.kdf_ != HpkeKdf.KDF_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.kdf_);
        }
        if (this.aead_ != HpkeAead.AEAD_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.aead_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof HpkeParams)) {
            return super.equals(obj);
        }
        HpkeParams other = (HpkeParams)obj;
        if (this.kem_ != other.kem_) {
            return false;
        }
        if (this.kdf_ != other.kdf_) {
            return false;
        }
        if (this.aead_ != other.aead_) {
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
        hash = 19 * hash + HpkeParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.kem_;
        hash = 37 * hash + 2;
        hash = 53 * hash + this.kdf_;
        hash = 37 * hash + 3;
        hash = 53 * hash + this.aead_;
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static HpkeParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HpkeParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HpkeParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HpkeParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HpkeParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static HpkeParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static HpkeParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static HpkeParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static HpkeParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static HpkeParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static HpkeParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static HpkeParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return HpkeParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(HpkeParams prototype) {
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

    public static HpkeParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<HpkeParams> parser() {
        return PARSER;
    }

    public Parser<HpkeParams> getParserForType() {
        return PARSER;
    }

    @Override
    public HpkeParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", HpkeParams.class.getName());
        DEFAULT_INSTANCE = new HpkeParams();
        PARSER = new AbstractParser<HpkeParams>(){

            @Override
            public HpkeParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = HpkeParams.newBuilder();
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
    implements HpkeParamsOrBuilder {
        private int bitField0_;
        private int kem_ = 0;
        private int kdf_ = 0;
        private int aead_ = 0;

        public static final Descriptors.Descriptor getDescriptor() {
            return Hpke.internal_static_google_crypto_tink_HpkeParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Hpke.internal_static_google_crypto_tink_HpkeParams_fieldAccessorTable.ensureFieldAccessorsInitialized(HpkeParams.class, Builder.class);
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
            this.kem_ = 0;
            this.kdf_ = 0;
            this.aead_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Hpke.internal_static_google_crypto_tink_HpkeParams_descriptor;
        }

        @Override
        public HpkeParams getDefaultInstanceForType() {
            return HpkeParams.getDefaultInstance();
        }

        @Override
        public HpkeParams build() {
            HpkeParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public HpkeParams buildPartial() {
            HpkeParams result = new HpkeParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(HpkeParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.kem_ = this.kem_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.kdf_ = this.kdf_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.aead_ = this.aead_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof HpkeParams) {
                return this.mergeFrom((HpkeParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(HpkeParams other) {
            if (other == HpkeParams.getDefaultInstance()) {
                return this;
            }
            if (other.kem_ != 0) {
                this.setKemValue(other.getKemValue());
            }
            if (other.kdf_ != 0) {
                this.setKdfValue(other.getKdfValue());
            }
            if (other.aead_ != 0) {
                this.setAeadValue(other.getAeadValue());
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
                            this.kem_ = input.readEnum();
                            this.bitField0_ |= 1;
                            continue block11;
                        }
                        case 16: {
                            this.kdf_ = input.readEnum();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 24: {
                            this.aead_ = input.readEnum();
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
        public int getKemValue() {
            return this.kem_;
        }

        public Builder setKemValue(int value) {
            this.kem_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        @Override
        public HpkeKem getKem() {
            HpkeKem result = HpkeKem.forNumber(this.kem_);
            return result == null ? HpkeKem.UNRECOGNIZED : result;
        }

        public Builder setKem(HpkeKem value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 1;
            this.kem_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearKem() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.kem_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getKdfValue() {
            return this.kdf_;
        }

        public Builder setKdfValue(int value) {
            this.kdf_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        @Override
        public HpkeKdf getKdf() {
            HpkeKdf result = HpkeKdf.forNumber(this.kdf_);
            return result == null ? HpkeKdf.UNRECOGNIZED : result;
        }

        public Builder setKdf(HpkeKdf value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 2;
            this.kdf_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearKdf() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.kdf_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getAeadValue() {
            return this.aead_;
        }

        public Builder setAeadValue(int value) {
            this.aead_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        @Override
        public HpkeAead getAead() {
            HpkeAead result = HpkeAead.forNumber(this.aead_);
            return result == null ? HpkeAead.UNRECOGNIZED : result;
        }

        public Builder setAead(HpkeAead value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 4;
            this.aead_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearAead() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.aead_ = 0;
            this.onChanged();
            return this;
        }
    }
}

