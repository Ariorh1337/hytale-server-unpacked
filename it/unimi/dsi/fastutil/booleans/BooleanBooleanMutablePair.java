/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.booleans.BooleanBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class BooleanBooleanMutablePair
implements BooleanBooleanPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected boolean left;
    protected boolean right;

    public BooleanBooleanMutablePair(boolean left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static BooleanBooleanMutablePair of(boolean left, boolean right) {
        return new BooleanBooleanMutablePair(left, right);
    }

    @Override
    public boolean leftBoolean() {
        return this.left;
    }

    @Override
    public BooleanBooleanMutablePair left(boolean l) {
        this.left = l;
        return this;
    }

    @Override
    public boolean rightBoolean() {
        return this.right;
    }

    @Override
    public BooleanBooleanMutablePair right(boolean r) {
        this.right = r;
        return this;
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

