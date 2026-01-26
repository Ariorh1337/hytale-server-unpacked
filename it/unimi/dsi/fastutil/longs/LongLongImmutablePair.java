/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongLongPair;
import java.io.Serializable;
import java.util.Objects;

public class LongLongImmutablePair
implements LongLongPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final long left;
    protected final long right;

    public LongLongImmutablePair(long left, long right) {
        this.left = left;
        this.right = right;
    }

    public static LongLongImmutablePair of(long left, long right) {
        return new LongLongImmutablePair(left, right);
    }

    @Override
    public long leftLong() {
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
        if (other instanceof LongLongPair) {
            return this.left == ((LongLongPair)other).leftLong() && this.right == ((LongLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.rightLong() + ">";
    }
}

