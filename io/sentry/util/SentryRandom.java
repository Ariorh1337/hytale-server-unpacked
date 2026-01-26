/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry.util;

import io.sentry.util.Random;
import org.jetbrains.annotations.NotNull;

public final class SentryRandom {
    @NotNull
    private static final SentryRandomThreadLocal instance = new SentryRandomThreadLocal();

    @NotNull
    public static Random current() {
        return (Random)instance.get();
    }

    private static class SentryRandomThreadLocal
    extends ThreadLocal<Random> {
        private SentryRandomThreadLocal() {
        }

        @Override
        protected Random initialValue() {
            return new Random();
        }
    }
}

