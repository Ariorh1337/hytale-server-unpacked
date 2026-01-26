/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonNumber;
import org.bson.BsonType;
import org.bson.assertions.Assertions;
import org.bson.types.Decimal128;

public final class BsonDecimal128
extends BsonNumber {
    private final Decimal128 value;

    public BsonDecimal128(Decimal128 value) {
        Assertions.notNull("value", value);
        this.value = value;
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.DECIMAL128;
    }

    public Decimal128 getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonDecimal128 that = (BsonDecimal128)o;
        return this.value.equals(that.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return "BsonDecimal128{value=" + this.value + '}';
    }

    @Override
    public int intValue() {
        return this.value.bigDecimalValue().intValue();
    }

    @Override
    public long longValue() {
        return this.value.bigDecimalValue().longValue();
    }

    @Override
    public double doubleValue() {
        return this.value.bigDecimalValue().doubleValue();
    }

    @Override
    public Decimal128 decimal128Value() {
        return this.value;
    }
}

