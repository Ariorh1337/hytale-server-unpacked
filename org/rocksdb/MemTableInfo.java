/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;

public class MemTableInfo {
    private final String columnFamilyName;
    private final long firstSeqno;
    private final long earliestSeqno;
    private final long numEntries;
    private final long numDeletes;

    MemTableInfo(String string, long l, long l2, long l3, long l4) {
        this.columnFamilyName = string;
        this.firstSeqno = l;
        this.earliestSeqno = l2;
        this.numEntries = l3;
        this.numDeletes = l4;
    }

    public String getColumnFamilyName() {
        return this.columnFamilyName;
    }

    public long getFirstSeqno() {
        return this.firstSeqno;
    }

    public long getEarliestSeqno() {
        return this.earliestSeqno;
    }

    public long getNumEntries() {
        return this.numEntries;
    }

    public long getNumDeletes() {
        return this.numDeletes;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        MemTableInfo memTableInfo = (MemTableInfo)object;
        return this.firstSeqno == memTableInfo.firstSeqno && this.earliestSeqno == memTableInfo.earliestSeqno && this.numEntries == memTableInfo.numEntries && this.numDeletes == memTableInfo.numDeletes && Objects.equals(this.columnFamilyName, memTableInfo.columnFamilyName);
    }

    public int hashCode() {
        return Objects.hash(this.columnFamilyName, this.firstSeqno, this.earliestSeqno, this.numEntries, this.numDeletes);
    }

    public String toString() {
        return "MemTableInfo{columnFamilyName='" + this.columnFamilyName + '\'' + ", firstSeqno=" + this.firstSeqno + ", earliestSeqno=" + this.earliestSeqno + ", numEntries=" + this.numEntries + ", numDeletes=" + this.numDeletes + '}';
    }
}

