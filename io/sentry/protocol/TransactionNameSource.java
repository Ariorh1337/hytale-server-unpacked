/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package io.sentry.protocol;

import java.util.Locale;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public enum TransactionNameSource {
    CUSTOM,
    URL,
    ROUTE,
    VIEW,
    COMPONENT,
    TASK;


    public String apiName() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}

