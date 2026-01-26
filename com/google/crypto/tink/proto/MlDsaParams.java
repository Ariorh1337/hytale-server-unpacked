/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.MlDsa;
import com.google.crypto.tink.proto.MlDsaInstance;
import com.google.crypto.tink.proto.MlDsaParamsOrBuilder;
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

public final class MlDsaParams
extends GeneratedMessage
implements MlDsaParamsOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int ML_DSA_INSTANCE_FIELD_NUMBER = 1;
    private int mlDsaInstance_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final MlDsaParams DEFAULT_INSTANCE;
    private static final Parser<MlDsaParams> PARSER;

    private MlDsaParams(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private MlDsaParams() {
        this.mlDsaInstance_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return MlDsa.internal_static_google_crypto_tink_MlDsaParams_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return MlDsa.internal_static_google_crypto_tink_MlDsaParams_fieldAccessorTable.ensureFieldAccessorsInitialized(MlDsaParams.class, Builder.class);
    }

    @Override
    public int getMlDsaInstanceValue() {
        return this.mlDsaInstance_;
    }

    @Override
    public MlDsaInstance getMlDsaInstance() {
        MlDsaInstance result = MlDsaInstance.forNumber(this.mlDsaInstance_);
        return result == null ? MlDsaInstance.UNRECOGNIZED : result;
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
        if (this.mlDsaInstance_ != MlDsaInstance.ML_DSA_UNKNOWN_INSTANCE.getNumber()) {
            output.writeEnum(1, this.mlDsaInstance_);
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
        if (this.mlDsaInstance_ != MlDsaInstance.ML_DSA_UNKNOWN_INSTANCE.getNumber()) {
            size += CodedOutputStream.computeEnumSize(1, this.mlDsaInstance_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof MlDsaParams)) {
            return super.equals(obj);
        }
        MlDsaParams other = (MlDsaParams)obj;
        if (this.mlDsaInstance_ != other.mlDsaInstance_) {
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
        hash = 19 * hash + MlDsaParams.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.mlDsaInstance_;
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static MlDsaParams parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static MlDsaParams parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static MlDsaParams parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static MlDsaParams parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static MlDsaParams parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static MlDsaParams parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static MlDsaParams parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static MlDsaParams parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static MlDsaParams parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static MlDsaParams parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static MlDsaParams parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static MlDsaParams parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return MlDsaParams.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(MlDsaParams prototype) {
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

    public static MlDsaParams getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<MlDsaParams> parser() {
        return PARSER;
    }

    public Parser<MlDsaParams> getParserForType() {
        return PARSER;
    }

    @Override
    public MlDsaParams getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", MlDsaParams.class.getName());
        DEFAULT_INSTANCE = new MlDsaParams();
        PARSER = new AbstractParser<MlDsaParams>(){

            @Override
            public MlDsaParams parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = MlDsaParams.newBuilder();
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
    implements MlDsaParamsOrBuilder {
        private int bitField0_;
        private int mlDsaInstance_ = 0;

        public static final Descriptors.Descriptor getDescriptor() {
            return MlDsa.internal_static_google_crypto_tink_MlDsaParams_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return MlDsa.internal_static_google_crypto_tink_MlDsaParams_fieldAccessorTable.ensureFieldAccessorsInitialized(MlDsaParams.class, Builder.class);
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
            this.mlDsaInstance_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return MlDsa.internal_static_google_crypto_tink_MlDsaParams_descriptor;
        }

        @Override
        public MlDsaParams getDefaultInstanceForType() {
            return MlDsaParams.getDefaultInstance();
        }

        @Override
        public MlDsaParams build() {
            MlDsaParams result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public MlDsaParams buildPartial() {
            MlDsaParams result = new MlDsaParams(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(MlDsaParams result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.mlDsaInstance_ = this.mlDsaInstance_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof MlDsaParams) {
                return this.mergeFrom((MlDsaParams)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(MlDsaParams other) {
            if (other == MlDsaParams.getDefaultInstance()) {
                return this;
            }
            if (other.mlDsaInstance_ != 0) {
                this.setMlDsaInstanceValue(other.getMlDsaInstanceValue());
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
                block9: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block9;
                        }
                        case 8: {
                            this.mlDsaInstance_ = input.readEnum();
                            this.bitField0_ |= 1;
                            continue block9;
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
        public int getMlDsaInstanceValue() {
            return this.mlDsaInstance_;
        }

        public Builder setMlDsaInstanceValue(int value) {
            this.mlDsaInstance_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        @Override
        public MlDsaInstance getMlDsaInstance() {
            MlDsaInstance result = MlDsaInstance.forNumber(this.mlDsaInstance_);
            return result == null ? MlDsaInstance.UNRECOGNIZED : result;
        }

        public Builder setMlDsaInstance(MlDsaInstance value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 1;
            this.mlDsaInstance_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearMlDsaInstance() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.mlDsaInstance_ = 0;
            this.onChanged();
            return this;
        }
    }
}

