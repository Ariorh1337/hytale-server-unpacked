/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.fastutil;

import java.util.Collection;

public interface FastCollection<E>
extends Collection<E> {
    public void forEachWithFloat(FastConsumerF<? super E> var1, float var2);

    public void forEachWithInt(FastConsumerI<? super E> var1, int var2);

    public void forEachWithLong(FastConsumerL<? super E> var1, long var2);

    public <A, B, C, D> void forEach(FastConsumerD9<? super E, A, B, C, D> var1, A var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, B var21, C var22, D var23);

    public <A, B, C, D> void forEach(FastConsumerD6<? super E, A, B, C, D> var1, A var2, double var3, double var5, double var7, double var9, double var11, double var13, B var15, C var16, D var17);

    @FunctionalInterface
    public static interface FastConsumerD6<A, B, C, D, E> {
        public void accept(A var1, B var2, double var3, double var5, double var7, double var9, double var11, double var13, C var15, D var16, E var17);
    }

    @FunctionalInterface
    public static interface FastConsumerD9<A, B, C, D, E> {
        public void accept(A var1, B var2, double var3, double var5, double var7, double var9, double var11, double var13, double var15, double var17, double var19, C var21, D var22, E var23);
    }

    @FunctionalInterface
    public static interface FastConsumerL<A> {
        public void accept(A var1, long var2);
    }

    @FunctionalInterface
    public static interface FastConsumerI<A> {
        public void accept(A var1, int var2);
    }

    @FunctionalInterface
    public static interface FastConsumerF<A> {
        public void accept(A var1, float var2);
    }
}

