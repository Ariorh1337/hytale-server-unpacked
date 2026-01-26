/*
 * Decompiled with CFR 0.152.
 */
package io.sentry;

import io.sentry.IVersionDetector;

public final class NoopVersionDetector
implements IVersionDetector {
    private static final NoopVersionDetector instance = new NoopVersionDetector();

    private NoopVersionDetector() {
    }

    public static NoopVersionDetector getInstance() {
        return instance;
    }

    @Override
    public boolean checkForMixedVersions() {
        return false;
    }
}

