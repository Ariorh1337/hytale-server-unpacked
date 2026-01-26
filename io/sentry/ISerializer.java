/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.JsonDeserializer;
import io.sentry.SentryEnvelope;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ISerializer {
    @Nullable
    public <T, R> T deserializeCollection(@NotNull Reader var1, @NotNull Class<T> var2, @Nullable JsonDeserializer<R> var3);

    @Nullable
    public <T> T deserialize(@NotNull Reader var1, @NotNull Class<T> var2);

    @Nullable
    public SentryEnvelope deserializeEnvelope(@NotNull InputStream var1);

    public <T> void serialize(@NotNull T var1, @NotNull Writer var2) throws IOException;

    public void serialize(@NotNull SentryEnvelope var1, @NotNull OutputStream var2) throws Exception;

    @NotNull
    public String serialize(@NotNull Map<String, Object> var1) throws Exception;
}

