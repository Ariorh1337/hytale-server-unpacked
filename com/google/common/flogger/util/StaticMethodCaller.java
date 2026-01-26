/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.util;

import com.google.common.flogger.util.Checks;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public final class StaticMethodCaller {
    @NullableDecl
    public static <T> T getInstanceFromSystemProperty(String propertyName, @NullableDecl String defaultClassName, Class<T> type) {
        String className = StaticMethodCaller.readProperty(propertyName, defaultClassName);
        if (className == null) {
            return null;
        }
        return StaticMethodCaller.callStaticMethod(className, "getInstance", type);
    }

    @NullableDecl
    public static <T> T callGetterFromSystemProperty(String propertyName, @NullableDecl String defaultValue, Class<T> type) {
        String getter = StaticMethodCaller.readProperty(propertyName, defaultValue);
        if (getter == null) {
            return null;
        }
        int idx = getter.indexOf(35);
        if (idx <= 0 || idx == getter.length() - 1) {
            StaticMethodCaller.error("invalid getter (expected <class>#<method>): %s\n", getter);
            return null;
        }
        return StaticMethodCaller.callStaticMethod(getter.substring(0, idx), getter.substring(idx + 1), type);
    }

    @NullableDecl
    public static <T> T callGetterFromSystemProperty(String propertyName, Class<T> type) {
        return StaticMethodCaller.callGetterFromSystemProperty(propertyName, null, type);
    }

    private static String readProperty(String propertyName, @NullableDecl String defaultValue) {
        Checks.checkNotNull(propertyName, "property name");
        try {
            return System.getProperty(propertyName, defaultValue);
        }
        catch (SecurityException e) {
            StaticMethodCaller.error("cannot read property name %s: %s", propertyName, e);
            return null;
        }
    }

    private static <T> T callStaticMethod(String className, String methodName, Class<T> type) {
        try {
            return type.cast(Class.forName(className).getMethod(methodName, new Class[0]).invoke(null, new Object[0]));
        }
        catch (ClassNotFoundException classNotFoundException) {
        }
        catch (ClassCastException e) {
            StaticMethodCaller.error("cannot cast result of calling '%s#%s' to '%s': %s\n", className, methodName, type.getName(), e);
        }
        catch (Exception e) {
            StaticMethodCaller.error("cannot call expected no-argument static method '%s#%s': %s\n", className, methodName, e);
        }
        return null;
    }

    private static void error(String msg, Object ... args) {
        System.err.println(StaticMethodCaller.class + ": " + String.format(msg, args));
    }

    private StaticMethodCaller() {
    }
}

