/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;
import org.rocksdb.WriteBatch;

public class TransactionLogIterator
extends RocksObject {
    public boolean isValid() {
        return TransactionLogIterator.isValid(this.nativeHandle_);
    }

    public void next() {
        TransactionLogIterator.next(this.nativeHandle_);
    }

    public void status() throws RocksDBException {
        TransactionLogIterator.status(this.nativeHandle_);
    }

    public BatchResult getBatch() {
        assert (this.isValid());
        return TransactionLogIterator.getBatch(this.nativeHandle_);
    }

    TransactionLogIterator(long l) {
        super(l);
    }

    @Override
    protected final void disposeInternal(long l) {
        TransactionLogIterator.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);

    private static native boolean isValid(long var0);

    private static native void next(long var0);

    private static native void status(long var0) throws RocksDBException;

    private static native BatchResult getBatch(long var0);

    public static final class BatchResult {
        private final long sequenceNumber_;
        private final WriteBatch writeBatch_;

        public BatchResult(long l, long l2) {
            this.sequenceNumber_ = l;
            this.writeBatch_ = new WriteBatch(l2, true);
        }

        public long sequenceNumber() {
            return this.sequenceNumber_;
        }

        public WriteBatch writeBatch() {
            return this.writeBatch_;
        }
    }
}

