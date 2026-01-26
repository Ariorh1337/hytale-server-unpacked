/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import org.jetbrains.annotations.Nullable;

interface UncaughtExceptionHandler {
    public Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler();

    public void setDefaultUncaughtExceptionHandler(@Nullable Thread.UncaughtExceptionHandler var1);

    public static final class Adapter
    implements UncaughtExceptionHandler {
        private static final Adapter INSTANCE = new Adapter();

        static UncaughtExceptionHandler getInstance() {
            return INSTANCE;
        }

        private Adapter() {
        }

        @Override
        public Thread.UncaughtExceptionHandler getDefaultUncaughtExceptionHandler() {
            return Thread.getDefaultUncaughtExceptionHandler();
        }

        @Override
        public void setDefaultUncaughtExceptionHandler(@Nullable Thread.UncaughtExceptionHandler handler) {
            Thread.setDefaultUncaughtExceptionHandler(handler);
        }
    }
}

