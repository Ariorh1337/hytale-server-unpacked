/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface ByteUnaryOperator
extends UnaryOperator<Byte>,
IntUnaryOperator {
    @Override
    public byte apply(byte var1);

    public static ByteUnaryOperator identity() {
        return i -> i;
    }

    public static ByteUnaryOperator negation() {
        return i -> -i;
    }

    @Override
    @Deprecated
    default public int applyAsInt(int x) {
        return this.apply(SafeMath.safeIntToByte(x));
    }

    @Override
    @Deprecated
    default public Byte apply(Byte x) {
        return this.apply((byte)x);
    }
}

