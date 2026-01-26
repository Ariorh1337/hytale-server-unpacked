/*
 * Decompiled with CFR 0.152.
 */
package io.sentry;

public interface ISentryLifecycleToken
extends AutoCloseable {
    @Override
    public void close();
}

