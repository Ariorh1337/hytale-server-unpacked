/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers;

import com.hypixel.hytale.builtin.hytalegenerator.ArrayUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.performanceinstruments.MemInstrument;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NCountedPixelBuffer<T>
extends NPixelBuffer<T> {
    public static final int BUFFER_SIZE_BITS = 3;
    public static final Vector3i SIZE_VOXEL_GRID = new Vector3i(8, 1, 8);
    public static final Bounds3i BOUNDS_VOXEL_GRID = new Bounds3i(Vector3i.ZERO, SIZE_VOXEL_GRID);
    @Nonnull
    private final Class<T> pixelType;
    @Nonnull
    private State state;
    @Nullable
    private CountedArrayContents<T> countedArrayContents;
    @Nullable
    private T singleValue;

    public NCountedPixelBuffer(@Nonnull Class<T> voxelType) {
        this.pixelType = voxelType;
        this.state = State.EMPTY;
        this.countedArrayContents = null;
        this.singleValue = null;
    }

    @Override
    @Nullable
    public T getPixelContent(@Nonnull Vector3i position) {
        assert (BOUNDS_VOXEL_GRID.contains(position));
        return switch (this.state.ordinal()) {
            case 1 -> this.singleValue;
            case 2 -> this.countedArrayContents.array[NCountedPixelBuffer.index(position)];
            default -> null;
        };
    }

    @Override
    public void setPixelContent(@Nonnull Vector3i position, @Nullable T value) {
        assert (BOUNDS_VOXEL_GRID.contains(position));
        switch (this.state.ordinal()) {
            case 1: {
                if (this.singleValue == value) {
                    return;
                }
                this.switchFromSingleValueToArray();
                this.setPixelContent(position, value);
                break;
            }
            case 2: {
                this.countedArrayContents.array[NCountedPixelBuffer.index((Vector3i)position)] = value;
                if (this.countedArrayContents.allBiomes.contains(value)) break;
                this.countedArrayContents.allBiomes.add(value);
                break;
            }
            default: {
                this.state = State.SINGLE_VALUE;
                this.singleValue = value;
            }
        }
    }

    @Override
    @Nonnull
    public Class<T> getPixelType() {
        return this.pixelType;
    }

    @Nonnull
    public List<T> getUniqueEntries() {
        switch (this.state.ordinal()) {
            case 1: {
                return List.of(this.singleValue);
            }
            case 2: {
                assert (this.countedArrayContents != null);
                return this.countedArrayContents.allBiomes;
            }
        }
        return List.of();
    }

    public void copyFrom(@Nonnull NCountedPixelBuffer<T> sourceBuffer) {
        this.state = sourceBuffer.state;
        switch (this.state.ordinal()) {
            case 1: {
                this.singleValue = sourceBuffer.singleValue;
                break;
            }
            case 2: {
                this.countedArrayContents = new CountedArrayContents();
                this.countedArrayContents.copyFrom(sourceBuffer.countedArrayContents);
                break;
            }
            default: {
                return;
            }
        }
    }

    @Override
    @Nonnull
    public MemInstrument.Report getMemoryUsage() {
        long size_bytes = 128L;
        if (this.countedArrayContents != null) {
            size_bytes += this.countedArrayContents.getMemoryUsage().size_bytes();
        }
        return new MemInstrument.Report(size_bytes);
    }

    private void switchFromSingleValueToArray() {
        assert (this.state == State.SINGLE_VALUE);
        this.state = State.ARRAY;
        this.countedArrayContents = new CountedArrayContents();
        Arrays.fill(this.countedArrayContents.array, this.singleValue);
        this.countedArrayContents.allBiomes.add(this.singleValue);
        this.singleValue = null;
    }

    private static int index(@Nonnull Vector3i position) {
        return position.y + position.x * NCountedPixelBuffer.SIZE_VOXEL_GRID.y + position.z * NCountedPixelBuffer.SIZE_VOXEL_GRID.y * NCountedPixelBuffer.SIZE_VOXEL_GRID.x;
    }

    private static enum State {
        EMPTY,
        SINGLE_VALUE,
        ARRAY;

    }

    public static class CountedArrayContents<T>
    implements MemInstrument {
        private final T[] array;
        private final List<T> allBiomes;

        public CountedArrayContents() {
            this.array = new Object[NCountedPixelBuffer.SIZE_VOXEL_GRID.x * NCountedPixelBuffer.SIZE_VOXEL_GRID.y * NCountedPixelBuffer.SIZE_VOXEL_GRID.z];
            this.allBiomes = new ArrayList<T>(1);
        }

        public void copyFrom(@Nonnull CountedArrayContents<T> countedArrayContents) {
            ArrayUtil.copy(countedArrayContents.array, this.array);
            this.allBiomes.clear();
            this.allBiomes.addAll(countedArrayContents.allBiomes);
        }

        @Override
        @Nonnull
        public MemInstrument.Report getMemoryUsage() {
            long size_bytes = 16L + 8L * (long)this.array.length;
            size_bytes += 32L;
            return new MemInstrument.Report(size_bytes += 8L * (long)this.allBiomes.size());
        }
    }
}

