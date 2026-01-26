/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.io;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProtocolVersion {
    private final String hash;

    public ProtocolVersion(String hash) {
        this.hash = hash;
    }

    public String getHash() {
        return this.hash;
    }

    public boolean equals(@Nullable Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ProtocolVersion that = (ProtocolVersion)o;
        return this.hash != null ? this.hash.equals(that.hash) : that.hash == null;
    }

    public int hashCode() {
        int result = 31 * (this.hash != null ? this.hash.hashCode() : 0);
        return result;
    }

    @Nonnull
    public String toString() {
        return "ProtocolVersion{hash='" + this.hash + "'}";
    }
}

