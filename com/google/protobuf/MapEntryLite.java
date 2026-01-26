/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.ExtensionRegistryLite;
import com.google.protobuf.FieldSet;
import com.google.protobuf.MapFieldLite;
import com.google.protobuf.MessageLite;
import com.google.protobuf.WireFormat;
import java.io.IOException;
import java.util.AbstractMap;
import java.util.Map;

public class MapEntryLite<K, V> {
    private static final int KEY_FIELD_NUMBER = 1;
    private static final int VALUE_FIELD_NUMBER = 2;
    private final Metadata<K, V> metadata;
    private final K key;
    private final V value;

    private MapEntryLite(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
        this.metadata = new Metadata<K, V>(keyType, defaultKey, valueType, defaultValue);
        this.key = defaultKey;
        this.value = defaultValue;
    }

    private MapEntryLite(Metadata<K, V> metadata, K key, V value) {
        this.metadata = metadata;
        this.key = key;
        this.value = value;
    }

    public K getKey() {
        return this.key;
    }

    public V getValue() {
        return this.value;
    }

    public static <K, V> MapEntryLite<K, V> newDefaultInstance(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
        return new MapEntryLite<K, V>(keyType, defaultKey, valueType, defaultValue);
    }

    static <K, V> void writeTo(CodedOutputStream output, Metadata<K, V> metadata, K key, V value) throws IOException {
        FieldSet.writeElement(output, metadata.keyType, 1, key);
        FieldSet.writeElement(output, metadata.valueType, 2, value);
    }

    static <K, V> int computeSerializedSize(Metadata<K, V> metadata, K key, V value) {
        return FieldSet.computeElementSize(metadata.keyType, 1, key) + FieldSet.computeElementSize(metadata.valueType, 2, value);
    }

    static <T> T parseField(CodedInputStream input, ExtensionRegistryLite extensionRegistry, WireFormat.FieldType type, T value) throws IOException {
        switch (type) {
            case MESSAGE: {
                MessageLite.Builder subBuilder = ((MessageLite)value).toBuilder();
                input.readMessage(subBuilder, extensionRegistry);
                return (T)subBuilder.buildPartial();
            }
            case ENUM: {
                return (T)Integer.valueOf(input.readEnum());
            }
            case GROUP: {
                throw new RuntimeException("Groups are not allowed in maps.");
            }
        }
        return (T)FieldSet.readPrimitiveField(input, type, true);
    }

    public void serializeTo(CodedOutputStream output, int fieldNumber, K key, V value) throws IOException {
        output.writeTag(fieldNumber, 2);
        output.writeUInt32NoTag(MapEntryLite.computeSerializedSize(this.metadata, key, value));
        MapEntryLite.writeTo(output, this.metadata, key, value);
    }

    public int computeMessageSize(int fieldNumber, K key, V value) {
        return CodedOutputStream.computeTagSize(fieldNumber) + CodedOutputStream.computeLengthDelimitedFieldSize(MapEntryLite.computeSerializedSize(this.metadata, key, value));
    }

    public Map.Entry<K, V> parseEntry(ByteString bytes, ExtensionRegistryLite extensionRegistry) throws IOException {
        return MapEntryLite.parseEntry(bytes.newCodedInput(), this.metadata, extensionRegistry);
    }

    static <K, V> Map.Entry<K, V> parseEntry(CodedInputStream input, Metadata<K, V> metadata, ExtensionRegistryLite extensionRegistry) throws IOException {
        int tag;
        Object key = metadata.defaultKey;
        Object value = metadata.defaultValue;
        while ((tag = input.readTag()) != 0) {
            if (tag == WireFormat.makeTag(1, metadata.keyType.getWireType())) {
                key = MapEntryLite.parseField(input, extensionRegistry, metadata.keyType, key);
                continue;
            }
            if (tag == WireFormat.makeTag(2, metadata.valueType.getWireType())) {
                value = MapEntryLite.parseField(input, extensionRegistry, metadata.valueType, value);
                continue;
            }
            if (input.skipField(tag)) continue;
            break;
        }
        return new AbstractMap.SimpleImmutableEntry(key, value);
    }

    public void parseInto(MapFieldLite<K, V> map, CodedInputStream input, ExtensionRegistryLite extensionRegistry) throws IOException {
        int tag;
        int length = input.readRawVarint32();
        int oldLimit = input.pushLimit(length);
        Object key = this.metadata.defaultKey;
        Object value = this.metadata.defaultValue;
        while ((tag = input.readTag()) != 0) {
            if (tag == WireFormat.makeTag(1, this.metadata.keyType.getWireType())) {
                key = MapEntryLite.parseField(input, extensionRegistry, this.metadata.keyType, key);
                continue;
            }
            if (tag == WireFormat.makeTag(2, this.metadata.valueType.getWireType())) {
                value = MapEntryLite.parseField(input, extensionRegistry, this.metadata.valueType, value);
                continue;
            }
            if (input.skipField(tag)) continue;
            break;
        }
        input.checkLastTagWas(0);
        input.popLimit(oldLimit);
        map.put(key, value);
    }

    Metadata<K, V> getMetadata() {
        return this.metadata;
    }

    static class Metadata<K, V> {
        public final WireFormat.FieldType keyType;
        public final K defaultKey;
        public final WireFormat.FieldType valueType;
        public final V defaultValue;

        public Metadata(WireFormat.FieldType keyType, K defaultKey, WireFormat.FieldType valueType, V defaultValue) {
            this.keyType = keyType;
            this.defaultKey = defaultKey;
            this.valueType = valueType;
            this.defaultValue = defaultValue;
        }
    }
}

