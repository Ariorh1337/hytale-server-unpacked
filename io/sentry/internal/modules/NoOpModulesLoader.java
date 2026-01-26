/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.internal.modules;

import io.sentry.internal.modules.IModulesLoader;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class NoOpModulesLoader
implements IModulesLoader {
    private static final NoOpModulesLoader instance = new NoOpModulesLoader();

    public static NoOpModulesLoader getInstance() {
        return instance;
    }

    private NoOpModulesLoader() {
    }

    @Override
    @Nullable
    public Map<String, String> getOrLoadModules() {
        return null;
    }
}

