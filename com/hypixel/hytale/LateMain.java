/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale;

import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.logger.backend.HytaleFileHandler;
import com.hypixel.hytale.logger.backend.HytaleLoggerBackend;
import com.hypixel.hytale.logger.sentry.SkipSentryException;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.HytaleServerConfig;
import com.hypixel.hytale.server.core.Options;
import io.sentry.Sentry;
import java.util.Map;
import java.util.logging.Level;

public class LateMain {
    public static void lateMain(String[] args) {
        try {
            if (Options.parse(args)) {
                return;
            }
            HytaleLogger.init();
            HytaleFileHandler.INSTANCE.enable();
            HytaleLogger.replaceStd();
            HytaleLoggerBackend.LOG_LEVEL_LOADER = name -> {
                Level configLevel;
                HytaleServerConfig config;
                for (Map.Entry<String, Level> e : Options.getOptionSet().valuesOf(Options.LOG_LEVELS)) {
                    if (!name.equals(e.getKey())) continue;
                    return e.getValue();
                }
                HytaleServer hytaleServer = HytaleServer.get();
                if (hytaleServer != null && (config = hytaleServer.getConfig()) != null && (configLevel = config.getLogLevels().get(name)) != null) {
                    return configLevel;
                }
                if (Options.getOptionSet().has(Options.SHUTDOWN_AFTER_VALIDATE)) {
                    return Level.WARNING;
                }
                return null;
            };
            if (Options.getOptionSet().has(Options.SHUTDOWN_AFTER_VALIDATE)) {
                HytaleLoggerBackend.reloadLogLevels();
            }
            new HytaleServer();
        }
        catch (Throwable t) {
            if (!SkipSentryException.hasSkipSentry(t)) {
                Sentry.captureException(t);
            }
            t.printStackTrace();
            throw new RuntimeException("Failed to create HytaleServer", t);
        }
    }
}

