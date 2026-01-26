/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.logger.sentry;

import io.sentry.Breadcrumb;
import io.sentry.Hint;
import io.sentry.IScopes;
import io.sentry.ScopesAdapter;
import io.sentry.Sentry;
import io.sentry.SentryAttribute;
import io.sentry.SentryAttributes;
import io.sentry.SentryEvent;
import io.sentry.SentryIntegrationPackageStorage;
import io.sentry.SentryLevel;
import io.sentry.SentryLogLevel;
import io.sentry.exception.ExceptionMechanismException;
import io.sentry.logger.SentryLogParameters;
import io.sentry.protocol.Mechanism;
import io.sentry.protocol.Message;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.LogRecord;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HytaleSentryHandler
extends Handler {
    public static final String MECHANISM_TYPE = "JulSentryHandler";
    public static final String THREAD_ID = "thread_id";
    private final IScopes scope;
    private boolean printfStyle;
    @Nonnull
    private Level minimumBreadcrumbLevel = Level.INFO;
    @Nonnull
    private Level minimumEventLevel = Level.SEVERE;
    @Nonnull
    private Level minimumLevel = Level.INFO;

    public HytaleSentryHandler(@Nonnull IScopes scope) {
        this(scope, true);
    }

    HytaleSentryHandler(@Nonnull IScopes scope, boolean configureFromLogManager) {
        this.setFilter(new DropSentryFilter());
        if (configureFromLogManager) {
            this.retrieveProperties();
        }
        this.scope = scope;
    }

    @Override
    public void publish(@Nonnull LogRecord record) {
        if (!this.isLoggable(record)) {
            return;
        }
        try {
            Hint hint;
            if (ScopesAdapter.getInstance().getOptions().getLogs().isEnabled() && record.getLevel().intValue() >= this.minimumLevel.intValue()) {
                this.captureLog(record);
            }
            if (record.getLevel().intValue() >= this.minimumEventLevel.intValue()) {
                hint = new Hint();
                hint.set("syntheticException", record);
                this.scope.captureEvent(this.createEvent(record), hint);
            }
            if (record.getLevel().intValue() >= this.minimumBreadcrumbLevel.intValue()) {
                hint = new Hint();
                hint.set("jul:logRecord", record);
                Sentry.addBreadcrumb(this.createBreadcrumb(record), hint);
            }
        }
        catch (RuntimeException e) {
            this.reportError("An exception occurred while creating a new event in Sentry", e, 1);
        }
    }

    protected void captureLog(@Nonnull LogRecord loggingEvent) {
        String formattedMessage;
        SentryLogLevel sentryLevel = HytaleSentryHandler.toSentryLogLevel(loggingEvent.getLevel());
        Object[] arguments = loggingEvent.getParameters();
        SentryAttributes attributes = SentryAttributes.of(new SentryAttribute[0]);
        String message = loggingEvent.getMessage();
        if (loggingEvent.getResourceBundle() != null && loggingEvent.getResourceBundle().containsKey(loggingEvent.getMessage())) {
            message = loggingEvent.getResourceBundle().getString(loggingEvent.getMessage());
        }
        if (!(formattedMessage = this.maybeFormatted(arguments, message)).equals(message)) {
            attributes.add(SentryAttribute.stringAttribute("sentry.message.template", message));
        }
        SentryLogParameters params = SentryLogParameters.create(attributes);
        params.setOrigin("auto.log.jul.hytale");
        Sentry.logger().log(sentryLevel, params, formattedMessage, arguments);
    }

    @Nonnull
    private String maybeFormatted(@Nonnull Object[] arguments, @Nonnull String message) {
        if (arguments != null) {
            try {
                return this.formatMessage(message, arguments);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        return message;
    }

    private void retrieveProperties() {
        String minimumLevel;
        String minimumEventLevel;
        LogManager manager = LogManager.getLogManager();
        String className = HytaleSentryHandler.class.getName();
        this.setPrintfStyle(Boolean.parseBoolean(manager.getProperty(className + ".printfStyle")));
        this.setLevel(this.parseLevelOrDefault(manager.getProperty(className + ".level")));
        String minimumBreadCrumbLevel = manager.getProperty(className + ".minimumBreadcrumbLevel");
        if (minimumBreadCrumbLevel != null) {
            this.setMinimumBreadcrumbLevel(this.parseLevelOrDefault(minimumBreadCrumbLevel));
        }
        if ((minimumEventLevel = manager.getProperty(className + ".minimumEventLevel")) != null) {
            this.setMinimumEventLevel(this.parseLevelOrDefault(minimumEventLevel));
        }
        if ((minimumLevel = manager.getProperty(className + ".minimumLevel")) != null) {
            this.setMinimumLevel(this.parseLevelOrDefault(minimumLevel));
        }
    }

    @Nullable
    private static SentryLevel formatLevel(@Nonnull Level level) {
        if (level.intValue() >= Level.SEVERE.intValue()) {
            return SentryLevel.ERROR;
        }
        if (level.intValue() >= Level.WARNING.intValue()) {
            return SentryLevel.WARNING;
        }
        if (level.intValue() >= Level.INFO.intValue()) {
            return SentryLevel.INFO;
        }
        if (level.intValue() >= Level.ALL.intValue()) {
            return SentryLevel.DEBUG;
        }
        return null;
    }

    @Nonnull
    private static SentryLogLevel toSentryLogLevel(@Nonnull Level level) {
        if (level.intValue() >= Level.SEVERE.intValue()) {
            return SentryLogLevel.ERROR;
        }
        if (level.intValue() >= Level.WARNING.intValue()) {
            return SentryLogLevel.WARN;
        }
        if (level.intValue() >= Level.INFO.intValue()) {
            return SentryLogLevel.INFO;
        }
        if (level.intValue() >= Level.FINE.intValue()) {
            return SentryLogLevel.DEBUG;
        }
        return SentryLogLevel.TRACE;
    }

    @Nonnull
    private Level parseLevelOrDefault(@Nonnull String levelName) {
        try {
            return Level.parse(levelName.trim());
        }
        catch (RuntimeException e) {
            return Level.WARNING;
        }
    }

    @Nonnull
    private Breadcrumb createBreadcrumb(@Nonnull LogRecord record) {
        Breadcrumb breadcrumb = new Breadcrumb();
        breadcrumb.setLevel(HytaleSentryHandler.formatLevel(record.getLevel()));
        breadcrumb.setCategory(record.getLoggerName());
        if (record.getParameters() != null) {
            try {
                breadcrumb.setMessage(this.formatMessage(record.getMessage(), record.getParameters()));
            }
            catch (RuntimeException e) {
                breadcrumb.setMessage(record.getMessage());
            }
        } else {
            breadcrumb.setMessage(record.getMessage());
        }
        return breadcrumb;
    }

    @Nonnull
    SentryEvent createEvent(@Nonnull LogRecord record) {
        SentryEvent event = new SentryEvent(new Date(record.getMillis()));
        event.setLevel(HytaleSentryHandler.formatLevel(record.getLevel()));
        event.setLogger(record.getLoggerName());
        Message sentryMessage = new Message();
        sentryMessage.setParams(this.toParams(record.getParameters()));
        String message = record.getMessage();
        if (record.getResourceBundle() != null && record.getResourceBundle().containsKey(record.getMessage())) {
            message = record.getResourceBundle().getString(record.getMessage());
        }
        sentryMessage.setMessage(message);
        if (record.getParameters() != null) {
            try {
                sentryMessage.setFormatted(this.formatMessage(message, record.getParameters()));
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        }
        event.setMessage(sentryMessage);
        Throwable throwable = record.getThrown();
        if (throwable != null) {
            Mechanism mechanism = new Mechanism();
            mechanism.setType(MECHANISM_TYPE);
            ExceptionMechanismException mechanismException = new ExceptionMechanismException(mechanism, throwable, Thread.currentThread());
            event.setThrowable(mechanismException);
        }
        event.setExtra(THREAD_ID, String.valueOf(record.getLongThreadID()));
        return event;
    }

    @Nonnull
    private List<String> toParams(@Nullable Object[] arguments) {
        ArrayList<String> result = new ArrayList<String>();
        if (arguments != null) {
            for (Object argument : arguments) {
                if (argument == null) continue;
                result.add(argument.toString());
            }
        }
        return result;
    }

    @Nonnull
    private String formatMessage(@Nonnull String message, @Nullable Object[] parameters) {
        String formatted = this.printfStyle ? String.format(message, parameters) : MessageFormat.format(message, parameters);
        return formatted;
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
        try {
            Sentry.close();
        }
        catch (RuntimeException e) {
            this.reportError("An exception occurred while closing the Sentry connection", e, 3);
        }
    }

    public void setPrintfStyle(boolean printfStyle) {
        this.printfStyle = printfStyle;
    }

    public void setMinimumBreadcrumbLevel(@Nullable Level minimumBreadcrumbLevel) {
        if (minimumBreadcrumbLevel != null) {
            this.minimumBreadcrumbLevel = minimumBreadcrumbLevel;
        }
    }

    @Nonnull
    public Level getMinimumBreadcrumbLevel() {
        return this.minimumBreadcrumbLevel;
    }

    public void setMinimumEventLevel(@Nullable Level minimumEventLevel) {
        if (minimumEventLevel != null) {
            this.minimumEventLevel = minimumEventLevel;
        }
    }

    @Nonnull
    public Level getMinimumEventLevel() {
        return this.minimumEventLevel;
    }

    public void setMinimumLevel(@Nullable Level minimumLevel) {
        if (minimumLevel != null) {
            this.minimumLevel = minimumLevel;
        }
    }

    @Nonnull
    public Level getMinimumLevel() {
        return this.minimumLevel;
    }

    public boolean isPrintfStyle() {
        return this.printfStyle;
    }

    static {
        SentryIntegrationPackageStorage.getInstance().addPackage("maven:io.sentry:sentry-jul", "8.29.0");
    }

    private static final class DropSentryFilter
    implements Filter {
        private DropSentryFilter() {
        }

        @Override
        public boolean isLoggable(@Nonnull LogRecord record) {
            String loggerName = record.getLoggerName();
            return loggerName == null || !loggerName.startsWith("server.io.sentry") || loggerName.startsWith("server.io.sentry.samples");
        }
    }
}

