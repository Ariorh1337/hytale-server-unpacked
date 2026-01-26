/*
 * Decompiled with CFR 0.152.
 */
package org.bouncycastle.util.test;

import org.bouncycastle.util.Strings;
import org.bouncycastle.util.test.Test;
import org.bouncycastle.util.test.TestResult;

public class SimpleTestResult
implements TestResult {
    private static final String SEPARATOR = Strings.lineSeparator();
    private boolean success;
    private String message;
    private Throwable exception;

    public SimpleTestResult(boolean bl, String string) {
        this.success = bl;
        this.message = string;
    }

    public SimpleTestResult(boolean bl, String string, Throwable throwable) {
        this.success = bl;
        this.message = string;
        this.exception = throwable;
    }

    public static TestResult successful(Test test, String string) {
        return new SimpleTestResult(true, test.getName() + ": " + string);
    }

    public static TestResult failed(Test test, String string) {
        return new SimpleTestResult(false, test.getName() + ": " + string);
    }

    public static TestResult failed(Test test, String string, Throwable throwable) {
        return new SimpleTestResult(false, test.getName() + ": " + string, throwable);
    }

    public static TestResult failed(Test test, String string, Object object, Object object2) {
        return SimpleTestResult.failed(test, string + SEPARATOR + "Expected: " + object + SEPARATOR + "Found   : " + object2);
    }

    public static String failedMessage(String string, String string2, String string3, String string4) {
        StringBuilder stringBuilder = new StringBuilder(string);
        stringBuilder.append(" failing ").append(string2);
        stringBuilder.append(SEPARATOR).append("    expected: ").append(string3);
        stringBuilder.append(SEPARATOR).append("    got     : ").append(string4);
        return stringBuilder.toString();
    }

    @Override
    public boolean isSuccessful() {
        return this.success;
    }

    @Override
    public String toString() {
        return this.message;
    }

    @Override
    public Throwable getException() {
        return this.exception;
    }
}

