/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum ChecksumType {
    kNoChecksum(0),
    kCRC32c(1),
    kxxHash(2),
    kxxHash64(3),
    kXXH3(4);

    private final byte value_;

    public byte getValue() {
        return this.value_;
    }

    private ChecksumType(byte by) {
        this.value_ = by;
    }
}

