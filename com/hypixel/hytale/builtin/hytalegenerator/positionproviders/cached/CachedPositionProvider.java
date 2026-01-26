/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.positionproviders.cached;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.cached.CacheThreadMemory;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class CachedPositionProvider
extends PositionProvider {
    @Nonnull
    private final PositionProvider positionProvider;
    private final int sectionSize;
    private WorkerIndexer.Data<CacheThreadMemory> threadData;

    public CachedPositionProvider(@Nonnull PositionProvider positionProvider, int sectionSize, int cacheSize, boolean useInternalThreadData, int threadCount) {
        if (sectionSize <= 0 || cacheSize < 0 || threadCount <= 0) {
            throw new IllegalArgumentException();
        }
        this.positionProvider = positionProvider;
        this.sectionSize = sectionSize;
        this.threadData = new WorkerIndexer.Data<CacheThreadMemory>(threadCount, () -> new CacheThreadMemory(cacheSize));
    }

    @Override
    public void positionsIn(@Nonnull PositionProvider.Context context) {
        this.get(context);
    }

    public void get(@Nonnull PositionProvider.Context context) {
        CacheThreadMemory cachedData = this.threadData.get(context.workerId);
        Vector3i minSection = this.sectionAddress(context.minInclusive);
        Vector3i maxSection = this.sectionAddress(context.maxExclusive);
        Vector3i sectionAddress = minSection.clone();
        sectionAddress.x = minSection.x;
        while (sectionAddress.x <= maxSection.x) {
            sectionAddress.z = minSection.z;
            while (sectionAddress.z <= maxSection.z) {
                sectionAddress.y = minSection.y;
                while (sectionAddress.y <= maxSection.y) {
                    long key = HashUtil.hash(sectionAddress.x, sectionAddress.y, sectionAddress.z);
                    Vector3d[] section = cachedData.sections.get(key);
                    if (section == null) {
                        Vector3d sectionMin = this.sectionMin(sectionAddress);
                        Vector3d sectionMax = sectionMin.clone().add(this.sectionSize, this.sectionSize, this.sectionSize);
                        ArrayList generatedPositions = new ArrayList();
                        PositionProvider.Context childContext = new PositionProvider.Context(sectionMin, sectionMax, generatedPositions::add, null, context.workerId);
                        this.positionProvider.positionsIn(childContext);
                        section = new Vector3d[generatedPositions.size()];
                        generatedPositions.toArray(section);
                        cachedData.sections.put(key, section);
                        cachedData.expirationList.addFirst(key);
                        if (cachedData.expirationList.size() > cachedData.size) {
                            long removedKey = cachedData.expirationList.removeLast();
                            cachedData.sections.remove(removedKey);
                        }
                    }
                    for (Vector3d position : section) {
                        if (!VectorUtil.isInside(position, context.minInclusive, context.maxExclusive)) continue;
                        context.consumer.accept(position.clone());
                    }
                    ++sectionAddress.y;
                }
                ++sectionAddress.z;
            }
            ++sectionAddress.x;
        }
    }

    @Nonnull
    private Vector3i sectionAddress(@Nonnull Vector3d pointer) {
        Vector3i address = pointer.toVector3i();
        address.x = this.sectionFloor(address.x) / this.sectionSize;
        address.y = this.sectionFloor(address.y) / this.sectionSize;
        address.z = this.sectionFloor(address.z) / this.sectionSize;
        return address;
    }

    @Nonnull
    private Vector3d sectionMin(@Nonnull Vector3i sectionAddress) {
        Vector3d min = sectionAddress.toVector3d();
        min.x *= (double)this.sectionSize;
        min.y *= (double)this.sectionSize;
        min.z *= (double)this.sectionSize;
        return min;
    }

    private int toSectionAddress(double position) {
        int positionAddress = (int)position;
        positionAddress = this.sectionFloor(positionAddress);
        return positionAddress /= this.sectionSize;
    }

    public int sectionFloor(int voxelAddress) {
        if (voxelAddress < 0) {
            return voxelAddress - voxelAddress % this.sectionSize - this.sectionSize;
        }
        return voxelAddress - voxelAddress % this.sectionSize;
    }
}

