/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectShortPair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectShortImmutablePair<K>
implements ObjectShortPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final short right;

    public ObjectShortImmutablePair(K left, short right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectShortImmutablePair<K> of(K left, short right) {
        return new ObjectShortImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public short rightShort() {
        return this.right;
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

