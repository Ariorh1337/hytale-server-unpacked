/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;

public class BsonString
extends BsonValue
implements Comparable<BsonString> {
    private final String value;

    public BsonString(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can not be null");
        }
        this.value = value;
    }

    @Override
    public int compareTo(BsonString o) {
        return this.value.compareTo(o.value);
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.STRING;
    }

    public String getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonString that = (BsonString)o;
        return this.value.equals(that.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return "BsonString{value='" + this.value + '\'' + '}';
    }
}

