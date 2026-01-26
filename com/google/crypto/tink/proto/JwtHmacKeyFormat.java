/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtHmac;
import com.google.crypto.tink.proto.JwtHmacAlgorithm;
import com.google.crypto.tink.proto.JwtHmacKeyFormatOrBuilder;
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

public final class JwtHmacKeyFormat
extends GeneratedMessage
implements JwtHmacKeyFormatOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_ = 0;
    public static final int ALGORITHM_FIELD_NUMBER = 2;
    private int algorithm_ = 0;
    public static final int KEY_SIZE_FIELD_NUMBER = 3;
    private int keySize_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final JwtHmacKeyFormat DEFAULT_INSTANCE;
    private static final Parser<JwtHmacKeyFormat> PARSER;

    private JwtHmacKeyFormat(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private JwtHmacKeyFormat() {
        this.algorithm_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return JwtHmac.internal_static_google_crypto_tink_JwtHmacKeyFormat_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return JwtHmac.internal_static_google_crypto_tink_JwtHmacKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(JwtHmacKeyFormat.class, Builder.class);
    }

    @Override
    public int getVersion() {
        return this.version_;
    }

    @Override
    public int getAlgorithmValue() {
        return this.algorithm_;
    }

    @Override
    public JwtHmacAlgorithm getAlgorithm() {
        JwtHmacAlgorithm result = JwtHmacAlgorithm.forNumber(this.algorithm_);
        return result == null ? JwtHmacAlgorithm.UNRECOGNIZED : result;
    }

    @Override
    public int getKeySize() {
        return this.keySize_;
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
        if (this.algorithm_ != JwtHmacAlgorithm.HS_UNKNOWN.getNumber()) {
            output.writeEnum(2, this.algorithm_);
        }
        if (this.keySize_ != 0) {
            output.writeUInt32(3, this.keySize_);
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
        if (this.algorithm_ != JwtHmacAlgorithm.HS_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.algorithm_);
        }
        if (this.keySize_ != 0) {
            size += CodedOutputStream.computeUInt32Size(3, this.keySize_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof JwtHmacKeyFormat)) {
            return super.equals(obj);
        }
        JwtHmacKeyFormat other = (JwtHmacKeyFormat)obj;
        if (this.getVersion() != other.getVersion()) {
            return false;
        }
        if (this.algorithm_ != other.algorithm_) {
            return false;
        }
        if (this.getKeySize() != other.getKeySize()) {
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
        hash = 19 * hash + JwtHmacKeyFormat.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getVersion();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.algorithm_;
        hash = 37 * hash + 3;
        hash = 53 * hash + this.getKeySize();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static JwtHmacKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static JwtHmacKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static JwtHmacKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static JwtHmacKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static JwtHmacKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static JwtHmacKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static JwtHmacKeyFormat parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static JwtHmacKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static JwtHmacKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static JwtHmacKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static JwtHmacKeyFormat parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static JwtHmacKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return JwtHmacKeyFormat.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(JwtHmacKeyFormat prototype) {
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

    public static JwtHmacKeyFormat getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<JwtHmacKeyFormat> parser() {
        return PARSER;
    }

    public Parser<JwtHmacKeyFormat> getParserForType() {
        return PARSER;
    }

    @Override
    public JwtHmacKeyFormat getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtHmacKeyFormat.class.getName());
        DEFAULT_INSTANCE = new JwtHmacKeyFormat();
        PARSER = new AbstractParser<JwtHmacKeyFormat>(){

            @Override
            public JwtHmacKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = JwtHmacKeyFormat.newBuilder();
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
    implements JwtHmacKeyFormatOrBuilder {
        private int bitField0_;
        private int version_;
        private int algorithm_ = 0;
        private int keySize_;

        public static final Descriptors.Descriptor getDescriptor() {
            return JwtHmac.internal_static_google_crypto_tink_JwtHmacKeyFormat_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return JwtHmac.internal_static_google_crypto_tink_JwtHmacKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(JwtHmacKeyFormat.class, Builder.class);
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
            this.version_ = 0;
            this.algorithm_ = 0;
            this.keySize_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return JwtHmac.internal_static_google_crypto_tink_JwtHmacKeyFormat_descriptor;
        }

        @Override
        public JwtHmacKeyFormat getDefaultInstanceForType() {
            return JwtHmacKeyFormat.getDefaultInstance();
        }

        @Override
        public JwtHmacKeyFormat build() {
            JwtHmacKeyFormat result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public JwtHmacKeyFormat buildPartial() {
            JwtHmacKeyFormat result = new JwtHmacKeyFormat(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(JwtHmacKeyFormat result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.version_ = this.version_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.algorithm_ = this.algorithm_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.keySize_ = this.keySize_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof JwtHmacKeyFormat) {
                return this.mergeFrom((JwtHmacKeyFormat)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(JwtHmacKeyFormat other) {
            if (other == JwtHmacKeyFormat.getDefaultInstance()) {
                return this;
            }
            if (other.getVersion() != 0) {
                this.setVersion(other.getVersion());
            }
            if (other.algorithm_ != 0) {
                this.setAlgorithmValue(other.getAlgorithmValue());
            }
            if (other.getKeySize() != 0) {
                this.setKeySize(other.getKeySize());
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
                        case 16: {
                            this.algorithm_ = input.readEnum();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 24: {
                            this.keySize_ = input.readUInt32();
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
        public int getAlgorithmValue() {
            return this.algorithm_;
        }

        public Builder setAlgorithmValue(int value) {
            this.algorithm_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        @Override
        public JwtHmacAlgorithm getAlgorithm() {
            JwtHmacAlgorithm result = JwtHmacAlgorithm.forNumber(this.algorithm_);
            return result == null ? JwtHmacAlgorithm.UNRECOGNIZED : result;
        }

        public Builder setAlgorithm(JwtHmacAlgorithm value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 2;
            this.algorithm_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearAlgorithm() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.algorithm_ = 0;
            this.onChanged();
            return this;
        }

        @Override
        public int getKeySize() {
            return this.keySize_;
        }

        public Builder setKeySize(int value) {
            this.keySize_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        public Builder clearKeySize() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.keySize_ = 0;
            this.onChanged();
            return this;
        }
    }
}

