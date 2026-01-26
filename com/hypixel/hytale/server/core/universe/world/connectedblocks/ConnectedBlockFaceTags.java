/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;

public class ConnectedBlockFaceTags {
    public static final BuilderCodec<ConnectedBlockFaceTags> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ConnectedBlockFaceTags.class, ConnectedBlockFaceTags::new).append(new KeyedCodec<T[]>("North", new ArrayCodec<String>(Codec.STRING, String[]::new), false), (o, tags) -> {
        HashSet<String> strings = new HashSet<String>(((String[])tags).length);
        strings.addAll(Arrays.asList(tags));
        o.blockFaceTags.put(Vector3i.NORTH, strings);
    }, o -> {
        if (o.blockFaceTags.containsKey(Vector3i.NORTH)) {
            return (String[])o.blockFaceTags.get(Vector3i.NORTH).toArray(String[]::new);
        }
        return new String[0];
    }).add()).append(new KeyedCodec<T[]>("East", new ArrayCodec<String>(Codec.STRING, String[]::new), false), (o, tags) -> {
        HashSet<String> strings = new HashSet<String>(((String[])tags).length);
        strings.addAll(Arrays.asList(tags));
        o.blockFaceTags.put(Vector3i.EAST, strings);
    }, o -> {
        if (o.blockFaceTags.containsKey(Vector3i.EAST)) {
            return (String[])o.blockFaceTags.get(Vector3i.EAST).toArray(String[]::new);
        }
        return new String[0];
    }).add()).append(new KeyedCodec<T[]>("South", new ArrayCodec<String>(Codec.STRING, String[]::new), false), (o, tags) -> {
        HashSet<String> strings = new HashSet<String>(((String[])tags).length);
        strings.addAll(Arrays.asList(tags));
        o.blockFaceTags.put(Vector3i.SOUTH, strings);
    }, o -> {
        if (o.blockFaceTags.containsKey(Vector3i.SOUTH)) {
            return (String[])o.blockFaceTags.get(Vector3i.SOUTH).toArray(String[]::new);
        }
        return new String[0];
    }).add()).append(new KeyedCodec<T[]>("West", new ArrayCodec<String>(Codec.STRING, String[]::new), false), (o, tags) -> {
        HashSet<String> strings = new HashSet<String>(((String[])tags).length);
        strings.addAll(Arrays.asList(tags));
        o.blockFaceTags.put(Vector3i.WEST, strings);
    }, o -> {
        if (o.blockFaceTags.containsKey(Vector3i.WEST)) {
            return (String[])o.blockFaceTags.get(Vector3i.WEST).toArray(String[]::new);
        }
        return new String[0];
    }).add()).append(new KeyedCodec<T[]>("Up", new ArrayCodec<String>(Codec.STRING, String[]::new), false), (o, tags) -> {
        HashSet<String> strings = new HashSet<String>(((String[])tags).length);
        strings.addAll(Arrays.asList(tags));
        o.blockFaceTags.put(Vector3i.UP, strings);
    }, o -> {
        if (o.blockFaceTags.containsKey(Vector3i.UP)) {
            return (String[])o.blockFaceTags.get(Vector3i.UP).toArray(String[]::new);
        }
        return new String[0];
    }).add()).append(new KeyedCodec<T[]>("Down", new ArrayCodec<String>(Codec.STRING, String[]::new), false), (o, tags) -> {
        HashSet<String> strings = new HashSet<String>(((String[])tags).length);
        strings.addAll(Arrays.asList(tags));
        o.blockFaceTags.put(Vector3i.DOWN, strings);
    }, o -> {
        if (o.blockFaceTags.containsKey(Vector3i.DOWN)) {
            return (String[])o.blockFaceTags.get(Vector3i.DOWN).toArray(String[]::new);
        }
        return new String[0];
    }).add()).build();
    public static final ConnectedBlockFaceTags EMPTY = new ConnectedBlockFaceTags();
    @Nonnull
    private final Map<Vector3i, HashSet<String>> blockFaceTags = new Object2ObjectOpenHashMap<Vector3i, HashSet<String>>();

    public boolean contains(Vector3i direction, String blockFaceTag) {
        return this.blockFaceTags.containsKey(direction) && this.blockFaceTags.get(direction).contains(blockFaceTag);
    }

    @Nonnull
    public Map<Vector3i, HashSet<String>> getBlockFaceTags() {
        return this.blockFaceTags;
    }

    public Set<String> getBlockFaceTags(Vector3i direction) {
        if (this.blockFaceTags.containsKey(direction)) {
            return this.blockFaceTags.get(direction);
        }
        return Collections.emptySet();
    }

    @Nonnull
    public Set<Vector3i> getDirections() {
        return this.blockFaceTags.keySet();
    }
}

