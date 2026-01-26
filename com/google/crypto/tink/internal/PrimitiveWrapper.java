/*
 * Decompiled with CFR 0.152.
 */
package com.google.crypto.tink.internal;

import com.google.crypto.tink.internal.KeysetHandleInterface;
import com.google.crypto.tink.internal.MonitoringAnnotations;
import java.security.GeneralSecurityException;

public interface PrimitiveWrapper<B, P> {
    public P wrap(KeysetHandleInterface var1, MonitoringAnnotations var2, PrimitiveFactory<B> var3) throws GeneralSecurityException;

    public Class<P> getPrimitiveClass();

    public Class<B> getInputPrimitiveClass();

    public static interface PrimitiveFactory<B> {
        public B create(KeysetHandleInterface.Entry var1) throws GeneralSecurityException;
    }
}

