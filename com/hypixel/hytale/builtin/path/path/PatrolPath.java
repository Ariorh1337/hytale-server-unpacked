/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.path.path;

import com.hypixel.fastutil.ints.Int2ObjectConcurrentHashMap;
import com.hypixel.hytale.builtin.path.PathPlugin;
import com.hypixel.hytale.builtin.path.entities.PatrolPathMarkerEntity;
import com.hypixel.hytale.builtin.path.path.IPrefabPath;
import com.hypixel.hytale.builtin.path.waypoint.IPrefabPathWaypoint;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class PatrolPath
implements IPrefabPath {
    private final UUID id;
    private final String name;
    private final int worldgenId;
    private final Int2ObjectConcurrentHashMap<IPrefabPathWaypoint> waypoints = new Int2ObjectConcurrentHashMap();
    private final AtomicInteger length = new AtomicInteger(0);
    private final AtomicInteger loadedCount = new AtomicInteger(0);
    private final AtomicBoolean pathChanged = new AtomicBoolean(false);
    private final ReentrantReadWriteLock listLock = new ReentrantReadWriteLock();
    private List<IPrefabPathWaypoint> waypointList;

    public PatrolPath(int worldgenId, UUID id, String name) {
        this.id = id;
        this.worldgenId = worldgenId;
        this.name = name;
    }

    @Override
    public UUID getId() {
        return this.id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    @Nonnull
    public List<IPrefabPathWaypoint> getPathWaypoints() {
        if (this.pathChanged.get()) {
            this.listLock.writeLock().lock();
            try {
                this.waypointList = new ObjectArrayList<IPrefabPathWaypoint>();
                int size = this.length.get();
                for (int i = 0; i < size; ++i) {
                    this.waypointList.add(this.waypoints.get(i));
                }
                this.pathChanged.set(false);
            }
            finally {
                this.listLock.writeLock().unlock();
            }
        }
        this.listLock.readLock().lock();
        try {
            List<IPrefabPathWaypoint> list = Collections.unmodifiableList(this.waypointList);
            return list;
        }
        finally {
            this.listLock.readLock().unlock();
        }
    }

    @Override
    public short registerNewWaypoint(@Nonnull IPrefabPathWaypoint waypoint, int worldGenId) {
        int index = this.length.getAndIncrement();
        PathPlugin.get().getLogger().at(Level.FINER).log("Adding waypoint %s to path %s.%s", (short)index, worldGenId, this.name);
        for (int i = 0; i < index; ++i) {
            PatrolPathMarkerEntity wp = (PatrolPathMarkerEntity)this.waypoints.get(i);
            wp.markNeedsSave();
        }
        this.pathChanged.set(true);
        return (short)index;
    }

    @Override
    public void registerNewWaypointAt(int index, @Nonnull IPrefabPathWaypoint waypoint, int worldGenId) {
        PatrolPathMarkerEntity wp;
        int i;
        for (i = 0; i < index; ++i) {
            wp = (PatrolPathMarkerEntity)this.waypoints.get(i);
            wp.markNeedsSave();
        }
        for (i = this.waypoints.size() - 1; i >= index; --i) {
            wp = (PatrolPathMarkerEntity)this.waypoints.remove(i);
            wp.setOrder((short)(i + 1));
            this.waypoints.put(i + 1, wp);
        }
        this.length.getAndIncrement();
        this.pathChanged.set(true);
    }

    @Override
    public void addLoadedWaypoint(@Nonnull IPrefabPathWaypoint waypoint, int pathLength, int index, int worldGenId) {
        PathPlugin.get().getLogger().at(Level.FINER).log("Loading waypoint %s to path %s.%s", index, worldGenId, this.name);
        IPrefabPathWaypoint old = this.waypoints.put(index, waypoint);
        if (old != null) {
            old.onReplaced();
            PathPlugin.get().getLogger().at(Level.WARNING).log("Waypoint %s replaced in path %s.%s", index, worldGenId, this.name);
        } else {
            this.loadedCount.getAndIncrement();
        }
        this.length.set(pathLength);
        this.pathChanged.set(true);
    }

    @Override
    public void removeWaypoint(int index, int worldGenId) {
        PatrolPathMarkerEntity wp;
        int i;
        this.waypoints.remove(index);
        this.length.getAndDecrement();
        this.loadedCount.getAndDecrement();
        for (i = 0; i < index; ++i) {
            wp = (PatrolPathMarkerEntity)this.waypoints.get(i);
            wp.markNeedsSave();
        }
        for (i = index; i < this.waypoints.size(); ++i) {
            wp = (PatrolPathMarkerEntity)this.waypoints.remove(i + 1);
            wp.setOrder(i);
            this.waypoints.put(i, wp);
        }
        this.pathChanged.set(true);
    }

    @Override
    public void unloadWaypoint(int index) {
        this.waypoints.remove(index);
        this.loadedCount.getAndDecrement();
    }

    @Override
    public boolean hasLoadedWaypoints() {
        return this.loadedCount.get() > 0;
    }

    @Override
    public boolean isFullyLoaded() {
        return this.loadedCount.get() == this.length.get();
    }

    @Override
    public int loadedWaypointCount() {
        return this.loadedCount.get();
    }

    @Override
    public int getWorldGenId() {
        return this.worldgenId;
    }

    @Override
    public Vector3d getNearestWaypointPosition(@Nonnull Vector3d origin, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        Vector3d nearest = Vector3d.MAX;
        double minDist2 = Double.MAX_VALUE;
        for (int i = 0; i < this.length.get(); ++i) {
            double dist2;
            IPrefabPathWaypoint wp = this.waypoints.get(i);
            if (wp == null || !((dist2 = origin.distanceSquaredTo(wp.getWaypointPosition(componentAccessor))) < minDist2)) continue;
            nearest = wp.getWaypointPosition(componentAccessor);
            minDist2 = dist2;
        }
        return nearest;
    }

    @Override
    public void mergeInto(@Nonnull IPrefabPath target, int worldGenId, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        for (int i = 0; i < this.length.get(); ++i) {
            IPrefabPathWaypoint waypoint = this.waypoints.get(i);
            waypoint.initialise(target.getId(), target.getName(), -1, waypoint.getPauseTime(), waypoint.getObservationAngle(), worldGenId, componentAccessor);
            target.addLoadedWaypoint(waypoint, target.length(), waypoint.getOrder(), worldGenId);
        }
        this.waypoints.clear();
        this.loadedCount.set(0);
        this.length.set(0);
        this.pathChanged.set(true);
    }

    @Override
    public void compact(int worldGenId) {
        short length = 0;
        for (int i = 0; i < this.length.get(); ++i) {
            PatrolPathMarkerEntity wp = (PatrolPathMarkerEntity)this.waypoints.remove(i);
            if (wp == null) continue;
            wp.setOrder(length);
            short s = length;
            length = (short)(length + 1);
            this.waypoints.put(s, wp);
        }
        PathPlugin.get().getLogger().at(Level.WARNING).log("Compacted path %s.%s from length %s to %s", worldGenId, this.name, this.length.get(), length);
        this.loadedCount.set(length);
        this.length.set(length);
        this.pathChanged.set(true);
    }

    @Override
    public int length() {
        return this.length.get();
    }

    @Override
    public IPrefabPathWaypoint get(int index) {
        if (index < 0 || index >= this.length.get()) {
            throw new IndexOutOfBoundsException();
        }
        return this.waypoints.get(index);
    }
}

