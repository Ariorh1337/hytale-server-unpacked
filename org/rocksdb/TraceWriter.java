/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.RocksDBException;
import org.rocksdb.Slice;

public interface TraceWriter {
    public void write(Slice var1) throws RocksDBException;

    public void closeWriter() throws RocksDBException;

    public long getFileSize();
}

