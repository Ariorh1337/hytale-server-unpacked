/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers;

import com.hypixel.hytale.builtin.hytalegenerator.ArrayUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.performanceinstruments.MemInstrument;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NVoxelBuffer<T>
extends NBuffer {
    public static final int BUFFER_SIZE_BITS = 3;
    public static final Vector3i SIZE = new Vector3i(8, 8, 8);
    private static final Bounds3i bounds = new Bounds3i(Vector3i.ZERO, SIZE);
    @Nonnull
    private final Class<T> voxelType;
    @Nonnull
    private State state;
    @Nullable
    private ArrayContents<T> arrayContents;
    @Nullable
    private T singleValue;
    @Nullable
    private NVoxelBuffer<T> referenceBuffer;

    public NVoxelBuffer(@Nonnull Class<T> voxelType) {
        this.voxelType = voxelType;
        this.state = State.EMPTY;
        this.arrayContents = null;
        this.singleValue = null;
        this.referenceBuffer = null;
    }

    @Nullable
    public T getVoxelContent(@Nonnull Vector3i position) {
        assert (bounds.contains(position));
        return switch (this.state.ordinal()) {
            case 1 -> this.singleValue;
            case 2 -> this.arrayContents.array[NVoxelBuffer.index(position)];
            case 3 -> this.referenceBuffer.getVoxelContent(position);
            default -> null;
        };
    }

    @Nonnull
    public Class<T> getVoxelType() {
        return this.voxelType;
    }

    public void setVoxelContent(@Nonnull Vector3i position, @Nullable T value) {
        assert (bounds.contains(position));
        switch (this.state.ordinal()) {
            case 1: {
                if (this.singleValue == value) {
                    return;
                }
                this.switchFromSingleValueToArray();
                this.setVoxelContent(position, value);
                break;
            }
            case 2: {
                this.arrayContents.array[NVoxelBuffer.index((Vector3i)position)] = value;
                break;
            }
            case 3: {
                this.dereference();
                this.setVoxelContent(position, value);
                break;
            }
            default: {
                this.state = State.SINGLE_VALUE;
                this.singleValue = value;
            }
        }
    }

    public void reference(@Nonnull NVoxelBuffer<T> sourceBuffer) {
        this.state = State.REFERENCE;
        this.referenceBuffer = this.lastReference(sourceBuffer);
        this.singleValue = null;
        this.arrayContents = null;
    }

    @Nonnull
    private NVoxelBuffer<T> lastReference(@Nonnull NVoxelBuffer<T> sourceBuffer) {
        while (sourceBuffer.state == State.REFERENCE) {
            sourceBuffer = sourceBuffer.referenceBuffer;
        }
        return sourceBuffer;
    }

    @Override
    @Nonnull
    public MemInstrument.Report getMemoryUsage() {
        long size_bytes = 128L;
        size_bytes += 40L;
        if (this.state == State.ARRAY) {
            size_bytes += this.arrayContents.getMemoryUsage().size_bytes();
        }
        return new MemInstrument.Report(size_bytes);
    }

    private void switchFromSingleValueToArray() {
        assert (this.state == State.SINGLE_VALUE);
        this.state = State.ARRAY;
        this.arrayContents = new ArrayContents();
        Arrays.fill(this.arrayContents.array, this.singleValue);
        this.singleValue = null;
    }

    private void dereference() {
        assert (this.state == State.REFERENCE);
        this.state = this.referenceBuffer.state;
        switch (this.state.ordinal()) {
            case 1: {
                this.singleValue = this.referenceBuffer.singleValue;
                break;
            }
            case 2: {
                this.arrayContents = new ArrayContents();
                ArrayUtil.copy(this.referenceBuffer.arrayContents.array, this.arrayContents.array);
                break;
            }
            case 3: {
                this.referenceBuffer = this.referenceBuffer.referenceBuffer;
                break;
            }
            default: {
                return;
            }
        }
    }

    private static int index(@Nonnull Vector3i position) {
        return position.y + position.x * NVoxelBuffer.SIZE.y + position.z * NVoxelBuffer.SIZE.y * NVoxelBuffer.SIZE.x;
    }

    private static enum State {
        EMPTY,
        SINGLE_VALUE,
        ARRAY,
        REFERENCE;

    }

    public static class ArrayContents<T>
    implements MemInstrument {
        private final T[] array;

        public ArrayContents() {
            this.array = new Object[NVoxelBuffer.SIZE.x * NVoxelBuffer.SIZE.y * NVoxelBuffer.SIZE.z];
        }

        @Override
        @Nonnull
        public MemInstrument.Report getMemoryUsage() {
            long size_bytes = 16L + 8L * (long)this.array.length;
            return new MemInstrument.Report(size_bytes);
        }
    }
}

