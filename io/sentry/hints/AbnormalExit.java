/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.hints;

import org.jetbrains.annotations.Nullable;

public interface AbnormalExit {
    @Nullable
    public String mechanism();

    public boolean ignoreCurrentThread();

    @Nullable
    public Long timestamp();
}

