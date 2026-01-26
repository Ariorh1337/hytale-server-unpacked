/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceLongPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceLongMutablePair<K>
implements ReferenceLongPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected long right;

    public ReferenceLongMutablePair(K left, long right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceLongMutablePair<K> of(K left, long right) {
        return new ReferenceLongMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ReferenceLongMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public long rightLong() {
        return this.right;
    }

    @Override
    public ReferenceLongMutablePair<K> right(long r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ReferenceLongPair) {
            return this.left == ((ReferenceLongPair)other).left() && this.right == ((ReferenceLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return System.identityHashCode(this.left) * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightLong() + ">";
    }
}

