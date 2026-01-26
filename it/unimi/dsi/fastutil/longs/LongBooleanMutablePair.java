/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.longs.LongBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class LongBooleanMutablePair
implements LongBooleanPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected long left;
    protected boolean right;

    public LongBooleanMutablePair(long left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static LongBooleanMutablePair of(long left, boolean right) {
        return new LongBooleanMutablePair(left, right);
    }

    @Override
    public long leftLong() {
        return this.left;
    }

    @Override
    public LongBooleanMutablePair left(long l) {
        this.left = l;
        return this;
    }

    @Override
    public boolean rightBoolean() {
        return this.right;
    }

    @Override
    public LongBooleanMutablePair right(boolean r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof LongBooleanPair) {
            return this.left == ((LongBooleanPair)other).leftLong() && this.right == ((LongBooleanPair)other).rightBoolean();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return HashCommon.long2int(this.left) * 19 + (this.right ? 1231 : 1237);
    }

    public String toString() {
        return "<" + this.leftLong() + "," + this.rightBoolean() + ">";
    }
}

