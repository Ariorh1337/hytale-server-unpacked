/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.managers;

import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.assets.spawns.config.NPCSpawn;
import com.hypixel.hytale.server.spawning.wrappers.SpawnWrapper;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.concurrent.locks.StampedLock;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class SpawnManager<T extends SpawnWrapper<U>, U extends NPCSpawn> {
    private final Int2ObjectMap<T> spawnWrapperCache = new Int2ObjectOpenHashMap<T>();
    private final Object2IntMap<String> wrapperNameMap = new Object2IntOpenHashMap<String>();
    private final StampedLock wrapperLock = new StampedLock();

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public T getSpawnWrapper(int spawnConfigIndex) {
        long stamp = this.wrapperLock.readLock();
        try {
            SpawnWrapper spawnWrapper = (SpawnWrapper)this.spawnWrapperCache.get(spawnConfigIndex);
            return (T)spawnWrapper;
        }
        finally {
            this.wrapperLock.unlockRead(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    public T removeSpawnWrapper(int spawnConfigurationIndex) {
        long stamp = this.wrapperLock.writeLock();
        try {
            SpawnWrapper spawnWrapper = (SpawnWrapper)this.spawnWrapperCache.remove(spawnConfigurationIndex);
            if (spawnWrapper == null) {
                T t = null;
                return t;
            }
            this.wrapperNameMap.removeInt(((NPCSpawn)spawnWrapper.getSpawn()).getId());
            SpawnWrapper spawnWrapper2 = spawnWrapper;
            return (T)spawnWrapper2;
        }
        finally {
            this.wrapperLock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean addSpawnWrapper(@Nonnull T spawnWrapper) {
        Object spawn = ((SpawnWrapper)spawnWrapper).getSpawn();
        int spawnConfigIndex = ((SpawnWrapper)spawnWrapper).getSpawnIndex();
        long stamp = this.wrapperLock.writeLock();
        try {
            this.spawnWrapperCache.put(spawnConfigIndex, spawnWrapper);
            this.wrapperNameMap.put(((NPCSpawn)spawn).getId(), spawnConfigIndex);
        }
        finally {
            this.wrapperLock.unlockWrite(stamp);
        }
        SpawningPlugin.get().getLogger().at(Level.FINE).log("Set up NPCSpawn %s", ((NPCSpawn)spawn).getId());
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onNPCLoaded(String name, @Nonnull IntSet changeSet) {
        long stamp = this.wrapperLock.writeLock();
        try {
            for (Int2ObjectMap.Entry entry : this.spawnWrapperCache.int2ObjectEntrySet()) {
                SpawnWrapper wrapper = (SpawnWrapper)entry.getValue();
                if (!wrapper.hasInvalidNPC(name)) continue;
                changeSet.add(wrapper.getSpawnIndex());
            }
        }
        finally {
            this.wrapperLock.unlockWrite(stamp);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void onNPCSpawnRemoved(String key) {
        int index;
        long stamp = this.wrapperLock.readLock();
        try {
            index = this.wrapperNameMap.getInt(key);
        }
        finally {
            this.wrapperLock.unlockRead(stamp);
        }
        this.untrackNPCs(index);
        this.removeSpawnWrapper(index);
    }

    protected void untrackNPCs(int index) {
    }
}

