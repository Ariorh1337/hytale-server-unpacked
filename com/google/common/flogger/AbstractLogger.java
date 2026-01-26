/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger;

import com.google.common.flogger.LoggingApi;
import com.google.common.flogger.backend.LogData;
import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.LoggingException;
import com.google.common.flogger.util.Checks;
import com.google.errorprone.annotations.CheckReturnValue;
import java.util.logging.Level;

@CheckReturnValue
public abstract class AbstractLogger<API extends LoggingApi<API>> {
    private final LoggerBackend backend;

    protected AbstractLogger(LoggerBackend backend) {
        this.backend = Checks.checkNotNull(backend, "backend");
    }

    public abstract API at(Level var1);

    public final API atSevere() {
        return this.at(Level.SEVERE);
    }

    public final API atWarning() {
        return this.at(Level.WARNING);
    }

    public final API atInfo() {
        return this.at(Level.INFO);
    }

    public final API atConfig() {
        return this.at(Level.CONFIG);
    }

    public final API atFine() {
        return this.at(Level.FINE);
    }

    public final API atFiner() {
        return this.at(Level.FINER);
    }

    public final API atFinest() {
        return this.at(Level.FINEST);
    }

    protected String getName() {
        return this.backend.getLoggerName();
    }

    protected final boolean isLoggable(Level level) {
        return this.backend.isLoggable(level);
    }

    final LoggerBackend getBackend() {
        return this.backend;
    }

    final void write(LogData data) {
        Checks.checkNotNull(data, "data");
        try {
            this.backend.log(data);
        }
        catch (RuntimeException error) {
            try {
                this.backend.handleError(error, data);
            }
            catch (LoggingException allowed) {
                throw allowed;
            }
            catch (RuntimeException runtimeException) {
                System.err.println("logging error: " + runtimeException.getMessage());
                runtimeException.printStackTrace(System.err);
            }
        }
    }
}

