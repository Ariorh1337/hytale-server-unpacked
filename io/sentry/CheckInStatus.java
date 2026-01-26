/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum CheckInStatus {
    IN_PROGRESS,
    OK,
    ERROR;


    @NotNull
    public String apiName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}

