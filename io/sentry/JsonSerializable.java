/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.ObjectWriter;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public interface JsonSerializable {
    public void serialize(@NotNull ObjectWriter var1, @NotNull ILogger var2) throws IOException;
}

