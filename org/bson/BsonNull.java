/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;

public final class BsonNull
extends BsonValue {
    public static final BsonNull VALUE = new BsonNull();

    @Override
    public BsonType getBsonType() {
        return BsonType.NULL;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        return o != null && this.getClass() == o.getClass();
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "BsonNull";
    }
}

