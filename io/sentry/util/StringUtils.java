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
import io.sentry.SentryLevel;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.StringCharacterIterator;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Pattern;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class StringUtils {
    private static final Charset UTF_8 = Charset.forName("UTF-8");
    public static final String PROPER_NIL_UUID = "00000000-0000-0000-0000-000000000000";
    private static final String CORRUPTED_NIL_UUID = "0000-0000";
    @NotNull
    private static final Pattern PATTERN_WORD_SNAKE_CASE = Pattern.compile("[\\W_]+");

    private StringUtils() {
    }

    @Nullable
    public static String getStringAfterDot(@Nullable String str) {
        if (str != null) {
            int lastDotIndex = str.lastIndexOf(".");
            if (lastDotIndex >= 0 && str.length() > lastDotIndex + 1) {
                return str.substring(lastDotIndex + 1);
            }
            return str;
        }
        return null;
    }

    @Nullable
    public static String capitalize(@Nullable String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase(Locale.ROOT) + str.substring(1).toLowerCase(Locale.ROOT);
    }

    @Nullable
    public static String camelCase(@Nullable String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        String[] words = PATTERN_WORD_SNAKE_CASE.split(str, -1);
        StringBuilder builder = new StringBuilder();
        for (String w : words) {
            builder.append(StringUtils.capitalize(w));
        }
        return builder.toString();
    }

    @Nullable
    public static String removeSurrounding(@Nullable String str, @Nullable String delimiter) {
        if (str != null && delimiter != null && str.startsWith(delimiter) && str.endsWith(delimiter)) {
            return str.substring(delimiter.length(), str.length() - delimiter.length());
        }
        return str;
    }

    @NotNull
    public static String byteCountToString(long bytes) {
        if (-1000L < bytes && bytes < 1000L) {
            return bytes + " B";
        }
        StringCharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999950L || bytes >= 999950L) {
            bytes /= 1000L;
            ci.next();
        }
        return String.format(Locale.ROOT, "%.1f %cB", (double)bytes / 1000.0, Character.valueOf(ci.current()));
    }

    @Nullable
    public static String calculateStringHash(@Nullable String str, @NotNull ILogger logger) {
        if (str == null || str.isEmpty()) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] messageDigest = md.digest(str.getBytes(UTF_8));
            BigInteger no = new BigInteger(1, messageDigest);
            StringBuilder stringBuilder = new StringBuilder(no.toString(16));
            return stringBuilder.toString();
        }
        catch (NoSuchAlgorithmException e) {
            logger.log(SentryLevel.INFO, "SHA-1 isn't available to calculate the hash.", e);
        }
        catch (Throwable e) {
            logger.log(SentryLevel.INFO, "string: %s could not calculate its hash", e, str);
        }
        return null;
    }

    public static int countOf(@NotNull String str, char character) {
        int count = 0;
        for (int i = 0; i < str.length(); ++i) {
            if (str.charAt(i) != character) continue;
            ++count;
        }
        return count;
    }

    public static String normalizeUUID(@NotNull String uuidString) {
        if (uuidString.equals(CORRUPTED_NIL_UUID)) {
            return PROPER_NIL_UUID;
        }
        return uuidString;
    }

    public static String join(@NotNull CharSequence delimiter, @NotNull Iterable<? extends CharSequence> elements) {
        @NotNull StringBuilder stringBuilder = new StringBuilder();
        @NotNull Iterator<? extends CharSequence> iterator = elements.iterator();
        if (iterator.hasNext()) {
            stringBuilder.append(iterator.next());
            while (iterator.hasNext()) {
                stringBuilder.append(delimiter);
                stringBuilder.append(iterator.next());
            }
        }
        return stringBuilder.toString();
    }

    @Nullable
    public static String toString(@Nullable Object object) {
        if (object == null) {
            return null;
        }
        return object.toString();
    }

    @NotNull
    public static String removePrefix(@Nullable String string, @NotNull String prefix) {
        if (string == null) {
            return "";
        }
        int index = string.indexOf(prefix);
        if (index == 0) {
            return string.substring(prefix.length());
        }
        return string;
    }

    @NotNull
    public static String substringBefore(@Nullable String string, @NotNull String separator) {
        if (string == null) {
            return "";
        }
        int index = string.indexOf(separator);
        if (index >= 0) {
            return string.substring(0, index);
        }
        return string;
    }
}

