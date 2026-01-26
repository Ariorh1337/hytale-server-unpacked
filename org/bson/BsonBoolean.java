/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;

public final class BsonBoolean
extends BsonValue
implements Comparable<BsonBoolean> {
    private final boolean value;
    public static final BsonBoolean TRUE = new BsonBoolean(true);
    public static final BsonBoolean FALSE = new BsonBoolean(false);

    public static BsonBoolean valueOf(boolean value) {
        return value ? TRUE : FALSE;
    }

    public BsonBoolean(boolean value) {
        this.value = value;
    }

    @Override
    public int compareTo(BsonBoolean o) {
        return Boolean.valueOf(this.value).compareTo(o.value);
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.BOOLEAN;
    }

    public boolean getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonBoolean that = (BsonBoolean)o;
        return this.value == that.value;
    }

    public int hashCode() {
        return this.value ? 1 : 0;
    }

    public String toString() {
        return "BsonBoolean{value=" + this.value + '}';
    }
}

