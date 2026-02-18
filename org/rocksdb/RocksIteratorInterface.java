/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.nio.ByteBuffer;
import org.rocksdb.RocksDBException;
import org.rocksdb.Snapshot;

public interface RocksIteratorInterface {
    public boolean isValid();

    public void seekToFirst();

    public void seekToLast();

    public void seek(byte[] var1);

    public void seekForPrev(byte[] var1);

    public void seek(ByteBuffer var1);

    public void seekForPrev(ByteBuffer var1);

    public void next();

    public void prev();

    public void status() throws RocksDBException;

    public void refresh() throws RocksDBException;

    public void refresh(Snapshot var1) throws RocksDBException;
}

