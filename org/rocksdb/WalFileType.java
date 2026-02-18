/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum WalFileType {
    kArchivedLogFile(0),
    kAliveLogFile(1);

    private final byte value;

    private WalFileType(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static WalFileType fromValue(byte by) {
        for (WalFileType walFileType : WalFileType.values()) {
            if (walFileType.value != by) continue;
            return walFileType;
        }
        throw new IllegalArgumentException("Illegal value provided for WalFileType: " + by);
    }
}

