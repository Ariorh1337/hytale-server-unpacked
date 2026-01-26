/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortLongPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortLongImmutablePair
implements ShortLongPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final short left;
    protected final long right;

    public ShortLongImmutablePair(short left, long right) {
        this.left = left;
        this.right = right;
    }

    public static ShortLongImmutablePair of(short left, long right) {
        return new ShortLongImmutablePair(left, right);
    }

    @Override
    public short leftShort() {
        return this.left;
    }

    @Override
    public long rightLong() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ShortLongPair) {
            return this.left == ((ShortLongPair)other).leftShort() && this.right == ((ShortLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.leftShort() + "," + this.rightLong() + ">";
    }
}

