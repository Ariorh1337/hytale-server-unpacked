/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;
import org.rocksdb.WriteStallCondition;

public class WriteStallInfo {
    private final String columnFamilyName;
    private final WriteStallCondition currentCondition;
    private final WriteStallCondition previousCondition;

    WriteStallInfo(String string, byte by, byte by2) {
        this.columnFamilyName = string;
        this.currentCondition = WriteStallCondition.fromValue(by);
        this.previousCondition = WriteStallCondition.fromValue(by2);
    }

    public String getColumnFamilyName() {
        return this.columnFamilyName;
    }

    public WriteStallCondition getCurrentCondition() {
        return this.currentCondition;
    }

    public WriteStallCondition getPreviousCondition() {
        return this.previousCondition;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        WriteStallInfo writeStallInfo = (WriteStallInfo)object;
        return Objects.equals(this.columnFamilyName, writeStallInfo.columnFamilyName) && this.currentCondition == writeStallInfo.currentCondition && this.previousCondition == writeStallInfo.previousCondition;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.columnFamilyName, this.currentCondition, this.previousCondition});
    }

    public String toString() {
        return "WriteStallInfo{columnFamilyName='" + this.columnFamilyName + '\'' + ", currentCondition=" + (Object)((Object)this.currentCondition) + ", previousCondition=" + (Object)((Object)this.previousCondition) + '}';
    }
}

