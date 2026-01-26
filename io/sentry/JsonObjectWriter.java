/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.ILogger;
import io.sentry.JsonObjectSerializer;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonWriter;
import java.io.IOException;
import java.io.Writer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class JsonObjectWriter
implements ObjectWriter {
    @NotNull
    private final JsonWriter jsonWriter;
    @NotNull
    private final JsonObjectSerializer jsonObjectSerializer;

    public JsonObjectWriter(@NotNull Writer out, int maxDepth) {
        this.jsonWriter = new JsonWriter(out);
        this.jsonObjectSerializer = new JsonObjectSerializer(maxDepth);
    }

    @Override
    public JsonObjectWriter beginArray() throws IOException {
        this.jsonWriter.beginArray();
        return this;
    }

    @Override
    public JsonObjectWriter endArray() throws IOException {
        this.jsonWriter.endArray();
        return this;
    }

    @Override
    public JsonObjectWriter beginObject() throws IOException {
        this.jsonWriter.beginObject();
        return this;
    }

    @Override
    public JsonObjectWriter endObject() throws IOException {
        this.jsonWriter.endObject();
        return this;
    }

    @Override
    public JsonObjectWriter name(@NotNull String name) throws IOException {
        this.jsonWriter.name(name);
        return this;
    }

    @Override
    public JsonObjectWriter value(@Nullable String value) throws IOException {
        this.jsonWriter.value(value);
        return this;
    }

    @Override
    public ObjectWriter jsonValue(@Nullable String value) throws IOException {
        this.jsonWriter.jsonValue(value);
        return this;
    }

    @Override
    public JsonObjectWriter nullValue() throws IOException {
        this.jsonWriter.nullValue();
        return this;
    }

    @Override
    public JsonObjectWriter value(boolean value) throws IOException {
        this.jsonWriter.value(value);
        return this;
    }

    @Override
    public JsonObjectWriter value(@Nullable Boolean value) throws IOException {
        this.jsonWriter.value(value);
        return this;
    }

    @Override
    public JsonObjectWriter value(double value) throws IOException {
        this.jsonWriter.value(value);
        return this;
    }

    @Override
    public JsonObjectWriter value(long value) throws IOException {
        this.jsonWriter.value(value);
        return this;
    }

    @Override
    public JsonObjectWriter value(@Nullable Number value) throws IOException {
        this.jsonWriter.value(value);
        return this;
    }

    @Override
    public JsonObjectWriter value(@NotNull ILogger logger, @Nullable Object object) throws IOException {
        this.jsonObjectSerializer.serialize(this, logger, object);
        return this;
    }

    @Override
    public void setLenient(boolean lenient) {
        this.jsonWriter.setLenient(lenient);
    }

    @Override
    public void setIndent(@Nullable String indent) {
        this.jsonWriter.setIndent(indent);
    }

    @Override
    @Nullable
    public String getIndent() {
        return this.jsonWriter.getIndent();
    }
}

