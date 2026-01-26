/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

public class BsonObjectId
extends BsonValue
implements Comparable<BsonObjectId> {
    private final ObjectId value;

    public BsonObjectId() {
        this(new ObjectId());
    }

    public BsonObjectId(ObjectId value) {
        if (value == null) {
            throw new IllegalArgumentException("value may not be null");
        }
        this.value = value;
    }

    public ObjectId getValue() {
        return this.value;
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.OBJECT_ID;
    }

    @Override
    public int compareTo(BsonObjectId o) {
        return this.value.compareTo(o.value);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonObjectId that = (BsonObjectId)o;
        return this.value.equals(that.value);
    }

    public int hashCode() {
        return this.value.hashCode();
    }

    public String toString() {
        return "BsonObjectId{value=" + this.value.toHexString() + '}';
    }
}

