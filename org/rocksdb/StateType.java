/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public enum StateType {
    STATE_UNKNOWN(0),
    STATE_MUTEX_WAIT(1);

    private final byte value;

    private StateType(byte by) {
        this.value = by;
    }

    byte getValue() {
        return this.value;
    }

    static StateType fromValue(byte by) throws IllegalArgumentException {
        for (StateType stateType : StateType.values()) {
            if (stateType.value != by) continue;
            return stateType;
        }
        throw new IllegalArgumentException("Unknown value for StateType: " + by);
    }
}

