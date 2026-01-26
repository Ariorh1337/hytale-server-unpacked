/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceBytePair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceByteImmutablePair<K>
implements ReferenceBytePair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final byte right;

    public ReferenceByteImmutablePair(K left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceByteImmutablePair<K> of(K left, byte right) {
        return new ReferenceByteImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public byte rightByte() {
        return this.right;
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

