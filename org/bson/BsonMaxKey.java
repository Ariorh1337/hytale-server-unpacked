/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;

public final class BsonMaxKey
extends BsonValue {
    @Override
    public BsonType getBsonType() {
        return BsonType.MAX_KEY;
    }

    public boolean equals(Object o) {
        return o instanceof BsonMaxKey;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "BsonMaxKey";
    }
}

