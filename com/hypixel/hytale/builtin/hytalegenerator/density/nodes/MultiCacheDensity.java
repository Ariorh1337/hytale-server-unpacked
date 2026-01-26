/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MultiCacheDensity
extends Density {
    @Nonnull
    private final WorkerIndexer.Data<Cache> threadData;
    @Nonnull
    private Density input;

    public MultiCacheDensity(@Nonnull Density input, int threadCount, int capacity) {
        this.input = input;
        this.threadData = new WorkerIndexer.Data<Cache>(threadCount, () -> new Cache(capacity));
    }

    @Override
    public double process(@Nonnull Density.Context context) {
        Cache cache = this.threadData.get(context.workerId);
        Entry matchingEntry = cache.find(context.position);
        if (matchingEntry == null) {
            matchingEntry = cache.getNext();
            if (matchingEntry.position == null) {
                matchingEntry.position = new Vector3d();
            }
            matchingEntry.position.assign(context.position);
            matchingEntry.value = this.input.process(context);
        }
        return matchingEntry.value;
    }

    @Override
    public void setInputs(@Nonnull Density[] inputs) {
        assert (inputs.length != 0);
        assert (inputs[0] != null);
        this.input = inputs[0];
    }

    private static class Cache {
        Entry[] entries;
        int oldestIndex;

        Cache(int size) {
            this.entries = new Entry[size];
            for (int i = 0; i < size; ++i) {
                this.entries[i] = new Entry();
            }
            this.oldestIndex = 0;
        }

        Entry getNext() {
            Entry entry = this.entries[this.oldestIndex];
            ++this.oldestIndex;
            if (this.oldestIndex >= this.entries.length) {
                this.oldestIndex = 0;
            }
            return entry;
        }

        @Nullable
        Entry find(@Nonnull Vector3d position) {
            int startIndex = this.oldestIndex - 1;
            if (startIndex < 0) {
                startIndex += this.entries.length;
            }
            int index = startIndex;
            do {
                if (position.equals(this.entries[index].position)) {
                    return this.entries[index];
                }
                if (++index < this.entries.length) continue;
                index = 0;
            } while (index != startIndex);
            return null;
        }
    }

    private static class Entry {
        Vector3d position = null;
        double value = 0.0;

        Entry() {
        }
    }
}

