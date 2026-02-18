/*
 * Decompiled with CFR 0.152.
 */
package org.rocksdb;

import java.util.Objects;

public class KeyMayExist {
    public final KeyMayExistEnum exists;
    public final int valueLength;

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        KeyMayExist keyMayExist = (KeyMayExist)object;
        return this.valueLength == keyMayExist.valueLength && this.exists == keyMayExist.exists;
    }

    public int hashCode() {
        return Objects.hash(new Object[]{this.exists, this.valueLength});
    }

    public KeyMayExist(KeyMayExistEnum keyMayExistEnum, int n) {
        this.exists = keyMayExistEnum;
        this.valueLength = n;
    }

    public static enum KeyMayExistEnum {
        kNotExist,
        kExistsWithoutValue,
        kExistsWithValue;

    }
}

