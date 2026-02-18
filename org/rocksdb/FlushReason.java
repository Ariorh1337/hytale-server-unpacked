/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum FlushReason {
    OTHERS(0),
    GET_LIVE_FILES(1),
    SHUTDOWN(2),
    EXTERNAL_FILE_INGESTION(3),
    MANUAL_COMPACTION(4),
    WRITE_BUFFER_MANAGER(5),
    WRITE_BUFFER_FULL(6),
    TEST(7),
    DELETE_FILES(8),
    AUTO_COMPACTION(9),
    MANUAL_FLUSH(10),
    ERROR_RECOVERY(11),
    ERROR_RECOVERY_RETRY_FLUSH(12),
    WAL_FULL(13),
    CATCH_UP_AFTER_ERROR_RECOVERY(14);

    private final byte value;

    private FlushReason(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static FlushReason fromValue(byte by) {
        for (FlushReason flushReason : FlushReason.values()) {
            if (flushReason.value != by) continue;
            return flushReason;
        }
        throw new IllegalArgumentException("Illegal value provided for FlushReason: " + by);
    }
}

