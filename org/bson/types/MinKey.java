/*
 * Decompiled with CFR 0.152.
 */
package org.bson.types;

import java.io.Serializable;

public final class MinKey
implements Serializable {
    private static final long serialVersionUID = 4075901136671855684L;

    public boolean equals(Object o) {
        return o instanceof MinKey;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "MinKey";
    }
}

