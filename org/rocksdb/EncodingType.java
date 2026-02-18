/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum EncodingType {
    kPlain(0),
    kPrefix(1);

    private final byte value_;

    public byte getValue() {
        return this.value_;
    }

    private EncodingType(byte by) {
        this.value_ = by;
    }
}

