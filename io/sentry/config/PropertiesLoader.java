/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.config;

import java.util.Properties;
import org.jetbrains.annotations.Nullable;

interface PropertiesLoader {
    @Nullable
    public Properties load();
}

