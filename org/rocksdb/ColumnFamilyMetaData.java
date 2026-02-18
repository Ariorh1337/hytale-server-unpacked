/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import java.util.List;
import org.rocksdb.LevelMetaData;

public class ColumnFamilyMetaData {
    private final long size;
    private final long fileCount;
    private final byte[] name;
    private final LevelMetaData[] levels;

    private ColumnFamilyMetaData(long l, long l2, byte[] byArray, LevelMetaData[] levelMetaDataArray) {
        this.size = l;
        this.fileCount = l2;
        this.name = byArray;
        this.levels = levelMetaDataArray;
    }

    public long size() {
        return this.size;
    }

    public long fileCount() {
        return this.fileCount;
    }

    public byte[] name() {
        return this.name;
    }

    public List<LevelMetaData> levels() {
        return Arrays.asList(this.levels);
    }
}

