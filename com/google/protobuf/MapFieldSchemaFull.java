/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.MapEntry;
import com.google.protobuf.MapEntryLite;
import com.google.protobuf.MapField;
import com.google.protobuf.MapFieldSchema;
import java.util.Map;

final class MapFieldSchemaFull
implements MapFieldSchema {
    MapFieldSchemaFull() {
    }

    @Override
    public Map<?, ?> forMutableMapData(Object mapField) {
        return ((MapField)mapField).getMutableMap();
    }

    @Override
    public Map<?, ?> forMapData(Object mapField) {
        return ((MapField)mapField).getMap();
    }

    @Override
    public boolean isImmutable(Object mapField) {
        return !((MapField)mapField).isMutable();
    }

    @Override
    public Object toImmutable(Object mapField) {
        ((MapField)mapField).makeImmutable();
        return mapField;
    }

    @Override
    public Object newMapField(Object mapDefaultEntry) {
        return MapField.newMapField((MapEntry)mapDefaultEntry);
    }

    @Override
    public MapEntryLite.Metadata<?, ?> forMapMetadata(Object mapDefaultEntry) {
        return ((MapEntry)mapDefaultEntry).getMetadata();
    }

    @Override
    public Object mergeFrom(Object destMapField, Object srcMapField) {
        return MapFieldSchemaFull.mergeFromFull(destMapField, srcMapField);
    }

    private static <K, V> Object mergeFromFull(Object destMapField, Object srcMapField) {
        MapField mine = (MapField)destMapField;
        MapField other = (MapField)srcMapField;
        if (!mine.isMutable()) {
            mine.copy();
        }
        mine.mergeFrom(other);
        return mine;
    }

    @Override
    public int getSerializedSize(int number, Object mapField, Object mapDefaultEntry) {
        return MapFieldSchemaFull.getSerializedSizeFull(number, mapField, mapDefaultEntry);
    }

    private static <K, V> int getSerializedSizeFull(int number, Object mapField, Object defaultEntryObject) {
        if (mapField == null) {
            return 0;
        }
        Map map = ((MapField)mapField).getMap();
        MapEntry defaultEntry = (MapEntry)defaultEntryObject;
        if (map.isEmpty()) {
            return 0;
        }
        int size = 0;
        for (Map.Entry entry : map.entrySet()) {
            size += CodedOutputStream.computeTagSize(number) + CodedOutputStream.computeLengthDelimitedFieldSize(MapEntryLite.computeSerializedSize(defaultEntry.getMetadata(), entry.getKey(), entry.getValue()));
        }
        return size;
    }
}

