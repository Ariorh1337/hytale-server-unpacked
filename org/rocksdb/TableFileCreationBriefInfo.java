/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.TableFileCreationReason;

public class TableFileCreationBriefInfo {
    private final String dbName;
    private final String columnFamilyName;
    private final String filePath;
    private final int jobId;
    private final TableFileCreationReason reason;

    protected TableFileCreationBriefInfo(String string, String string2, String string3, int n, byte by) {
        this.dbName = string;
        this.columnFamilyName = string2;
        this.filePath = string3;
        this.jobId = n;
        this.reason = TableFileCreationReason.fromValue(by);
    }

    public String getDbName() {
        return this.dbName;
    }

    public String getColumnFamilyName() {
        return this.columnFamilyName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public int getJobId() {
        return this.jobId;
    }

    public TableFileCreationReason getReason() {
        return this.reason;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TableFileCreationBriefInfo tableFileCreationBriefInfo = (TableFileCreationBriefInfo)object;
        return this.jobId == tableFileCreationBriefInfo.jobId && Objects.equals(this.dbName, tableFileCreationBriefInfo.dbName) && Objects.equals(this.columnFamilyName, tableFileCreationBriefInfo.columnFamilyName) && Objects.equals(this.filePath, tableFileCreationBriefInfo.filePath) && this.reason == tableFileCreationBriefInfo.reason;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.dbName, this.columnFamilyName, this.filePath, this.jobId, this.reason});
    }

    public String toString() {
        return "TableFileCreationBriefInfo{dbName='" + this.dbName + '\'' + ", columnFamilyName='" + this.columnFamilyName + '\'' + ", filePath='" + this.filePath + '\'' + ", jobId=" + this.jobId + ", reason=" + (Object)((Object)this.reason) + '}';
    }
}

