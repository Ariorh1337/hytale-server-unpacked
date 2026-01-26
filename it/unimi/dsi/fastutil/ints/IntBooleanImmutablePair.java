/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.ints.IntBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class IntBooleanImmutablePair
implements IntBooleanPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final int left;
    protected final boolean right;

    public IntBooleanImmutablePair(int left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static IntBooleanImmutablePair of(int left, boolean right) {
        return new IntBooleanImmutablePair(left, right);
    }

    @Override
    public int leftInt() {
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
        if (other instanceof IntBooleanPair) {
            return this.left == ((IntBooleanPair)other).leftInt() && this.right == ((IntBooleanPair)other).rightBoolean();
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
        return "<" + this.leftInt() + "," + this.rightBoolean() + ">";
    }
}

