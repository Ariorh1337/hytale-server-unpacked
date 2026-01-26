/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonType;
import org.bson.BsonValue;

public class BsonJavaScript
extends BsonValue {
    private final String code;

    public BsonJavaScript(String code) {
        this.code = code;
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.JAVASCRIPT;
    }

    public String getCode() {
        return this.code;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonJavaScript code1 = (BsonJavaScript)o;
        return this.code.equals(code1.code);
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    public String toString() {
        return "BsonJavaScript{code='" + this.code + '\'' + '}';
    }
}

