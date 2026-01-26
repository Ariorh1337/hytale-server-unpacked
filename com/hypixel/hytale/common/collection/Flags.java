/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.collection;

import com.hypixel.hytale.common.collection.Flag;
import com.hypixel.hytale.common.util.StringUtil;
import javax.annotation.Nonnull;

public class Flags<T extends Flag> {
    private int flags;

    public Flags(@Nonnull T flag) {
        this.set(flag, true);
    }

    @SafeVarargs
    public Flags(T ... flags) {
        for (T flag : flags) {
            this.set(flag, true);
        }
    }

    public Flags(int flags) {
        this.flags = flags;
    }

    public int getFlags() {
        return this.flags;
    }

    public boolean is(@Nonnull T flag) {
        return (this.flags & flag.mask()) != 0;
    }

    public boolean not(@Nonnull T flag) {
        return (this.flags & flag.mask()) == 0;
    }

    public boolean set(@Nonnull T flag, boolean value) {
        if (value) {
            return (this.flags |= flag.mask()) != this.flags;
        }
        return (this.flags &= ~flag.mask()) != this.flags;
    }

    public boolean toggle(@Nonnull T flag) {
        return ((this.flags ^= flag.mask()) & flag.mask()) != 0;
    }

    @Nonnull
    public String toString() {
        return StringUtil.toPaddedBinaryString(this.flags);
    }
}

