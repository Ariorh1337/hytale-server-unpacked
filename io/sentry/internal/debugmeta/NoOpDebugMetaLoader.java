/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.debugmeta;

import io.sentry.internal.debugmeta.IDebugMetaLoader;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class NoOpDebugMetaLoader
implements IDebugMetaLoader {
    private static final NoOpDebugMetaLoader instance = new NoOpDebugMetaLoader();

    public static NoOpDebugMetaLoader getInstance() {
        return instance;
    }

    private NoOpDebugMetaLoader() {
    }

    @Override
    @Nullable
    public List<Properties> loadDebugMeta() {
        return null;
    }
}

