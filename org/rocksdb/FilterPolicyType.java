/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import org.rocksdb.BloomFilter;
import org.rocksdb.Filter;

public enum FilterPolicyType {
    kUnknownFilterPolicy(0),
    kBloomFilterPolicy(1),
    kRibbonFilterPolicy(2);

    private final byte value_;

    public Filter createFilter(long l, double d) {
        if (this == kBloomFilterPolicy) {
            return new BloomFilter(l, d);
        }
        return null;
    }

    public byte getValue() {
        return this.value_;
    }

    private FilterPolicyType(byte by) {
        this.value_ = by;
    }
}

