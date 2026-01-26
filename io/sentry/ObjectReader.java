/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.Closeable;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ObjectReader
extends Closeable {
    @Nullable
    public static Date dateOrNull(@Nullable String dateString, @NotNull ILogger logger) {
        if (dateString == null) {
            return null;
        }
        try {
            return DateUtils.getDateTime(dateString);
        }
        catch (Exception ignored) {
            try {
                return DateUtils.getDateTimeWithMillisPrecision(dateString);
            }
            catch (Exception e) {
                logger.log(SentryLevel.ERROR, "Error when deserializing millis timestamp format.", e);
                return null;
            }
        }
    }

    public void nextUnknown(ILogger var1, Map<String, Object> var2, String var3);

    @Nullable
    public <T> List<T> nextListOrNull(@NotNull ILogger var1, @NotNull JsonDeserializer<T> var2) throws IOException;

    @Nullable
    public <T> Map<String, T> nextMapOrNull(@NotNull ILogger var1, @NotNull JsonDeserializer<T> var2) throws IOException;

    @Nullable
    public <T> Map<String, List<T>> nextMapOfListOrNull(@NotNull ILogger var1, @NotNull JsonDeserializer<T> var2) throws IOException;

    @Nullable
    public <T> T nextOrNull(@NotNull ILogger var1, @NotNull JsonDeserializer<T> var2) throws Exception;

    @Nullable
    public Date nextDateOrNull(ILogger var1) throws IOException;

    @Nullable
    public TimeZone nextTimeZoneOrNull(ILogger var1) throws IOException;

    @Nullable
    public Object nextObjectOrNull() throws IOException;

    @NotNull
    public JsonToken peek() throws IOException;

    @NotNull
    public String nextName() throws IOException;

    public void beginObject() throws IOException;

    public void endObject() throws IOException;

    public void beginArray() throws IOException;

    public void endArray() throws IOException;

    public boolean hasNext() throws IOException;

    public int nextInt() throws IOException;

    @Nullable
    public Integer nextIntegerOrNull() throws IOException;

    public long nextLong() throws IOException;

    @Nullable
    public Long nextLongOrNull() throws IOException;

    public String nextString() throws IOException;

    @Nullable
    public String nextStringOrNull() throws IOException;

    public boolean nextBoolean() throws IOException;

    @Nullable
    public Boolean nextBooleanOrNull() throws IOException;

    public double nextDouble() throws IOException;

    @Nullable
    public Double nextDoubleOrNull() throws IOException;

    public float nextFloat() throws IOException;

    @Nullable
    public Float nextFloatOrNull() throws IOException;

    public void nextNull() throws IOException;

    public void setLenient(boolean var1);

    public void skipValue() throws IOException;
}

