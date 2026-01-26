/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IScope;
import org.jetbrains.annotations.NotNull;

public interface ScopeCallback {
    public void run(@NotNull IScope var1);
}

