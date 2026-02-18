/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public class SstFileMetaData {
    private final String fileName;
    private final String path;
    private final long size;
    private final long smallestSeqno;
    private final long largestSeqno;
    private final byte[] smallestKey;
    private final byte[] largestKey;
    private final long numReadsSampled;
    private final boolean beingCompacted;
    private final long numEntries;
    private final long numDeletions;
    private final byte[] fileChecksum;

    protected SstFileMetaData(String string, String string2, long l, long l2, long l3, byte[] byArray, byte[] byArray2, long l4, boolean bl, long l5, long l6, byte[] byArray3) {
        this.fileName = string;
        this.path = string2;
        this.size = l;
        this.smallestSeqno = l2;
        this.largestSeqno = l3;
        this.smallestKey = byArray;
        this.largestKey = byArray2;
        this.numReadsSampled = l4;
        this.beingCompacted = bl;
        this.numEntries = l5;
        this.numDeletions = l6;
        this.fileChecksum = byArray3;
    }

    public String fileName() {
        return this.fileName;
    }

    public String path() {
        return this.path;
    }

    public long size() {
        return this.size;
    }

    public long smallestSeqno() {
        return this.smallestSeqno;
    }

    public long largestSeqno() {
        return this.largestSeqno;
    }

    public byte[] smallestKey() {
        return this.smallestKey;
    }

    public byte[] largestKey() {
        return this.largestKey;
    }

    public long numReadsSampled() {
        return this.numReadsSampled;
    }

    public boolean beingCompacted() {
        return this.beingCompacted;
    }

    public long numEntries() {
        return this.numEntries;
    }

    public long numDeletions() {
        return this.numDeletions;
    }

    public byte[] fileChecksum() {
        return this.fileChecksum;
    }
}

