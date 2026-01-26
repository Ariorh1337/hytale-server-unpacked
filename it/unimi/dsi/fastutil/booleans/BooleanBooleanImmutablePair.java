/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanBooleanImmutablePair
implements BooleanBooleanPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final boolean left;
    protected final boolean right;

    public BooleanBooleanImmutablePair(boolean left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanBooleanImmutablePair of(boolean left, boolean right) {
        return new BooleanBooleanImmutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
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
        if (other instanceof BooleanBooleanPair) {
            return this.left == ((BooleanBooleanPair)other).leftBoolean() && this.right == ((BooleanBooleanPair)other).rightBoolean();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left ? 1231 : 1237) * 19 + (this.right ? 1231 : 1237);
    }

    public String toString() {
        return "<" + this.leftBoolean() + "," + this.rightBoolean() + ">";
    }
}

