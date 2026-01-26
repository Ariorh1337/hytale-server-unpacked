/*
 * Decompiled with CFR 0.152.
 */
package org.bson.types;

import java.io.Serializable;

public class Code
implements Serializable {
    private static final long serialVersionUID = 475535263314046697L;
    private final String code;

    public Code(String code) {
        this.code = code;
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
        Code code1 = (Code)o;
        return this.code.equals(code1.code);
    }

    public int hashCode() {
        return this.code.hashCode();
    }

    public String toString() {
        return "Code{code='" + this.code + '\'' + '}';
    }
}

