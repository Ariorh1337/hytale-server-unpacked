/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AbstractSlice;
import org.rocksdb.RocksObject;

public abstract class AbstractCompactionFilter<T extends AbstractSlice<?>>
extends RocksObject {
    protected AbstractCompactionFilter(long l) {
        super(l);
    }

    @Override
    protected final native void disposeInternal(long var1);

    public static class Context {
        private final boolean fullCompaction;
        private final boolean manualCompaction;

        public Context(boolean bl, boolean bl2) {
            this.fullCompaction = bl;
            this.manualCompaction = bl2;
        }

        public boolean isFullCompaction() {
            return this.fullCompaction;
        }

        public boolean isManualCompaction() {
            return this.manualCompaction;
        }
    }
}

