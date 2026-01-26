/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.jetbrains.annotations.TestOnly
 */
package io.sentry;

import io.sentry.SentryStackTraceFactory;
import io.sentry.exception.ExceptionMechanismException;
import io.sentry.protocol.Mechanism;
import io.sentry.protocol.SentryException;
import io.sentry.protocol.SentryStackFrame;
import io.sentry.protocol.SentryStackTrace;
import io.sentry.protocol.SentryThread;
import io.sentry.util.Objects;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;

@ApiStatus.Internal
public final class SentryExceptionFactory {
    @NotNull
    private final SentryStackTraceFactory sentryStackTraceFactory;

    public SentryExceptionFactory(@NotNull SentryStackTraceFactory sentryStackTraceFactory) {
        this.sentryStackTraceFactory = Objects.requireNonNull(sentryStackTraceFactory, "The SentryStackTraceFactory is required.");
    }

    @NotNull
    public List<SentryException> getSentryExceptionsFromThread(@NotNull SentryThread thread, @NotNull Mechanism mechanism, @NotNull Throwable throwable) {
        SentryStackTrace threadStacktrace = thread.getStacktrace();
        if (threadStacktrace == null) {
            return new ArrayList<SentryException>(0);
        }
        ArrayList<SentryException> exceptions = new ArrayList<SentryException>(1);
        exceptions.add(this.getSentryException(throwable, mechanism, thread.getId(), threadStacktrace.getFrames(), true));
        return exceptions;
    }

    @NotNull
    public List<SentryException> getSentryExceptions(@NotNull Throwable throwable) {
        return this.getSentryExceptions(this.extractExceptionQueue(throwable));
    }

    @NotNull
    private List<SentryException> getSentryExceptions(@NotNull Deque<SentryException> exceptions) {
        return new ArrayList<SentryException>(exceptions);
    }

    @NotNull
    private SentryException getSentryException(@NotNull Throwable throwable, @Nullable Mechanism exceptionMechanism, @Nullable Long threadId, @Nullable List<SentryStackFrame> frames, boolean snapshot) {
        String exceptionPackageName;
        Package exceptionPackage = throwable.getClass().getPackage();
        String fullClassName = throwable.getClass().getName();
        SentryException exception = new SentryException();
        String exceptionMessage = throwable.getMessage();
        String exceptionClassName = exceptionPackage != null ? fullClassName.replace(exceptionPackage.getName() + ".", "") : fullClassName;
        String string = exceptionPackageName = exceptionPackage != null ? exceptionPackage.getName() : null;
        if (frames != null && !frames.isEmpty()) {
            SentryStackTrace sentryStackTrace = new SentryStackTrace(frames);
            if (snapshot) {
                sentryStackTrace.setSnapshot(true);
            }
            exception.setStacktrace(sentryStackTrace);
        }
        exception.setThreadId(threadId);
        exception.setType(exceptionClassName);
        exception.setMechanism(exceptionMechanism);
        exception.setModule(exceptionPackageName);
        exception.setValue(exceptionMessage);
        return exception;
    }

    @TestOnly
    @NotNull
    Deque<SentryException> extractExceptionQueue(@NotNull Throwable throwable) {
        return this.extractExceptionQueueInternal(throwable, new AtomicInteger(-1), new HashSet<Throwable>(), new ArrayDeque<SentryException>(), null);
    }

    Deque<SentryException> extractExceptionQueueInternal(@NotNull Throwable throwable, @NotNull AtomicInteger exceptionId, @NotNull HashSet<Throwable> circularityDetector, @NotNull Deque<SentryException> exceptions, @Nullable String mechanismTypeOverride) {
        int parentId = exceptionId.get();
        for (Throwable currentThrowable = throwable; currentThrowable != null && circularityDetector.add(currentThrowable); currentThrowable = currentThrowable.getCause()) {
            Thread thread;
            Mechanism exceptionMechanism;
            String mechanismType;
            boolean snapshot = false;
            String string = mechanismType = mechanismTypeOverride == null ? "chained" : mechanismTypeOverride;
            if (currentThrowable instanceof ExceptionMechanismException) {
                ExceptionMechanismException exceptionMechanismThrowable = (ExceptionMechanismException)currentThrowable;
                exceptionMechanism = exceptionMechanismThrowable.getExceptionMechanism();
                currentThrowable = exceptionMechanismThrowable.getThrowable();
                thread = exceptionMechanismThrowable.getThread();
                snapshot = exceptionMechanismThrowable.isSnapshot();
            } else {
                exceptionMechanism = new Mechanism();
                thread = Thread.currentThread();
            }
            boolean includeSentryFrames = Boolean.FALSE.equals(exceptionMechanism.isHandled());
            List<SentryStackFrame> frames = this.sentryStackTraceFactory.getStackFrames(currentThrowable.getStackTrace(), includeSentryFrames);
            SentryException exception = this.getSentryException(currentThrowable, exceptionMechanism, thread.getId(), frames, snapshot);
            exceptions.addFirst(exception);
            if (exceptionMechanism.getType() == null) {
                exceptionMechanism.setType(mechanismType);
            }
            if (exceptionId.get() >= 0) {
                exceptionMechanism.setParentId(parentId);
            }
            int currentExceptionId = exceptionId.incrementAndGet();
            exceptionMechanism.setExceptionId(currentExceptionId);
            Throwable[] suppressed = currentThrowable.getSuppressed();
            if (suppressed != null && suppressed.length > 0) {
                for (Throwable suppressedThrowable : suppressed) {
                    this.extractExceptionQueueInternal(suppressedThrowable, exceptionId, circularityDetector, exceptions, "suppressed");
                }
            }
            parentId = currentExceptionId;
            mechanismTypeOverride = null;
        }
        return exceptions;
    }
}

