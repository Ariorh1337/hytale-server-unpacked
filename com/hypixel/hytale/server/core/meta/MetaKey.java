/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.meta;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MetaKey<T> {
    private final int id;

    MetaKey(int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        MetaKey metaKey = (MetaKey)o;
        return this.id == metaKey.id;
    }

    public int hashCode() {
        return this.id;
    }

    @Nonnull
    public String toString() {
        return "MetaKey{id=" + this.id + "}";
    }
}

