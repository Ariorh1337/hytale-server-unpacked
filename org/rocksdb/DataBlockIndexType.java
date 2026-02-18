/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum DataBlockIndexType {
    kDataBlockBinarySearch(0),
    kDataBlockBinaryAndHash(1);

    private final byte value;

    private DataBlockIndexType(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }
}

