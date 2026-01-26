/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceCharPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceCharImmutablePair<K>
implements ReferenceCharPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final char right;

    public ReferenceCharImmutablePair(K left, char right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceCharImmutablePair<K> of(K left, char right) {
        return new ReferenceCharImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ReferenceCharPair) {
            return this.left == ((ReferenceCharPair)other).left() && this.right == ((ReferenceCharPair)other).rightChar();
        }
        if (other instanceof Pair) {
            return this.left == ((Pair)other).left() && Objects.equals(Character.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return System.identityHashCode(this.left) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightChar() + ">";
    }
}

