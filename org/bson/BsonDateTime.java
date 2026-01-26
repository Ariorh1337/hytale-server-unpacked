/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;

public class BsonDateTime
extends BsonValue
implements Comparable<BsonDateTime> {
    private final long value;

    public BsonDateTime(long value) {
        this.value = value;
    }

    @Override
    public int compareTo(BsonDateTime o) {
        return Long.compare(this.value, o.value);
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.DATE_TIME;
    }

    public long getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonDateTime that = (BsonDateTime)o;
        return this.value == that.value;
    }

    public int hashCode() {
        return (int)(this.value ^ this.value >>> 32);
    }

    public String toString() {
        return "BsonDateTime{value=" + this.value + '}';
    }
}

