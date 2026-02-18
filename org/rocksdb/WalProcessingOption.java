/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum WalProcessingOption {
    CONTINUE_PROCESSING(0),
    IGNORE_CURRENT_RECORD(1),
    STOP_REPLAY(2),
    CORRUPTED_RECORD(3);

    private final byte value;

    private WalProcessingOption(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    public static WalProcessingOption fromValue(byte by) {
        for (WalProcessingOption walProcessingOption : WalProcessingOption.values()) {
            if (walProcessingOption.value != by) continue;
            return walProcessingOption;
        }
        throw new IllegalArgumentException("Illegal value provided for WalProcessingOption: " + by);
    }
}

