/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

import java.util.Arrays;
import org.bson.BsonType;
import org.bson.BsonValue;
import org.bson.assertions.Assertions;

public final class BsonRegularExpression
extends BsonValue {
    private final String pattern;
    private final String options;

    public BsonRegularExpression(String pattern, String options) {
        this.pattern = Assertions.notNull("pattern", pattern);
        this.options = options == null ? "" : this.sortOptionCharacters(options);
    }

    public BsonRegularExpression(String pattern) {
        this(pattern, null);
    }

    @Override
    public BsonType getBsonType() {
        return BsonType.REGULAR_EXPRESSION;
    }

    public String getPattern() {
        return this.pattern;
    }

    public String getOptions() {
        return this.options;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        BsonRegularExpression that = (BsonRegularExpression)o;
        if (!this.options.equals(that.options)) {
            return false;
        }
        return this.pattern.equals(that.pattern);
    }

    public int hashCode() {
        int result = this.pattern.hashCode();
        result = 31 * result + this.options.hashCode();
        return result;
    }

    public String toString() {
        return "BsonRegularExpression{pattern='" + this.pattern + '\'' + ", options='" + this.options + '\'' + '}';
    }

    private String sortOptionCharacters(String options) {
        char[] chars = options.toCharArray();
        Arrays.sort(chars);
        return new String(chars);
    }
}

