/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.Status;

public class FileOperationInfo {
    private final String path;
    private final long offset;
    private final long length;
    private final long startTimestamp;
    private final long duration;
    private final Status status;

    FileOperationInfo(String string, long l, long l2, long l3, long l4, Status status) {
        this.path = string;
        this.offset = l;
        this.length = l2;
        this.startTimestamp = l3;
        this.duration = l4;
        this.status = status;
    }

    public String getPath() {
        return this.path;
    }

    public long getOffset() {
        return this.offset;
    }

    public long getLength() {
        return this.length;
    }

    public long getStartTimestamp() {
        return this.startTimestamp;
    }

    public long getDuration() {
        return this.duration;
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
        FileOperationInfo fileOperationInfo = (FileOperationInfo)object;
        return this.offset == fileOperationInfo.offset && this.length == fileOperationInfo.length && this.startTimestamp == fileOperationInfo.startTimestamp && this.duration == fileOperationInfo.duration && Objects.equals(this.path, fileOperationInfo.path) && Objects.equals(this.status, fileOperationInfo.status);
    }

    public int hashCode() {
        return Objects.hash(this.path, this.offset, this.length, this.startTimestamp, this.duration, this.status);
    }

    public String toString() {
        return "FileOperationInfo{path='" + this.path + '\'' + ", offset=" + this.offset + ", length=" + this.length + ", startTimestamp=" + this.startTimestamp + ", duration=" + this.duration + ", status=" + this.status + '}';
    }
}

