/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import java.util.List;
import org.rocksdb.SstFileMetaData;

public class LevelMetaData {
    private final int level;
    private final long size;
    private final SstFileMetaData[] files;

    private LevelMetaData(int n, long l, SstFileMetaData[] sstFileMetaDataArray) {
        this.level = n;
        this.size = l;
        this.files = sstFileMetaDataArray;
    }

    public int level() {
        return this.level;
    }

    public long size() {
        return this.size;
    }

    public List<SstFileMetaData> files() {
        return Arrays.asList(this.files);
    }
}

