/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum IndexType {
    kBinarySearch(0),
    kHashSearch(1),
    kTwoLevelIndexSearch(2),
    kBinarySearchWithFirstKey(3);

    private final byte value_;

    public byte getValue() {
        return this.value_;
    }

    private IndexType(byte by) {
        this.value_ = by;
    }
}

