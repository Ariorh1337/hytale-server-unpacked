/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum IndexShorteningMode {
    kNoShortening(0),
    kShortenSeparators(1),
    kShortenSeparatorsAndSuccessor(2);

    private final byte value;

    private IndexShorteningMode(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }
}

