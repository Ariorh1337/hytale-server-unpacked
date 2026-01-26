/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.context;

import com.google.common.flogger.context.ScopedLoggingContext;
import com.google.errorprone.annotations.CheckReturnValue;

public final class ScopedLoggingContexts {
    private ScopedLoggingContexts() {
    }

    @CheckReturnValue
    public static ScopedLoggingContext.Builder newContext() {
        return ScopedLoggingContext.getInstance().newContext();
    }
}

