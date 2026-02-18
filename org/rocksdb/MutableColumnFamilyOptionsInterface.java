/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.AdvancedMutableColumnFamilyOptionsInterface;
import org.rocksdb.CompressionType;

public interface MutableColumnFamilyOptionsInterface<T extends MutableColumnFamilyOptionsInterface<T>>
extends AdvancedMutableColumnFamilyOptionsInterface<T> {
    public T setWriteBufferSize(long var1);

    public long writeBufferSize();

    public T setDisableAutoCompactions(boolean var1);

    public boolean disableAutoCompactions();

    public T setLevel0FileNumCompactionTrigger(int var1);

    public int level0FileNumCompactionTrigger();

    public T setMaxCompactionBytes(long var1);

    public long maxCompactionBytes();

    public T setMaxBytesForLevelBase(long var1);

    public long maxBytesForLevelBase();

    public T setCompressionType(CompressionType var1);

    public CompressionType compressionType();
}

