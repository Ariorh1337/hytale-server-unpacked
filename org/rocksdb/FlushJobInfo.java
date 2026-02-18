/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.FlushReason;
import org.rocksdb.TableProperties;

public class FlushJobInfo {
    private final long columnFamilyId;
    private final String columnFamilyName;
    private final String filePath;
    private final long threadId;
    private final int jobId;
    private final boolean triggeredWritesSlowdown;
    private final boolean triggeredWritesStop;
    private final long smallestSeqno;
    private final long largestSeqno;
    private final TableProperties tableProperties;
    private final FlushReason flushReason;

    FlushJobInfo(long l, String string, String string2, long l2, int n, boolean bl, boolean bl2, long l3, long l4, TableProperties tableProperties, byte by) {
        this.columnFamilyId = l;
        this.columnFamilyName = string;
        this.filePath = string2;
        this.threadId = l2;
        this.jobId = n;
        this.triggeredWritesSlowdown = bl;
        this.triggeredWritesStop = bl2;
        this.smallestSeqno = l3;
        this.largestSeqno = l4;
        this.tableProperties = tableProperties;
        this.flushReason = FlushReason.fromValue(by);
    }

    public long getColumnFamilyId() {
        return this.columnFamilyId;
    }

    public String getColumnFamilyName() {
        return this.columnFamilyName;
    }

    public String getFilePath() {
        return this.filePath;
    }

    public long getThreadId() {
        return this.threadId;
    }

    public int getJobId() {
        return this.jobId;
    }

    public boolean isTriggeredWritesSlowdown() {
        return this.triggeredWritesSlowdown;
    }

    public boolean isTriggeredWritesStop() {
        return this.triggeredWritesStop;
    }

    public long getSmallestSeqno() {
        return this.smallestSeqno;
    }

    public long getLargestSeqno() {
        return this.largestSeqno;
    }

    public TableProperties getTableProperties() {
        return this.tableProperties;
    }

    public FlushReason getFlushReason() {
        return this.flushReason;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        FlushJobInfo flushJobInfo = (FlushJobInfo)object;
        return this.columnFamilyId == flushJobInfo.columnFamilyId && this.threadId == flushJobInfo.threadId && this.jobId == flushJobInfo.jobId && this.triggeredWritesSlowdown == flushJobInfo.triggeredWritesSlowdown && this.triggeredWritesStop == flushJobInfo.triggeredWritesStop && this.smallestSeqno == flushJobInfo.smallestSeqno && this.largestSeqno == flushJobInfo.largestSeqno && Objects.equals(this.columnFamilyName, flushJobInfo.columnFamilyName) && Objects.equals(this.filePath, flushJobInfo.filePath) && Objects.equals(this.tableProperties, flushJobInfo.tableProperties) && this.flushReason == flushJobInfo.flushReason;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.columnFamilyId, this.columnFamilyName, this.filePath, this.threadId, this.jobId, this.triggeredWritesSlowdown, this.triggeredWritesStop, this.smallestSeqno, this.largestSeqno, this.tableProperties, this.flushReason});
    }

    public String toString() {
        return "FlushJobInfo{columnFamilyId=" + this.columnFamilyId + ", columnFamilyName='" + this.columnFamilyName + '\'' + ", filePath='" + this.filePath + '\'' + ", threadId=" + this.threadId + ", jobId=" + this.jobId + ", triggeredWritesSlowdown=" + this.triggeredWritesSlowdown + ", triggeredWritesStop=" + this.triggeredWritesStop + ", smallestSeqno=" + this.smallestSeqno + ", largestSeqno=" + this.largestSeqno + ", tableProperties=" + this.tableProperties + ", flushReason=" + (Object)((Object)this.flushReason) + '}';
    }
}

