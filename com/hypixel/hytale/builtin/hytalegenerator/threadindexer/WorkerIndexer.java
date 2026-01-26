/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator.threadindexer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nonnull;

public class WorkerIndexer {
    private final int workerCount;
    @Nonnull
    private final List<Id> ids;

    public WorkerIndexer(int workerCount) {
        if (workerCount <= 0) {
            throw new IllegalArgumentException("workerCount must be > 0");
        }
        this.workerCount = workerCount;
        ArrayList<Id> tempIds = new ArrayList<Id>(workerCount);
        for (int i = 0; i < workerCount; ++i) {
            tempIds.add(new Id(i));
        }
        this.ids = Collections.unmodifiableList(tempIds);
    }

    public int getWorkerCount() {
        return this.workerCount;
    }

    @Nonnull
    public List<Id> getWorkedIds() {
        return this.ids;
    }

    @Nonnull
    public Session createSession() {
        return new Session();
    }

    public static class Id {
        public static final int UNKNOWN_THREAD_ID = -1;
        public static final Id UNKNOWN = new Id(-1);
        public final int id;

        private Id(int id) {
            this.id = id;
        }

        @Nonnull
        public String toString() {
            return String.valueOf(this.id);
        }
    }

    public class Session {
        private int index = 0;

        public Id next() {
            if (this.index >= WorkerIndexer.this.workerCount) {
                throw new IllegalStateException("worker count exceeded");
            }
            return WorkerIndexer.this.ids.get(this.index++);
        }

        public boolean hasNext() {
            return this.index < WorkerIndexer.this.workerCount;
        }
    }

    public static class Data<T> {
        private T[] data;
        private Supplier<T> initialize;

        public Data(int size, @Nonnull Supplier<T> initialize) {
            this.data = new Object[size];
            this.initialize = initialize;
        }

        public boolean isValid(@Nonnull Id id) {
            return id != null && id.id < this.data.length && id.id >= 0;
        }

        @Nonnull
        public T get(@Nonnull Id id) {
            if (!this.isValid(id)) {
                throw new IllegalArgumentException("Invalid thread id " + String.valueOf(id));
            }
            if (this.data[id.id] == null) {
                this.data[id.id] = this.initialize.get();
                assert (this.data[id.id] != null);
            }
            return this.data[id.id];
        }
    }
}

