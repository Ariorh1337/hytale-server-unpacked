/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.GeneratedMessage;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MapEntry;
import com.google.protobuf.MapField;
import com.google.protobuf.MapFieldBuilder;
import com.google.protobuf.MapFieldReflectionAccessor;
import com.google.protobuf.Message;
import com.google.protobuf.Parser;
import com.google.protobuf.RuntimeVersion;
import com.google.protobuf.StructOrBuilder;
import com.google.protobuf.StructProto;
import com.google.protobuf.UninitializedMessageException;
import com.google.protobuf.Value;
import com.google.protobuf.ValueOrBuilder;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Map;

public final class Struct
extends GeneratedMessage
implements StructOrBuilder {
    private static final long serialVersionUID = 0L;
    public static final int FIELDS_FIELD_NUMBER = 1;
    private MapField<String, Value> fields_;
    private byte memoizedIsInitialized = (byte)-1;
    private static final Struct DEFAULT_INSTANCE;
    private static final Parser<Struct> PARSER;

    private Struct(GeneratedMessage.Builder<?> builder) {
        super(builder);
    }

    private Struct() {
    }

    public static final Descriptors.Descriptor getDescriptor() {
        return StructProto.internal_static_google_protobuf_Struct_descriptor;
    }

    @Override
    protected MapFieldReflectionAccessor internalGetMapFieldReflection(int number) {
        switch (number) {
            case 1: {
                return this.internalGetFields();
            }
        }
        throw new RuntimeException("Invalid map field number: " + number);
    }

    @Override
    protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
        return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Builder.class);
    }

    private MapField<String, Value> internalGetFields() {
        if (this.fields_ == null) {
            return MapField.emptyMapField(FieldsDefaultEntryHolder.defaultEntry);
        }
        return this.fields_;
    }

    @Override
    public int getFieldsCount() {
        return this.internalGetFields().getMap().size();
    }

    @Override
    public boolean containsFields(String key) {
        if (key == null) {
            throw new NullPointerException("map key");
        }
        return this.internalGetFields().getMap().containsKey(key);
    }

    @Override
    @Deprecated
    public Map<String, Value> getFields() {
        return this.getFieldsMap();
    }

    @Override
    public Map<String, Value> getFieldsMap() {
        return this.internalGetFields().getMap();
    }

    @Override
    public Value getFieldsOrDefault(String key, Value defaultValue) {
        if (key == null) {
            throw new NullPointerException("map key");
        }
        Map<String, Value> map = this.internalGetFields().getMap();
        return map.containsKey(key) ? map.get(key) : defaultValue;
    }

    @Override
    public Value getFieldsOrThrow(String key) {
        if (key == null) {
            throw new NullPointerException("map key");
        }
        Map<String, Value> map = this.internalGetFields().getMap();
        if (!map.containsKey(key)) {
            throw new IllegalArgumentException();
        }
        return map.get(key);
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
        GeneratedMessage.serializeStringMapTo(output, this.internalGetFields(), FieldsDefaultEntryHolder.defaultEntry, 1);
        this.getUnknownFields().writeTo(output);
    }

    @Override
    public int getSerializedSize() {
        int size = this.memoizedSize;
        if (size != -1) {
            return size;
        }
        size = 0;
        for (Map.Entry<String, Value> entry : this.internalGetFields().getMap().entrySet()) {
            Message fields__ = ((MapEntry.Builder)FieldsDefaultEntryHolder.defaultEntry.newBuilderForType()).setKey(entry.getKey()).setValue(entry.getValue()).build();
            size += CodedOutputStream.computeMessageSize(1, fields__);
        }
        this.memoizedSize = size += this.getUnknownFields().getSerializedSize();
        return size;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof Struct)) {
            return super.equals(obj);
        }
        Struct other = (Struct)obj;
        if (!this.internalGetFields().equals(other.internalGetFields())) {
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
        hash = 19 * hash + Struct.getDescriptor().hashCode();
        if (!this.internalGetFields().getMap().isEmpty()) {
            hash = 37 * hash + 1;
            hash = 53 * hash + this.internalGetFields().hashCode();
        }
        this.memoizedHashCode = hash = 29 * hash + this.getUnknownFields().hashCode();
        return hash;
    }

    public static Struct parseFrom(ByteBuffer data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Struct parseFrom(ByteBuffer data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Struct parseFrom(ByteString data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Struct parseFrom(ByteString data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Struct parseFrom(byte[] data) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data);
    }

    public static Struct parseFrom(byte[] data, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        return PARSER.parseFrom(data, extensionRegistry);
    }

    public static Struct parseFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static Struct parseFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    public static Struct parseDelimitedFrom(InputStream input) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input);
    }

    public static Struct parseDelimitedFrom(InputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }

    public static Struct parseFrom(CodedInputStream input) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input);
    }

    public static Struct parseFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        return GeneratedMessage.parseWithIOException(PARSER, input, extensionRegistry);
    }

    @Override
    public Builder newBuilderForType() {
        return Struct.newBuilder();
    }

    public static Builder newBuilder() {
        return DEFAULT_INSTANCE.toBuilder();
    }

    public static Builder newBuilder(Struct prototype) {
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

    public static Struct getDefaultInstance() {
        return DEFAULT_INSTANCE;
    }

    public static Parser<Struct> parser() {
        return PARSER;
    }

    public Parser<Struct> getParserForType() {
        return PARSER;
    }

    @Override
    public Struct getDefaultInstanceForType() {
        return DEFAULT_INSTANCE;
    }

    static {
        RuntimeVersion.validateProtobufGencodeVersion(RuntimeVersion.RuntimeDomain.PUBLIC, 4, 33, 0, "", "Struct");
        DEFAULT_INSTANCE = new Struct();
        PARSER = new AbstractParser<Struct>(){

            @Override
            public Struct parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                Builder builder = Struct.newBuilder();
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
    implements StructOrBuilder {
        private int bitField0_;
        private static final FieldsConverter fieldsConverter = new FieldsConverter();
        private MapFieldBuilder<String, ValueOrBuilder, Value, Value.Builder> fields_;

        public static final Descriptors.Descriptor getDescriptor() {
            return StructProto.internal_static_google_protobuf_Struct_descriptor;
        }

        @Override
        protected MapFieldReflectionAccessor internalGetMapFieldReflection(int number) {
            switch (number) {
                case 1: {
                    return this.internalGetFields();
                }
            }
            throw new RuntimeException("Invalid map field number: " + number);
        }

        @Override
        protected MapFieldReflectionAccessor internalGetMutableMapFieldReflection(int number) {
            switch (number) {
                case 1: {
                    return this.internalGetMutableFields();
                }
            }
            throw new RuntimeException("Invalid map field number: " + number);
        }

        @Override
        protected GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return StructProto.internal_static_google_protobuf_Struct_fieldAccessorTable.ensureFieldAccessorsInitialized(Struct.class, Builder.class);
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
            this.internalGetMutableFields().clear();
            return this;
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return StructProto.internal_static_google_protobuf_Struct_descriptor;
        }

        @Override
        public Struct getDefaultInstanceForType() {
            return Struct.getDefaultInstance();
        }

        @Override
        public Struct build() {
            Struct result = this.buildPartial();
            if (!result.isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public Struct buildPartial() {
            Struct result = new Struct(this);
            if (this.bitField0_ != 0) {
                this.buildPartial0(result);
            }
            this.onBuilt();
            return result;
        }

        private void buildPartial0(Struct result) {
            int from_bitField0_ = this.bitField0_;
            if ((from_bitField0_ & 1) != 0) {
                result.fields_ = this.internalGetFields().build(FieldsDefaultEntryHolder.defaultEntry);
            }
        }

        @Override
        public Builder mergeFrom(Message other) {
            if (other instanceof Struct) {
                return this.mergeFrom((Struct)other);
            }
            super.mergeFrom(other);
            return this;
        }

        public Builder mergeFrom(Struct other) {
            if (other == Struct.getDefaultInstance()) {
                return this;
            }
            this.internalGetMutableFields().mergeFrom(other.internalGetFields());
            this.bitField0_ |= 1;
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
                            MapEntry<String, Value> fields__ = input.readMessage(FieldsDefaultEntryHolder.defaultEntry.getParserForType(), extensionRegistry);
                            this.internalGetMutableFields().ensureBuilderMap().put(fields__.getKey(), fields__.getValue());
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

        private MapFieldBuilder<String, ValueOrBuilder, Value, Value.Builder> internalGetFields() {
            if (this.fields_ == null) {
                return new MapFieldBuilder<String, ValueOrBuilder, Value, Value.Builder>(fieldsConverter);
            }
            return this.fields_;
        }

        private MapFieldBuilder<String, ValueOrBuilder, Value, Value.Builder> internalGetMutableFields() {
            if (this.fields_ == null) {
                this.fields_ = new MapFieldBuilder(fieldsConverter);
            }
            this.bitField0_ |= 1;
            this.onChanged();
            return this.fields_;
        }

        @Override
        public int getFieldsCount() {
            return this.internalGetFields().ensureBuilderMap().size();
        }

        @Override
        public boolean containsFields(String key) {
            if (key == null) {
                throw new NullPointerException("map key");
            }
            return this.internalGetFields().ensureBuilderMap().containsKey(key);
        }

        @Override
        @Deprecated
        public Map<String, Value> getFields() {
            return this.getFieldsMap();
        }

        @Override
        public Map<String, Value> getFieldsMap() {
            return this.internalGetFields().getImmutableMap();
        }

        @Override
        public Value getFieldsOrDefault(String key, Value defaultValue) {
            if (key == null) {
                throw new NullPointerException("map key");
            }
            Map<String, ValueOrBuilder> map = this.internalGetMutableFields().ensureBuilderMap();
            return map.containsKey(key) ? fieldsConverter.build(map.get(key)) : defaultValue;
        }

        @Override
        public Value getFieldsOrThrow(String key) {
            if (key == null) {
                throw new NullPointerException("map key");
            }
            Map<String, ValueOrBuilder> map = this.internalGetMutableFields().ensureBuilderMap();
            if (!map.containsKey(key)) {
                throw new IllegalArgumentException();
            }
            return fieldsConverter.build(map.get(key));
        }

        public Builder clearFields() {
            this.bitField0_ &= 0xFFFFFFFE;
            this.internalGetMutableFields().clear();
            return this;
        }

        public Builder removeFields(String key) {
            if (key == null) {
                throw new NullPointerException("map key");
            }
            this.internalGetMutableFields().ensureBuilderMap().remove(key);
            return this;
        }

        @Deprecated
        public Map<String, Value> getMutableFields() {
            this.bitField0_ |= 1;
            return this.internalGetMutableFields().ensureMessageMap();
        }

        public Builder putFields(String key, Value value) {
            if (key == null) {
                throw new NullPointerException("map key");
            }
            if (value == null) {
                throw new NullPointerException("map value");
            }
            this.internalGetMutableFields().ensureBuilderMap().put(key, value);
            this.bitField0_ |= 1;
            return this;
        }

        public Builder putAllFields(Map<String, Value> values) {
            for (Map.Entry<String, Value> e : values.entrySet()) {
                if (e.getKey() != null && e.getValue() != null) continue;
                throw new NullPointerException();
            }
            this.internalGetMutableFields().ensureBuilderMap().putAll(values);
            this.bitField0_ |= 1;
            return this;
        }

        public Value.Builder putFieldsBuilderIfAbsent(String key) {
            Map<String, ValueOrBuilder> builderMap = this.internalGetMutableFields().ensureBuilderMap();
            ValueOrBuilder entry = builderMap.get(key);
            if (entry == null) {
                entry = Value.newBuilder();
                builderMap.put(key, entry);
            }
            if (entry instanceof Value) {
                entry = ((Value)entry).toBuilder();
                builderMap.put(key, entry);
            }
            return (Value.Builder)entry;
        }

        private static final class FieldsConverter
        implements MapFieldBuilder.Converter<String, ValueOrBuilder, Value> {
            private FieldsConverter() {
            }

            @Override
            public Value build(ValueOrBuilder val) {
                if (val instanceof Value) {
                    return (Value)val;
                }
                return ((Value.Builder)val).build();
            }

            @Override
            public MapEntry<String, Value> defaultEntry() {
                return FieldsDefaultEntryHolder.defaultEntry;
            }
        }
    }

    private static final class FieldsDefaultEntryHolder {
        static final MapEntry<String, Value> defaultEntry = MapEntry.newDefaultInstance(StructProto.internal_static_google_protobuf_Struct_FieldsEntry_descriptor, WireFormat.FieldType.STRING, "", WireFormat.FieldType.MESSAGE, Value.getDefaultInstance());

        private FieldsDefaultEntryHolder() {
        }
    }
}

