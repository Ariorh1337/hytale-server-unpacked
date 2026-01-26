/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.datastructures.voxelspace.VoxelSpace;
import com.hypixel.hytale.common.util.ExceptionUtil;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import javax.annotation.Nonnull;

public class VoxelSpaceUtil {
    public static <V> void parallelCopy(@Nonnull VoxelSpace<V> source, @Nonnull VoxelSpace<V> destination, int concurrency) {
        if (concurrency < 1) {
            throw new IllegalArgumentException("negative concurrency");
        }
        int minX = source.minX();
        int minY = source.minY();
        int minZ = source.minZ();
        int sizeX = source.sizeX();
        int sizeY = source.sizeY();
        int sizeZ = source.sizeZ();
        LinkedList<CompletionStage> tasks = new LinkedList<CompletionStage>();
        int bSize = source.sizeX() * source.sizeY() * source.sizeZ() / concurrency;
        int b = 0;
        while (b < concurrency) {
            int finalB = b++;
            tasks.add(CompletableFuture.runAsync(() -> {
                for (int i = finalB * bSize; i < (finalB + 1) * bSize; ++i) {
                    int x = i % sizeX + minX;
                    int y = i / sizeX % sizeY + minY;
                    int z = i / (sizeX * sizeY) % sizeZ + minZ;
                    if (!source.isInsideSpace(x, y, z) || !destination.isInsideSpace(x, y, z)) continue;
                    destination.set(source.getContent(x, y, z), x, y, z);
                }
            }).handle((r, e) -> {
                if (e == null) {
                    return r;
                }
                LoggerUtil.logException("a VoxelSpace async process", e, LoggerUtil.getLogger());
                return null;
            }));
        }
        try {
            while (!tasks.isEmpty()) {
                ((CompletableFuture)tasks.removeFirst()).get();
            }
        }
        catch (InterruptedException | ExecutionException e2) {
            Thread.currentThread().interrupt();
            Object msg = "Exception thrown by HytaleGenerator while attempting an asynchronous copy of a VoxelSpace:\n";
            msg = (String)msg + ExceptionUtil.toStringWithStack(e2);
            LoggerUtil.getLogger().severe((String)msg);
        }
    }

    private static class BatchTransfer<T>
    implements Runnable {
        private final VoxelSpace<T> source;
        private final VoxelSpace<T> destination;
        private final int minX;
        private final int minY;
        private final int minZ;
        private final int maxX;
        private final int maxY;
        private final int maxZ;

        private BatchTransfer(VoxelSpace<T> source, VoxelSpace<T> destination, int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
            this.source = source;
            this.destination = destination;
            this.minX = minX;
            this.minY = minY;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxY = maxY;
            this.maxZ = maxZ;
        }

        @Override
        public void run() {
            try {
                for (int x = this.minX; x < this.maxX; ++x) {
                    for (int y = this.minY; y < this.maxY; ++y) {
                        for (int z = this.minZ; z < this.maxZ; ++z) {
                            if (!this.destination.isInsideSpace(x, y, z)) continue;
                            this.destination.set(this.source.getContent(x, y, z), x, y, z);
                        }
                    }
                }
            }
            catch (Exception e) {
                Object msg = "Exception thrown by HytaleGenerator while attempting a BatchTransfer operation:\n";
                msg = (String)msg + ExceptionUtil.toStringWithStack(e);
                LoggerUtil.getLogger().severe((String)msg);
            }
        }
    }
}

