/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util.network;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.network.NetworkBody;
import io.sentry.vendor.gson.stream.JsonReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class NetworkBodyParser {
    private NetworkBodyParser() {
    }

    @Nullable
    public static NetworkBody fromBytes(@Nullable byte[] bytes, @Nullable String contentType, @Nullable String charset, int maxSizeBytes, @NotNull ILogger logger) {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        if (contentType != null && NetworkBodyParser.isBinaryContentType(contentType)) {
            return new NetworkBody("[Binary data, " + bytes.length + " bytes, type: " + contentType + "]");
        }
        try {
            String effectiveCharset = charset != null ? charset : "UTF-8";
            int size = Math.min(bytes.length, maxSizeBytes);
            boolean isPartial = bytes.length > maxSizeBytes;
            String content = new String(bytes, 0, size, effectiveCharset);
            return NetworkBodyParser.parse(content, contentType, isPartial, logger);
        }
        catch (UnsupportedEncodingException e) {
            logger.log(SentryLevel.WARNING, "Failed to decode bytes: " + e.getMessage(), new Object[0]);
            return new NetworkBody("[Failed to decode bytes, " + bytes.length + " bytes]", Collections.singletonList(NetworkBody.NetworkBodyWarning.BODY_PARSE_ERROR));
        }
    }

    @Nullable
    private static NetworkBody parse(@Nullable String content, @Nullable String contentType, boolean isPartial, @Nullable ILogger logger) {
        if (content == null || content.isEmpty()) {
            return null;
        }
        if (contentType != null) {
            @NotNull String lowerContentType = contentType.toLowerCase(Locale.ROOT);
            if (lowerContentType.contains("application/x-www-form-urlencoded")) {
                return NetworkBodyParser.parseFormUrlEncoded(content, isPartial, logger);
            }
            if (lowerContentType.contains("application/json")) {
                return NetworkBodyParser.parseJson(content, isPartial, logger);
            }
        }
        List<NetworkBody.NetworkBodyWarning> warnings = isPartial ? Collections.singletonList(NetworkBody.NetworkBodyWarning.TEXT_TRUNCATED) : null;
        return new NetworkBody(content, warnings);
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @NotNull
    private static NetworkBody parseJson(@NotNull String content, boolean isPartial, @Nullable ILogger logger) {
        try (JsonReader reader = new JsonReader(new StringReader(content));){
            @NotNull SaferJsonParser.Result result = SaferJsonParser.parse(reader);
            @Nullable Object data = result.data;
            if (!(data != null || isPartial || result.errored || result.hitMaxDepth)) {
                NetworkBody networkBody = new NetworkBody(null);
                return networkBody;
            }
            @Nullable List<NetworkBody.NetworkBodyWarning> warnings = isPartial || result.hitMaxDepth ? Collections.singletonList(NetworkBody.NetworkBodyWarning.JSON_TRUNCATED) : (result.errored ? Collections.singletonList(NetworkBody.NetworkBodyWarning.INVALID_JSON) : null);
            NetworkBody networkBody = new NetworkBody(data, warnings);
            return networkBody;
        }
        catch (Exception e) {
            if (logger == null) return new NetworkBody(null, Collections.singletonList(NetworkBody.NetworkBodyWarning.INVALID_JSON));
            logger.log(SentryLevel.WARNING, "Failed to parse JSON: " + e.getMessage(), new Object[0]);
            return new NetworkBody(null, Collections.singletonList(NetworkBody.NetworkBodyWarning.INVALID_JSON));
        }
    }

    @NotNull
    private static NetworkBody parseFormUrlEncoded(@NotNull String content, boolean isPartial, @Nullable ILogger logger) {
        try {
            String[] pairs;
            HashMap<String, Object> params = new HashMap<String, Object>();
            for (String pair : pairs = content.split("&", -1)) {
                String value;
                int idx = pair.indexOf("=");
                if (idx <= 0) continue;
                String key = URLDecoder.decode(pair.substring(0, idx), "UTF-8");
                String string = value = idx < pair.length() - 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : "";
                if (params.containsKey(key)) {
                    ArrayList<String> list;
                    Object existing = params.get(key);
                    if (existing instanceof List) {
                        list = (ArrayList<String>)existing;
                        list.add(value);
                        continue;
                    }
                    list = new ArrayList<String>();
                    list.add((String)existing);
                    list.add(value);
                    params.put(key, list);
                    continue;
                }
                params.put(key, value);
            }
            List<NetworkBody.NetworkBodyWarning> warnings = isPartial ? Collections.singletonList(NetworkBody.NetworkBodyWarning.TEXT_TRUNCATED) : null;
            return new NetworkBody(params, warnings);
        }
        catch (UnsupportedEncodingException e) {
            if (logger != null) {
                logger.log(SentryLevel.WARNING, "Failed to parse form data: " + e.getMessage(), new Object[0]);
            }
            return new NetworkBody(null, Collections.singletonList(NetworkBody.NetworkBodyWarning.BODY_PARSE_ERROR));
        }
    }

    private static boolean isBinaryContentType(@NotNull String contentType) {
        @NotNull String lower = contentType.toLowerCase(Locale.ROOT);
        return lower.contains("image/") || lower.contains("video/") || lower.contains("audio/") || lower.contains("application/octet-stream") || lower.contains("application/pdf") || lower.contains("application/zip") || lower.contains("application/gzip");
    }

    private static class SaferJsonParser {
        private static final int MAX_DEPTH = 100;
        final Result result = new Result();

        private SaferJsonParser() {
        }

        @NotNull
        public static Result parse(@NotNull JsonReader reader) {
            SaferJsonParser parser = new SaferJsonParser();
            parser.result.data = parser.parse(reader, 0);
            return parser.result;
        }

        @Nullable
        private Object parse(@NotNull JsonReader reader, int currentDepth) {
            if (this.result.errored) {
                return null;
            }
            if (currentDepth >= 100) {
                this.result.hitMaxDepth = true;
                return null;
            }
            try {
                switch (reader.peek()) {
                    case BEGIN_OBJECT: {
                        @NotNull LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
                        try {
                            reader.beginObject();
                            while (reader.hasNext() && !this.result.errored) {
                                String name = reader.nextName();
                                map.put(name, this.parse(reader, currentDepth + 1));
                            }
                            reader.endObject();
                        }
                        catch (Exception e) {
                            this.result.errored = true;
                            return map;
                        }
                        return map;
                    }
                    case BEGIN_ARRAY: {
                        @NotNull ArrayList<Object> list = new ArrayList<Object>();
                        try {
                            reader.beginArray();
                            while (reader.hasNext() && !this.result.errored) {
                                list.add(this.parse(reader, currentDepth + 1));
                            }
                            reader.endArray();
                        }
                        catch (Exception e) {
                            this.result.errored = true;
                            return list;
                        }
                        return list;
                    }
                    case STRING: {
                        return reader.nextString();
                    }
                    case NUMBER: {
                        return reader.nextDouble();
                    }
                    case BOOLEAN: {
                        return reader.nextBoolean();
                    }
                    case NULL: {
                        reader.nextNull();
                        return null;
                    }
                }
                this.result.errored = true;
                return null;
            }
            catch (Exception ignored) {
                this.result.errored = true;
                return null;
            }
        }

        private static class Result {
            @Nullable
            private Object data;
            private boolean hitMaxDepth;
            private boolean errored;

            private Result() {
            }
        }
    }
}

