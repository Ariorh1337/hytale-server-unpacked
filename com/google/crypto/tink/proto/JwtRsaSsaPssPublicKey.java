/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.JwtRsaSsaPss;
import com.google.crypto.tink.proto.JwtRsaSsaPssAlgorithm;
import com.google.crypto.tink.proto.JwtRsaSsaPssPublicKeyOrBuilder;
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
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.Parser;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class JwtRsaSsaPssPublicKey
extends GeneratedMessage
implements JwtRsaSsaPssPublicKeyOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int VERSION_FIELD_NUMBER = 1;
    private int version_ = 0;
    public static final int ALGORITHM_FIELD_NUMBER = 2;
    private int algorithm_ = 0;
    public static final int N_FIELD_NUMBER = 3;
    private ByteString n_ = ByteString.EMPTY;
    public static final int E_FIELD_NUMBER = 4;
    private ByteString e_ = ByteString.EMPTY;
    public static final int CUSTOM_KID_FIELD_NUMBER = 5;
    private CustomKid customKid_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final JwtRsaSsaPssPublicKey DEFAULT_INSTANCE;
    private static final Parser<JwtRsaSsaPssPublicKey> PARSER;

    private JwtRsaSsaPssPublicKey(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private JwtRsaSsaPssPublicKey() {
        this.algorithm_ = 0;
        this.n_ = ByteString.EMPTY;
        this.e_ = ByteString.EMPTY;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_fieldAccessorTable.ensureFieldAccessorsInitialized(JwtRsaSsaPssPublicKey.class, Builder.class);
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
    public JwtRsaSsaPssAlgorithm getAlgorithm() {
        JwtRsaSsaPssAlgorithm result = JwtRsaSsaPssAlgorithm.forNumber(this.algorithm_);
        return result == null ? JwtRsaSsaPssAlgorithm.UNRECOGNIZED : result;
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
    public boolean hasCustomKid() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public CustomKid getCustomKid() {
        return this.customKid_ == null ? CustomKid.getDefaultInstance() : this.customKid_;
    }

    @Override
    public CustomKidOrBuilder getCustomKidOrBuilder() {
        return this.customKid_ == null ? CustomKid.getDefaultInstance() : this.customKid_;
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
        if (this.algorithm_ != JwtRsaSsaPssAlgorithm.PS_UNKNOWN.getNumber()) {
            output.writeEnum(2, this.algorithm_);
        }
        if (!this.n_.isEmpty()) {
            output.writeBytes(3, this.n_);
        }
        if (!this.e_.isEmpty()) {
            output.writeBytes(4, this.e_);
        }
        if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(5, this.getCustomKid());
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
        if (this.algorithm_ != JwtRsaSsaPssAlgorithm.PS_UNKNOWN.getNumber()) {
            size += CodedOutputStream.computeEnumSize(2, this.algorithm_);
        }
        if (!this.n_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(3, this.n_);
        }
        if (!this.e_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(4, this.e_);
        }
        if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(5, this.getCustomKid());
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof JwtRsaSsaPssPublicKey)) {
            return super.equals(obj);
        }
        JwtRsaSsaPssPublicKey other = (JwtRsaSsaPssPublicKey)obj;
        if (this.getVersion() != other.getVersion()) {
            return false;
        }
        if (this.algorithm_ != other.algorithm_) {
            return false;
        }
        if (!this.getN().equals(other.getN())) {
            return false;
        }
        if (!this.getE().equals(other.getE())) {
            return false;
        }
        if (this.hasCustomKid() != other.hasCustomKid()) {
            return false;
        }
        if (this.hasCustomKid() && !this.getCustomKid().equals(other.getCustomKid())) {
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
        hash = 19 * hash + JwtRsaSsaPssPublicKey.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getVersion();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.algorithm_;
        hash = 37 * hash + 3;
        hash = 53 * hash + this.getN().hashCode();
        hash = 37 * hash + 4;
        hash = 53 * hash + this.getE().hashCode();
        if (this.hasCustomKid()) {
            hash = 37 * hash + 5;
            hash = 53 * hash + this.getCustomKid().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static JwtRsaSsaPssPublicKey parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static JwtRsaSsaPssPublicKey parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static JwtRsaSsaPssPublicKey parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static JwtRsaSsaPssPublicKey parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return JwtRsaSsaPssPublicKey.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(JwtRsaSsaPssPublicKey prototype) {
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

    public static JwtRsaSsaPssPublicKey getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<JwtRsaSsaPssPublicKey> parser() {
        return PARSER;
    }

    public Parser<JwtRsaSsaPssPublicKey> getParserForType() {
        return PARSER;
    }

    @Override
    public JwtRsaSsaPssPublicKey getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", JwtRsaSsaPssPublicKey.class.getName());
        DEFAULT_INSTANCE = new JwtRsaSsaPssPublicKey();
        PARSER = new AbstractParser<JwtRsaSsaPssPublicKey>(){

            @Override
            public JwtRsaSsaPssPublicKey parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = JwtRsaSsaPssPublicKey.newBuilder();
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

    public static final class CustomKid
    extends GeneratedMessage
    implements CustomKidOrBuilder {
        private static final long serialVersionUID = 0L;
        public static final int VALUE_FIELD_NUMBER = 1;
        private volatile Object value_ = "";
        private byte memoizedIsInitialized = (byte)-1;
        private static final CustomKid DEFAULT_INSTANCE;
        private static final Parser<CustomKid> PARSER;

        private CustomKid(GeneratedMessage.Builder<?> builder) {
            super(builder);
        }

        private CustomKid() {
            this.value_ = "";
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_CustomKid_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_CustomKid_fieldAccessorTable.ensureFieldAccessorsInitialized(CustomKid.class, Builder.class);
        }

        @Override
        public String getValue() {
            Object ref = this.value_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.value_ = s;
            return s;
        }

        @Override
        public ByteString getValueBytes() {
            Object ref = this.value_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.value_ = b;
                return b;
            }
            return (ByteString)ref;
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
            if (!GeneratedMessage.isStringEmpty(this.value_)) {
                GeneratedMessage.writeString(output, 1, this.value_);
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
            if (!GeneratedMessage.isStringEmpty(this.value_)) {
                size += GeneratedMessage.computeStringSize(1, this.value_);
            }
            this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof CustomKid)) {
                return super.equals(obj);
            }
            CustomKid other = (CustomKid)obj;
            if (!this.getValue().equals(other.getValue())) {
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
            hash = 19 * hash + CustomKid.getDescriptor().hashCode();
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getValue().hashCode();
            this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
            return hash;
        }

        public static CustomKid parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CustomKid parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CustomKid parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CustomKid parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CustomKid parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static CustomKid parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static CustomKid parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static CustomKid parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static CustomKid parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
        }

        public static CustomKid parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static CustomKid parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static CustomKid parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return CustomKid.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(CustomKid prototype) {
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

        public static CustomKid getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<CustomKid> parser() {
            return PARSER;
        }

        public Parser<CustomKid> getParserForType() {
            return PARSER;
        }

        @Override
        public CustomKid getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", CustomKid.class.getName());
            DEFAULT_INSTANCE = new CustomKid();
            PARSER = new AbstractParser<CustomKid>(){

                @Override
                public CustomKid parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    Builder builder = CustomKid.newBuilder();
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
        implements CustomKidOrBuilder {
            private int bitField0_;
            private Object value_ = "";

            public static final Descriptors.Descriptor getDescriptor() {
                return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_CustomKid_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_CustomKid_fieldAccessorTable.ensureFieldAccessorsInitialized(CustomKid.class, Builder.class);
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
                this.value_ = "";
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_CustomKid_descriptor;
            }

            @Override
            public CustomKid getDefaultInstanceForType() {
                return CustomKid.getDefaultInstance();
            }

            @Override
            public CustomKid build() {
                CustomKid result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public CustomKid buildPartial() {
                CustomKid result = new CustomKid(this);
                if (this.bitField0_ != 0) {
                    this.buildPartial0(result);
                }
                this.onBuilt();
                return result;
            }

            private void buildPartial0(CustomKid result) {
                int from_bitField0_ = this.bitField0_;
                if ((from_bitField0_ & 1) != 0) {
                    result.value_ = this.value_;
                }
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof CustomKid) {
                    return this.mergeFrom((CustomKid)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(CustomKid other) {
                if (other == CustomKid.getDefaultInstance()) {
                    return this;
                }
                if (!other.getValue().isEmpty()) {
                    this.value_ = other.value_;
                    this.bitField0_ |= 1;
                    this.onChanged();
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
                            case 10: {
                                this.value_ = input.readStringRequireUtf8();
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
            public String getValue() {
                Object ref = this.value_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    this.value_ = s;
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getValueBytes() {
                Object ref = this.value_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.value_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setValue(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.value_ = value;
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder clearValue() {
                this.value_ = CustomKid.getDefaultInstance().getValue();
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }

            public Builder setValueBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                CustomKid.checkByteStringIsUtf8(value);
                this.value_ = value;
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }
        }
    }

    public static final class Builder
    extends GeneratedMessage.Builder<Builder>
    implements JwtRsaSsaPssPublicKeyOrBuilder {
        private int bitField0_;
        private int version_;
        private int algorithm_ = 0;
        private ByteString n_ = ByteString.EMPTY;
        private ByteString e_ = ByteString.EMPTY;
        private CustomKid customKid_;
        private SingleFieldBuilder<CustomKid, CustomKid.Builder, CustomKidOrBuilder> customKidBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_fieldAccessorTable.ensureFieldAccessorsInitialized(JwtRsaSsaPssPublicKey.class, Builder.class);
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
                this.internalGetCustomKidFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.version_ = 0;
            this.algorithm_ = 0;
            this.n_ = ByteString.EMPTY;
            this.e_ = ByteString.EMPTY;
            this.customKid_ = null;
            if (this.customKidBuilder_ != null) {
                this.customKidBuilder_.dispose();
                this.customKidBuilder_ = null;
            }
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return JwtRsaSsaPss.internal_static_google_crypto_tink_JwtRsaSsaPssPublicKey_descriptor;
        }

        @Override
        public JwtRsaSsaPssPublicKey getDefaultInstanceForType() {
            return JwtRsaSsaPssPublicKey.getDefaultInstance();
        }

        @Override
        public JwtRsaSsaPssPublicKey build() {
            JwtRsaSsaPssPublicKey result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public JwtRsaSsaPssPublicKey buildPartial() {
            JwtRsaSsaPssPublicKey result = new JwtRsaSsaPssPublicKey(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(JwtRsaSsaPssPublicKey result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.version_ = this.version_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.algorithm_ = this.algorithm_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.n_ = this.n_;
            }
            if ((from_bitField0_ & 8) != 0) {
                result.e_ = this.e_;
            }
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 0x10) != 0) {
                result.customKid_ = this.customKidBuilder_ == null ? this.customKid_ : this.customKidBuilder_.build();
                to_bitField0_ |= 1;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof JwtRsaSsaPssPublicKey) {
                return this.mergeFrom((JwtRsaSsaPssPublicKey)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(JwtRsaSsaPssPublicKey other) {
            if (other == JwtRsaSsaPssPublicKey.getDefaultInstance()) {
                return this;
            }
            if (other.getVersion() != 0) {
                this.setVersion(other.getVersion());
            }
            if (other.algorithm_ != 0) {
                this.setAlgorithmValue(other.getAlgorithmValue());
            }
            if (!other.getN().isEmpty()) {
                this.setN(other.getN());
            }
            if (!other.getE().isEmpty()) {
                this.setE(other.getE());
            }
            if (other.hasCustomKid()) {
                this.mergeCustomKid(other.getCustomKid());
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
                block13: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block13;
                        }
                        case 8: {
                            this.version_ = input.readUInt32();
                            this.bitField0_ |= 1;
                            continue block13;
                        }
                        case 16: {
                            this.algorithm_ = input.readEnum();
                            this.bitField0_ |= 2;
                            continue block13;
                        }
                        case 26: {
                            this.n_ = input.readBytes();
                            this.bitField0_ |= 4;
                            continue block13;
                        }
                        case 34: {
                            this.e_ = input.readBytes();
                            this.bitField0_ |= 8;
                            continue block13;
                        }
                        case 42: {
                            input.readMessage(this.internalGetCustomKidFieldBuilder().getBuilder(), extensionRegistry);
                            this.bitField0_ |= 0x10;
                            continue block13;
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
        public JwtRsaSsaPssAlgorithm getAlgorithm() {
            JwtRsaSsaPssAlgorithm result = JwtRsaSsaPssAlgorithm.forNumber(this.algorithm_);
            return result == null ? JwtRsaSsaPssAlgorithm.UNRECOGNIZED : result;
        }

        public Builder setAlgorithm(JwtRsaSsaPssAlgorithm value) {
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
            this.n_ = JwtRsaSsaPssPublicKey.getDefaultInstance().getN();
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
            this.e_ = JwtRsaSsaPssPublicKey.getDefaultInstance().getE();
            this.onChanged();
            return this;
        }

        @Override
        public boolean hasCustomKid() {
            return (this.bitField0_ & 0x10) != 0;
        }

        @Override
        public CustomKid getCustomKid() {
            if (this.customKidBuilder_ == null) {
                return this.customKid_ == null ? CustomKid.getDefaultInstance() : this.customKid_;
            }
            return this.customKidBuilder_.getMessage();
        }

        public Builder setCustomKid(CustomKid value) {
            if (this.customKidBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.customKid_ = value;
            } else {
                this.customKidBuilder_.setMessage(value);
            }
            this.bitField0_ |= 0x10;
            this.onChanged();
            return this;
        }

        public Builder setCustomKid(CustomKid.Builder builderForValue) {
            if (this.customKidBuilder_ == null) {
                this.customKid_ = builderForValue.build();
            } else {
                this.customKidBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 0x10;
            this.onChanged();
            return this;
        }

        public Builder mergeCustomKid(CustomKid value) {
            if (this.customKidBuilder_ == null) {
                if ((this.bitField0_ & 0x10) != 0 && this.customKid_ != null && this.customKid_ != CustomKid.getDefaultInstance()) {
                    this.getCustomKidBuilder().mergeFrom(value);
                } else {
                    this.customKid_ = value;
                }
            } else {
                this.customKidBuilder_.mergeFrom(value);
            }
            if (this.customKid_ != null) {
                this.bitField0_ |= 0x10;
                this.onChanged();
            }
            return this;
        }

        public Builder clearCustomKid() {
            this.bitField0_ &= 0xFFFFFFEF;
            this.customKid_ = null;
            if (this.customKidBuilder_ != null) {
                this.customKidBuilder_.dispose();
                this.customKidBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public CustomKid.Builder getCustomKidBuilder() {
            this.bitField0_ |= 0x10;
            this.onChanged();
            return this.internalGetCustomKidFieldBuilder().getBuilder();
        }

        @Override
        public CustomKidOrBuilder getCustomKidOrBuilder() {
            if (this.customKidBuilder_ != null) {
                return this.customKidBuilder_.getMessageOrBuilder();
            }
            return this.customKid_ == null ? CustomKid.getDefaultInstance() : this.customKid_;
        }

        private SingleFieldBuilder<CustomKid, CustomKid.Builder, CustomKidOrBuilder> internalGetCustomKidFieldBuilder() {
            if (this.customKidBuilder_ == null) {
                this.customKidBuilder_ = new SingleFieldBuilder(this.getCustomKid(), this.getParentForChildren(), this.isClean());
                this.customKid_ = null;
            }
            return this.customKidBuilder_;
        }
    }

    public static interface CustomKidOrBuilder
    extends MessageOrBuilder {
        public String getValue();

        public ByteString getValueBytes();
    }
}

