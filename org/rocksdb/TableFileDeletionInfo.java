/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.Status;

public class TableFileDeletionInfo {
    private final String dbName;
    private final String filePath;
    private final int jobId;
    private final Status status;

    TableFileDeletionInfo(String string, String string2, int n, Status status) {
        this.dbName = string;
        this.filePath = string2;
        this.jobId = n;
        this.status = status;
    }

    public String getDbName() {
        return this.dbName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public int getJobId() {
        return this.jobId;
    }

    public Status getStatus() {
        return this.status;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TableFileDeletionInfo tableFileDeletionInfo = (TableFileDeletionInfo)object;
        return this.jobId == tableFileDeletionInfo.jobId && Objects.equals(this.dbName, tableFileDeletionInfo.dbName) && Objects.equals(this.filePath, tableFileDeletionInfo.filePath) && Objects.equals(this.status, tableFileDeletionInfo.status);
    }

    public int hashCode() {
        return Objects.hash(this.dbName, this.filePath, this.jobId, this.status);
    }

    public String toString() {
        return "TableFileDeletionInfo{dbName='" + this.dbName + '\'' + ", filePath='" + this.filePath + '\'' + ", jobId=" + this.jobId + ", status=" + this.status + '}';
    }
}

