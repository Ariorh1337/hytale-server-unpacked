/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.IntUnaryOperator;
import java.util.function.UnaryOperator;

@FunctionalInterface
public interface CharUnaryOperator
extends UnaryOperator<Character>,
IntUnaryOperator {
    @Override
    public char apply(char var1);

    public static CharUnaryOperator identity() {
        return i -> i;
    }

    @Override
    @Deprecated
    default public int applyAsInt(int x) {
        return this.apply(SafeMath.safeIntToChar(x));
    }

    @Override
    @Deprecated
    default public Character apply(Character x) {
        return Character.valueOf(this.apply(x.charValue()));
    }
}

