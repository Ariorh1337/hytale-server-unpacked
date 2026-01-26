/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.shorts.ShortBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class ShortBooleanImmutablePair
implements ShortBooleanPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final short left;
    protected final boolean right;

    public ShortBooleanImmutablePair(short left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static ShortBooleanImmutablePair of(short left, boolean right) {
        return new ShortBooleanImmutablePair(left, right);
    }

    @Override
    public short leftShort() {
        return this.left;
    }

    @Override
    public boolean rightBoolean() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ShortBooleanPair) {
            return this.left == ((ShortBooleanPair)other).leftShort() && this.right == ((ShortBooleanPair)other).rightBoolean();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return this.left * 19 + (this.right ? 1231 : 1237);
    }

    public String toString() {
        return "<" + this.leftShort() + "," + this.rightBoolean() + ">";
    }
}

