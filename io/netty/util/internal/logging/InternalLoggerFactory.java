/*
 * Decompiled with CFR 0.152.
 */
package io.netty.util.internal.logging;

import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.JdkLoggerFactory;
import io.netty.util.internal.logging.Log4J2LoggerFactory;
import io.netty.util.internal.logging.Log4JLoggerFactory;
import io.netty.util.internal.logging.Slf4JLoggerFactory;

public abstract class InternalLoggerFactory {
    private static volatile InternalLoggerFactory defaultFactory;

    private static InternalLoggerFactory newDefaultFactory(String name) {
        InternalLoggerFactory f = InternalLoggerFactory.useSlf4JLoggerFactory(name);
        if (f != null) {
            return f;
        }
        f = InternalLoggerFactory.useLog4J2LoggerFactory(name);
        if (f != null) {
            return f;
        }
        f = InternalLoggerFactory.useLog4JLoggerFactory(name);
        if (f != null) {
            return f;
        }
        return InternalLoggerFactory.useJdkLoggerFactory(name);
    }

    private static InternalLoggerFactory useSlf4JLoggerFactory(String name) {
        try {
            InternalLoggerFactory f = Slf4JLoggerFactory.getInstanceWithNopCheck();
            f.newInstance(name).debug("Using SLF4J as the default logging framework");
            return f;
        }
        catch (LinkageError ignore) {
            return null;
        }
        catch (Exception ignore) {
            return null;
        }
    }

    private static InternalLoggerFactory useLog4J2LoggerFactory(String name) {
        try {
            InternalLoggerFactory f = Log4J2LoggerFactory.INSTANCE;
            f.newInstance(name).debug("Using Log4J2 as the default logging framework");
            return f;
        }
        catch (LinkageError ignore) {
            return null;
        }
        catch (Exception ignore) {
            return null;
        }
    }

    private static InternalLoggerFactory useLog4JLoggerFactory(String name) {
        try {
            InternalLoggerFactory f = Log4JLoggerFactory.INSTANCE;
            f.newInstance(name).debug("Using Log4J as the default logging framework");
            return f;
        }
        catch (LinkageError ignore) {
            return null;
        }
        catch (Exception ignore) {
            return null;
        }
    }

    private static InternalLoggerFactory useJdkLoggerFactory(String name) {
        InternalLoggerFactory f = JdkLoggerFactory.INSTANCE;
        f.newInstance(name).debug("Using java.util.logging as the default logging framework");
        return f;
    }

    public static InternalLoggerFactory getDefaultFactory() {
        if (defaultFactory == null) {
            defaultFactory = InternalLoggerFactory.newDefaultFactory(InternalLoggerFactory.class.getName());
        }
        return defaultFactory;
    }

    public static void setDefaultFactory(InternalLoggerFactory defaultFactory) {
        InternalLoggerFactory.defaultFactory = ObjectUtil.checkNotNull(defaultFactory, "defaultFactory");
    }

    public static InternalLogger getInstance(Class<?> clazz) {
        return InternalLoggerFactory.getInstance(clazz.getName());
    }

    public static InternalLogger getInstance(String name) {
        return InternalLoggerFactory.getDefaultFactory().newInstance(name);
    }

    protected abstract InternalLogger newInstance(String var1);
}

