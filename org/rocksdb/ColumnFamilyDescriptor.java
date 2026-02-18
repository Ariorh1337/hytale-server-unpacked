/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Arrays;
import org.rocksdb.ColumnFamilyOptions;

public class ColumnFamilyDescriptor {
    private final byte[] columnFamilyName_;
    private final ColumnFamilyOptions columnFamilyOptions_;

    public ColumnFamilyDescriptor(byte[] byArray) {
        this(byArray, new ColumnFamilyOptions());
    }

    public ColumnFamilyDescriptor(byte[] byArray, ColumnFamilyOptions columnFamilyOptions) {
        this.columnFamilyName_ = byArray;
        this.columnFamilyOptions_ = columnFamilyOptions;
    }

    public byte[] getName() {
        return this.columnFamilyName_;
    }

    public ColumnFamilyOptions getOptions() {
        return this.columnFamilyOptions_;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        ColumnFamilyDescriptor columnFamilyDescriptor = (ColumnFamilyDescriptor)object;
        return Arrays.equals(this.columnFamilyName_, columnFamilyDescriptor.columnFamilyName_) && this.columnFamilyOptions_.nativeHandle_ == columnFamilyDescriptor.columnFamilyOptions_.nativeHandle_;
    }

    public int hashCode() {
        int n = (int)(this.columnFamilyOptions_.nativeHandle_ ^ this.columnFamilyOptions_.nativeHandle_ >>> 32);
        n = 31 * n + Arrays.hashCode(this.columnFamilyName_);
        return n;
    }
}

