/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.ISerializer;
import io.sentry.JsonSerializable;
import io.sentry.SentryLevel;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerArray;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class JsonSerializationUtils {
    private static final Charset UTF_8 = Charset.forName("UTF-8");

    @NotNull
    public static Map<String, Object> calendarToMap(@NotNull Calendar calendar) {
        @NotNull HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("year", calendar.get(1));
        map.put("month", calendar.get(2));
        map.put("dayOfMonth", calendar.get(5));
        map.put("hourOfDay", calendar.get(11));
        map.put("minute", calendar.get(12));
        map.put("second", calendar.get(13));
        return map;
    }

    @NotNull
    public static List<Object> atomicIntegerArrayToList(@NotNull AtomicIntegerArray array) {
        int numberOfItems = array.length();
        @NotNull ArrayList<Object> list = new ArrayList<Object>(numberOfItems);
        for (int i = 0; i < numberOfItems; ++i) {
            list.add(array.get(i));
        }
        return list;
    }

    /*
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public static byte[] bytesFrom(@NotNull ISerializer serializer, @NotNull ILogger logger, @NotNull JsonSerializable serializable) {
        try (ByteArrayOutputStream stream = new ByteArrayOutputStream();){
            byte[] byArray;
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter((OutputStream)stream, UTF_8));){
                serializer.serialize(serializable, writer);
                byArray = stream.toByteArray();
            }
            return byArray;
        }
        catch (Throwable t) {
            logger.log(SentryLevel.ERROR, "Could not serialize serializable", t);
            return null;
        }
    }

    public static long byteSizeOf(@NotNull ISerializer serializer, @NotNull ILogger logger, @Nullable JsonSerializable serializable) {
        if (serializable == null) {
            return 0L;
        }
        try {
            ByteCountingWriter writer = new ByteCountingWriter();
            serializer.serialize(serializable, writer);
            return writer.getByteCount();
        }
        catch (Throwable t) {
            logger.log(SentryLevel.ERROR, "Could not calculate size of serializable", t);
            return 0L;
        }
    }

    private static final class ByteCountingWriter
    extends Writer {
        private long byteCount = 0L;

        private ByteCountingWriter() {
        }

        @Override
        public void write(char[] cbuf, int off, int len) {
            for (int i = off; i < off + len; ++i) {
                this.byteCount += (long)ByteCountingWriter.utf8ByteCount(cbuf[i]);
            }
        }

        @Override
        public void write(int c) {
            this.byteCount += (long)ByteCountingWriter.utf8ByteCount((char)c);
        }

        @Override
        public void write(@NotNull String str, int off, int len) {
            for (int i = off; i < off + len; ++i) {
                this.byteCount += (long)ByteCountingWriter.utf8ByteCount(str.charAt(i));
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() {
        }

        public long getByteCount() {
            return this.byteCount;
        }

        private static int utf8ByteCount(char c) {
            if (c <= '\u007f') {
                return 1;
            }
            if (c <= '\u07ff') {
                return 2;
            }
            if (Character.isSurrogate(c)) {
                return 2;
            }
            return 3;
        }
    }
}

