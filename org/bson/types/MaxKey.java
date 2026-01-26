/*
 * Decompiled with CFR 0.152.
 */
package org.bson.types;

import java.io.Serializable;

public final class MaxKey
implements Serializable {
    private static final long serialVersionUID = 5123414776151687185L;

    public boolean equals(Object o) {
        return o instanceof MaxKey;
    }

    public int hashCode() {
        return 0;
    }

    public String toString() {
        return "MaxKey";
    }
}

