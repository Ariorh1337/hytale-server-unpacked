/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyTemplate;
import com.google.crypto.tink.proto.KeyTemplateOrBuilder;
import com.google.crypto.tink.proto.KmsEnvelope;
import com.google.crypto.tink.proto.KmsEnvelopeAeadKeyFormatOrBuilder;
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

public final class KmsEnvelopeAeadKeyFormat
extends GeneratedMessage
implements KmsEnvelopeAeadKeyFormatOrBuilder {
    private static final long serialVersionUID = 0L;
    private int bitField0_;
    public static final int KEK_URI_FIELD_NUMBER = 1;
    private volatile Object kekUri_ = "";
    public static final int DEK_TEMPLATE_FIELD_NUMBER = 2;
    private KeyTemplate dekTemplate_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final KmsEnvelopeAeadKeyFormat DEFAULT_INSTANCE;
    private static final Parser<KmsEnvelopeAeadKeyFormat> PARSER;

    private KmsEnvelopeAeadKeyFormat(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private KmsEnvelopeAeadKeyFormat() {
        this.kekUri_ = "";
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return KmsEnvelope.internal_static_google_crypto_tink_KmsEnvelopeAeadKeyFormat_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return KmsEnvelope.internal_static_google_crypto_tink_KmsEnvelopeAeadKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(KmsEnvelopeAeadKeyFormat.class, Builder.class);
    }

    @Override
    public String getKekUri() {
        Object ref = this.kekUri_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.kekUri_ = s;
        return s;
    }

    @Override
    public ByteString getKekUriBytes() {
        Object ref = this.kekUri_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.kekUri_ = b;
            return b;
        }
        return (ByteString)ref;
    }

    @Override
    public boolean hasDekTemplate() {
        return (this.bitField0_ & 1) != 0;
    }

    @Override
    public KeyTemplate getDekTemplate() {
        return this.dekTemplate_ == null ? KeyTemplate.getDefaultInstance() : this.dekTemplate_;
    }

    @Override
    public KeyTemplateOrBuilder getDekTemplateOrBuilder() {
        return this.dekTemplate_ == null ? KeyTemplate.getDefaultInstance() : this.dekTemplate_;
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
        if (!GeneratedMessage.isStringEmpty(this.kekUri_)) {
            GeneratedMessage.writeString(output, 1, this.kekUri_);
        }
        if ((this.bitField0_ & 1) != 0) {
            output.writeMessage(2, this.getDekTemplate());
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
        if (!GeneratedMessage.isStringEmpty(this.kekUri_)) {
            size += GeneratedMessage.computeStringSize(1, this.kekUri_);
        }
        if ((this.bitField0_ & 1) != 0) {
            size += CodedOutputStream.computeMessageSize(2, this.getDekTemplate());
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KmsEnvelopeAeadKeyFormat)) {
            return super.equals(obj);
        }
        KmsEnvelopeAeadKeyFormat other = (KmsEnvelopeAeadKeyFormat)obj;
        if (!this.getKekUri().equals(other.getKekUri())) {
            return false;
        }
        if (this.hasDekTemplate() != other.hasDekTemplate()) {
            return false;
        }
        if (this.hasDekTemplate() && !this.getDekTemplate().equals(other.getDekTemplate())) {
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
        hash = 19 * hash + KmsEnvelopeAeadKeyFormat.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getKekUri().hashCode();
        if (this.hasDekTemplate()) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getDekTemplate().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static KmsEnvelopeAeadKeyFormat parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static KmsEnvelopeAeadKeyFormat parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static KmsEnvelopeAeadKeyFormat parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return KmsEnvelopeAeadKeyFormat.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(KmsEnvelopeAeadKeyFormat prototype) {
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

    public static KmsEnvelopeAeadKeyFormat getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<KmsEnvelopeAeadKeyFormat> parser() {
        return PARSER;
    }

    public Parser<KmsEnvelopeAeadKeyFormat> getParserForType() {
        return PARSER;
    }

    @Override
    public KmsEnvelopeAeadKeyFormat getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KmsEnvelopeAeadKeyFormat.class.getName());
        DEFAULT_INSTANCE = new KmsEnvelopeAeadKeyFormat();
        PARSER = new AbstractParser<KmsEnvelopeAeadKeyFormat>(){

            @Override
            public KmsEnvelopeAeadKeyFormat parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = KmsEnvelopeAeadKeyFormat.newBuilder();
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
    implements KmsEnvelopeAeadKeyFormatOrBuilder {
        private int bitField0_;
        private Object kekUri_ = "";
        private KeyTemplate dekTemplate_;
        private SingleFieldBuilder<KeyTemplate, KeyTemplate.Builder, KeyTemplateOrBuilder> dekTemplateBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return KmsEnvelope.internal_static_google_crypto_tink_KmsEnvelopeAeadKeyFormat_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return KmsEnvelope.internal_static_google_crypto_tink_KmsEnvelopeAeadKeyFormat_fieldAccessorTable.ensureFieldAccessorsInitialized(KmsEnvelopeAeadKeyFormat.class, Builder.class);
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
                this.internalGetDekTemplateFieldBuilder();
            }
        }

        @Override
        public Builder clear() {
            super.clear();
            this.bitField0_ = 0;
            this.kekUri_ = "";
            this.dekTemplate_ = null;
            if (this.dekTemplateBuilder_ != null) {
                this.dekTemplateBuilder_.dispose();
                this.dekTemplateBuilder_ = null;
            }
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return KmsEnvelope.internal_static_google_crypto_tink_KmsEnvelopeAeadKeyFormat_descriptor;
        }

        @Override
        public KmsEnvelopeAeadKeyFormat getDefaultInstanceForType() {
            return KmsEnvelopeAeadKeyFormat.getDefaultInstance();
        }

        @Override
        public KmsEnvelopeAeadKeyFormat build() {
            KmsEnvelopeAeadKeyFormat result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public KmsEnvelopeAeadKeyFormat buildPartial() {
            KmsEnvelopeAeadKeyFormat result = new KmsEnvelopeAeadKeyFormat(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(KmsEnvelopeAeadKeyFormat result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.kekUri_ = this.kekUri_;
            }
            int to_bitField0_ = 0;
            if ((from_bitField0_ & 2) != 0) {
                result.dekTemplate_ = this.dekTemplateBuilder_ == null ? this.dekTemplate_ : this.dekTemplateBuilder_.build();
                to_bitField0_ |= 1;
            }
            result.bitField0_ |= to_bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof KmsEnvelopeAeadKeyFormat) {
                return this.mergeFrom((KmsEnvelopeAeadKeyFormat)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(KmsEnvelopeAeadKeyFormat other) {
            if (other == KmsEnvelopeAeadKeyFormat.getDefaultInstance()) {
                return this;
            }
            if (!other.getKekUri().isEmpty()) {
                this.kekUri_ = other.kekUri_;
                this.bitField0_ |= 1;
                this.onChanged();
            }
            if (other.hasDekTemplate()) {
                this.mergeDekTemplate(other.getDekTemplate());
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
                        case 10: {
                            this.kekUri_ = input.readStringRequireUtf8();
                            this.bitField0_ |= 1;
                            continue block10;
                        }
                        case 18: {
                            input.readMessage(this.internalGetDekTemplateFieldBuilder().getBuilder(), extensionRegistry);
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
        public String getKekUri() {
            Object ref = this.kekUri_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.kekUri_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        public ByteString getKekUriBytes() {
            Object ref = this.kekUri_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.kekUri_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        public Builder setKekUri(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.kekUri_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearKekUri() {
            this.kekUri_ = KmsEnvelopeAeadKeyFormat.getDefaultInstance().getKekUri();
            this.bitField0_ &= 0xFFFFFFFE;
            this.onChanged();
            return this;
        }

        public Builder setKekUriBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            KmsEnvelopeAeadKeyFormat.checkByteStringIsUtf8(value);
            this.kekUri_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        @Override
        public boolean hasDekTemplate() {
            return (this.bitField0_ & 2) != 0;
        }

        @Override
        public KeyTemplate getDekTemplate() {
            if (this.dekTemplateBuilder_ == null) {
                return this.dekTemplate_ == null ? KeyTemplate.getDefaultInstance() : this.dekTemplate_;
            }
            return this.dekTemplateBuilder_.getMessage();
        }

        public Builder setDekTemplate(KeyTemplate value) {
            if (this.dekTemplateBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.dekTemplate_ = value;
            } else {
                this.dekTemplateBuilder_.setMessage(value);
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder setDekTemplate(KeyTemplate.Builder builderForValue) {
            if (this.dekTemplateBuilder_ == null) {
                this.dekTemplate_ = builderForValue.build();
            } else {
                this.dekTemplateBuilder_.setMessage(builderForValue.build());
            }
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder mergeDekTemplate(KeyTemplate value) {
            if (this.dekTemplateBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0 && this.dekTemplate_ != null && this.dekTemplate_ != KeyTemplate.getDefaultInstance()) {
                    this.getDekTemplateBuilder().mergeFrom(value);
                } else {
                    this.dekTemplate_ = value;
                }
            } else {
                this.dekTemplateBuilder_.mergeFrom(value);
            }
            if (this.dekTemplate_ != null) {
                this.bitField0_ |= 2;
                this.onChanged();
            }
            return this;
        }

        public Builder clearDekTemplate() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.dekTemplate_ = null;
            if (this.dekTemplateBuilder_ != null) {
                this.dekTemplateBuilder_.dispose();
                this.dekTemplateBuilder_ = null;
            }
            this.onChanged();
            return this;
        }

        public KeyTemplate.Builder getDekTemplateBuilder() {
            this.bitField0_ |= 2;
            this.onChanged();
            return this.internalGetDekTemplateFieldBuilder().getBuilder();
        }

        @Override
        public KeyTemplateOrBuilder getDekTemplateOrBuilder() {
            if (this.dekTemplateBuilder_ != null) {
                return this.dekTemplateBuilder_.getMessageOrBuilder();
            }
            return this.dekTemplate_ == null ? KeyTemplate.getDefaultInstance() : this.dekTemplate_;
        }

        private SingleFieldBuilder<KeyTemplate, KeyTemplate.Builder, KeyTemplateOrBuilder> internalGetDekTemplateFieldBuilder() {
            if (this.dekTemplateBuilder_ == null) {
                this.dekTemplateBuilder_ = new SingleFieldBuilder(this.getDekTemplate(), this.getParentForChildren(), this.isClean());
                this.dekTemplate_ = null;
            }
            return this.dekTemplateBuilder_;
        }
    }
}

