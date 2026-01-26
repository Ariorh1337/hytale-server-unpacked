/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceBytePair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceByteMutablePair<K>
implements ReferenceBytePair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected byte right;

    public ReferenceByteMutablePair(K left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceByteMutablePair<K> of(K left, byte right) {
        return new ReferenceByteMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ReferenceByteMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    @Override
    public ReferenceByteMutablePair<K> right(byte r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ReferenceBytePair) {
            return this.left == ((ReferenceBytePair)other).left() && this.right == ((ReferenceBytePair)other).rightByte();
        }
        if (other instanceof Pair) {
            return this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return System.identityHashCode(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightByte() + ">";
    }
}

