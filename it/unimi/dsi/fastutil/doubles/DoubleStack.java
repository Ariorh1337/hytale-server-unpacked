/*
 * Decompiled with CFR 0.152.
 */
package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Stack;

public interface DoubleStack
extends Stack<Double> {
    @Override
    public void push(double var1);

    public double popDouble();

    public double topDouble();

    public double peekDouble(int var1);

    @Override
    @Deprecated
    default public void push(Double o) {
        this.push((double)o);
    }

    @Override
    @Deprecated
    default public Double pop() {
        return this.popDouble();
    }

    @Override
    @Deprecated
    default public Double top() {
        return this.topDouble();
    }

    @Override
    @Deprecated
    default public Double peek(int i) {
        return this.peekDouble(i);
    }
}

