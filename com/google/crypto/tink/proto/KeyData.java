/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.proto;

import com.google.crypto.tink.proto.KeyDataOrBuilder;
import com.google.crypto.tink.proto.Tink;
import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.Internal;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.ProtocolMessageEnum;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.UninitializedMessageException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public final class KeyData
extends GeneratedMessage
implements KeyDataOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int TYPE_URL_FIELD_NUMBER = 1;
    private volatile Object typeUrl_ = "";
    public static final int VALUE_FIELD_NUMBER = 2;
    private ByteString value_ = ByteString.EMPTY;
    public static final int KEY_MATERIAL_TYPE_FIELD_NUMBER = 3;
    private int keyMaterialType_ = 0;
    private byte memoizedIsInitialized = (byte)-1;
    private static final KeyData DEFAULT_INSTANCE;
    private static final Parser<KeyData> PARSER;

    private KeyData(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private KeyData() {
        this.typeUrl_ = "";
        this.value_ = ByteString.EMPTY;
        this.keyMaterialType_ = 0;
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return Tink.internal_static_google_crypto_tink_KeyData_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return Tink.internal_static_google_crypto_tink_KeyData_fieldAccessorTable.ensureFieldAccessorsInitialized(KeyData.class, Builder.class);
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
    public ByteString getValue() {
        return this.value_;
    }

    @Override
    public int getKeyMaterialTypeValue() {
        return this.keyMaterialType_;
    }

    @Override
    public KeyMaterialType getKeyMaterialType() {
        KeyMaterialType result = KeyMaterialType.forNumber(this.keyMaterialType_);
        return result == null ? KeyMaterialType.UNRECOGNIZED : result;
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
        if (!this.value_.isEmpty()) {
            output.writeBytes(2, this.value_);
        }
        if (this.keyMaterialType_ != KeyMaterialType.UNKNOWN_KEYMATERIAL.getNumber()) {
            output.writeEnum(3, this.keyMaterialType_);
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
        if (!this.value_.isEmpty()) {
            size += CodedOutputStream.computeBytesSize(2, this.value_);
        }
        if (this.keyMaterialType_ != KeyMaterialType.UNKNOWN_KEYMATERIAL.getNumber()) {
            size += CodedOutputStream.computeEnumSize(3, this.keyMaterialType_);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof KeyData)) {
            return super.equals(obj);
        }
        KeyData other = (KeyData)obj;
        if (!this.getTypeUrl().equals(other.getTypeUrl())) {
            return false;
        }
        if (!this.getValue().equals(other.getValue())) {
            return false;
        }
        if (this.keyMaterialType_ != other.keyMaterialType_) {
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
        hash = 19 * hash + KeyData.getDescriptor().hashCode();
        hash = 37 * hash + 1;
        hash = 53 * hash + this.getTypeUrl().hashCode();
        hash = 37 * hash + 2;
        hash = 53 * hash + this.getValue().hashCode();
        hash = 37 * hash + 3;
        hash = 53 * hash + this.keyMaterialType_;
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static KeyData parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KeyData parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KeyData parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KeyData parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KeyData parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static KeyData parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static KeyData parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static KeyData parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static KeyData parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static KeyData parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static KeyData parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static KeyData parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return KeyData.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(KeyData prototype) {
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

    public static KeyData getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<KeyData> parser() {
        return PARSER;
    }

    public Parser<KeyData> getParserForType() {
        return PARSER;
    }

    @Override
    public KeyData getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeyData.class.getName());
        DEFAULT_INSTANCE = new KeyData();
        PARSER = new AbstractParser<KeyData>(){

            @Override
            public KeyData parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = KeyData.newBuilder();
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
    implements KeyDataOrBuilder {
        private int bitField0_;
        private Object typeUrl_ = "";
        private ByteString value_ = ByteString.EMPTY;
        private int keyMaterialType_ = 0;

        public static final Descriptors.Descriptor getDescriptor() {
            return Tink.internal_static_google_crypto_tink_KeyData_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return Tink.internal_static_google_crypto_tink_KeyData_fieldAccessorTable.ensureFieldAccessorsInitialized(KeyData.class, Builder.class);
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
            this.value_ = ByteString.EMPTY;
            this.keyMaterialType_ = 0;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return Tink.internal_static_google_crypto_tink_KeyData_descriptor;
        }

        @Override
        public KeyData getDefaultInstanceForType() {
            return KeyData.getDefaultInstance();
        }

        @Override
        public KeyData build() {
            KeyData result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public KeyData buildPartial() {
            KeyData result = new KeyData(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(KeyData result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.typeUrl_ = this.typeUrl_;
            }
            if ((from_bitField0_ & 2) != 0) {
                result.value_ = this.value_;
            }
            if ((from_bitField0_ & 4) != 0) {
                result.keyMaterialType_ = this.keyMaterialType_;
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof KeyData) {
                return this.mergeFrom((KeyData)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(KeyData other) {
            if (other == KeyData.getDefaultInstance()) {
                return this;
            }
            if (!other.getTypeUrl().isEmpty()) {
                this.typeUrl_ = other.typeUrl_;
                this.bitField0_ |= 1;
                this.onChanged();
            }
            if (!other.getValue().isEmpty()) {
                this.setValue(other.getValue());
            }
            if (other.keyMaterialType_ != 0) {
                this.setKeyMaterialTypeValue(other.getKeyMaterialTypeValue());
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
                            this.typeUrl_ = input.readStringRequireUtf8();
                            this.bitField0_ |= 1;
                            continue block11;
                        }
                        case 18: {
                            this.value_ = input.readBytes();
                            this.bitField0_ |= 2;
                            continue block11;
                        }
                        case 24: {
                            this.keyMaterialType_ = input.readEnum();
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
            this.typeUrl_ = KeyData.getDefaultInstance().getTypeUrl();
            this.bitField0_ &= 0xFFFFFFFE;
            this.onChanged();
            return this;
        }

        public Builder setTypeUrlBytes(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            KeyData.checkByteStringIsUtf8(value);
            this.typeUrl_ = value;
            this.bitField0_ |= 1;
            this.onChanged();
            return this;
        }

        @Override
        public ByteString getValue() {
            return this.value_;
        }

        public Builder setValue(ByteString value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.value_ = value;
            this.bitField0_ |= 2;
            this.onChanged();
            return this;
        }

        public Builder clearValue() {
            this.bitField0_ &= 0xFFFFFFFD;
            this.value_ = KeyData.getDefaultInstance().getValue();
            this.onChanged();
            return this;
        }

        @Override
        public int getKeyMaterialTypeValue() {
            return this.keyMaterialType_;
        }

        public Builder setKeyMaterialTypeValue(int value) {
            this.keyMaterialType_ = value;
            this.bitField0_ |= 4;
            this.onChanged();
            return this;
        }

        @Override
        public KeyMaterialType getKeyMaterialType() {
            KeyMaterialType result = KeyMaterialType.forNumber(this.keyMaterialType_);
            return result == null ? KeyMaterialType.UNRECOGNIZED : result;
        }

        public Builder setKeyMaterialType(KeyMaterialType value) {
            if (value == null) {
                throw new NullPointerException();
            }
            this.bitField0_ |= 4;
            this.keyMaterialType_ = value.getNumber();
            this.onChanged();
            return this;
        }

        public Builder clearKeyMaterialType() {
            this.bitField0_ &= 0xFFFFFFFB;
            this.keyMaterialType_ = 0;
            this.onChanged();
            return this;
        }
    }

    public static enum KeyMaterialType implements ProtocolMessageEnum
    {
        UNKNOWN_KEYMATERIAL(0),
        SYMMETRIC(1),
        ASYMMETRIC_PRIVATE(2),
        ASYMMETRIC_PUBLIC(3),
        REMOTE(4),
        UNRECOGNIZED(-1);

        public static final int UNKNOWN_KEYMATERIAL_VALUE = 0;
        public static final int SYMMETRIC_VALUE = 1;
        public static final int ASYMMETRIC_PRIVATE_VALUE = 2;
        public static final int ASYMMETRIC_PUBLIC_VALUE = 3;
        public static final int REMOTE_VALUE = 4;
        private static final Internal.EnumLiteMap<KeyMaterialType> internalValueMap;
        private static final KeyMaterialType[] VALUES;
        private final int value;

        @Override
        public final int getNumber() {
            if (this == UNRECOGNIZED) {
                throw new IllegalArgumentException("Can't get the number of an unknown enum value.");
            }
            return this.value;
        }

        @Deprecated
        public static KeyMaterialType valueOf(int value) {
            return KeyMaterialType.forNumber(value);
        }

        public static KeyMaterialType forNumber(int value) {
            switch (value) {
                case 0: {
                    return UNKNOWN_KEYMATERIAL;
                }
                case 1: {
                    return SYMMETRIC;
                }
                case 2: {
                    return ASYMMETRIC_PRIVATE;
                }
                case 3: {
                    return ASYMMETRIC_PUBLIC;
                }
                case 4: {
                    return REMOTE;
                }
            }
            return null;
        }

        public static Internal.EnumLiteMap<KeyMaterialType> internalGetValueMap() {
            return internalValueMap;
        }

        @Override
        public final Descriptors.EnumValueDescriptor getValueDescriptor() {
            if (this == UNRECOGNIZED) {
                throw new IllegalStateException("Can't get the descriptor of an unrecognized enum value.");
            }
            return KeyMaterialType.getDescriptor().getValues().get(this.ordinal());
        }

        @Override
        public final Descriptors.EnumDescriptor getDescriptorForType() {
            return KeyMaterialType.getDescriptor();
        }

        public static Descriptors.EnumDescriptor getDescriptor() {
            return KeyData.getDescriptor().getEnumTypes().get(0);
        }

        public static KeyMaterialType valueOf(Descriptors.EnumValueDescriptor desc) {
            if (desc.getType() != KeyMaterialType.getDescriptor()) {
                throw new IllegalArgumentException("EnumValueDescriptor is not for this type.");
            }
            if (desc.getIndex() == -1) {
                return UNRECOGNIZED;
            }
            return VALUES[desc.getIndex()];
        }

        private KeyMaterialType(int value) {
            this.value = value;
        }

        static {
            RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 32, 1, "", KeyMaterialType.class.getName());
            internalValueMap = new Internal.EnumLiteMap<KeyMaterialType>(){

                @Override
                public KeyMaterialType findValueByNumber(int number) {
                    return KeyMaterialType.forNumber(number);
                }
            };
            VALUES = KeyMaterialType.values();
        }
    }
}

