/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.logger.backend;

import com.hypixel.hytale.logger.HytaleLogger;
import java.util.logging.Level;

public class HytaleUncaughtExceptionHandler
implements Thread.UncaughtExceptionHandler {
    public static final HytaleUncaughtExceptionHandler INSTANCE = new HytaleUncaughtExceptionHandler();

    public static void setup() {
        Thread.setDefaultUncaughtExceptionHandler(INSTANCE);
        System.setProperty("java.util.concurrent.ForkJoinPool.common.exceptionHandler", HytaleUncaughtExceptionHandler.class.getName());
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.SEVERE).withCause(e)).log("Exception in thread: %s", t);
    }
}

