/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyData;
import com.google.crypto.tink.proto.KeyDataOrBuilder;
import com.google.crypto.tink.proto.KeyStatusType;
import com.google.crypto.tink.proto.KeysetOrBuilder;
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
import com.google.protobuf.SingleFieldBuilder;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class Keyset
extends GeneratedMessage
implements KeysetOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int PRIMARY_KEY_ID_FIELD_NUMBER = 1;
    private int primaryKeyId_ = 0;
    public static final int KEY_FIELD_NUMBER = 2;
    private List<Key> key_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final Keyset DEFAULT_INSTANCE;
    private static final Parser<Keyset> PARSER;

    private Keyset(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private Keyset() {
        this.key_ = Collections.emptyList();
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Tink.internal_static_google_crypto_tink_Keyset_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Tink.internal_static_google_crypto_tink_Keyset_fieldAccessorTable.ensureFieldAccessorsInitialized(Keyset.class, Builder.class);
    }

    @Override
    public int getPrimaryKeyId() {
        return this.primaryKeyId_;
    }

    @Override
    public List<Key> getKeyList() {
        return this.key_;
    }

    @Override
    public List<? extends KeyOrBuilder> getKeyOrBuilderList() {
        return this.key_;
    }

    @Override
    public int getKeyCount() {
        return this.key_.size();
    }

    @Override
    public Key getKey(int index) {
        return this.key_.get(index);
    }

    @Override
    public KeyOrBuilder getKeyOrBuilder(int index) {
        return this.key_.get(index);
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
        for (int i = 0; i < this.key_.size(); ++i) {
            output.writeMessage(2, this.key_.get(i));
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
        for (int i = 0; i < this.key_.size(); ++i) {
            size += CodedOutputStream.computeMessageSize(2, this.key_.get(i));
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Keyset)) {
            return super.equals(obj);
        }
        Keyset other = (Keyset)obj;
        if (this.getPrimaryKeyId() != other.getPrimaryKeyId()) {
            return false;
        }
        if (!this.getKeyList().equals(other.getKeyList())) {
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
        hash = 19 * hash + Keyset.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getPrimaryKeyId();
        if (this.getKeyCount() > 0) {
            hash = 37 * hash + 2;
            hash = 53 * hash + this.getKeyList().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static Keyset parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Keyset parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Keyset parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Keyset parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Keyset parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Keyset parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Keyset parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static Keyset parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Keyset parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static Keyset parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static Keyset parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static Keyset parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return Keyset.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(Keyset prototype) {
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

    public static Keyset getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<Keyset> parser() {
        return PARSER;
    }

    public Parser<Keyset> getParserForType() {
        return PARSER;
    }

    @Override
    public Keyset getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Keyset.class.getName());
        DEFAULT_INSTANCE = new Keyset();
        PARSER = new AbstractParser<Keyset>(){

            @Override
            public Keyset parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = Keyset.newBuilder();
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
    implements KeysetOrBuilder {
        private int bitField0_;
        private int primaryKeyId_;
        private List<Key> key_ = Collections.emptyList();
        private RepeatedFieldBuilder<Key, Key.Builder, KeyOrBuilder> keyBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return Tink.internal_static_google_crypto_tink_Keyset_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Tink.internal_static_google_crypto_tink_Keyset_fieldAccessorTable.ensureFieldAccessorsInitialized(Keyset.class, Builder.class);
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
            if (this.keyBuilder_ == null) {
                this.key_ = Collections.emptyList();
            } else {
                this.key_ = null;
                this.keyBuilder_.clear();
            }
            this.bitField0_ &= 0xFFFFFFFD;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Tink.internal_static_google_crypto_tink_Keyset_descriptor;
        }

        @Override
        public Keyset getDefaultInstanceForType() {
            return Keyset.getDefaultInstance();
        }

        @Override
        public Keyset build() {
            Keyset result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public Keyset buildPartial() {
            Keyset result = new Keyset(this);
            this.buildPartialRepeatedFields(result);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartialRepeatedFields(Keyset result) {
            if (this.keyBuilder_ == null) {
                if ((this.bitField0_ & 2) != 0) {
                    this.key_ = Collections.unmodifiableList(this.key_);
                    this.bitField0_ &= 0xFFFFFFFD;
                }
                result.key_ = this.key_;
            } else {
                result.key_ = this.keyBuilder_.build();
            }
        }

        private void buildPartial0(Keyset result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.primaryKeyId_ = this.primaryKeyId_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof Keyset) {
                return this.mergeFrom((Keyset)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(Keyset other) {
            if (other == Keyset.getDefaultInstance()) {
                return this;
            }
            if (other.getPrimaryKeyId() != 0) {
                this.setPrimaryKeyId(other.getPrimaryKeyId());
            }
            if (this.keyBuilder_ == null) {
                if (!other.key_.isEmpty()) {
                    if (this.key_.isEmpty()) {
                        this.key_ = other.key_;
                        this.bitField0_ &= 0xFFFFFFFD;
                    } else {
                        this.ensureKeyIsMutable();
                        this.key_.addAll(other.key_);
                    }
                    this.onChanged();
                }
            } else if (!other.key_.isEmpty()) {
                if (this.keyBuilder_.isEmpty()) {
                    this.keyBuilder_.dispose();
                    this.keyBuilder_ = null;
                    this.key_ = other.key_;
                    this.bitField0_ &= 0xFFFFFFFD;
                    this.keyBuilder_ = alwaysUseFieldBuilders ? this.internalGetKeyFieldBuilder() : null;
                } else {
                    this.keyBuilder_.addAllMessages(other.key_);
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
                            Key m = input.readMessage(Key.parser(), extensionRegistry);
                            if (this.keyBuilder_ == null) {
                                this.ensureKeyIsMutable();
                                this.key_.add(m);
                                continue block10;
                            }
                            this.keyBuilder_.addMessage(m);
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

        private void ensureKeyIsMutable() {
            if ((this.bitField0_ & 2) == 0) {
                this.key_ = new ArrayList<Key>(this.key_);
                this.bitField0_ |= 2;
            }
        }

        @Override
        public List<Key> getKeyList() {
            if (this.keyBuilder_ == null) {
                return Collections.unmodifiableList(this.key_);
            }
            return this.keyBuilder_.getMessageList();
        }

        @Override
        public int getKeyCount() {
            if (this.keyBuilder_ == null) {
                return this.key_.size();
            }
            return this.keyBuilder_.getCount();
        }

        @Override
        public Key getKey(int index) {
            if (this.keyBuilder_ == null) {
                return this.key_.get(index);
            }
            return this.keyBuilder_.getMessage(index);
        }

        public Builder setKey(int index, Key value) {
            if (this.keyBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureKeyIsMutable();
                this.key_.set(index, value);
                this.onChanged();
            } else {
                this.keyBuilder_.setMessage(index, value);
            }
            return this;
        }

        public Builder setKey(int index, Key.Builder builderForValue) {
            if (this.keyBuilder_ == null) {
                this.ensureKeyIsMutable();
                this.key_.set(index, builderForValue.build());
                this.onChanged();
            } else {
                this.keyBuilder_.setMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addKey(Key value) {
            if (this.keyBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureKeyIsMutable();
                this.key_.add(value);
                this.onChanged();
            } else {
                this.keyBuilder_.addMessage(value);
            }
            return this;
        }

        public Builder addKey(int index, Key value) {
            if (this.keyBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureKeyIsMutable();
                this.key_.add(index, value);
                this.onChanged();
            } else {
                this.keyBuilder_.addMessage(index, value);
            }
            return this;
        }

        public Builder addKey(Key.Builder builderForValue) {
            if (this.keyBuilder_ == null) {
                this.ensureKeyIsMutable();
                this.key_.add(builderForValue.build());
                this.onChanged();
            } else {
                this.keyBuilder_.addMessage(builderForValue.build());
            }
            return this;
        }

        public Builder addKey(int index, Key.Builder builderForValue) {
            if (this.keyBuilder_ == null) {
                this.ensureKeyIsMutable();
                this.key_.add(index, builderForValue.build());
                this.onChanged();
            } else {
                this.keyBuilder_.addMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addAllKey(Iterable<? extends Key> values) {
            if (this.keyBuilder_ == null) {
                this.ensureKeyIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.key_);
                this.onChanged();
            } else {
                this.keyBuilder_.addAllMessages(values);
            }
            return this;
        }

        public Builder clearKey() {
            if (this.keyBuilder_ == null) {
                this.key_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFD;
                this.onChanged();
            } else {
                this.keyBuilder_.clear();
            }
            return this;
        }

        public Builder removeKey(int index) {
            if (this.keyBuilder_ == null) {
                this.ensureKeyIsMutable();
                this.key_.remove(index);
                this.onChanged();
            } else {
                this.keyBuilder_.remove(index);
            }
            return this;
        }

        public Key.Builder getKeyBuilder(int index) {
            return this.internalGetKeyFieldBuilder().getBuilder(index);
        }

        @Override
        public KeyOrBuilder getKeyOrBuilder(int index) {
            if (this.keyBuilder_ == null) {
                return this.key_.get(index);
            }
            return this.keyBuilder_.getMessageOrBuilder(index);
        }

        @Override
        public List<? extends KeyOrBuilder> getKeyOrBuilderList() {
            if (this.keyBuilder_ != null) {
                return this.keyBuilder_.getMessageOrBuilderList();
            }
            return Collections.unmodifiableList(this.key_);
        }

        public Key.Builder addKeyBuilder() {
            return this.internalGetKeyFieldBuilder().addBuilder(Key.getDefaultInstance());
        }

        public Key.Builder addKeyBuilder(int index) {
            return this.internalGetKeyFieldBuilder().addBuilder(index, Key.getDefaultInstance());
        }

        public List<Key.Builder> getKeyBuilderList() {
            return this.internalGetKeyFieldBuilder().getBuilderList();
        }

        private RepeatedFieldBuilder<Key, Key.Builder, KeyOrBuilder> internalGetKeyFieldBuilder() {
            if (this.keyBuilder_ == null) {
                this.keyBuilder_ = new RepeatedFieldBuilder(this.key_, (this.bitField0_ & 2) != 0, this.getParentForChildren(), this.isClean());
                this.key_ = null;
            }
            return this.keyBuilder_;
        }
    }

    public static final class Key
    extends GeneratedMessage
    implements KeyOrBuilder {
        private static final long serialVersionUID = 0L;
        private int bitField0_;
        public static final int KEY_DATA_FIELD_NUMBER = 1;
        private KeyData keyData_;
        public static final int STATUS_FIELD_NUMBER = 2;
        private int status_ = 0;
        public static final int KEY_ID_FIELD_NUMBER = 3;
        private int keyId_ = 0;
        public static final int OUTPUT_PREFIX_TYPE_FIELD_NUMBER = 4;
        private int outputPrefixType_ = 0;
        private byte memoizedIsInitialized = (byte)-1;
        private static final Key DEFAULT_INSTANCE;
        private static final Parser<Key> PARSER;

        private Key(GeneratedMessage.Builder<?> builder) {
            super(builder);
        }

        private Key() {
            this.status_ = 0;
            this.outputPrefixType_ = 0;
        }

        public static final Descriptors.Descriptor getDescriptor() {
            return Tink.internal_static_google_crypto_tink_Keyset_Key_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Tink.internal_static_google_crypto_tink_Keyset_Key_fieldAccessorTable.ensureFieldAccessorsInitialized(Key.class, Builder.class);
        }

        @Override
        public boolean hasKeyData() {
            return (this.bitField0_ & 1) != 0;
        }

        @Override
        public KeyData getKeyData() {
            return this.keyData_ == null ? KeyData.getDefaultInstance() : this.keyData_;
        }

        @Override
        public KeyDataOrBuilder getKeyDataOrBuilder() {
            return this.keyData_ == null ? KeyData.getDefaultInstance() : this.keyData_;
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
            if ((this.bitField0_ & 1) != 0) {
                output.writeMessage(1, this.getKeyData());
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
            if ((this.bitField0_ & 1) != 0) {
                size += CodedOutputStream.computeMessageSize(1, this.getKeyData());
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
            if (!(obj instanceof Key)) {
                return super.equals(obj);
            }
            Key other = (Key)obj;
            if (this.hasKeyData() != other.hasKeyData()) {
                return false;
            }
            if (this.hasKeyData() && !this.getKeyData().equals(other.getKeyData())) {
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
            hash = 19 * hash + Key.getDescriptor().hashCode();
            if (this.hasKeyData()) {
                hash = 37 * hash + 1;
                hash = 53 * hash + this.getKeyData().hashCode();
            }
            hash = 37 * hash + 2;
            hash = 53 * hash + this.status_;
            hash = 37 * hash + 3;
            hash = 53 * hash + this.getKeyId();
            hash = 37 * hash + 4;
            hash = 53 * hash + this.outputPrefixType_;
            this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
            return hash;
        }

        public static Key parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Key parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Key parseFrom(ByteString data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Key parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Key parseFrom(byte[] data) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static Key parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static Key parseFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static Key parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        public static Key parseDelimitedFrom(InputStream input) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
        }

        public static Key parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
        }

        public static Key parseFrom(CodedInputStream input) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input);
        }

        public static Key parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
            return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
        }

        @Override
        public Builder newBuilderForType() {
            return Key.newBuilder();
        }

        public static Builder newBuilder() {
            return DEFAULT_INSTANCE.toBuilder();
        }

        public static Builder newBuilder(Key prototype) {
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

        public static Key getDefaultInstance() {
            return DEFAULT_INSTANCE;
        }

        public static Parser<Key> parser() {
            return PARSER;
        }

        public Parser<Key> getParserForType() {
            return PARSER;
        }

        @Override
        public Key getDefaultInstanceForType() {
            return DEFAULT_INSTANCE;
        }

        static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", Key.class.getName());
            DEFAULT_INSTANCE = new Key();
            PARSER = new AbstractParser<Key>(){

                @Override
                public Key parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    Builder builder = Key.newBuilder();
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
        implements KeyOrBuilder {
            private int bitField0_;
            private KeyData keyData_;
            private SingleFieldBuilder<KeyData, KeyData.Builder, KeyDataOrBuilder> keyDataBuilder_;
            private int status_ = 0;
            private int keyId_;
            private int outputPrefixType_ = 0;

            public static final Descriptors.Descriptor getDescriptor() {
                return Tink.internal_static_google_crypto_tink_Keyset_Key_descriptor;
            }

            @Override
            protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return Tink.internal_static_google_crypto_tink_Keyset_Key_fieldAccessorTable.ensureFieldAccessorsInitialized(Key.class, Builder.class);
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
                    this.internalGetKeyDataFieldBuilder();
                }
            }

            @Override
            public Builder clear() {
                super.clear();
                this.bitField0_ = 0;
                this.keyData_ = null;
                if (this.keyDataBuilder_ != null) {
                    this.keyDataBuilder_.dispose();
                    this.keyDataBuilder_ = null;
                }
                this.status_ = 0;
                this.keyId_ = 0;
                this.outputPrefixType_ = 0;
                return this;
            }

            @Override
            public Descriptors.Descriptor getDescriptorForType() {
                return Tink.internal_static_google_crypto_tink_Keyset_Key_descriptor;
            }

            @Override
            public Key getDefaultInstanceForType() {
                return Key.getDefaultInstance();
            }

            @Override
            public Key build() {
                Key result = this.buildPartial();
                if (!result.isInitialized()) {
                    throw Builder.newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public Key buildPartial() {
                Key result = new Key(this);
                if (this.bitField0_ != 0) {
                    this.buildPartial0(result);
                }
                this.onBuilt();
                return result;
            }

            private void buildPartial0(Key result) {
                int from_bitField0_ = this.bitField0_;
                int to_bitField0_ = 0;
                if ((from_bitField0_ & 1) != 0) {
                    result.keyData_ = this.keyDataBuilder_ == null ? this.keyData_ : this.keyDataBuilder_.build();
                    to_bitField0_ |= 1;
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
                result.bitField0_ |= to_bitField0_;
            }

            @Override
            public Builder mergeFrom(Message other) {
                if (other instanceof Key) {
                    return this.mergeFrom((Key)other);
                }
                super.mergeFrom(other);
                return this;
            }

            public Builder mergeFrom(Key other) {
                if (other == Key.getDefaultInstance()) {
                    return this;
                }
                if (other.hasKeyData()) {
                    this.mergeKeyData(other.getKeyData());
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
                                input.readMessage(this.internalGetKeyDataFieldBuilder().getBuilder(), extensionRegistry);
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
            public boolean hasKeyData() {
                return (this.bitField0_ & 1) != 0;
            }

            @Override
            public KeyData getKeyData() {
                if (this.keyDataBuilder_ == null) {
                    return this.keyData_ == null ? KeyData.getDefaultInstance() : this.keyData_;
                }
                return this.keyDataBuilder_.getMessage();
            }

            public Builder setKeyData(KeyData value) {
                if (this.keyDataBuilder_ == null) {
                    if (value == null) {
                        throw new NullPointerException();
                    }
                    this.keyData_ = value;
                } else {
                    this.keyDataBuilder_.setMessage(value);
                }
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder setKeyData(KeyData.Builder builderForValue) {
                if (this.keyDataBuilder_ == null) {
                    this.keyData_ = builderForValue.build();
                } else {
                    this.keyDataBuilder_.setMessage(builderForValue.build());
                }
                this.bitField0_ |= 1;
                this.onChanged();
                return this;
            }

            public Builder mergeKeyData(KeyData value) {
                if (this.keyDataBuilder_ == null) {
                    if ((this.bitField0_ & 1) != 0 && this.keyData_ != null && this.keyData_ != KeyData.getDefaultInstance()) {
                        this.getKeyDataBuilder().mergeFrom(value);
                    } else {
                        this.keyData_ = value;
                    }
                } else {
                    this.keyDataBuilder_.mergeFrom(value);
                }
                if (this.keyData_ != null) {
                    this.bitField0_ |= 1;
                    this.onChanged();
                }
                return this;
            }

            public Builder clearKeyData() {
                this.bitField0_ &= 0xFFFFFFFE;
                this.keyData_ = null;
                if (this.keyDataBuilder_ != null) {
                    this.keyDataBuilder_.dispose();
                    this.keyDataBuilder_ = null;
                }
                this.onChanged();
                return this;
            }

            public KeyData.Builder getKeyDataBuilder() {
                this.bitField0_ |= 1;
                this.onChanged();
                return this.internalGetKeyDataFieldBuilder().getBuilder();
            }

            @Override
            public KeyDataOrBuilder getKeyDataOrBuilder() {
                if (this.keyDataBuilder_ != null) {
                    return this.keyDataBuilder_.getMessageOrBuilder();
                }
                return this.keyData_ == null ? KeyData.getDefaultInstance() : this.keyData_;
            }

            private SingleFieldBuilder<KeyData, KeyData.Builder, KeyDataOrBuilder> internalGetKeyDataFieldBuilder() {
                if (this.keyDataBuilder_ == null) {
                    this.keyDataBuilder_ = new SingleFieldBuilder(this.getKeyData(), this.getParentForChildren(), this.isClean());
                    this.keyData_ = null;
                }
                return this.keyDataBuilder_;
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

    public static interface KeyOrBuilder
    extends MessageOrBuilder {
        public boolean hasKeyData();

        public KeyData getKeyData();

        public KeyDataOrBuilder getKeyDataOrBuilder();

        public int getStatusValue();

        public KeyStatusType getStatus();

        public int getKeyId();

        public int getOutputPrefixTypeValue();

        public OutputPrefixType getOutputPrefixType();
    }
}

