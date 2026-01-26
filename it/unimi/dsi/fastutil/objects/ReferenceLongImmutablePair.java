/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceLongPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceLongImmutablePair<K>
implements ReferenceLongPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final long right;

    public ReferenceLongImmutablePair(K left, long right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceLongImmutablePair<K> of(K left, long right) {
        return new ReferenceLongImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public long rightLong() {
        return this.right;
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

