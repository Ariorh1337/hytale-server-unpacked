/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanIntPair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanIntImmutablePair
implements BooleanIntPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final boolean left;
    protected final int right;

    public BooleanIntImmutablePair(boolean left, int right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanIntImmutablePair of(boolean left, int right) {
        return new BooleanIntImmutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
        return this.left;
    }

    @Override
    public int rightInt() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof BooleanIntPair) {
            return this.left == ((BooleanIntPair)other).leftBoolean() && this.right == ((BooleanIntPair)other).rightInt();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left ? 1231 : 1237) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.leftBoolean() + "," + this.rightInt() + ">";
    }
}

