/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.bytes.ByteBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class ByteBooleanImmutablePair
implements ByteBooleanPair,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final byte left;
    protected final boolean right;

    public ByteBooleanImmutablePair(byte left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static ByteBooleanImmutablePair of(byte left, boolean right) {
        return new ByteBooleanImmutablePair(left, right);
    }

    @Override
    public byte leftByte() {
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
        if (other instanceof ByteBooleanPair) {
            return this.left == ((ByteBooleanPair)other).leftByte() && this.right == ((ByteBooleanPair)other).rightBoolean();
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
        return "<" + this.leftByte() + "," + this.rightBoolean() + ">";
    }
}

