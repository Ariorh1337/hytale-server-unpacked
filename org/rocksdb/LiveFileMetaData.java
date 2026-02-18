/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.SstFileMetaData;

public class LiveFileMetaData
extends SstFileMetaData {
    private final byte[] columnFamilyName;
    private final int level;

    private LiveFileMetaData(byte[] byArray, int n, String string, String string2, long l, long l2, long l3, byte[] byArray2, byte[] byArray3, long l4, boolean bl, long l5, long l6, byte[] byArray4) {
        super(string, string2, l, l2, l3, byArray2, byArray3, l4, bl, l5, l6, byArray4);
        this.columnFamilyName = byArray;
        this.level = n;
    }

    public byte[] columnFamilyName() {
        return this.columnFamilyName;
    }

    public int level() {
        return this.level;
    }

    public long newLiveFileMetaDataHandle() {
        return this.newLiveFileMetaDataHandle(this.columnFamilyName(), this.columnFamilyName().length, this.level(), this.fileName(), this.path(), this.size(), this.smallestSeqno(), this.largestSeqno(), this.smallestKey(), this.smallestKey().length, this.largestKey(), this.largestKey().length, this.numReadsSampled(), this.beingCompacted(), this.numEntries(), this.numDeletions());
    }

    private native long newLiveFileMetaDataHandle(byte[] var1, int var2, int var3, String var4, String var5, long var6, long var8, long var10, byte[] var12, int var13, byte[] var14, int var15, long var16, boolean var18, long var19, long var21);
}

