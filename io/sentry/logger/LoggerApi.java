/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.logger;

import io.sentry.HostnameCache;
import io.sentry.IScope;
import io.sentry.ISpan;
import io.sentry.PropagationContext;
import io.sentry.Scopes;
import io.sentry.SentryAttribute;
import io.sentry.SentryAttributeType;
import io.sentry.SentryAttributes;
import io.sentry.SentryDate;
import io.sentry.SentryLevel;
import io.sentry.SentryLogEvent;
import io.sentry.SentryLogEventAttributeValue;
import io.sentry.SentryLogLevel;
import io.sentry.SentryOptions;
import io.sentry.SpanId;
import io.sentry.logger.ILoggerApi;
import io.sentry.logger.SentryLogParameters;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.Platform;
import io.sentry.util.TracingUtils;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class LoggerApi
implements ILoggerApi {
    @NotNull
    private final Scopes scopes;

    public LoggerApi(@NotNull Scopes scopes) {
        this.scopes = scopes;
    }

    @Override
    public void trace(@Nullable String message, Object ... args) {
        this.log(SentryLogLevel.TRACE, message, args);
    }

    @Override
    public void debug(@Nullable String message, Object ... args) {
        this.log(SentryLogLevel.DEBUG, message, args);
    }

    @Override
    public void info(@Nullable String message, Object ... args) {
        this.log(SentryLogLevel.INFO, message, args);
    }

    @Override
    public void warn(@Nullable String message, Object ... args) {
        this.log(SentryLogLevel.WARN, message, args);
    }

    @Override
    public void error(@Nullable String message, Object ... args) {
        this.log(SentryLogLevel.ERROR, message, args);
    }

    @Override
    public void fatal(@Nullable String message, Object ... args) {
        this.log(SentryLogLevel.FATAL, message, args);
    }

    @Override
    public void log(@NotNull SentryLogLevel level, @Nullable String message, Object ... args) {
        this.captureLog(level, SentryLogParameters.create(null, null), message, args);
    }

    @Override
    public void log(@NotNull SentryLogLevel level, @Nullable SentryDate timestamp, @Nullable String message, Object ... args) {
        this.captureLog(level, SentryLogParameters.create(timestamp, null), message, args);
    }

    @Override
    public void log(@NotNull SentryLogLevel level, @NotNull SentryLogParameters params, @Nullable String message, Object ... args) {
        this.captureLog(level, params, message, args);
    }

    private void captureLog(@NotNull SentryLogLevel level, @NotNull SentryLogParameters params, @Nullable String message, Object ... args) {
        @NotNull SentryOptions options = this.scopes.getOptions();
        try {
            if (!this.scopes.isEnabled()) {
                options.getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'logger' call is a no-op.", new Object[0]);
                return;
            }
            if (!options.getLogs().isEnabled()) {
                options.getLogger().log(SentryLevel.WARNING, "Sentry Log is disabled and this 'logger' call is a no-op.", new Object[0]);
                return;
            }
            if (message == null) {
                return;
            }
            @Nullable SentryDate timestamp = params.getTimestamp();
            @NotNull SentryDate timestampToUse = timestamp == null ? options.getDateProvider().now() : timestamp;
            @NotNull String messageToUse = this.maybeFormatMessage(message, args);
            @NotNull IScope combinedScope = this.scopes.getCombinedScopeView();
            @NotNull PropagationContext propagationContext = combinedScope.getPropagationContext();
            @Nullable ISpan span = combinedScope.getSpan();
            if (span == null) {
                TracingUtils.maybeUpdateBaggage(combinedScope, options);
            }
            @NotNull SentryId traceId = span == null ? propagationContext.getTraceId() : span.getSpanContext().getTraceId();
            @NotNull SpanId spanId = span == null ? propagationContext.getSpanId() : span.getSpanContext().getSpanId();
            SentryLogEvent logEvent = new SentryLogEvent(traceId, timestampToUse, messageToUse, level);
            logEvent.setAttributes(this.createAttributes(params, message, spanId, args));
            logEvent.setSeverityNumber(level.getSeverityNumber());
            this.scopes.getClient().captureLog(logEvent, combinedScope);
        }
        catch (Throwable e) {
            options.getLogger().log(SentryLevel.ERROR, "Error while capturing log event", e);
        }
    }

    @NotNull
    private String maybeFormatMessage(@NotNull String message, @Nullable Object[] args) {
        if (args == null || args.length == 0) {
            return message;
        }
        try {
            return String.format(message, args);
        }
        catch (Throwable t) {
            this.scopes.getOptions().getLogger().log(SentryLevel.ERROR, "Error while running log through String.format", t);
            return message;
        }
    }

    @NotNull
    private HashMap<String, SentryLogEventAttributeValue> createAttributes(@NotNull SentryLogParameters params, @NotNull String message, @NotNull SpanId spanId, Object ... args) {
        SentryId scopeReplayId;
        String string;
        SdkVersion sdkVersion;
        SentryAttributes incomingAttributes;
        @NotNull HashMap<String, SentryLogEventAttributeValue> attributes = new HashMap<String, SentryLogEventAttributeValue>();
        @NotNull String origin = params.getOrigin();
        if (!"manual".equalsIgnoreCase(origin)) {
            attributes.put("sentry.origin", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)origin));
        }
        if ((incomingAttributes = params.getAttributes()) != null) {
            for (SentryAttribute sentryAttribute : incomingAttributes.getAttributes().values()) {
                @Nullable Object value = sentryAttribute.getValue();
                @NotNull SentryAttributeType type = sentryAttribute.getType() == null ? this.getType(value) : sentryAttribute.getType();
                attributes.put(sentryAttribute.getName(), new SentryLogEventAttributeValue(type, value));
            }
        }
        if (args != null) {
            int i = 0;
            for (Object arg : args) {
                @NotNull SentryAttributeType type = this.getType(arg);
                attributes.put("sentry.message.parameter." + i, new SentryLogEventAttributeValue(type, arg));
                ++i;
            }
            if (i > 0 && attributes.get("sentry.message.template") == null) {
                attributes.put("sentry.message.template", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)message));
            }
        }
        if ((sdkVersion = this.scopes.getOptions().getSdkVersion()) != null) {
            attributes.put("sentry.sdk.name", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)sdkVersion.getName()));
            attributes.put("sentry.sdk.version", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)sdkVersion.getVersion()));
        }
        if ((string = this.scopes.getOptions().getEnvironment()) != null) {
            attributes.put("sentry.environment", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)string));
        }
        if (!SentryId.EMPTY_ID.equals(scopeReplayId = this.scopes.getCombinedScopeView().getReplayId())) {
            attributes.put("sentry.replay_id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)scopeReplayId.toString()));
        } else {
            @NotNull SentryId controllerReplayId = this.scopes.getOptions().getReplayController().getReplayId();
            if (!SentryId.EMPTY_ID.equals(controllerReplayId)) {
                attributes.put("sentry.replay_id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)controllerReplayId.toString()));
                attributes.put("sentry._internal.replay_is_buffering", new SentryLogEventAttributeValue(SentryAttributeType.BOOLEAN, (Object)true));
            }
        }
        @Nullable String release = this.scopes.getOptions().getRelease();
        if (release != null) {
            attributes.put("sentry.release", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)release));
        }
        attributes.put("sentry.trace.parent_span_id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)spanId));
        if (Platform.isJvm()) {
            this.setServerName(attributes);
        }
        this.setUser(attributes);
        return attributes;
    }

    private void setServerName(@NotNull HashMap<String, SentryLogEventAttributeValue> attributes) {
        String hostname;
        @NotNull SentryOptions options = this.scopes.getOptions();
        @Nullable String optionsServerName = options.getServerName();
        if (optionsServerName != null) {
            attributes.put("server.address", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)optionsServerName));
        } else if (options.isAttachServerName() && (hostname = HostnameCache.getInstance().getHostname()) != null) {
            attributes.put("server.address", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)hostname));
        }
    }

    private void setUser(@NotNull HashMap<String, SentryLogEventAttributeValue> attributes) {
        @Nullable User user = this.scopes.getCombinedScopeView().getUser();
        if (user == null) {
            @Nullable String id = this.scopes.getOptions().getDistinctId();
            if (id != null) {
                attributes.put("user.id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)id));
            }
        } else {
            String email;
            String username;
            @Nullable String id = user.getId();
            if (id != null) {
                attributes.put("user.id", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)id));
            }
            if ((username = user.getUsername()) != null) {
                attributes.put("user.name", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)username));
            }
            if ((email = user.getEmail()) != null) {
                attributes.put("user.email", new SentryLogEventAttributeValue(SentryAttributeType.STRING, (Object)email));
            }
        }
    }

    @NotNull
    private SentryAttributeType getType(@Nullable Object arg) {
        if (arg instanceof Boolean) {
            return SentryAttributeType.BOOLEAN;
        }
        if (arg instanceof Integer) {
            return SentryAttributeType.INTEGER;
        }
        if (arg instanceof Number) {
            return SentryAttributeType.DOUBLE;
        }
        return SentryAttributeType.STRING;
    }
}

