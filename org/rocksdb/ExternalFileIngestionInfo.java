/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.TableProperties;

public class ExternalFileIngestionInfo {
    private final String columnFamilyName;
    private final String externalFilePath;
    private final String internalFilePath;
    private final long globalSeqno;
    private final TableProperties tableProperties;

    ExternalFileIngestionInfo(String string, String string2, String string3, long l, TableProperties tableProperties) {
        this.columnFamilyName = string;
        this.externalFilePath = string2;
        this.internalFilePath = string3;
        this.globalSeqno = l;
        this.tableProperties = tableProperties;
    }

    public String getColumnFamilyName() {
        return this.columnFamilyName;
    }

    public String getExternalFilePath() {
        return this.externalFilePath;
    }

    public String getInternalFilePath() {
        return this.internalFilePath;
    }

    public long getGlobalSeqno() {
        return this.globalSeqno;
    }

    public TableProperties getTableProperties() {
        return this.tableProperties;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ExternalFileIngestionInfo externalFileIngestionInfo = (ExternalFileIngestionInfo)object;
        return this.globalSeqno == externalFileIngestionInfo.globalSeqno && Objects.equals(this.columnFamilyName, externalFileIngestionInfo.columnFamilyName) && Objects.equals(this.externalFilePath, externalFileIngestionInfo.externalFilePath) && Objects.equals(this.internalFilePath, externalFileIngestionInfo.internalFilePath) && Objects.equals(this.tableProperties, externalFileIngestionInfo.tableProperties);
    }

    public int hashCode() {
        return Objects.hash(this.columnFamilyName, this.externalFilePath, this.internalFilePath, this.globalSeqno, this.tableProperties);
    }

    public String toString() {
        return "ExternalFileIngestionInfo{columnFamilyName='" + this.columnFamilyName + '\'' + ", externalFilePath='" + this.externalFilePath + '\'' + ", internalFilePath='" + this.internalFilePath + '\'' + ", globalSeqno=" + this.globalSeqno + ", tableProperties=" + this.tableProperties + '}';
    }
}

