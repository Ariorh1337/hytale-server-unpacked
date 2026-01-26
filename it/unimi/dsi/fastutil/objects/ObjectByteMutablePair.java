/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectBytePair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectByteMutablePair<K>
implements ObjectBytePair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected byte right;

    public ObjectByteMutablePair(K left, byte right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectByteMutablePair<K> of(K left, byte right) {
        return new ObjectByteMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ObjectByteMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public byte rightByte() {
        return this.right;
    }

    @Override
    public ObjectByteMutablePair<K> right(byte r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ObjectBytePair) {
            return Objects.equals(this.left, ((ObjectBytePair)other).left()) && this.right == ((ObjectBytePair)other).rightByte();
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
        return "<" + this.left() + "," + this.rightByte() + ">";
    }
}

