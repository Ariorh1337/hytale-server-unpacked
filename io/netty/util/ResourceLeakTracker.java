/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.netty.util;

import org.jetbrains.annotations.Nullable;

public interface ResourceLeakTracker<T> {
    public void record();

    public void record(Object var1);

    public boolean close(T var1);

    @Nullable
    default public Throwable getCloseStackTraceIfAny() {
        return null;
    }
}

