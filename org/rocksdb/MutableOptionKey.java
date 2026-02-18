/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

public interface MutableOptionKey {
    public String name();

    public ValueType getValueType();

    public static enum ValueType {
        DOUBLE,
        LONG,
        INT,
        BOOLEAN,
        INT_ARRAY,
        ENUM,
        STRING;

    }
}

