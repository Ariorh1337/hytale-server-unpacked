/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package io.sentry;

import io.sentry.IProfileConverter;
import io.sentry.protocol.profiling.SentryProfile;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;

public final class NoOpProfileConverter
implements IProfileConverter {
    private static final NoOpProfileConverter instance = new NoOpProfileConverter();

    private NoOpProfileConverter() {
    }

    public static NoOpProfileConverter getInstance() {
        return instance;
    }

    @Override
    @NotNull
    public SentryProfile convertFromFile(@NotNull String jfrFilePath) throws IOException {
        return new SentryProfile();
    }
}

