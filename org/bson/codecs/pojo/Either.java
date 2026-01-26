/*
 * Decompiled with CFR 0.152.
 */
package org.bson.codecs.pojo;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import org.bson.assertions.Assertions;

final class Either<L, R> {
    private final L left;
    private final R right;

    public static <L, R> Either<L, R> left(L value) {
        return new Either<L, Object>(Assertions.notNull("value", value), null);
    }

    public static <L, R> Either<L, R> right(R value) {
        return new Either<Object, R>(null, Assertions.notNull("value", value));
    }

    private Either(L l, R r) {
        this.left = l;
        this.right = r;
    }

    public <T> T map(Function<? super L, ? extends T> lFunc, Function<? super R, ? extends T> rFunc) {
        return this.left != null ? lFunc.apply(this.left) : rFunc.apply(this.right);
    }

    public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
        if (this.left != null) {
            lFunc.accept(this.left);
        }
        if (this.right != null) {
            rFunc.accept(this.right);
        }
    }

    public String toString() {
        return "Either{left=" + this.left + ", right=" + this.right + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Either either = (Either)o;
        return Objects.equals(this.left, either.left) && Objects.equals(this.right, either.right);
    }

    public int hashCode() {
        return Objects.hash(this.left, this.right);
    }
}

