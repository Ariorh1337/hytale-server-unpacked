/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceCharPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceCharMutablePair<K>
implements ReferenceCharPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected char right;

    public ReferenceCharMutablePair(K left, char right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceCharMutablePair<K> of(K left, char right) {
        return new ReferenceCharMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ReferenceCharMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public char rightChar() {
        return this.right;
    }

    @Override
    public ReferenceCharMutablePair<K> right(char r) {
        this.right = r;
        return this;
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

