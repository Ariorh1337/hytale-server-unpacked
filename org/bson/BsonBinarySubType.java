/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

public enum BsonBinarySubType {
    BINARY(0),
    FUNCTION(1),
    OLD_BINARY(2),
    UUID_LEGACY(3),
    UUID_STANDARD(4),
    MD5(5),
    USER_DEFINED(-128);

    private final byte value;

    public static boolean isUuid(byte value) {
        return value == UUID_LEGACY.getValue() || value == UUID_STANDARD.getValue();
    }

    private BsonBinarySubType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return this.value;
    }
}

