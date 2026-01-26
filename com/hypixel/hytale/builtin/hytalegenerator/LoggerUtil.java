/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.hytalegenerator;

import com.hypixel.hytale.common.util.ExceptionUtil;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

public class LoggerUtil {
    public static final String HYTALE_GENERATOR_NAME = "HytaleGenerator";

    public static Logger getLogger() {
        return Logger.getLogger(HYTALE_GENERATOR_NAME);
    }

    public static void logException(@Nonnull String contextDescription, @Nonnull Throwable e) {
        LoggerUtil.logException(contextDescription, e, LoggerUtil.getLogger());
    }

    public static void logException(@Nonnull String contextDescription, @Nonnull Throwable e, @Nonnull Logger logger) {
        Object msg = "Exception occurred during ";
        msg = (String)msg + contextDescription;
        msg = (String)msg + " \n";
        msg = (String)msg + ExceptionUtil.toStringWithStack(e);
        logger.severe((String)msg);
    }
}

