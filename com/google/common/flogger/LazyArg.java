/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public interface LazyArg<T> {
    @NullableDecl
    public T evaluate();
}

