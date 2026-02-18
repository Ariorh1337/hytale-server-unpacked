/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

enum ComparatorType {
    JAVA_COMPARATOR(0),
    JAVA_NATIVE_COMPARATOR_WRAPPER(1);

    private final byte value;

    private ComparatorType(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static ComparatorType getComparatorType(byte by) {
        for (ComparatorType comparatorType : ComparatorType.values()) {
            if (comparatorType.getValue() != by) continue;
            return comparatorType;
        }
        throw new IllegalArgumentException("Illegal value provided for ComparatorType.");
    }
}

