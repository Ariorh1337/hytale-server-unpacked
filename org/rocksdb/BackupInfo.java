/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public class BackupInfo {
    private final int backupId_;
    private final long timestamp_;
    private final long size_;
    private final int numberFiles_;
    private final String app_metadata_;

    BackupInfo(int n, long l, long l2, int n2, String string) {
        this.backupId_ = n;
        this.timestamp_ = l;
        this.size_ = l2;
        this.numberFiles_ = n2;
        this.app_metadata_ = string;
    }

    public int backupId() {
        return this.backupId_;
    }

    public long timestamp() {
        return this.timestamp_;
    }

    public long size() {
        return this.size_;
    }

    public int numberFiles() {
        return this.numberFiles_;
    }

    public String appMetadata() {
        return this.app_metadata_;
    }
}

