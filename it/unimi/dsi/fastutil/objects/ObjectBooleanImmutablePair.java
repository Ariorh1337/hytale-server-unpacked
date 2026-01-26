/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectBooleanImmutablePair<K>
implements ObjectBooleanPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final boolean right;

    public ObjectBooleanImmutablePair(K left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectBooleanImmutablePair<K> of(K left, boolean right) {
        return new ObjectBooleanImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public boolean rightBoolean() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ObjectBooleanPair) {
            return Objects.equals(this.left, ((ObjectBooleanPair)other).left()) && this.right == ((ObjectBooleanPair)other).rightBoolean();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + (this.right ? 1231 : 1237);
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightBoolean() + ">";
    }
}

