/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.WalFileType;

public class LogFile {
    private final String pathName;
    private final long logNumber;
    private final WalFileType type;
    private final long startSequence;
    private final long sizeFileBytes;

    private LogFile(String string, long l, byte by, long l2, long l3) {
        this.pathName = string;
        this.logNumber = l;
        this.type = WalFileType.fromValue(by);
        this.startSequence = l2;
        this.sizeFileBytes = l3;
    }

    public String pathName() {
        return this.pathName;
    }

    public long logNumber() {
        return this.logNumber;
    }

    public WalFileType type() {
        return this.type;
    }

    public long startSequence() {
        return this.startSequence;
    }

    public long sizeFileBytes() {
        return this.sizeFileBytes;
    }
}

