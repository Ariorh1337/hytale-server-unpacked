/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.path;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.builtin.path.path.IPrefabPath;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabPathCollection {
    private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();
    private final int worldgenId;
    private final Map<UUID, IPrefabPath> paths = new Object2ObjectOpenHashMap<UUID, IPrefabPath>();
    private final Int2ObjectMap<PathSet> pathsByFriendlyName = new Int2ObjectOpenHashMap<PathSet>();

    public PrefabPathCollection(int id) {
        this.worldgenId = id;
    }

    @Nullable
    public IPrefabPath getNearestPrefabPath(int nameIndex, @Nonnull Vector3d position, Set<UUID> disallowedPaths, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        PathSet set = this.pathsByFriendlyName.getOrDefault(nameIndex, (PathSet)null);
        if (set == null) {
            return null;
        }
        return set.getNearestPath(position, disallowedPaths, componentAccessor);
    }

    public IPrefabPath getPath(UUID id) {
        return this.paths.getOrDefault(id, null);
    }

    public IPrefabPath getOrConstructPath(@Nonnull UUID id, @Nonnull String name, @Nonnull Int2ObjectConcurrentHashMap.IntBiObjFunction<UUID, String, IPrefabPath> pathGenerator) {
        IPrefabPath path = this.paths.computeIfAbsent(id, k -> {
            LOGGER.at(Level.FINER).log("Adding path %s.%s", this.worldgenId, k);
            return (IPrefabPath)pathGenerator.apply(this.worldgenId, (UUID)k, name);
        });
        int nameIndex = AssetRegistry.getOrCreateTagIndex(name);
        PathSet set = this.pathsByFriendlyName.computeIfAbsent(nameIndex, s -> new PathSet());
        set.add(path);
        return path;
    }

    @Nullable
    public IPrefabPath getNearestPrefabPath(@Nonnull Vector3d position, @Nullable Set<UUID> disallowedPaths, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        IPrefabPath nearest = null;
        double minDist2 = Double.MAX_VALUE;
        for (IPrefabPath path : this.paths.values()) {
            double dist2;
            if (disallowedPaths != null && disallowedPaths.contains(path.getId()) || !((dist2 = position.distanceSquaredTo(path.getNearestWaypointPosition(position, componentAccessor))) < minDist2)) continue;
            nearest = path;
            minDist2 = dist2;
        }
        return nearest;
    }

    public void removePathWaypoint(UUID id, int index) {
        this.removePathWaypoint(id, index, false);
    }

    public void unloadPathWaypoint(UUID id, int index) {
        this.removePathWaypoint(id, index, true);
    }

    private void removePathWaypoint(UUID id, int index, boolean unload) {
        IPrefabPath path = this.getPath(id);
        LOGGER.at(Level.FINER).log("%s waypoint %s from path %s.%s", unload ? "Unloading" : "Removing", index, this.worldgenId, id);
        if (path == null) {
            LOGGER.at(Level.SEVERE).log("Path %s.%s not found", this.worldgenId, (Object)id);
            return;
        }
        if (unload) {
            path.unloadWaypoint(index);
        } else {
            path.removeWaypoint(index, this.worldgenId);
        }
        if (path.length() == 0 || !path.hasLoadedWaypoints()) {
            LOGGER.at(Level.FINER).log("%s path %s.%s", unload ? "Unloading" : "Removing", this.worldgenId, id);
            this.removePath(id);
        }
    }

    public void removePath(UUID id) {
        IPrefabPath removed = this.paths.remove(id);
        ((PathSet)this.pathsByFriendlyName.get(AssetRegistry.getTagIndex(removed.getName()))).remove(removed);
    }

    public boolean isEmpty() {
        return this.paths.isEmpty();
    }

    public void forEach(BiConsumer<UUID, IPrefabPath> consumer) {
        this.paths.forEach(consumer);
    }

    private static class PathSet {
        private final List<IPrefabPath> paths = new ObjectArrayList<IPrefabPath>();

        private PathSet() {
        }

        public void add(IPrefabPath path) {
            this.paths.add(path);
        }

        public void remove(IPrefabPath path) {
            this.paths.remove(path);
        }

        @Nullable
        public IPrefabPath getNearestPath(@Nonnull Vector3d position, @Nullable Set<UUID> disallowedPaths, ComponentAccessor<EntityStore> componentAccessor) {
            IPrefabPath nearest = null;
            double minDist2 = Double.MAX_VALUE;
            for (int i = 0; i < this.paths.size(); ++i) {
                Vector3d nearestWp;
                double dist2;
                IPrefabPath path = this.paths.get(i);
                if (disallowedPaths != null && disallowedPaths.contains(path.getId()) || !((dist2 = position.distanceSquaredTo(nearestWp = path.getNearestWaypointPosition(position, componentAccessor))) < minDist2)) continue;
                nearest = path;
                minDist2 = dist2;
            }
            return nearest;
        }
    }
}

