/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ILogger;
import java.io.IOException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectWriter {
    public ObjectWriter beginArray() throws IOException;

    public ObjectWriter endArray() throws IOException;

    public ObjectWriter beginObject() throws IOException;

    public ObjectWriter endObject() throws IOException;

    public ObjectWriter name(@NotNull String var1) throws IOException;

    public ObjectWriter value(@Nullable String var1) throws IOException;

    public ObjectWriter jsonValue(@Nullable String var1) throws IOException;

    public ObjectWriter nullValue() throws IOException;

    public ObjectWriter value(boolean var1) throws IOException;

    public ObjectWriter value(@Nullable Boolean var1) throws IOException;

    public ObjectWriter value(double var1) throws IOException;

    public ObjectWriter value(long var1) throws IOException;

    public ObjectWriter value(@Nullable Number var1) throws IOException;

    public ObjectWriter value(@NotNull ILogger var1, @Nullable Object var2) throws IOException;

    public void setLenient(boolean var1);

    public void setIndent(@Nullable String var1);

    @Nullable
    public String getIndent();
}

