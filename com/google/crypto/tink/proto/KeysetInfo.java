/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.proto.KeysetInfoOrBuilder;
import com.google.crypto.tink.proto.OutputPrefixType;
import com.google.crypto.tink.proto.Tink;
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
import com.google.protobuf.MessageOrBuilder;
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

public final class KeysetInfo
extends GeneratedMessage
implements KeysetInfoOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int PRIMARY_KEY_ID_FIELD_NUMBER = 1;
    private int primaryKeyId_ = 0;
    public static final int KEY_INFO_FIELD_NUMBER = 2;
    private List<KeyInfo> keyInfo_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final KeysetInfo DEFAULT_INSTANCE;
    private static final Parser<KeysetInfo> PARSER;

    private KeysetInfo(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private KeysetInfo() {
        this.keyInfo_ = Collections.emptyList();
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Tink.internal_static_google_crypto_tink_KeysetInfo_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Tink.internal_static_google_crypto_tink_KeysetInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(KeysetInfo.class, Builder.class);
    }

    @Override
    public int getPrimaryKeyId() {
        return this.primaryKeyId_;
    }

    @Override
    public List<KeyInfo> getKeyInfoList() {
        return this.keyInfo_;
    }

    @Override
    public List<? extends KeyInfoOrBuilder> getKeyInfoOrBuilderList() {
        return this.keyInfo_;
    }

    @Override
    public int getKeyInfoCount() {
        return this.keyInfo_.size();
    }

    @Override
    public KeyInfo getKeyInfo(int index) {
        return this.keyInfo_.get(index);
    }

    @Override
    public KeyInfoOrBuilder getKeyInfoOrBuilder(int index) {
        return this.keyInfo_.get(index);
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
        if (this.primaryKeyId_ != 0) {
            output.writeUInt32(1, this.primaryKeyId_);
        }
        for (int i = 0; i < this.keyInfo_.size(); ++i) {
            output.writeMessage(2, this.keyInfo_.get(i));
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
        if (this.primaryKeyId_ != 0) {
            size += CodedOutputStream.computeUInt32Size(1, this.primaryKeyId_);
        }
        for (int i = 0; i < this.keyInfo_.size(); ++i) {
            size += CodedOutputStream.computeMessageSize(2, this.keyInfo_.get(i));
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeysetInfo)) {
            return super.equals(obj);
        }
        KeysetInfo other = (KeysetInfo)obj;
        if (this.getPrimaryKeyId() != other.getPrimaryKeyId()) {
            return false;
        }
        if (!this.getKeyInfoList().equals(other.getKeyInfoList())) {
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
        hash = 19 * hash + KeysetInfo.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getPrimaryKeyId();
        if (this.getKeyInfoCount() > 0) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getKeyInfoList().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static KeysetInfo parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KeysetInfo parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KeysetInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KeysetInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KeysetInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KeysetInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KeysetInfo parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static KeysetInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static KeysetInfo parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static KeysetInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static KeysetInfo parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static KeysetInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return KeysetInfo.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(KeysetInfo prototype) {
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

    public static KeysetInfo getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<KeysetInfo> parser() {
        return PARSER;
    }

    public Parser<KeysetInfo> getParserForType() {
        return PARSER;
    }

    @Override
    public KeysetInfo getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeysetInfo.class.getName());
        DEFAULT_INSTANCE = new KeysetInfo();
        PARSER = new AbstractParser<KeysetInfo>(){

            @Override
            public KeysetInfo parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = KeysetInfo.newBuilder();
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
    implements KeysetInfoOrBuilder {
        private int bitField0_;
        private int primaryKeyId_;
        private List<KeyInfo> keyInfo_ = Collections.emptyList();
        private RepeatedFieldBuilder<KeyInfo, KeyInfo.Builder, KeyInfoOrBuilder> keyInfoBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return Tink.internal_static_google_crypto_tink_KeysetInfo_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Tink.internal_static_google_crypto_tink_KeysetInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(KeysetInfo.class, Builder.class);
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
            this.primaryKeyId_ = 0;
            if (this.keyInfoBuilder_ == null) {
                this.keyInfo_ = Collections.emptyList();
            } else {
                this.keyInfo_ = null;
                this.keyInfoBuilder_.clear();
            }
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Tink.internal_static_google_crypto_tink_KeysetInfo_descriptor;
        }

        @Override
        public KeysetInfo getDefaultInstanceForType() {
            return KeysetInfo.getDefaultInstance();
        }

        @Override
        public KeysetInfo build() {
            KeysetInfo result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public KeysetInfo buildPartial() {
            KeysetInfo result = new KeysetInfo(this);
            this.buildPartialRepeatedFields(result);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartialRepeatedFields(KeysetInfo result) {
            if (this.keyInfoBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0) {
                    this.keyInfo_ = Collections.unmodifiableList(this.keyInfo_);
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                result.keyInfo_ = this.keyInfo_;
            } else {
                result.keyInfo_ = this.keyInfoBuilder_.build();
            }
        }

        private void buildPartial0(KeysetInfo result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.primaryKeyId_ = this.primaryKeyId_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof KeysetInfo) {
                return this.mergeFrom((KeysetInfo)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(KeysetInfo other) {
            if (other == KeysetInfo.getDefaultInstance()) {
                return this;
            }
            if (other.getPrimaryKeyId() != 0) {
                this.setPrimaryKeyId(other.getPrimaryKeyId());
            }
            if (this.keyInfoBuilder_ == null) {
                if (!other.keyInfo_.isEmpty()) {
                    if (this.keyInfo_.isEmpty()) {
                        this.keyInfo_ = other.keyInfo_;
                        this.bitField0_ &= 0xFFFFFFFD;
                    } else {
                        this.ensureKeyInfoIsMutable();
                        this.keyInfo_.addAll(other.keyInfo_);
                    }
                    this.onChanged();
                }
            } else if (!other.keyInfo_.isEmpty()) {
                if (this.keyInfoBuilder_.isEmpty()) {
                    this.keyInfoBuilder_.dispose();
                    this.keyInfoBuilder_ = null;
                    this.keyInfo_ = other.keyInfo_;
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.keyInfoBuilder_ = alwaysUseFieldBuilders ? this.internalGetKeyInfoFieldBuilder() : null;
                } else {
                    this.keyInfoBuilder_.addAllMessages(other.keyInfo_);
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
                        case 8: {
                            this.primaryKeyId_ = input.readUInt32();
                            this.bitField0_ |= 1;
                            continue block10;
                        }
                        case 18: {
                            KeyInfo m = input.readMessage(KeyInfo.parser(), extensionRegistry);
                            if (this.keyInfoBuilder_ == null) {
                                this.ensureKeyInfoIsMutable();
                                this.keyInfo_.add(m);
                                continue block10;
                            }
                            this.keyInfoBuilder_.addMessage(m);
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
        public int getPrimaryKeyId() {
            return this.primaryKeyId_;
        }

        public Builder setPrimaryKeyId(int value) {
            this.primaryKeyId_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        public Builder clearPrimaryKeyId() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.primaryKeyId_ = 0;
            this.onChanged();
            return this;
        }

        private void ensureKeyInfoIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
                this.keyInfo_ = new ArrayList<KeyInfo>(this.keyInfo_);
                this.bitField0_ |= 2;
            }
        }

        @Override
        public List<KeyInfo> getKeyInfoList() {
            if (this.keyInfoBuilder_ == null) {
                return Collections.unmodifiableList(this.keyInfo_);
            }
            return this.keyInfoBuilder_.getMessageList();
        }

        @Override
        public int getKeyInfoCount() {
            if (this.keyInfoBuilder_ == null) {
                return this.keyInfo_.size();
            }
            return this.keyInfoBuilder_.getCount();
        }

        @Override
        public KeyInfo getKeyInfo(int index) {
            if (this.keyInfoBuilder_ == null) {
                return this.keyInfo_.get(index);
            }
            return this.keyInfoBuilder_.getMessage(index);
        }

        public Builder setKeyInfo(int index, KeyInfo value) {
            if (this.keyInfoBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureKeyInfoIsMutable();
                this.keyInfo_.set(index, value);
                this.onChanged();
            } else {
                this.keyInfoBuilder_.setMessage(index, value);
            }
            return this;
        }

        public Builder setKeyInfo(int index, KeyInfo.Builder builderForValue) {
            if (this.keyInfoBuilder_ == null) {
                this.ensureKeyInfoIsMutable();
                this.keyInfo_.set(index, builderForValue.build());
                this.onChanged();
            } else {
                this.keyInfoBuilder_.setMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addKeyInfo(KeyInfo value) {
            if (this.keyInfoBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureKeyInfoIsMutable();
                this.keyInfo_.add(value);
                this.onChanged();
            } else {
                this.keyInfoBuilder_.addMessage(value);
            }
            return this;
        }

        public Builder addKeyInfo(int index, KeyInfo value) {
            if (this.keyInfoBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureKeyInfoIsMutable();
                this.keyInfo_.add(index, value);
                this.onChanged();
            } else {
                this.keyInfoBuilder_.addMessage(index, value);
            }
            return this;
        }

        public Builder addKeyInfo(KeyInfo.Builder builderForValue) {
            if (this.keyInfoBuilder_ == null) {
                this.ensureKeyInfoIsMutable();
                this.keyInfo_.add(builderForValue.build());
                this.onChanged();
            } else {
                this.keyInfoBuilder_.addMessage(builderForValue.build());
            }
            return this;
        }

        public Builder addKeyInfo(int index, KeyInfo.Builder builderForValue) {
            if (this.keyInfoBuilder_ == null) {
                this.ensureKeyInfoIsMutable();
                this.keyInfo_.add(index, builderForValue.build());
                this.onChanged();
            } else {
                this.keyInfoBuilder_.addMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addAllKeyInfo(Iterable<? extends KeyInfo> values) {
            if (this.keyInfoBuilder_ == null) {
                this.ensureKeyInfoIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.keyInfo_);
                this.onChanged();
            } else {
                this.keyInfoBuilder_.addAllMessages(values);
            }
            return this;
        }

        public Builder clearKeyInfo() {
            if (this.keyInfoBuilder_ == null) {
                this.keyInfo_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFD;
                this.onChanged();
            } else {
                this.keyInfoBuilder_.clear();
            }
            return this;
        }

        public Builder removeKeyInfo(int index) {
            if (this.keyInfoBuilder_ == null) {
                this.ensureKeyInfoIsMutable();
                this.keyInfo_.remove(index);
                this.onChanged();
            } else {
                this.keyInfoBuilder_.remove(index);
            }
            return this;
        }

        public KeyInfo.Builder getKeyInfoBuilder(int index) {
            return this.internalGetKeyInfoFieldBuilder().getBuilder(index);
        }

        @Override
        public KeyInfoOrBuilder getKeyInfoOrBuilder(int index) {
            if (this.keyInfoBuilder_ == null) {
                return this.keyInfo_.get(index);
            }
            return this.keyInfoBuilder_.getMessageOrBuilder(index);
        }

        @Override
        public List<? extends KeyInfoOrBuilder> getKeyInfoOrBuilderList() {
            if (this.keyInfoBuilder_ != null) {
                return this.keyInfoBuilder_.getMessageOrBuilderList();
            }
            return Collections.unmodifiableList(this.keyInfo_);
        }

        public KeyInfo.Builder addKeyInfoBuilder() {
            return this.internalGetKeyInfoFieldBuilder().addBuilder(KeyInfo.getDefaultInstance());
        }

        public KeyInfo.Builder addKeyInfoBuilder(int index) {
            return this.internalGetKeyInfoFieldBuilder().addBuilder(index, KeyInfo.getDefaultInstance());
        }

        public List<KeyInfo.Builder> getKeyInfoBuilderList() {
            return this.internalGetKeyInfoFieldBuilder().getBuilderList();
        }

        private RepeatedFieldBuilder<KeyInfo, KeyInfo.Builder, KeyInfoOrBuilder> internalGetKeyInfoFieldBuilder() {
            if (this.keyInfoBuilder_ == null) {
                this.keyInfoBuilder_ = new RepeatedFieldBuilder(this.keyInfo_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
                this.keyInfo_ = null;
            }
            return this.keyInfoBuilder_;
        }
    }

    public static final class KeyInfo
    extends GeneratedMessage
    implements KeyInfoOrBuilder {
        private static final long serialVersionUID = 0L;
        public static final int TYPE_URL_FIELD_NUMBER = 1;
        private volatile Object typeUrl_ = "";
        public static final int STATUS_FIELD_NUMBER = 2;
        private int status_ = 0;
        public static final int KEY_ID_FIELD_NUMBER = 3;
        private int keyId_ = 0;
        public static final int OUTPUT_PREFIX_TYPE_FIELD_NUMBER = 4;
        private int outputPrefixType_ = 0;
        private byte memoizedIsInitialized = (byte)-1;
        private static final KeyInfo DEFAULT_INSTANCE;
        private static final Parser<KeyInfo> PARSER;

        private KeyInfo(GeneratedMessage.Builder<?> builder) {
            super(builder);
        }

        private KeyInfo() {
            this.typeUrl_ = "";
            this.status_ = 0;
            this.outputPrefixType_ = 0;
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(KeyInfo.class, Builder.class);
        }

        @Override
        public String getTypeUrl() {
            Object ref = this.typeUrl_;
            if (ref instanceof String) {
                return (String)ref;
            }
            ByteString bs = (ByteString)ref;
            String s = bs.toStringUtf8();
            this.typeUrl_ = s;
            return s;
        }

        @Override
        public ByteString getTypeUrlBytes() {
            Object ref = this.typeUrl_;
            if (ref instanceof String) {
                ByteString b = ByteString.copyFromUtf8((String)ref);
                this.typeUrl_ = b;
                return b;
            }
            return (ByteString)ref;
        }

        @Override
        public int getStatusValue() {
            return this.status_;
        }

        @Override
        public KeyStatusType getStatus() {
            KeyStatusType result = KeyStatusType.forNumber(this.status_);
            return result == null ? KeyStatusType.UNRECOGNIZED : result;
        }

        @Override
        public int getKeyId() {
            return this.keyId_;
        }

        @Override
        public int getOutputPrefixTypeValue() {
            return this.outputPrefixType_;
        }

        @Override
        public OutputPrefixType getOutputPrefixType() {
            OutputPrefixType result = OutputPrefixType.forNumber(this.outputPrefixType_);
            return result == null ? OutputPrefixType.UNRECOGNIZED : result;
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
            if (!GeneratedMessage.isStringEmpty(this.typeUrl_)) {
                GeneratedMessage.writeString(output, 1, this.typeUrl_);
            }
            if (this.status_ != KeyStatusType.UNKNOWN_STATUS.getNumber()) {
                output.writeEnum(2, this.status_);
            }
            if (this.keyId_ != 0) {
                output.writeUInt32(3, this.keyId_);
            }
            if (this.outputPrefixType_ != OutputPrefixType.UNKNOWN_PREFIX.getNumber()) {
                output.writeEnum(4, this.outputPrefixType_);
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
            if (!GeneratedMessage.isStringEmpty(this.typeUrl_)) {
                size += GeneratedMessage.computeStringSize(1, this.typeUrl_);
            }
            if (this.status_ != KeyStatusType.UNKNOWN_STATUS.getNumber()) {
                size += CodedOutputStream.computeEnumSize(2, this.status_);
            }
            if (this.keyId_ != 0) {
                size += CodedOutputStream.computeUInt32Size(3, this.keyId_);
            }
            if (this.outputPrefixType_ != OutputPrefixType.UNKNOWN_PREFIX.getNumber()) {
                size += CodedOutputStream.computeEnumSize(4, this.outputPrefixType_);
            }
            this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
            return size;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (!(obj instanceof KeyInfo)) {
                return super.equals(obj);
            }
            KeyInfo other = (KeyInfo)obj;
            if (!this.getTypeUrl().equals(other.getTypeUrl())) {
                return false;
            }
            if (this.status_ != other.status_) {
                return false;
            }
            if (this.getKeyId() != other.getKeyId()) {
                return false;
            }
            if (this.outputPrefixType_ != other.outputPrefixType_) {
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
            hash = 19 * hash + KeyInfo.getDescriptor().hashCode();
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getTypeUrl().hashCode();
            hash = 37 * hash + 2;
            hash = 53 * hash + this.status_;
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getKeyId();
            hash = 37 * hash + 4;
            hash = 53 * hash + this.outputPrefixType_;
            this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
            return hash;
        }

        public static KeyInfo parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static KeyInfo parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static KeyInfo parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static KeyInfo parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static KeyInfo parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static KeyInfo parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static KeyInfo parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static KeyInfo parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static KeyInfo parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
        }

        public static KeyInfo parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static KeyInfo parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static KeyInfo parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return KeyInfo.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(KeyInfo prototype) {
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

        public static KeyInfo getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<KeyInfo> parser() {
            return PARSER;
        }

        public Parser<KeyInfo> getParserForType() {
            return PARSER;
        }

        @Override
        public KeyInfo getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeyInfo.class.getName());
            DEFAULT_INSTANCE = new KeyInfo();
            PARSER = new AbstractParser<KeyInfo>(){

                @Override
                public KeyInfo parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    Builder builder = KeyInfo.newBuilder();
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
        implements KeyInfoOrBuilder {
            private int bitField0_;
            private Object typeUrl_ = "";
            private int status_ = 0;
            private int keyId_;
            private int outputPrefixType_ = 0;

            public static final Descriptors.Descriptor getDescriptor() {
                return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_fieldAccessorTable.ensureFieldAccessorsInitialized(KeyInfo.class, Builder.class);
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
                this.typeUrl_ = "";
                this.status_ = 0;
                this.keyId_ = 0;
                this.outputPrefixType_ = 0;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return Tink.internal_static_google_crypto_tink_KeysetInfo_KeyInfo_descriptor;
            }

            @Override
            public KeyInfo getDefaultInstanceForType() {
                return KeyInfo.getDefaultInstance();
            }

            @Override
            public KeyInfo build() {
                KeyInfo result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public KeyInfo buildPartial() {
                KeyInfo result = new KeyInfo(this);
                if (this.bitField0_ != 0) {
                    this.buildPartial0(result);
                }
                this.onBuilt();
                return result;
            }

            private void buildPartial0(KeyInfo result) {
                int from_bitField0_ = this.bitField0_;
                if ((from_bitField0_ & 1) != 0) {
                    result.typeUrl_ = this.typeUrl_;
                }
                if ((from_bitField0_ & 2) != 0) {
                    result.status_ = this.status_;
                }
                if ((from_bitField0_ & 4) != 0) {
                    result.keyId_ = this.keyId_;
                }
                if ((from_bitField0_ & 8) != 0) {
                    result.outputPrefixType_ = this.outputPrefixType_;
                }
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof KeyInfo) {
                    return this.mergeFrom((KeyInfo)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(KeyInfo other) {
                if (other == KeyInfo.getDefaultInstance()) {
                    return this;
                }
                if (!other.getTypeUrl().isEmpty()) {
                    this.typeUrl_ = other.typeUrl_;
                    this.bitField0_ |= 1;
                    this.onChanged();
                }
                if (other.status_ != 0) {
                    this.setStatusValue(other.getStatusValue());
                }
                if (other.getKeyId() != 0) {
                    this.setKeyId(other.getKeyId());
                }
                if (other.outputPrefixType_ != 0) {
                    this.setOutputPrefixTypeValue(other.getOutputPrefixTypeValue());
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
                            case 10: {
                                this.typeUrl_ = input.readStringRequireUtf8();
                                this.bitField0_ |= 1;
                                continue block12;
                            }
                            case 16: {
                                this.status_ = input.readEnum();
                                this.bitField0_ |= 2;
                                continue block12;
                            }
                            case 24: {
                                this.keyId_ = input.readUInt32();
                                this.bitField0_ |= 4;
                                continue block12;
                            }
                            case 32: {
                                this.outputPrefixType_ = input.readEnum();
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
            public String getTypeUrl() {
                Object ref = this.typeUrl_;
                if (!(ref instanceof String)) {
                    ByteString bs = (ByteString)ref;
                    String s = bs.toStringUtf8();
                    this.typeUrl_ = s;
                    return s;
                }
                return (String)ref;
            }

            @Override
            public ByteString getTypeUrlBytes() {
                Object ref = this.typeUrl_;
                if (ref instanceof String) {
                    ByteString b = ByteString.copyFromUtf8((String)ref);
                    this.typeUrl_ = b;
                    return b;
                }
                return (ByteString)ref;
            }

            public Builder setTypeUrl(String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.typeUrl_ = value;
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder clearTypeUrl() {
                this.typeUrl_ = KeyInfo.getDefaultInstance().getTypeUrl();
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
                return this;
            }

            public Builder setTypeUrlBytes(ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                KeyInfo.checkByteStringIsUtf8(value);
                this.typeUrl_ = value;
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            @Override
            public int getStatusValue() {
                return this.status_;
            }

            public Builder setStatusValue(int value) {
                this.status_ = value;
                this.bitField0_ |= 2;
                this.onChanged();
                return this;
            }

            @Override
            public KeyStatusType getStatus() {
                KeyStatusType result = KeyStatusType.forNumber(this.status_);
                return result == null ? KeyStatusType.UNRECOGNIZED : result;
            }

            public Builder setStatus(KeyStatusType value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 2;
                this.status_ = value.getNumber();
                this.onChanged();
                return this;
            }

            public Builder clearStatus() {
                this.bitField0_ &= 0xFFFFFFFD;
                this.status_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public int getKeyId() {
                return this.keyId_;
            }

            public Builder setKeyId(int value) {
                this.keyId_ = value;
                this.bitField0_ |= 4;
                this.onChanged();
                return this;
            }

            public Builder clearKeyId() {
                this.bitField0_ &= 0xFFFFFFFB;
                this.keyId_ = 0;
                this.onChanged();
                return this;
            }

            @Override
            public int getOutputPrefixTypeValue() {
                return this.outputPrefixType_;
            }

            public Builder setOutputPrefixTypeValue(int value) {
                this.outputPrefixType_ = value;
                this.bitField0_ |= 8;
                this.onChanged();
                return this;
            }

            @Override
            public OutputPrefixType getOutputPrefixType() {
                OutputPrefixType result = OutputPrefixType.forNumber(this.outputPrefixType_);
                return result == null ? OutputPrefixType.UNRECOGNIZED : result;
            }

            public Builder setOutputPrefixType(OutputPrefixType value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.bitField0_ |= 8;
                this.outputPrefixType_ = value.getNumber();
                this.onChanged();
                return this;
            }

            public Builder clearOutputPrefixType() {
                this.bitField0_ &= 0xFFFFFFF7;
                this.outputPrefixType_ = 0;
                this.onChanged();
                return this;
            }
        }
    }

    public static interface KeyInfoOrBuilder
    extends MessageOrBuilder {
        public String getTypeUrl();

        public ByteString getTypeUrlBytes();

        public int getStatusValue();

        public KeyStatusType getStatus();

        public int getKeyId();

        public int getOutputPrefixTypeValue();

        public OutputPrefixType getOutputPrefixType();
    }
}

