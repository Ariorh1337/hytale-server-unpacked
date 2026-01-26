/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonValue;

public class BsonElement {
    private final String name;
    private final BsonValue value;

    public BsonElement(String name, BsonValue value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public BsonValue getValue() {
        return this.value;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonElement that = (BsonElement)o;
        if (this.getName() != null ? !this.getName().equals(that.getName()) : that.getName() != null) {
            return false;
        }
        return !(this.getValue() != null ? !this.getValue().equals(that.getValue()) : that.getValue() != null);
    }

    public int hashCode() {
        int result = this.getName() != null ? this.getName().hashCode() : 0;
        result = 31 * result + (this.getValue() != null ? this.getValue().hashCode() : 0);
        return result;
    }
}

