/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

import com.google.protobuf.AbstractMessage;
import com.google.protobuf.MapEntry;
import com.google.protobuf.MapField;
import com.google.protobuf.MapFieldLite;
import com.google.protobuf.MapFieldReflectionAccessor;
import com.google.protobuf.Message;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.MutabilityOracle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapFieldBuilder<KeyT, MessageOrBuilderT extends MessageOrBuilder, MessageT extends MessageOrBuilderT, BuilderT extends MessageOrBuilderT>
extends MapFieldReflectionAccessor {
    Map<KeyT, MessageOrBuilderT> builderMap = new LinkedHashMap<KeyT, MessageOrBuilderT>();
    Map<KeyT, MessageT> messageMap = null;
    List<Message> messageList = null;
    Converter<KeyT, MessageOrBuilderT, MessageT> converter;

    public MapFieldBuilder(Converter<KeyT, MessageOrBuilderT, MessageT> converter) {
        this.converter = converter;
    }

    private List<MapEntry<KeyT, MessageT>> getMapEntryList() {
        ArrayList<MapEntry<KeyT, MessageT>> list = new ArrayList<MapEntry<KeyT, MessageT>>(this.messageList.size());
        Class<?> valueClass = ((MessageOrBuilder)this.converter.defaultEntry().getValue()).getClass();
        for (Message entry : this.messageList) {
            MapEntry typedEntry = (MapEntry)entry;
            if (valueClass.isInstance(typedEntry.getValue())) {
                list.add(typedEntry);
                continue;
            }
            list.add((MapEntry<KeyT, MessageT>)((MapEntry.Builder)((AbstractMessage.Builder)this.converter.defaultEntry().toBuilder()).mergeFrom(entry)).build());
        }
        return list;
    }

    public Map<KeyT, MessageOrBuilderT> ensureBuilderMap() {
        if (this.builderMap != null) {
            return this.builderMap;
        }
        if (this.messageMap != null) {
            this.builderMap = new LinkedHashMap<KeyT, MessageOrBuilderT>(this.messageMap.size());
            for (Map.Entry<KeyT, MessageT> entry : this.messageMap.entrySet()) {
                this.builderMap.put(entry.getKey(), (MessageOrBuilder)entry.getValue());
            }
            this.messageMap = null;
            return this.builderMap;
        }
        this.builderMap = new LinkedHashMap<KeyT, MessageOrBuilderT>(this.messageList.size());
        for (MapEntry<KeyT, MessageT> entry : this.getMapEntryList()) {
            this.builderMap.put(entry.getKey(), (MessageOrBuilder)entry.getValue());
        }
        this.messageList = null;
        return this.builderMap;
    }

    public List<Message> ensureMessageList() {
        if (this.messageList != null) {
            return this.messageList;
        }
        if (this.builderMap != null) {
            this.messageList = new ArrayList<Message>(this.builderMap.size());
            for (Map.Entry<KeyT, MessageOrBuilderT> entry : this.builderMap.entrySet()) {
                this.messageList.add(((MapEntry.Builder)this.converter.defaultEntry().toBuilder()).setKey(entry.getKey()).setValue(this.converter.build((MessageOrBuilder)entry.getValue())).build());
            }
            this.builderMap = null;
            return this.messageList;
        }
        this.messageList = new ArrayList<Message>(this.messageMap.size());
        for (Map.Entry<KeyT, MessageT> entry : this.messageMap.entrySet()) {
            this.messageList.add(((MapEntry.Builder)this.converter.defaultEntry().toBuilder()).setKey(entry.getKey()).setValue((MessageOrBuilder)entry.getValue()).build());
        }
        this.messageMap = null;
        return this.messageList;
    }

    public Map<KeyT, MessageT> ensureMessageMap() {
        this.messageMap = this.populateMutableMap();
        this.builderMap = null;
        this.messageList = null;
        return this.messageMap;
    }

    public Map<KeyT, MessageT> getImmutableMap() {
        return new MapField.MutabilityAwareMap<KeyT, MessageT>(MutabilityOracle.IMMUTABLE, this.populateMutableMap());
    }

    private Map<KeyT, MessageT> populateMutableMap() {
        if (this.messageMap != null) {
            return this.messageMap;
        }
        if (this.builderMap != null) {
            LinkedHashMap<KeyT, MessageT> toReturn = new LinkedHashMap<KeyT, MessageT>(this.builderMap.size());
            for (Map.Entry<KeyT, MessageOrBuilderT> entry : this.builderMap.entrySet()) {
                toReturn.put(entry.getKey(), this.converter.build((MessageOrBuilder)entry.getValue()));
            }
            return toReturn;
        }
        LinkedHashMap<KeyT, MessageOrBuilder> toReturn = new LinkedHashMap<KeyT, MessageOrBuilder>(this.messageList.size());
        for (MapEntry<KeyT, MessageT> entry : this.getMapEntryList()) {
            toReturn.put(entry.getKey(), (MessageOrBuilder)entry.getValue());
        }
        return toReturn;
    }

    public void mergeFrom(MapField<KeyT, MessageT> other) {
        this.ensureBuilderMap().putAll(MapFieldLite.copy(other.getMap()));
    }

    public void clear() {
        this.builderMap = new LinkedHashMap<KeyT, MessageOrBuilderT>();
        this.messageMap = null;
        this.messageList = null;
    }

    private boolean typedEquals(MapFieldBuilder<KeyT, MessageOrBuilderT, MessageT, BuilderT> other) {
        return MapFieldLite.equals(this.ensureBuilderMap(), other.ensureBuilderMap());
    }

    public boolean equals(Object object) {
        if (!(object instanceof MapFieldBuilder)) {
            return false;
        }
        return this.typedEquals((MapFieldBuilder)object);
    }

    public int hashCode() {
        return MapFieldLite.calculateHashCodeForMap(this.ensureBuilderMap());
    }

    public MapFieldBuilder<KeyT, MessageOrBuilderT, MessageT, BuilderT> copy() {
        MapFieldBuilder<KeyT, MessageOrBuilderT, MessageT, BuilderT> clone = new MapFieldBuilder<KeyT, MessageOrBuilderT, MessageT, BuilderT>(this.converter);
        clone.ensureBuilderMap().putAll(this.ensureBuilderMap());
        return clone;
    }

    public MapField<KeyT, MessageT> build(MapEntry<KeyT, MessageT> defaultEntry) {
        MapField<KeyT, MessageT> mapField = MapField.newMapField(defaultEntry);
        Map<KeyT, MessageT> map = mapField.getMutableMap();
        for (Map.Entry<KeyT, MessageOrBuilderT> entry : this.ensureBuilderMap().entrySet()) {
            map.put(entry.getKey(), this.converter.build((MessageOrBuilder)entry.getValue()));
        }
        mapField.makeImmutable();
        return mapField;
    }

    @Override
    List<Message> getList() {
        return this.ensureMessageList();
    }

    @Override
    List<Message> getMutableList() {
        return this.ensureMessageList();
    }

    @Override
    Message getMapEntryMessageDefaultInstance() {
        return this.converter.defaultEntry();
    }

    public static interface Converter<KeyT, MessageOrBuilderT extends MessageOrBuilder, MessageT extends MessageOrBuilderT> {
        public MessageT build(MessageOrBuilderT var1);

        public MapEntry<KeyT, MessageT> defaultEntry();
    }
}

