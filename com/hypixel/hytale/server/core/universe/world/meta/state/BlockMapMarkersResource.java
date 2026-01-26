/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.meta.state;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.component.Resource;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.annotation.Nonnull;

public class BlockMapMarkersResource
implements Resource<ChunkStore> {
    public static final BuilderCodec<BlockMapMarkersResource> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(BlockMapMarkersResource.class, BlockMapMarkersResource::new).append(new KeyedCodec("Markers", new MapCodec<BlockMapMarkerData, HashMap>(BlockMapMarkerData.CODEC, HashMap::new), true), (o, markersMap) -> {
        if (markersMap == null) {
            return;
        }
        for (Map.Entry entry : markersMap.entrySet()) {
            o.markers.put((long)Long.valueOf((String)entry.getKey()), (BlockMapMarkerData)entry.getValue());
        }
    }, o -> {
        HashMap<String, BlockMapMarkerData> returnMap = new HashMap<String, BlockMapMarkerData>(o.markers.size());
        for (Map.Entry entry : o.markers.entrySet()) {
            returnMap.put(String.valueOf(entry.getKey()), (BlockMapMarkerData)entry.getValue());
        }
        return returnMap;
    }).add()).build();
    private Long2ObjectMap<BlockMapMarkerData> markers = new Long2ObjectOpenHashMap<BlockMapMarkerData>();

    public BlockMapMarkersResource() {
    }

    public BlockMapMarkersResource(Long2ObjectMap<BlockMapMarkerData> markers) {
        this.markers = markers;
    }

    public static ResourceType<ChunkStore, BlockMapMarkersResource> getResourceType() {
        return BlockModule.get().getBlockMapMarkersResourceType();
    }

    @Nonnull
    public Long2ObjectMap<BlockMapMarkerData> getMarkers() {
        return this.markers;
    }

    public void addMarker(@Nonnull Vector3i position, @Nonnull String name, @Nonnull String icon) {
        long key = BlockUtil.pack(position);
        this.markers.put(key, new BlockMapMarkerData(position, name, icon, UUID.randomUUID().toString()));
    }

    public void removeMarker(@Nonnull Vector3i position) {
        long key = BlockUtil.pack(position);
        this.markers.remove(key);
    }

    @Override
    public Resource<ChunkStore> clone() {
        return new BlockMapMarkersResource(new Long2ObjectOpenHashMap<BlockMapMarkerData>(this.markers));
    }

    public static class BlockMapMarkerData {
        public static final BuilderCodec<BlockMapMarkerData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockMapMarkerData.class, BlockMapMarkerData::new).append(new KeyedCodec<Vector3i>("Position", Vector3i.CODEC), (o, v) -> {
            o.position = v;
        }, o -> o.position).add()).append(new KeyedCodec<String>("Name", Codec.STRING), (o, v) -> {
            o.name = v;
        }, o -> o.name).add()).append(new KeyedCodec<String>("Icon", Codec.STRING), (o, v) -> {
            o.icon = v;
        }, o -> o.icon).add()).append(new KeyedCodec<String>("MarkerId", Codec.STRING), (o, v) -> {
            o.markerId = v;
        }, o -> o.markerId).add()).build();
        private Vector3i position;
        private String name;
        private String icon;
        private String markerId;

        public BlockMapMarkerData() {
        }

        public BlockMapMarkerData(Vector3i position, String name, String icon, String markerId) {
            this.position = position;
            this.name = name;
            this.icon = icon;
            this.markerId = markerId;
        }

        public Vector3i getPosition() {
            return this.position;
        }

        public String getName() {
            return this.name;
        }

        public String getIcon() {
            return this.icon;
        }

        public String getMarkerId() {
            return this.markerId;
        }
    }
}

