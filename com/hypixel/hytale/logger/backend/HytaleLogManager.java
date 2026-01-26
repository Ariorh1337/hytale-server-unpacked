/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.logger.backend;

import com.hypixel.hytale.logger.backend.HytaleConsole;
import com.hypixel.hytale.logger.backend.HytaleFileHandler;
import com.hypixel.hytale.logger.backend.HytaleLoggerBackend;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import javax.annotation.Nonnull;

public class HytaleLogManager
extends LogManager {
    public static HytaleLogManager instance;

    public HytaleLogManager() {
        instance = this;
        this.getLogger("Hytale");
    }

    @Override
    public void reset() {
    }

    private void reset0() {
        super.reset();
    }

    @Override
    @Nonnull
    public Logger getLogger(@Nonnull String name) {
        Logger logger = super.getLogger(name);
        return logger != null ? logger : new HytaleJdkLogger(HytaleLoggerBackend.getLogger(name));
    }

    public static void resetFinally() {
        HytaleConsole.INSTANCE.shutdown();
        HytaleFileHandler.INSTANCE.shutdown();
        if (instance != null) {
            instance.reset0();
        }
    }

    private static class HytaleJdkLogger
    extends Logger {
        @Nonnull
        private final HytaleLoggerBackend backend;

        public HytaleJdkLogger(@Nonnull HytaleLoggerBackend backend) {
            super(backend.getLoggerName(), null);
            this.backend = backend;
        }

        @Override
        public String getName() {
            return this.backend.getLoggerName();
        }

        @Override
        @Nonnull
        public Level getLevel() {
            return this.backend.getLevel();
        }

        @Override
        public boolean isLoggable(@Nonnull Level level) {
            return this.backend.isLoggable(level);
        }

        @Override
        public void log(@Nonnull LogRecord record) {
            this.backend.log(record);
        }

        @Override
        public void setLevel(@Nonnull Level newLevel) {
            this.backend.setLevel(newLevel);
        }
    }
}

