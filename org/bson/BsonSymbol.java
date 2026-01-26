/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;

public class BsonSymbol
extends BsonValue {
    private final String symbol;

    public BsonSymbol(String value) {
        if (value == null) {
            throw new IllegalArgumentException("Value can not be null");
        }
        this.symbol = value;
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.SYMBOL;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonSymbol symbol1 = (BsonSymbol)o;
        return this.symbol.equals(symbol1.symbol);
    }

    public int hashCode() {
        return this.symbol.hashCode();
    }

    public String toString() {
        return this.symbol;
    }
}

