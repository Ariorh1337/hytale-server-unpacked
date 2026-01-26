/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.SafeMath;
import java.util.function.BinaryOperator;
import java.util.function.IntBinaryOperator;

@FunctionalInterface
public interface CharBinaryOperator
extends BinaryOperator<Character>,
IntBinaryOperator {
    @Override
    public char apply(char var1, char var2);

    @Override
    @Deprecated
    default public int applyAsInt(int x, int y) {
        return this.apply(SafeMath.safeIntToChar(x), SafeMath.safeIntToChar(y));
    }

    @Override
    @Deprecated
    default public Character apply(Character x, Character y) {
        return Character.valueOf(this.apply(x.charValue(), y.charValue()));
    }
}

