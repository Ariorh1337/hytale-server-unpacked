/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortShortPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortShortImmutablePair
implements ShortShortPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final short left;
    protected final short right;

    public ShortShortImmutablePair(short left, short right) {
        this.left = left;
        this.right = right;
    }

    public static ShortShortImmutablePair of(short left, short right) {
        return new ShortShortImmutablePair(left, right);
    }

    @Override
    public short leftShort() {
        return this.left;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ShortShortPair) {
            return this.left == ((ShortShortPair)other).leftShort() && this.right == ((ShortShortPair)other).rightShort();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftShort() + "," + this.rightShort() + ">";
    }
}

