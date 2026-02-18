/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum OperationStage {
    STAGE_UNKNOWN(0),
    STAGE_FLUSH_RUN(1),
    STAGE_FLUSH_WRITE_L0(2),
    STAGE_COMPACTION_PREPARE(3),
    STAGE_COMPACTION_RUN(4),
    STAGE_COMPACTION_PROCESS_KV(5),
    STAGE_COMPACTION_INSTALL(6),
    STAGE_COMPACTION_SYNC_FILE(7),
    STAGE_PICK_MEMTABLES_TO_FLUSH(8),
    STAGE_MEMTABLE_ROLLBACK(9),
    STAGE_MEMTABLE_INSTALL_FLUSH_RESULTS(10);

    private final byte value;

    private OperationStage(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static OperationStage fromValue(byte by) throws IllegalArgumentException {
        for (OperationStage operationStage : OperationStage.values()) {
            if (operationStage.value != by) continue;
            return operationStage;
        }
        throw new IllegalArgumentException("Unknown value for OperationStage: " + by);
    }
}

