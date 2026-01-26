/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonNumber;
import org.bson.BsonType;
import org.bson.types.Decimal128;

public final class BsonInt64
extends BsonNumber
implements Comparable<BsonInt64> {
    private final long value;

    public BsonInt64(long value) {
        this.value = value;
    }

    @Override
    public int compareTo(BsonInt64 o) {
        return this.value < o.value ? -1 : (this.value == o.value ? 0 : 1);
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.INT64;
    }

    public long getValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return this.value;
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    @Override
    public Decimal128 decimal128Value() {
        return new Decimal128(this.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonInt64 bsonInt64 = (BsonInt64)o;
        return this.value == bsonInt64.value;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    public String toString() {
        return "BsonInt64{value=" + this.value + '}';
    }
}

