/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectDoubleImmutablePair<K>
implements ObjectDoublePair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final double right;

    public ObjectDoubleImmutablePair(K left, double right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectDoubleImmutablePair<K> of(K left, double right) {
        return new ObjectDoubleImmutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ObjectDoublePair) {
            return Objects.equals(this.left, ((ObjectDoublePair)other).left()) && this.right == ((ObjectDoublePair)other).rightDouble();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + HashCommon.double2int(this.right);
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightDouble() + ">";
    }
}

