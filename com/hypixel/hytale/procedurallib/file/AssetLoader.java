/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.procedurallib.file;

import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nonnull;

public interface AssetLoader<T> {
    public Class<T> type();

    @Nonnull
    public T load(@Nonnull InputStream var1) throws IOException;
}

