/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectShortPair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectShortMutablePair<K>
implements ObjectShortPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected short right;

    public ObjectShortMutablePair(K left, short right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectShortMutablePair<K> of(K left, short right) {
        return new ObjectShortMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ObjectShortMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public short rightShort() {
        return this.right;
    }

    @Override
    public ObjectShortMutablePair<K> right(short r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ObjectShortPair) {
            return Objects.equals(this.left, ((ObjectShortPair)other).left()) && this.right == ((ObjectShortPair)other).rightShort();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightShort() + ">";
    }
}

