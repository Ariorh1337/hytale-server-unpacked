/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectLongPair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectLongMutablePair<K>
implements ObjectLongPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected long right;

    public ObjectLongMutablePair(K left, long right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectLongMutablePair<K> of(K left, long right) {
        return new ObjectLongMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ObjectLongMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public long rightLong() {
        return this.right;
    }

    @Override
    public ObjectLongMutablePair<K> right(long r) {
        this.right = r;
        return this;
    }

    public boolean equals(Object other) {
        if (other == null) {
            return false;
        }
        if (other instanceof ObjectLongPair) {
            return Objects.equals(this.left, ((ObjectLongPair)other).left()) && this.right == ((ObjectLongPair)other).rightLong();
        }
        if (other instanceof Pair) {
            return Objects.equals(this.left, ((Pair)other).left()) && Objects.equals(this.right, ((Pair)other).right());
        }
        return false;
    }

    public int hashCode() {
        return (this.left == null ? 0 : this.left.hashCode()) * 19 + HashCommon.long2int(this.right);
    }

    public String toString() {
        return "<" + this.left() + "," + this.rightLong() + ">";
    }
}

