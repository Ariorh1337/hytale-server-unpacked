/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.AbstractParser;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.Descriptors;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.Message;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.google.protobuf.UnknownFieldSet;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

public final class MapEntry<K, V>
extends AbstractMessage {
    private final K key;
    private final V value;
    private final Metadata<K, V> metadata;
    private volatile int cachedSerializedSize = -1;

    private MapEntry(Descriptors.Descriptor descriptor, WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
        this.key = defaultKey;
        this.value = defaultValue;
        this.metadata = new Metadata(descriptor, this, keyType, valueType);
    }

    private MapEntry(Metadata metadata, K key, V value) {
        this.key = key;
        this.value = value;
        this.metadata = metadata;
    }

    private MapEntry(Metadata<K, V> metadata, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
        try {
            this.metadata = metadata;
            Map.Entry<K, V> entry = MapEntryLite.parseEntry(input, metadata, extensionRegistry);
            this.key = entry.getKey();
            this.value = entry.getValue();
        }
        catch (InvalidProtocolBufferException e) {
            throw e.setUnfinishedMessage(this);
        }
        catch (IOException e) {
            throw new InvalidProtocolBufferException(e).setUnfinishedMessage(this);
        }
    }

    public static <K, V> MapEntry<K, V> newDefaultInstance(Descriptors.Descriptor descriptor, WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
        return new MapEntry<K, V>(descriptor, keyType, defaultKey, valueType, defaultValue);
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    @Override
    public int getSerializedSize() {
        int size;
        if (this.cachedSerializedSize != -1) {
            return this.cachedSerializedSize;
        }
        this.cachedSerializedSize = size = MapEntryLite.computeSerializedSize(this.metadata, this.key, this.value);
        return size;
    }

    @Override
    public void writeTo(CodedOutputStream output) throws IOException {
        MapEntryLite.writeTo(output, this.metadata, this.key, this.value);
    }

    @Override
    public boolean isInitialized() {
        return MapEntry.isInitialized(this.metadata, this.value);
    }

    public Parser<MapEntry<K, V>> getParserForType() {
        return this.metadata.parser;
    }

    @Override
    public Builder<K, V> newBuilderForType() {
        return new Builder(this.metadata);
    }

    @Override
    public Builder<K, V> toBuilder() {
        return new Builder(this.metadata, this.key, this.value, true, true);
    }

    @Override
    public MapEntry<K, V> getDefaultInstanceForType() {
        return new MapEntry<Object, Object>(this.metadata, this.metadata.defaultKey, this.metadata.defaultValue);
    }

    @Override
    public Descriptors.Descriptor getDescriptorForType() {
        return this.metadata.descriptor;
    }

    @Override
    public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
        TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<Descriptors.FieldDescriptor, Object>();
        for (Descriptors.FieldDescriptor field : this.metadata.descriptor.getFields()) {
            if (!this.hasField(field)) continue;
            result.put(field, this.getField(field));
        }
        return Collections.unmodifiableMap(result);
    }

    private void checkFieldDescriptor(Descriptors.FieldDescriptor field) {
        if (field.getContainingType() != this.metadata.descriptor) {
            throw new RuntimeException("Wrong FieldDescriptor \"" + field.getFullName() + "\" used in message \"" + this.metadata.descriptor.getFullName());
        }
    }

    @Override
    public boolean hasField(Descriptors.FieldDescriptor field) {
        this.checkFieldDescriptor(field);
        return true;
    }

    @Override
    public Object getField(Descriptors.FieldDescriptor field) {
        Object result;
        this.checkFieldDescriptor(field);
        Object object = result = field.getNumber() == 1 ? this.getKey() : this.getValue();
        if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
            result = field.getEnumType().findValueByNumberCreatingIfUnknown((Integer)result);
        }
        return result;
    }

    @Override
    public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
        throw new RuntimeException("There is no repeated field in a map entry message.");
    }

    @Override
    public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
        throw new RuntimeException("There is no repeated field in a map entry message.");
    }

    @Override
    public UnknownFieldSet getUnknownFields() {
        return UnknownFieldSet.getDefaultInstance();
    }

    private static <V> boolean isInitialized(Metadata metadata, V value) {
        if (metadata.valueType.getJavaType() == WireFormat.JavaType.MESSAGE) {
            return ((MessageLite)value).isInitialized();
        }
        return true;
    }

    final Metadata<K, V> getMetadata() {
        return this.metadata;
    }

    private static final class Metadata<K, V>
    extends MapEntryLite.Metadata<K, V> {
        public final Descriptors.Descriptor descriptor;
        public final Parser<MapEntry<K, V>> parser;

        public Metadata(Descriptors.Descriptor descriptor, MapEntry<K, V> defaultInstance, WireFormat.FieldType keyType, WireFormat.FieldType valueType) {
            super(keyType, ((MapEntry)defaultInstance).key, valueType, ((MapEntry)defaultInstance).value);
            this.descriptor = descriptor;
            this.parser = new AbstractParser<MapEntry<K, V>>(){

                @Override
                public MapEntry<K, V> parsePartialFrom(CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws InvalidProtocolBufferException {
                    return new MapEntry(this, input, extensionRegistry);
                }
            };
        }
    }

    public static class Builder<K, V>
    extends AbstractMessage.Builder<Builder<K, V>> {
        private final Metadata<K, V> metadata;
        private K key;
        private V value;
        private boolean hasKey;
        private boolean hasValue;

        private Builder(Metadata<K, V> metadata) {
            this(metadata, metadata.defaultKey, metadata.defaultValue, false, false);
        }

        private Builder(Metadata<K, V> metadata, K key, V value, boolean hasKey, boolean hasValue) {
            this.metadata = metadata;
            this.key = key;
            this.value = value;
            this.hasKey = hasKey;
            this.hasValue = hasValue;
        }

        public K getKey() {
            return this.key;
        }

        public V getValue() {
            return this.value;
        }

        public Builder<K, V> setKey(K key) {
            this.key = key;
            this.hasKey = true;
            return this;
        }

        public Builder<K, V> clearKey() {
            this.key = this.metadata.defaultKey;
            this.hasKey = false;
            return this;
        }

        public Builder<K, V> setValue(V value) {
            this.value = value;
            this.hasValue = true;
            return this;
        }

        public Builder<K, V> clearValue() {
            this.value = this.metadata.defaultValue;
            this.hasValue = false;
            return this;
        }

        @Override
        public MapEntry<K, V> build() {
            Message result = this.buildPartial();
            if (!((MapEntry)result).isInitialized()) {
                throw Builder.newUninitializedMessageException(result);
            }
            return result;
        }

        @Override
        public MapEntry<K, V> buildPartial() {
            return new MapEntry(this.metadata, this.key, this.value);
        }

        @Override
        public Descriptors.Descriptor getDescriptorForType() {
            return this.metadata.descriptor;
        }

        private void checkFieldDescriptor(Descriptors.FieldDescriptor field) {
            if (field.getContainingType() != this.metadata.descriptor) {
                throw new RuntimeException("Wrong FieldDescriptor \"" + field.getFullName() + "\" used in message \"" + this.metadata.descriptor.getFullName());
            }
        }

        @Override
        public Message.Builder newBuilderForField(Descriptors.FieldDescriptor field) {
            this.checkFieldDescriptor(field);
            if (field.getNumber() != 2 || field.getJavaType() != Descriptors.FieldDescriptor.JavaType.MESSAGE) {
                throw new RuntimeException("\"" + field.getFullName() + "\" is not a message value field.");
            }
            return ((Message)this.value).newBuilderForType();
        }

        @Override
        public Builder<K, V> setField(Descriptors.FieldDescriptor field, Object value) {
            this.checkFieldDescriptor(field);
            if (value == null) {
                throw new NullPointerException(field.getFullName() + " is null");
            }
            if (field.getNumber() == 1) {
                this.setKey(value);
            } else {
                if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
                    value = ((Descriptors.EnumValueDescriptor)value).getNumber();
                } else if (field.getType() == Descriptors.FieldDescriptor.Type.MESSAGE && !this.metadata.defaultValue.getClass().isInstance(value)) {
                    value = ((Message)this.metadata.defaultValue).toBuilder().mergeFrom((Message)value).build();
                }
                this.setValue(value);
            }
            return this;
        }

        @Override
        public Builder<K, V> clearField(Descriptors.FieldDescriptor field) {
            this.checkFieldDescriptor(field);
            if (field.getNumber() == 1) {
                this.clearKey();
            } else {
                this.clearValue();
            }
            return this;
        }

        @Override
        public Builder<K, V> setRepeatedField(Descriptors.FieldDescriptor field, int index, Object value) {
            throw new RuntimeException("There is no repeated field in a map entry message.");
        }

        @Override
        public Builder<K, V> addRepeatedField(Descriptors.FieldDescriptor field, Object value) {
            throw new RuntimeException("There is no repeated field in a map entry message.");
        }

        @Override
        public Builder<K, V> setUnknownFields(UnknownFieldSet unknownFields) {
            return this;
        }

        @Override
        public MapEntry<K, V> getDefaultInstanceForType() {
            return new MapEntry(this.metadata, this.metadata.defaultKey, this.metadata.defaultValue);
        }

        @Override
        public boolean isInitialized() {
            return MapEntry.isInitialized(this.metadata, this.value);
        }

        @Override
        public Map<Descriptors.FieldDescriptor, Object> getAllFields() {
            TreeMap<Descriptors.FieldDescriptor, Object> result = new TreeMap<Descriptors.FieldDescriptor, Object>();
            for (Descriptors.FieldDescriptor field : this.metadata.descriptor.getFields()) {
                if (!this.hasField(field)) continue;
                result.put(field, this.getField(field));
            }
            return Collections.unmodifiableMap(result);
        }

        @Override
        public boolean hasField(Descriptors.FieldDescriptor field) {
            this.checkFieldDescriptor(field);
            return field.getNumber() == 1 ? this.hasKey : this.hasValue;
        }

        @Override
        public Object getField(Descriptors.FieldDescriptor field) {
            Object result;
            this.checkFieldDescriptor(field);
            Object object = result = field.getNumber() == 1 ? this.getKey() : this.getValue();
            if (field.getType() == Descriptors.FieldDescriptor.Type.ENUM) {
                result = field.getEnumType().findValueByNumberCreatingIfUnknown((Integer)result);
            }
            return result;
        }

        @Override
        public int getRepeatedFieldCount(Descriptors.FieldDescriptor field) {
            throw new RuntimeException("There is no repeated field in a map entry message.");
        }

        @Override
        public Object getRepeatedField(Descriptors.FieldDescriptor field, int index) {
            throw new RuntimeException("There is no repeated field in a map entry message.");
        }

        @Override
        public UnknownFieldSet getUnknownFields() {
            return UnknownFieldSet.getDefaultInstance();
        }

        @Override
        public Builder<K, V> clone() {
            return new Builder<K, V>(this.metadata, this.key, this.value, this.hasKey, this.hasValue);
        }
    }
}

