/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.store;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CodecKey<T> {
    private final String id;

    public CodecKey(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        CodecKey codecKey = (CodecKey)o;
        return this.id != null ? this.id.equals(codecKey.id) : codecKey.id == null;
    }

    public int hashCode() {
        return this.id != null ? this.id.hashCode() : 0;
    }

    @Nonnull
    public String toString() {
        return "CodecKey{id='" + this.id + "'}";
    }
}

