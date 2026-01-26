/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.math.BigDecimal;
import org.bson.BsonNumber;
import org.bson.BsonType;
import org.bson.types.Decimal128;

public class BsonDouble
extends BsonNumber
implements Comparable<BsonDouble> {
    private final double value;

    public BsonDouble(double value) {
        this.value = value;
    }

    @Override
    public int compareTo(BsonDouble o) {
        return Double.compare(this.value, o.value);
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.DOUBLE;
    }

    public double getValue() {
        return this.value;
    }

    @Override
    public int intValue() {
        return (int)this.value;
    }

    @Override
    public long longValue() {
        return (long)this.value;
    }

    @Override
    public Decimal128 decimal128Value() {
        if (Double.isNaN(this.value)) {
            return Decimal128.NaN;
        }
        if (Double.isInfinite(this.value)) {
            return this.value > 0.0 ? Decimal128.POSITIVE_INFINITY : Decimal128.NEGATIVE_INFINITY;
        }
        return new Decimal128(new BigDecimal(this.value));
    }

    @Override
    public double doubleValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonDouble that = (BsonDouble)o;
        return Double.compare(that.value, this.value) == 0;
    }

    public int hashCode() {
        long temp = Double.doubleToLongBits(this.value);
        return (int)(temp ^ temp >>> 32);
    }

    public String toString() {
        return "BsonDouble{value=" + this.value + '}';
    }
}

