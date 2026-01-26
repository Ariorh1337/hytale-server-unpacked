/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger;

import com.google.common.flogger.AbstractLogger;
import com.google.common.flogger.LogContext;
import com.google.common.flogger.LoggingApi;
import com.google.common.flogger.backend.LoggerBackend;
import com.google.common.flogger.backend.Platform;
import com.google.common.flogger.parser.DefaultPrintfMessageParser;
import com.google.common.flogger.parser.MessageParser;
import com.google.errorprone.annotations.CheckReturnValue;
import java.util.logging.Level;

@CheckReturnValue
public final class FluentLogger
extends AbstractLogger<Api> {
    static final NoOp NO_OP = new NoOp();

    public static FluentLogger forEnclosingClass() {
        String loggingClass = Platform.getCallerFinder().findLoggingClass(FluentLogger.class);
        return new FluentLogger(Platform.getBackend(loggingClass));
    }

    FluentLogger(LoggerBackend backend) {
        super(backend);
    }

    @Override
    public Api at(Level level) {
        boolean isLoggable = this.isLoggable(level);
        boolean isForced = Platform.shouldForceLogging(this.getName(), level, isLoggable);
        return isLoggable || isForced ? new Context(level, isForced) : NO_OP;
    }

    final class Context
    extends LogContext<FluentLogger, Api>
    implements Api {
        private Context(Level level, boolean isForced) {
            super(level, isForced);
        }

        @Override
        protected FluentLogger getLogger() {
            return FluentLogger.this;
        }

        @Override
        protected Api api() {
            return this;
        }

        @Override
        protected Api noOp() {
            return NO_OP;
        }

        @Override
        protected MessageParser getMessageParser() {
            return DefaultPrintfMessageParser.getInstance();
        }
    }

    private static final class NoOp
    extends LoggingApi.NoOp<Api>
    implements Api {
        private NoOp() {
        }
    }

    public static interface Api
    extends LoggingApi<Api> {
    }
}

