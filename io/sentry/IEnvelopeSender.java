/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.Hint;
import org.jetbrains.annotations.NotNull;

public interface IEnvelopeSender {
    public void processEnvelopeFile(@NotNull String var1, @NotNull Hint var2);
}

