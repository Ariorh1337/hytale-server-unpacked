/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ReferenceBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class ReferenceBooleanImmutablePair<K>
implements ReferenceBooleanPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected final K left;
    protected final boolean right;

    public ReferenceBooleanImmutablePair(K left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ReferenceBooleanImmutablePair<K> of(K left, boolean right) {
        return new ReferenceBooleanImmutablePair<K>(left, right);
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
        if (other instanceof ReferenceBooleanPair) {
            return this.left == ((ReferenceBooleanPair)other).left() && this.right == ((ReferenceBooleanPair)other).rightBoolean();
        }
        if (other instanceof Pair) {
            return this.left == ((Pair)other).left() && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return System.identityHashCode(this.left) * 19 + (this.right ? 1231 : 1237);
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightBoolean() + ">";
    }
}

