/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.util;

import com.google.errorprone.annotations.CheckReturnValue;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

@CheckReturnValue
final class FastStackGetter {
    private final Object javaLangAccess;
    private final Method getElementMethod;
    private final Method getDepthMethod;

    @NullableDecl
    public static FastStackGetter createIfSupported() {
        try {
            Object javaLangAccess = Class.forName("sun.misc.SharedSecrets").getMethod("getJavaLangAccess", new Class[0]).invoke(null, new Object[0]);
            Method getElementMethod = Class.forName("sun.misc.JavaLangAccess").getMethod("getStackTraceElement", Throwable.class, Integer.TYPE);
            Method getDepthMethod = Class.forName("sun.misc.JavaLangAccess").getMethod("getStackTraceDepth", Throwable.class);
            StackTraceElement unusedElement = (StackTraceElement)getElementMethod.invoke(javaLangAccess, new Throwable(), 0);
            int unusedDepth = (Integer)getDepthMethod.invoke(javaLangAccess, new Throwable());
            return new FastStackGetter(javaLangAccess, getElementMethod, getDepthMethod);
        }
        catch (ThreadDeath t) {
            throw t;
        }
        catch (Throwable t) {
            return null;
        }
    }

    private FastStackGetter(Object javaLangAccess, Method getElementMethod, Method getDepthMethod) {
        this.javaLangAccess = javaLangAccess;
        this.getElementMethod = getElementMethod;
        this.getDepthMethod = getDepthMethod;
    }

    public StackTraceElement getStackTraceElement(Throwable throwable, int n) {
        try {
            return (StackTraceElement)this.getElementMethod.invoke(this.javaLangAccess, throwable, n);
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException)e.getCause();
            }
            if (e.getCause() instanceof Error) {
                throw (Error)e.getCause();
            }
            throw new RuntimeException(e.getCause());
        }
        catch (IllegalAccessException e) {
            throw new AssertionError((Object)e);
        }
    }

    public int getStackTraceDepth(Throwable throwable) {
        try {
            return (Integer)this.getDepthMethod.invoke(this.javaLangAccess, throwable);
        }
        catch (InvocationTargetException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException)e.getCause();
            }
            if (e.getCause() instanceof Error) {
                throw (Error)e.getCause();
            }
            throw new RuntimeException(e.getCause());
        }
        catch (IllegalAccessException e) {
            throw new AssertionError((Object)e);
        }
    }
}

