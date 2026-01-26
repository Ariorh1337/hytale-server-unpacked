/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;

public final class BsonMinKey
extends BsonValue {
    @Override
    public BsonType getBsonType() {
        return BsonType.MIN_KEY;
    }

    public boolean equals(Object o) {
        return o instanceof BsonMinKey;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "BsonMinKey";
    }
}

