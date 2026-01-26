/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectDoublePair;
import java.io.Serializable;
import java.util.Objects;

public class ObjectDoubleMutablePair<K>
implements ObjectDoublePair<K>,
Serializable {
    private static final long serialVersionUID = 0L;
    protected K left;
    protected double right;

    public ObjectDoubleMutablePair(K left, double right) {
        this.left = left;
        this.right = right;
    }

    public static <K> ObjectDoubleMutablePair<K> of(K left, double right) {
        return new ObjectDoubleMutablePair<K>(left, right);
    }

    @Override
    public K left() {
        return this.left;
    }

    public ObjectDoubleMutablePair<K> left(K l) {
        this.left = l;
        return this;
    }

    @Override
    public double rightDouble() {
        return this.right;
    }

    @Override
    public ObjectDoubleMutablePair<K> right(double r) {
        this.right = r;
        return this;
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

