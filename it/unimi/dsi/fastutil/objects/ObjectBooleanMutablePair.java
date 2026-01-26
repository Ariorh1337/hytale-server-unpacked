/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectBooleanPair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectBooleanMutablePair<K>
implements ObjectBooleanPair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected boolean right;

    public ObjectBooleanMutablePair(K left, boolean right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectBooleanMutablePair<K> of(K left, boolean right) {
        return new ObjectBooleanMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ObjectBooleanMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public boolean rightBoolean() {
        return this.right;
    }

    @Override
    public ObjectBooleanMutablePair<K> right(boolean r) {
        this.right = r;
        return this;
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

