/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers;

import com.hypixel.hytale.builtin.hytalegenerator.ArrayUtil;
import com.hypixel.hytale.builtin.hytalegenerator.bounds.Bounds3i;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.bufferbundle.buffers.NPixelBuffer;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.performanceinstruments.MemInstrument;
import com.hypixel.hytale.math.vector.Vector3i;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class NSimplePixelBuffer<T>
extends NPixelBuffer<T> {
    private static final Bounds3i bounds = new Bounds3i(Vector3i.ZERO, SIZE);
    @Nonnull
    private final Class<T> pixelType;
    @Nonnull
    private State state;
    @Nullable
    private ArrayContents<T> arrayContents;
    @Nullable
    private T singleValue;

    public NSimplePixelBuffer(@Nonnull Class<T> pixelType) {
        this.pixelType = pixelType;
        this.state = State.EMPTY;
        this.arrayContents = null;
        this.singleValue = null;
    }

    @Override
    @Nullable
    public T getPixelContent(@Nonnull Vector3i position) {
        assert (bounds.contains(position));
        return switch (this.state.ordinal()) {
            case 1 -> this.singleValue;
            case 2 -> this.arrayContents.array[NSimplePixelBuffer.index(position)];
            default -> null;
        };
    }

    @Override
    public void setPixelContent(@Nonnull Vector3i position, @Nullable T value) {
        assert (bounds.contains(position));
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
                this.arrayContents.array[NSimplePixelBuffer.index((Vector3i)position)] = value;
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

    public void copyFrom(@Nonnull NSimplePixelBuffer<T> sourceBuffer) {
        this.state = sourceBuffer.state;
        switch (this.state.ordinal()) {
            case 1: {
                this.singleValue = sourceBuffer.singleValue;
                break;
            }
            case 2: {
                this.arrayContents = new ArrayContents();
                ArrayUtil.copy(sourceBuffer.arrayContents.array, this.arrayContents.array);
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
        if (this.arrayContents != null) {
            size_bytes += this.arrayContents.getMemoryUsage().size_bytes();
        }
        return new MemInstrument.Report(size_bytes);
    }

    private void ensureContents() {
        if (this.arrayContents != null) {
            return;
        }
        this.arrayContents = new ArrayContents();
    }

    private void switchFromSingleValueToArray() {
        assert (this.state == State.SINGLE_VALUE);
        this.state = State.ARRAY;
        this.arrayContents = new ArrayContents();
        Arrays.fill(this.arrayContents.array, this.singleValue);
        this.singleValue = null;
    }

    private static int index(@Nonnull Vector3i position) {
        return position.y + position.x * NSimplePixelBuffer.SIZE.y + position.z * NSimplePixelBuffer.SIZE.y * NSimplePixelBuffer.SIZE.x;
    }

    private static enum State {
        EMPTY,
        SINGLE_VALUE,
        ARRAY;

    }

    public static class ArrayContents<T>
    implements MemInstrument {
        private final T[] array;

        public ArrayContents() {
            this.array = new Object[NPixelBuffer.SIZE.x * NPixelBuffer.SIZE.y * NPixelBuffer.SIZE.z];
        }

        @Override
        @Nonnull
        public MemInstrument.Report getMemoryUsage() {
            long size_bytes = 16L + 8L * (long)this.array.length;
            return new MemInstrument.Report(size_bytes);
        }
    }
}

