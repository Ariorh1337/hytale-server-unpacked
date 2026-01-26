/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

public enum SentryAttributeType {
    STRING,
    BOOLEAN,
    INTEGER,
    DOUBLE;


    @NotNull
    public String apiName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}

