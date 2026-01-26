/*
 * Decompiled with CFR 0.152.
 */
package org.bson.diagnostics;

public interface Logger {
    public String getName();

    default public boolean isTraceEnabled() {
        return false;
    }

    default public void trace(String msg) {
    }

    default public void trace(String msg, Throwable t) {
    }

    default public boolean isDebugEnabled() {
        return false;
    }

    default public void debug(String msg) {
    }

    default public void debug(String msg, Throwable t) {
    }

    default public boolean isInfoEnabled() {
        return false;
    }

    default public void info(String msg) {
    }

    default public void info(String msg, Throwable t) {
    }

    default public boolean isWarnEnabled() {
        return false;
    }

    default public void warn(String msg) {
    }

    default public void warn(String msg, Throwable t) {
    }

    default public boolean isErrorEnabled() {
        return false;
    }

    default public void error(String msg) {
    }

    default public void error(String msg, Throwable t) {
    }
}

