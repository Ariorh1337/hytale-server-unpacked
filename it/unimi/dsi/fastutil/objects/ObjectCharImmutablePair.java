/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectCharPair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectCharImmutablePair<K>
implements ObjectCharPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final char right;

    public ObjectCharImmutablePair(K left, char right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectCharImmutablePair<K> of(K left, char right) {
        return new ObjectCharImmutablePair<K>(left, right);
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
        if (other instanceof ObjectCharPair) {
            return Objects.equals(this.left, ((ObjectCharPair)other).left()) && this.right == ((ObjectCharPair)other).rightChar();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(Character.valueOf(this.right), ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + this.right;
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightChar() + ">";
    }
}

