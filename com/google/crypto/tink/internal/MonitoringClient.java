/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.annotations.Alpha;
import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.MonitoringAnnotations;

@Alpha
public interface MonitoringClient {
    public Logger createLogger(KeysetHandleInterface var1, MonitoringAnnotations var2, String var3, String var4);

    public static interface Logger {
        default public void log(int keyId, long numBytesAsInput) {
        }

        default public void logFailure() {
        }

        default public void logKeyExport(int keyId) {
        }
    }
}

