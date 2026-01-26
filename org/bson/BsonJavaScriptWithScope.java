/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import org.bson.BsonDocument;
import org.bson.BsonType;
import org.bson.BsonValue;

public class BsonJavaScriptWithScope
extends BsonValue {
    private final String code;
    private final BsonDocument scope;

    public BsonJavaScriptWithScope(String code, BsonDocument scope) {
        if (code == null) {
            throw new IllegalArgumentException("code can not be null");
        }
        if (scope == null) {
            throw new IllegalArgumentException("scope can not be null");
        }
        this.code = code;
        this.scope = scope;
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.JAVASCRIPT_WITH_SCOPE;
    }

    public String getCode() {
        return this.code;
    }

    public BsonDocument getScope() {
        return this.scope;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonJavaScriptWithScope that = (BsonJavaScriptWithScope)o;
        if (!this.code.equals(that.code)) {
            return false;
        }
        return this.scope.equals(that.scope);
    }

    public int hashCode() {
        int result = this.code.hashCode();
        result = 31 * result + this.scope.hashCode();
        return result;
    }

    public String toString() {
        return "BsonJavaScriptWithScope{code=" + this.getCode() + "scope=" + this.scope + '}';
    }

    static BsonJavaScriptWithScope clone(BsonJavaScriptWithScope from) {
        return new BsonJavaScriptWithScope(from.code, from.scope.clone());
    }
}

