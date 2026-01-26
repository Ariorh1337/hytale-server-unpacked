/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KmsAead;
import com.google.crypto.tink.proto.KmsAeadKeyFormatOrBuilder;
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

public final class KmsAeadKeyFormat
extends GeneratedMessage
implements KmsAeadKeyFormatOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int KEY_URI_FIELD_NUMBER = 1;
    private volatile Object keyUri_ = "";
    private byte memoizedIsInitialized = (byte)-1;
    private static final KmsAeadKeyFormat DEFAULT_INSTANCE;
    private static final Parser<KmsAeadKeyFormat> PARSER;

    private KmsAeadKeyFormat(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private KmsAeadKeyFormat() {
        this.keyUri_ = "";
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return KmsAead.internal_static_google_crypto_tink_KmsAeadKeyFormat_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return KmsAead.internal_static_google_crypto_tink_KmsAeadKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(KmsAeadKeyFormat.class, Builder.class);
    }

    @Override
    public String getKeyUri() {
        Object ref = this.keyUri_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.keyUri_ = s;
        return s;
    }

    @Override
    public ByteString getKeyUriBytes() {
        Object ref = this.keyUri_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.keyUri_ = b;
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
        if (!GeneratedMessage.isStringEmpty(this.keyUri_)) {
            GeneratedMessage.writeString(output, 1, this.keyUri_);
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
        if (!GeneratedMessage.isStringEmpty(this.keyUri_)) {
            size += GeneratedMessage.computeStringSize(1, this.keyUri_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KmsAeadKeyFormat)) {
            return super.equals(obj);
        }
        KmsAeadKeyFormat other = (KmsAeadKeyFormat)obj;
        if (!this.getKeyUri().equals(other.getKeyUri())) {
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
        hash = 19 * hash + KmsAeadKeyFormat.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getKeyUri().hashCode();
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static KmsAeadKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KmsAeadKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KmsAeadKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KmsAeadKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KmsAeadKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KmsAeadKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KmsAeadKeyFormat parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static KmsAeadKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static KmsAeadKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static KmsAeadKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static KmsAeadKeyFormat parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static KmsAeadKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return KmsAeadKeyFormat.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(KmsAeadKeyFormat prototype) {
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

    public static KmsAeadKeyFormat getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<KmsAeadKeyFormat> parser() {
        return PARSER;
    }

    public Parser<KmsAeadKeyFormat> getParserForType() {
        return PARSER;
    }

    @Override
    public KmsAeadKeyFormat getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KmsAeadKeyFormat.class.getName());
        DEFAULT_INSTANCE = new KmsAeadKeyFormat();
        PARSER = new AbstractParser<KmsAeadKeyFormat>(){

            @Override
            public KmsAeadKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = KmsAeadKeyFormat.newBuilder();
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
    implements KmsAeadKeyFormatOrBuilder {
        private int bitField0_;
        private Object keyUri_ = "";

        public static final Descriptors.Descriptor getDescriptor() {
            return KmsAead.internal_static_google_crypto_tink_KmsAeadKeyFormat_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return KmsAead.internal_static_google_crypto_tink_KmsAeadKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(KmsAeadKeyFormat.class, Builder.class);
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
            this.keyUri_ = "";
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return KmsAead.internal_static_google_crypto_tink_KmsAeadKeyFormat_descriptor;
        }

        @Override
        public KmsAeadKeyFormat getDefaultInstanceForType() {
            return KmsAeadKeyFormat.getDefaultInstance();
        }

        @Override
        public KmsAeadKeyFormat build() {
            KmsAeadKeyFormat result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public KmsAeadKeyFormat buildPartial() {
            KmsAeadKeyFormat result = new KmsAeadKeyFormat(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(KmsAeadKeyFormat result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.keyUri_ = this.keyUri_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof KmsAeadKeyFormat) {
                return this.mergeFrom((KmsAeadKeyFormat)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(KmsAeadKeyFormat other) {
            if (other == KmsAeadKeyFormat.getDefaultInstance()) {
                return this;
            }
            if (!other.getKeyUri().isEmpty()) {
                this.keyUri_ = other.keyUri_;
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
                            this.keyUri_ = input.readStringRequireUtf8();
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
        public String getKeyUri() {
            Object ref = this.keyUri_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.keyUri_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        public ByteString getKeyUriBytes() {
            Object ref = this.keyUri_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.keyUri_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        public Builder setKeyUri(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.keyUri_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearKeyUri() {
            this.keyUri_ = KmsAeadKeyFormat.getDefaultInstance().getKeyUri();
            this.bitField0_ &= 0xFFFFFFFE;
            this.onChanged();
            return this;
        }

        public Builder setKeyUriBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            KmsAeadKeyFormat.checkByteStringIsUtf8(value);
            this.keyUri_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }
    }
}

