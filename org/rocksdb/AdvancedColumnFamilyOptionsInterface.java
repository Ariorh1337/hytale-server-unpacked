/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.List;
import org.rocksdb.ColumnFamilyOptionsInterface;
import org.rocksdb.CompactionOptionsFIFO;
import org.rocksdb.CompactionOptionsUniversal;
import org.rocksdb.CompactionPriority;
import org.rocksdb.CompactionStyle;
import org.rocksdb.CompressionType;

public interface AdvancedColumnFamilyOptionsInterface<T extends AdvancedColumnFamilyOptionsInterface<T> & ColumnFamilyOptionsInterface<T>> {
    public T setMinWriteBufferNumberToMerge(int var1);

    public int minWriteBufferNumberToMerge();

    public T setInplaceUpdateSupport(boolean var1);

    public boolean inplaceUpdateSupport();

    public T setBloomLocality(int var1);

    public int bloomLocality();

    public T setCompressionPerLevel(List<CompressionType> var1);

    public List<CompressionType> compressionPerLevel();

    public T setNumLevels(int var1);

    public int numLevels();

    public T setLevelCompactionDynamicLevelBytes(boolean var1);

    public boolean levelCompactionDynamicLevelBytes();

    public T setMaxCompactionBytes(long var1);

    public long maxCompactionBytes();

    public ColumnFamilyOptionsInterface<T> setCompactionStyle(CompactionStyle var1);

    public CompactionStyle compactionStyle();

    public T setCompactionPriority(CompactionPriority var1);

    public CompactionPriority compactionPriority();

    public T setCompactionOptionsUniversal(CompactionOptionsUniversal var1);

    public CompactionOptionsUniversal compactionOptionsUniversal();

    public T setCompactionOptionsFIFO(CompactionOptionsFIFO var1);

    public CompactionOptionsFIFO compactionOptionsFIFO();

    public T setOptimizeFiltersForHits(boolean var1);

    public boolean optimizeFiltersForHits();

    public T setForceConsistencyChecks(boolean var1);

    public boolean forceConsistencyChecks();
}

