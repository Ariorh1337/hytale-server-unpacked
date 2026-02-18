/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.Status;
import org.rocksdb.TableFileCreationBriefInfo;
import org.rocksdb.TableProperties;

public class TableFileCreationInfo
extends TableFileCreationBriefInfo {
    private final long fileSize;
    private final TableProperties tableProperties;
    private final Status status;

    protected TableFileCreationInfo(long l, TableProperties tableProperties, Status status, String string, String string2, String string3, int n, byte by) {
        super(string, string2, string3, n, by);
        this.fileSize = l;
        this.tableProperties = tableProperties;
        this.status = status;
    }

    public long getFileSize() {
        return this.fileSize;
    }

    public TableProperties getTableProperties() {
        return this.tableProperties;
    }

    public Status getStatus() {
        return this.status;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        TableFileCreationInfo tableFileCreationInfo = (TableFileCreationInfo)object;
        return this.fileSize == tableFileCreationInfo.fileSize && Objects.equals(this.tableProperties, tableFileCreationInfo.tableProperties) && Objects.equals(this.status, tableFileCreationInfo.status);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.fileSize, this.tableProperties, this.status);
    }

    @Override
    public String toString() {
        return "TableFileCreationInfo{fileSize=" + this.fileSize + ", tableProperties=" + this.tableProperties + ", status=" + this.status + '}';
    }
}

