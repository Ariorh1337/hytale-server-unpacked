/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface ByteBinaryOperator
extends BinaryOperator<Byte>,
IntBinaryOperator {
    @Override
    public byte apply(byte var1, byte var2);

    @Override
    @Deprecated
    default public int applyAsInt(int x, int y) {
        return this.apply(SafeMath.safeIntToByte(x), SafeMath.safeIntToByte(y));
    }

    @Override
    @Deprecated
    default public Byte apply(Byte x, Byte y) {
        return this.apply((byte)x, (byte)y);
    }
}

