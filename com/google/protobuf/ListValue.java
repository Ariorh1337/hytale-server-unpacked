/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

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
import com.google.protobuf.ListValueOrBuilder;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.RepeatedFieldBuilder;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.StructProto;
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.Value;
import com.google.protobuf.ValueOrBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ListValue
extends GeneratedMessage
implements ListValueOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int VALUES_FIELD_NUMBER = 1;
    private List<Value> values_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final ListValue DEFAULT_INSTANCE;
    private static final Parser<ListValue> PARSER;

    private ListValue(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private ListValue() {
        this.values_ = Collections.emptyList();
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return StructProto.internal_static_google_protobuf_ListValue_descriptor;
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable.ensureFieldAccessorsInitialized(ListValue.class, Builder.class);
    }

    @Override
    public List<Value> getValuesList() {
        return this.values_;
    }

    @Override
    public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
        return this.values_;
    }

    @Override
    public int getValuesCount() {
        return this.values_.size();
    }

    @Override
    public Value getValues(int index) {
        return this.values_.get(index);
    }

    @Override
    public ValueOrBuilder getValuesOrBuilder(int index) {
        return this.values_.get(index);
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
        for (int i = 0; i < this.values_.size(); ++i) {
            output.writeMessage(1, this.values_.get(i));
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
        for (int i = 0; i < this.values_.size(); ++i) {
            size += CodedOutputStream.computeMessageSize(1, this.values_.get(i));
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ListValue)) {
            return super.equals(obj);
        }
        ListValue other = (ListValue)obj;
        if (!this.getValuesList().equals(other.getValuesList())) {
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
        hash = 19 * hash + ListValue.getDescriptor().hashCode();
        if (this.getValuesCount() > 0) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.getValuesList().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static ListValue parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static ListValue parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static ListValue parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static ListValue parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static ListValue parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static ListValue parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static ListValue parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static ListValue parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static ListValue parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static ListValue parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static ListValue parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static ListValue parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return ListValue.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(ListValue prototype) {
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

    public static ListValue getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<ListValue> parser() {
        return PARSER;
    }

    public Parser<ListValue> getParserForType() {
        return PARSER;
    }

    @Override
    public ListValue getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "ListValue");
        DEFAULT_INSTANCE = new ListValue();
        PARSER = new AbstractParser<ListValue>(){

            @Override
            public ListValue parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = ListValue.newBuilder();
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
    implements ListValueOrBuilder {
        private int bitField0_;
        private List<Value> values_ = Collections.emptyList();
        private RepeatedFieldBuilder<Value, Value.Builder, ValueOrBuilder> valuesBuilder_;

        public static final Descriptors.Descriptor getDescriptor() {
            return StructProto.internal_static_google_protobuf_ListValue_descriptor;
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return StructProto.internal_static_google_protobuf_ListValue_fieldAccessorTable.ensureFieldAccessorsInitialized(ListValue.class, Builder.class);
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
            if (this.valuesBuilder_ == null) {
                this.values_ = Collections.emptyList();
            } else {
                this.values_ = null;
                this.valuesBuilder_.clear();
            }
            this.bitField0_ &= 0xFFFFFFFE;
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return StructProto.internal_static_google_protobuf_ListValue_descriptor;
        }

        @Override
        public ListValue getDefaultInstanceForType() {
            return ListValue.getDefaultInstance();
        }

        @Override
        public ListValue build() {
            ListValue result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public ListValue buildPartial() {
            ListValue result = new ListValue(this);
            this.buildPartialRepeatedFields(result);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartialRepeatedFields(ListValue result) {
            if (this.valuesBuilder_ == null) {
                if ((this.bitField0_ & 1) != 0) {
                    this.values_ = Collections.unmodifiableList(this.values_);
                    this.bitField0_ &= 0xFFFFFFFE;
                }
                result.values_ = this.values_;
            } else {
                result.values_ = this.valuesBuilder_.build();
            }
        }

        private void buildPartial0(ListValue result) {
            int from_bitField0_ = this.bitField0_;
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof ListValue) {
                return this.mergeFrom((ListValue)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(ListValue other) {
            if (other == ListValue.getDefaultInstance()) {
                return this;
            }
            if (this.valuesBuilder_ == null) {
                if (!other.values_.isEmpty()) {
                    if (this.values_.isEmpty()) {
                        this.values_ = other.values_;
                        this.bitField0_ &= 0xFFFFFFFE;
                    } else {
                        this.ensureValuesIsMutable();
                        this.values_.addAll(other.values_);
                    }
                    this.onChanged();
                }
            } else if (!other.values_.isEmpty()) {
                if (this.valuesBuilder_.isEmpty()) {
                    this.valuesBuilder_.dispose();
                    this.valuesBuilder_ = null;
                    this.values_ = other.values_;
                    this.bitField0_ &= 0xFFFFFFFE;
                    this.valuesBuilder_ = GeneratedMessage.alwaysUseFieldBuilders ? this.internalGetValuesFieldBuilder() : null;
                } else {
                    this.valuesBuilder_.addAllMessages(other.values_);
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
                block9: while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0: {
                            done = true;
                            continue block9;
                        }
                        case 10: {
                            Value m = input.readMessage(Value.parser(), extensionRegistry);
                            if (this.valuesBuilder_ == null) {
                                this.ensureValuesIsMutable();
                                this.values_.add(m);
                                continue block9;
                            }
                            this.valuesBuilder_.addMessage(m);
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

        private void ensureValuesIsMutable() {
            if ((this.bitField0_ & 1) == 0) {
                this.values_ = new ArrayList<Value>(this.values_);
                this.bitField0_ |= 1;
            }
        }

        @Override
        public List<Value> getValuesList() {
            if (this.valuesBuilder_ == null) {
                return Collections.unmodifiableList(this.values_);
            }
            return this.valuesBuilder_.getMessageList();
        }

        @Override
        public int getValuesCount() {
            if (this.valuesBuilder_ == null) {
                return this.values_.size();
            }
            return this.valuesBuilder_.getCount();
        }

        @Override
        public Value getValues(int index) {
            if (this.valuesBuilder_ == null) {
                return this.values_.get(index);
            }
            return this.valuesBuilder_.getMessage(index);
        }

        public Builder setValues(int index, Value value) {
            if (this.valuesBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureValuesIsMutable();
                this.values_.set(index, value);
                this.onChanged();
            } else {
                this.valuesBuilder_.setMessage(index, value);
            }
            return this;
        }

        public Builder setValues(int index, Value.Builder builderForValue) {
            if (this.valuesBuilder_ == null) {
                this.ensureValuesIsMutable();
                this.values_.set(index, builderForValue.build());
                this.onChanged();
            } else {
                this.valuesBuilder_.setMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addValues(Value value) {
            if (this.valuesBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureValuesIsMutable();
                this.values_.add(value);
                this.onChanged();
            } else {
                this.valuesBuilder_.addMessage(value);
            }
            return this;
        }

        public Builder addValues(int index, Value value) {
            if (this.valuesBuilder_ == null) {
                if (value == null) {
                    throw new NullPointerException();
                }
                this.ensureValuesIsMutable();
                this.values_.add(index, value);
                this.onChanged();
            } else {
                this.valuesBuilder_.addMessage(index, value);
            }
            return this;
        }

        public Builder addValues(Value.Builder builderForValue) {
            if (this.valuesBuilder_ == null) {
                this.ensureValuesIsMutable();
                this.values_.add(builderForValue.build());
                this.onChanged();
            } else {
                this.valuesBuilder_.addMessage(builderForValue.build());
            }
            return this;
        }

        public Builder addValues(int index, Value.Builder builderForValue) {
            if (this.valuesBuilder_ == null) {
                this.ensureValuesIsMutable();
                this.values_.add(index, builderForValue.build());
                this.onChanged();
            } else {
                this.valuesBuilder_.addMessage(index, builderForValue.build());
            }
            return this;
        }

        public Builder addAllValues(Iterable<? extends Value> values) {
            if (this.valuesBuilder_ == null) {
                this.ensureValuesIsMutable();
                AbstractMessageLite.Builder.addAll(values, this.values_);
                this.onChanged();
            } else {
                this.valuesBuilder_.addAllMessages(values);
            }
            return this;
        }

        public Builder clearValues() {
            if (this.valuesBuilder_ == null) {
                this.values_ = Collections.emptyList();
                this.bitField0_ &= 0xFFFFFFFE;
                this.onChanged();
            } else {
                this.valuesBuilder_.clear();
            }
            return this;
        }

        public Builder removeValues(int index) {
            if (this.valuesBuilder_ == null) {
                this.ensureValuesIsMutable();
                this.values_.remove(index);
                this.onChanged();
            } else {
                this.valuesBuilder_.remove(index);
            }
            return this;
        }

        public Value.Builder getValuesBuilder(int index) {
            return this.internalGetValuesFieldBuilder().getBuilder(index);
        }

        @Override
        public ValueOrBuilder getValuesOrBuilder(int index) {
            if (this.valuesBuilder_ == null) {
                return this.values_.get(index);
            }
            return this.valuesBuilder_.getMessageOrBuilder(index);
        }

        @Override
        public List<? extends ValueOrBuilder> getValuesOrBuilderList() {
            if (this.valuesBuilder_ != null) {
                return this.valuesBuilder_.getMessageOrBuilderList();
            }
            return Collections.unmodifiableList(this.values_);
        }

        public Value.Builder addValuesBuilder() {
            return this.internalGetValuesFieldBuilder().addBuilder(Value.getDefaultInstance());
        }

        public Value.Builder addValuesBuilder(int index) {
            return this.internalGetValuesFieldBuilder().addBuilder(index, Value.getDefaultInstance());
        }

        public List<Value.Builder> getValuesBuilderList() {
            return this.internalGetValuesFieldBuilder().getBuilderList();
        }

        private RepeatedFieldBuilder<Value, Value.Builder, ValueOrBuilder> internalGetValuesFieldBuilder() {
            if (this.valuesBuilder_ == null) {
                this.valuesBuilder_ = new RepeatedFieldBuilder(this.values_, (this.bitField0_ & 1) != 0, this.getParentForChildren(), this.isClean());
                this.values_ = null;
            }
            return this.valuesBuilder_;
        }
    }
}

