/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.Config;
import com.google.crypto.tink.proto.KeyTypeEntry;
import com.google.crypto.tink.proto.KeyTypeEntryOrBuilder;
import com.google.crypto.tink.proto.RegistryConfigOrBuilder;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractMessageLite;
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
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public final class RegistryConfig
extends GeneratedMessage
implements RegistryConfigOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int CONFIG_NAME_FIELD_NUMBER = 1;
    private volatile Object configName_ = "";
    public static final int ENTRY_FIELD_NUMBER = 2;
    private List<KeyTypeEntry> entry_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final RegistryConfig DEFAULT_INSTANCE;
    private static final Parser<RegistryConfig> PARSER;

    private RegistryConfig(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private RegistryConfig() {
        this.configName_ = "";
        this.entry_ = Collections.emptyList();
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Config.internal_static_google_crypto_tink_RegistryConfig_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Config.internal_static_google_crypto_tink_RegistryConfig_fieldAccessorTable.ensureFieldAccessorsInitialized(RegistryConfig.class, Builder.class);
    }

    @Override
    public String getConfigName() {
        Object ref = this.configName_;
        if (ref instanceof String) {
            return (String)ref;
        }
        ByteString bs = (ByteString)ref;
        String s = bs.toStringUtf8();
        this.configName_ = s;
        return s;
    }

    @Override
    public ByteString getConfigNameBytes() {
        Object ref = this.configName_;
        if (ref instanceof String) {
            ByteString b = ByteString.copyFromUtf8((String)ref);
            this.configName_ = b;
            return b;
        }
        return (ByteString)ref;
    }

    @Override
    public List<KeyTypeEntry> getEntryList() {
        return this.entry_;
    }

    @Override
    public List<? extends KeyTypeEntryOrBuilder> getEntryOrBuilderList() {
        return this.entry_;
    }

    @Override
    public int getEntryCount() {
        return this.entry_.size();
    }

    @Override
    public KeyTypeEntry getEntry(int index) {
        return this.entry_.get(index);
    }

    @Override
    public KeyTypeEntryOrBuilder getEntryOrBuilder(int index) {
        return this.entry_.get(index);
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
        if (!GeneratedMessage.isStringEmpty(this.configName_)) {
            GeneratedMessage.writeString(output, 1, this.configName_);
        }
        for (int i = 0; i < this.entry_.size(); ++i) {
            output.writeMessage(2, this.entry_.get(i));
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
        if (!GeneratedMessage.isStringEmpty(this.configName_)) {
            size += GeneratedMessage.computeStringSize(1, this.configName_);
        }
        for (int i = 0; i < this.entry_.size(); ++i) {
            size += CodedOutputStream.computeMessageSize(2, this.entry_.get(i));
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof RegistryConfig)) {
            return super.equals(obj);
        }
        RegistryConfig other = (RegistryConfig)obj;
        if (!this.getConfigName().equals(other.getConfigName())) {
            return false;
        }
        if (!this.getEntryList().equals(other.getEntryList())) {
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
        hash = 19 * hash + RegistryConfig.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getConfigName().hashCode();
        if (this.getEntryCount() > 0) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getEntryList().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static RegistryConfig parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RegistryConfig parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RegistryConfig parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RegistryConfig parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RegistryConfig parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static RegistryConfig parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static RegistryConfig parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static RegistryConfig parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static RegistryConfig parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static RegistryConfig parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static RegistryConfig parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static RegistryConfig parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return RegistryConfig.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(RegistryConfig prototype) {
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

    public static RegistryConfig getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<RegistryConfig> parser() {
        return PARSER;
    }

    public Parser<RegistryConfig> getParserForType() {
        return PARSER;
    }

    @Override
    public RegistryConfig getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", RegistryConfig.class.getName());
        DEFAULT_INSTANCE = new RegistryConfig();
        PARSER = new AbstractParser<RegistryConfig>(){

            @Override
            public RegistryConfig parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = RegistryConfig.newBuilder();
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
    implements RegistryConfigOrBuilder {
        private int bitField0_;
        private Object configName_ = "";
        private List<KeyTypeEntry> entry_ = Collections.emptyList();
        private RepeatedFieldBuilder<KeyTypeEntry, KeyTypeEntry.Builder, KeyTypeEntryOrBuilder> entryBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return Config.internal_static_google_crypto_tink_RegistryConfig_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Config.internal_static_google_crypto_tink_RegistryConfig_fieldAccessorTable.ensureFieldAccessorsInitialized(RegistryConfig.class, Builder.class);
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
            this.configName_ = "";
            if (this.entryBuilder_ == null) {
                this.entry_ = Collections.emptyList();
            } else {
                this.entry_ = null;
                this.entryBuilder_.clear();
            }
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Config.internal_static_google_crypto_tink_RegistryConfig_descriptor;
        }

        @Override
        public RegistryConfig getDefaultInstanceForType() {
            return RegistryConfig.getDefaultInstance();
        }

        @Override
        public RegistryConfig build() {
            RegistryConfig result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public RegistryConfig buildPartial() {
            RegistryConfig result = new RegistryConfig(this);
            this.buildPartialRepeatedFields(result);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartialRepeatedFields(RegistryConfig result) {
            if (this.entryBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0) {
                    this.entry_ = Collections.unmodifiableList(this.entry_);
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                result.entry_ = this.entry_;
            } else {
                result.entry_ = this.entryBuilder_.build();
            }
        }

        private void buildPartial0(RegistryConfig result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.configName_ = this.configName_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof RegistryConfig) {
                return this.mergeFrom((RegistryConfig)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(RegistryConfig other) {
            if (other == RegistryConfig.getDefaultInstance()) {
                return this;
            }
            if (!other.getConfigName().isEmpty()) {
                this.configName_ = other.configName_;
                this.bitField0_ |= 1;
                this.onChanged();
            }
            if (this.entryBuilder_ == null) {
                if (!other.entry_.isEmpty()) {
                    if (this.entry_.isEmpty()) {
                        this.entry_ = other.entry_;
                        this.bitField0_ &= 0xFFFFFFFD;
                    } else {
                        this.ensureEntryIsMutable();
                        this.entry_.addAll(other.entry_);
                    }
                    this.onChanged();
                }
            } else if (!other.entry_.isEmpty()) {
                if (this.entryBuilder_.isEmpty()) {
                    this.entryBuilder_.dispose();
                    this.entryBuilder_ = null;
                    this.entry_ = other.entry_;
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.entryBuilder_ = alwaysUseFieldBuilders ? this.internalGetEntryFieldBuilder() : null;
                } else {
                    this.entryBuilder_.addAllMessages(other.entry_);
                }
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
                            this.configName_ = input.readStringRequireUtf8();
                            this.bitField0_ |= 1;
                            continue block10;
                        }
                        case 18: {
                            KeyTypeEntry m = input.readMessage(KeyTypeEntry.parser(), extensionRegistry);
                            if (this.entryBuilder_ == null) {
                                this.ensureEntryIsMutable();
                                this.entry_.add(m);
                                continue block10;
                            }
                            this.entryBuilder_.addMessage(m);
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
        public String getConfigName() {
            Object ref = this.configName_;
            if (!(ref instanceof String)) {
                ByteString bs = (ByteString)ref;
                String s = bs.toStringUtf8();
                this.configName_ = s;
                return s;
            }
            return (String)ref;
        }

        @Override
        public ByteString getConfigNameBytes() {
            Object ref = this.configName_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.configName_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        public Builder setConfigName(String value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.configName_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearConfigName() {
            this.configName_ = RegistryConfig.getDefaultInstance().getConfigName();
            this.bitField0_ &= 0xFFFFFFFE;
            this.onChanged();
            return this;
        }

        public Builder setConfigNameBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            RegistryConfig.checkByteStringIsUtf8(value);
            this.configName_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        private void ensureEntryIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
                this.entry_ = new ArrayList<KeyTypeEntry>(this.entry_);
                this.bitField0_ |= 2;
            }
        }

        @Override
        public List<KeyTypeEntry> getEntryList() {
            if (this.entryBuilder_ == null) {
                return Collections.unmodifiableList(this.entry_);
            }
            return this.entryBuilder_.getMessageList();
        }

        @Override
        public int getEntryCount() {
            if (this.entryBuilder_ == null) {
                return this.entry_.size();
            }
            return this.entryBuilder_.getCount();
        }

        @Override
        public KeyTypeEntry getEntry(int index) {
            if (this.entryBuilder_ == null) {
                return this.entry_.get(index);
            }
            return this.entryBuilder_.getMessage(index);
        }

        public Builder setEntry(int index, KeyTypeEntry value) {
            if (this.entryBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureEntryIsMutable();
                this.entry_.set(index, value);
                this.onChanged();
            } else {
                this.entryBuilder_.setMessage(index, value);
            }
            return this;
        }

        public Builder setEntry(int index, KeyTypeEntry.Builder builderForValue) {
            if (this.entryBuilder_ == null) {
                this.ensureEntryIsMutable();
                this.entry_.set(index, builderForValue.build());
                this.onChanged();
            } else {
                this.entryBuilder_.setMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addEntry(KeyTypeEntry value) {
            if (this.entryBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureEntryIsMutable();
                this.entry_.add(value);
                this.onChanged();
            } else {
                this.entryBuilder_.addMessage(value);
            }
            return this;
        }

        public Builder addEntry(int index, KeyTypeEntry value) {
            if (this.entryBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureEntryIsMutable();
                this.entry_.add(index, value);
                this.onChanged();
            } else {
                this.entryBuilder_.addMessage(index, value);
            }
            return this;
        }

        public Builder addEntry(KeyTypeEntry.Builder builderForValue) {
            if (this.entryBuilder_ == null) {
                this.ensureEntryIsMutable();
                this.entry_.add(builderForValue.build());
                this.onChanged();
            } else {
                this.entryBuilder_.addMessage(builderForValue.build());
            }
            return this;
        }

        public Builder addEntry(int index, KeyTypeEntry.Builder builderForValue) {
            if (this.entryBuilder_ == null) {
                this.ensureEntryIsMutable();
                this.entry_.add(index, builderForValue.build());
                this.onChanged();
            } else {
                this.entryBuilder_.addMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addAllEntry(Iterable<? extends KeyTypeEntry> values) {
            if (this.entryBuilder_ == null) {
                this.ensureEntryIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.entry_);
                this.onChanged();
            } else {
                this.entryBuilder_.addAllMessages(values);
            }
            return this;
        }

        public Builder clearEntry() {
            if (this.entryBuilder_ == null) {
                this.entry_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFD;
                this.onChanged();
            } else {
                this.entryBuilder_.clear();
            }
            return this;
        }

        public Builder removeEntry(int index) {
            if (this.entryBuilder_ == null) {
                this.ensureEntryIsMutable();
                this.entry_.remove(index);
                this.onChanged();
            } else {
                this.entryBuilder_.remove(index);
            }
            return this;
        }

        public KeyTypeEntry.Builder getEntryBuilder(int index) {
            return this.internalGetEntryFieldBuilder().getBuilder(index);
        }

        @Override
        public KeyTypeEntryOrBuilder getEntryOrBuilder(int index) {
            if (this.entryBuilder_ == null) {
                return this.entry_.get(index);
            }
            return this.entryBuilder_.getMessageOrBuilder(index);
        }

        @Override
        public List<? extends KeyTypeEntryOrBuilder> getEntryOrBuilderList() {
            if (this.entryBuilder_ != null) {
                return this.entryBuilder_.getMessageOrBuilderList();
            }
            return Collections.unmodifiableList(this.entry_);
        }

        public KeyTypeEntry.Builder addEntryBuilder() {
            return this.internalGetEntryFieldBuilder().addBuilder(KeyTypeEntry.getDefaultInstance());
        }

        public KeyTypeEntry.Builder addEntryBuilder(int index) {
            return this.internalGetEntryFieldBuilder().addBuilder(index, KeyTypeEntry.getDefaultInstance());
        }

        public List<KeyTypeEntry.Builder> getEntryBuilderList() {
            return this.internalGetEntryFieldBuilder().getBuilderList();
        }

        private RepeatedFieldBuilder<KeyTypeEntry, KeyTypeEntry.Builder, KeyTypeEntryOrBuilder> internalGetEntryFieldBuilder() {
            if (this.entryBuilder_ == null) {
                this.entryBuilder_ = new RepeatedFieldBuilder(this.entry_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
                this.entry_ = null;
            }
            return this.entryBuilder_;
        }
    }
}

