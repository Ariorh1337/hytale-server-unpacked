/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import java.util.Objects;
import org.rocksdb.ColumnFamilyDescriptor;
import org.rocksdb.RocksDB;
import org.rocksdb.RocksDBException;
import org.rocksdb.RocksObject;

public class ColumnFamilyHandle
extends RocksObject {
    private final RocksDB rocksDB_;

    ColumnFamilyHandle(RocksDB rocksDB, long l) {
        super(l);
        assert (rocksDB != null);
        this.rocksDB_ = rocksDB;
    }

    ColumnFamilyHandle(long l) {
        super(l);
        this.rocksDB_ = null;
        this.disOwnNativeHandle();
    }

    public byte[] getName() throws RocksDBException {
        assert (this.isOwningHandle() || this.isDefaultColumnFamily());
        return ColumnFamilyHandle.getName(this.nativeHandle_);
    }

    public int getID() {
        assert (this.isOwningHandle() || this.isDefaultColumnFamily());
        return ColumnFamilyHandle.getID(this.nativeHandle_);
    }

    public ColumnFamilyDescriptor getDescriptor() throws RocksDBException {
        assert (this.isOwningHandle() || this.isDefaultColumnFamily());
        return ColumnFamilyHandle.getDescriptor(this.nativeHandle_);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ColumnFamilyHandle columnFamilyHandle = (ColumnFamilyHandle)object;
        try {
            return this.rocksDB_.nativeHandle_ == columnFamilyHandle.rocksDB_.nativeHandle_ && this.getID() == columnFamilyHandle.getID() && Arrays.equals(this.getName(), columnFamilyHandle.getName());
        }
        catch (RocksDBException rocksDBException) {
            throw new RuntimeException("Cannot compare column family handles", rocksDBException);
        }
    }

    public int hashCode() {
        try {
            int n = Objects.hash(this.getID(), this.rocksDB_.nativeHandle_);
            n = 31 * n + Arrays.hashCode(this.getName());
            return n;
        }
        catch (RocksDBException rocksDBException) {
            throw new RuntimeException("Cannot calculate hash code of column family handle", rocksDBException);
        }
    }

    protected boolean isDefaultColumnFamily() {
        return this.nativeHandle_ == this.rocksDB_.getDefaultColumnFamily().nativeHandle_;
    }

    @Override
    protected void disposeInternal() {
        if (this.rocksDB_.isOwningHandle()) {
            this.disposeInternal(this.nativeHandle_);
        }
    }

    private static native byte[] getName(long var0) throws RocksDBException;

    private static native int getID(long var0);

    private static native ColumnFamilyDescriptor getDescriptor(long var0) throws RocksDBException;

    @Override
    protected final void disposeInternal(long l) {
        ColumnFamilyHandle.disposeInternalJni(l);
    }

    private static native void disposeInternalJni(long var0);
}

