/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import com.hypixel.hytale.common.util.StringCompareUtil;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.DoubleFunction;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.regex.Pattern;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class StringUtil {
    public static final Pattern RAW_ARGS_PATTERN = Pattern.compile(" -- ");
    @Nonnull
    private static final char[] GRAPH_CHARS = new char[]{'_', '\u2584', '\u2500', '\u2580', '\u00af'};

    public static boolean isNumericString(@Nonnull String str) {
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c >= '0' && c <= '9') continue;
            return false;
        }
        return true;
    }

    public static boolean isAlphaNumericHyphenString(@Nonnull String str) {
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c >= '0' && c <= 'z' && (c <= '9' || c >= 'A') && (c <= 'Z' || c >= 'a') || c == '-') continue;
            return false;
        }
        return true;
    }

    public static boolean isAlphaNumericHyphenUnderscoreString(@Nonnull String str) {
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            if (c >= '0' && c <= 'z' && (c <= '9' || c >= 'A') && (c <= 'Z' || c >= 'a') || c == '-' || c == '_') continue;
            return false;
        }
        return true;
    }

    public static boolean isCapitalized(@Nonnull String keyStr, char delim) {
        boolean wasDelimOrFirst = true;
        for (int i = 0; i < keyStr.length(); ++i) {
            char c = keyStr.charAt(i);
            if (wasDelimOrFirst && c != Character.toUpperCase(c)) {
                return false;
            }
            wasDelimOrFirst = c == delim;
        }
        return true;
    }

    @Nonnull
    public static String capitalize(@Nonnull String keyStr, char delim) {
        boolean wasDelimOrFirst = true;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < keyStr.length(); ++i) {
            char c = keyStr.charAt(i);
            sb.append(wasDelimOrFirst ? Character.toUpperCase(c) : c);
            wasDelimOrFirst = c == delim;
        }
        return sb.toString();
    }

    @Nullable
    public static <V extends Enum<V>> V parseEnum(@Nonnull V[] enumConstants, String str) {
        return (V)StringUtil.parseEnum(enumConstants, (String)str, (MatchType)MatchType.EQUALS);
    }

    @Nullable
    public static <V extends Enum<V>> V parseEnum(@Nonnull V[] enumConstants, String str, MatchType matchType) {
        if (matchType == MatchType.EQUALS) {
            for (V enumValue : enumConstants) {
                if (!((Enum)enumValue).name().equals(str)) continue;
                return enumValue;
            }
        } else if (matchType == MatchType.CASE_INSENSITIVE) {
            for (V enumValue : enumConstants) {
                if (!((Enum)enumValue).name().equalsIgnoreCase(str)) continue;
                return enumValue;
            }
        } else {
            str = str.toLowerCase();
            V closest = null;
            int diff = -2;
            for (V enumValue : enumConstants) {
                int index = StringCompareUtil.indexOfDifference(str, ((Enum)enumValue).name().toLowerCase());
                if ((index <= diff || diff == -1) && index != -1 && diff != -2) continue;
                closest = enumValue;
                diff = index;
            }
            if (diff > -2) {
                return closest;
            }
        }
        return null;
    }

    @Nonnull
    @Deprecated(forRemoval=true)
    public static String[] parseArgs(String rawString, @Nonnull Map<String, String> argOptions) {
        String[] rawSplit = RAW_ARGS_PATTERN.split(rawString, 2);
        Object argsStr = rawSplit[0];
        boolean hasRaw = rawSplit.length > 1;
        int quote = 0;
        int start = 0;
        ObjectArrayList argsList = new ObjectArrayList();
        block17: for (int i = 0; i < ((String)argsStr).length(); ++i) {
            char c = ((String)argsStr).charAt(i);
            block0 : switch (c) {
                case '\\': {
                    if (i + 1 >= ((String)argsStr).length()) {
                        throw new IllegalStateException("Invalid escape at end of string, index " + (i + 1) + " in: " + rawString);
                    }
                    char c1 = ((String)argsStr).charAt(i + 1);
                    switch (c1) {
                        case '\"': 
                        case '\'': 
                        case '\\': {
                            argsStr = ((String)argsStr).substring(0, i) + ((String)argsStr).substring(i + 1);
                            ++i;
                            continue block17;
                        }
                    }
                    throw new IllegalStateException("Invalid escape for char " + c1 + " at index " + (i + 1) + " in: " + rawString);
                }
                case ' ': {
                    if (quote != 0) continue block17;
                    if (start != i) {
                        argsList.add(((String)argsStr).substring(start, i));
                    }
                    start = i + 1;
                    continue block17;
                }
                case '\"': {
                    switch (quote) {
                        case 0: {
                            quote = 34;
                            break;
                        }
                        case 34: {
                            quote = 0;
                            argsList.add(((String)argsStr).substring(start, i + 1));
                            start = i + 1;
                        }
                    }
                    continue block17;
                }
                case '\'': {
                    switch (quote) {
                        case 0: {
                            quote = 39;
                            break block0;
                        }
                        case 39: {
                            quote = 0;
                            argsList.add(((String)argsStr).substring(start, i + 1));
                            start = i + 1;
                        }
                    }
                }
            }
        }
        if (quote != 0) {
            throw new IllegalStateException("Unbalanced quotes!");
        }
        if (start != ((String)argsStr).length()) {
            argsList.add(((String)argsStr).substring(start));
        }
        argsList.removeIf(arg -> {
            if (arg.startsWith("--")) {
                String[] split = arg.substring(2).split("=", 2);
                String value = "";
                if (split.length > 1) {
                    value = split[1];
                    value = StringUtil.removeQuotes(value);
                }
                argOptions.put(split[0], value);
                return true;
            }
            return false;
        });
        argsList.replaceAll(value -> StringUtil.removeQuotes(value.trim()));
        if (hasRaw) {
            String[] strings = new String[argsList.size() + 1];
            argsList.toArray(strings);
            strings[argsList.size()] = rawSplit[1].trim();
            return strings;
        }
        return (String[])argsList.toArray(String[]::new);
    }

    @Nonnull
    public static String[] parseArgs(String rawString) {
        String[] rawSplit = RAW_ARGS_PATTERN.split(rawString, 2);
        Object argsStr = rawSplit[0];
        boolean hasRaw = rawSplit.length > 1;
        int quote = 0;
        int start = 0;
        ObjectArrayList argsList = new ObjectArrayList();
        block17: for (int i = 0; i < ((String)argsStr).length(); ++i) {
            char c = ((String)argsStr).charAt(i);
            block0 : switch (c) {
                case '\\': {
                    if (i + 1 >= ((String)argsStr).length()) {
                        throw new IllegalStateException("Invalid escape at end of string, index " + (i + 1) + " in: " + rawString);
                    }
                    char c1 = ((String)argsStr).charAt(i + 1);
                    switch (c1) {
                        case '\"': 
                        case '\'': 
                        case '\\': {
                            argsStr = ((String)argsStr).substring(0, i) + ((String)argsStr).substring(i + 1);
                            ++i;
                            continue block17;
                        }
                    }
                    throw new IllegalStateException("Invalid escape for char " + c1 + " at index " + (i + 1) + " in: " + rawString);
                }
                case ' ': {
                    if (quote != 0) continue block17;
                    if (start != i) {
                        argsList.add(((String)argsStr).substring(start, i));
                    }
                    start = i + 1;
                    continue block17;
                }
                case '\"': {
                    switch (quote) {
                        case 0: {
                            quote = 34;
                            break;
                        }
                        case 34: {
                            quote = 0;
                            argsList.add(((String)argsStr).substring(start, i + 1));
                            start = i + 1;
                        }
                    }
                    continue block17;
                }
                case '\'': {
                    switch (quote) {
                        case 0: {
                            quote = 39;
                            break block0;
                        }
                        case 39: {
                            quote = 0;
                            argsList.add(((String)argsStr).substring(start, i + 1));
                            start = i + 1;
                        }
                    }
                }
            }
        }
        if (quote != 0) {
            throw new IllegalStateException("Unbalanced quotes!");
        }
        if (start != ((String)argsStr).length()) {
            argsList.add(((String)argsStr).substring(start));
        }
        argsList.replaceAll(value -> StringUtil.removeQuotes(value.trim()));
        if (hasRaw) {
            String[] strings = new String[argsList.size() + 1];
            argsList.toArray(strings);
            strings[argsList.size()] = rawSplit[1].trim();
            return strings;
        }
        return (String[])argsList.toArray(String[]::new);
    }

    @Nonnull
    public static String removeQuotes(@Nonnull String value) {
        switch (value.charAt(0)) {
            case '\"': 
            case '\'': {
                value = value.substring(1, value.length() - 1);
            }
        }
        return value;
    }

    @Nonnull
    public static String stripQuotes(@Nonnull String s) {
        if (s.length() >= 2) {
            char first = s.charAt(0);
            char last = s.charAt(s.length() - 1);
            if (first == '\"' && last == '\"' || first == '\'' && last == '\'') {
                return s.substring(1, s.length() - 1);
            }
        }
        return s;
    }

    public static boolean isGlobMatching(@Nonnull String pattern, @Nonnull String text) {
        return pattern.equals(text) || StringUtil.isGlobMatching(pattern, 0, text, 0);
    }

    public static boolean isGlobMatching(@Nonnull String pattern, int patternPos, @Nonnull String text, int textPos) {
        while (patternPos < pattern.length()) {
            char charAt = pattern.charAt(patternPos);
            if (charAt == '*') {
                ++patternPos;
                while (patternPos < pattern.length() && pattern.charAt(patternPos) == '*') {
                    ++patternPos;
                }
                if (patternPos == pattern.length()) {
                    return true;
                }
                char matchChar = pattern.charAt(patternPos);
                while (textPos < text.length()) {
                    if (matchChar == text.charAt(textPos) && StringUtil.isGlobMatching(pattern, patternPos + 1, text, textPos + 1)) {
                        return true;
                    }
                    ++textPos;
                }
                return false;
            }
            if (textPos == text.length()) {
                return false;
            }
            if (charAt != '?' && charAt != text.charAt(textPos)) {
                return false;
            }
            ++patternPos;
            ++textPos;
        }
        return textPos == text.length();
    }

    public static boolean isGlobPattern(@Nonnull String text) {
        for (int i = 0; i < text.length(); ++i) {
            char c = text.charAt(i);
            if (c != '?' && c != '*') continue;
            return true;
        }
        return false;
    }

    @Nonnull
    public static String humanizeTime(@Nonnull Duration duration, boolean useSeconds) {
        long length = duration.toMillis();
        long days = length / 86400000L;
        long hours = (length - days * 86400000L) / 3600000L;
        long minutes = (length - (days * 86400000L + hours * 3600000L)) / 60000L;
        String base = days + "d " + hours + "h " + minutes + "m";
        if (useSeconds) {
            long seconds = (length - (days * 86400000L + hours * 3600000L + minutes * 60000L)) / 1000L;
            base = base + " " + seconds + "s";
        }
        return base;
    }

    @Nonnull
    public static String humanizeTime(@Nonnull Duration length) {
        return StringUtil.humanizeTime(length, false);
    }

    @Nonnull
    public static <T> List<T> sortByFuzzyDistance(@Nonnull String str, @Nonnull Collection<T> collection, int length) {
        List<T> list = StringUtil.sortByFuzzyDistance(str, collection);
        return list.size() > length ? list.subList(0, length) : list;
    }

    @Nonnull
    public static <T> List<T> sortByFuzzyDistance(@Nonnull String str, @Nonnull Collection<T> collection) {
        Object2IntOpenHashMap<T> map = new Object2IntOpenHashMap<T>(collection.size());
        for (T value : collection) {
            map.put(value, StringCompareUtil.getFuzzyDistance(value.toString(), str, Locale.ENGLISH));
        }
        ObjectArrayList<T> list = new ObjectArrayList<T>(collection);
        list.sort(Comparator.comparingInt(map::getInt).reversed());
        return list;
    }

    @Nonnull
    public static String toPaddedBinaryString(int val) {
        byte[] buf = new byte[]{48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48, 48};
        int leadingZeros = Integer.numberOfLeadingZeros(val);
        int mag = 32 - leadingZeros;
        int pos = Math.max(mag, 1);
        do {
            buf[leadingZeros + --pos] = (byte)(48 + (val & 1));
            val >>>= 1;
        } while (pos > 0);
        return new String(buf, 0);
    }

    @Nonnull
    public static String trimEnd(@Nonnull String str, @Nonnull String end) {
        if (!str.endsWith(end)) {
            return str;
        }
        return str.substring(0, str.length() - end.length());
    }

    public static void generateGraph(@Nonnull StringBuilder sb, int width, int height, long minX, long maxX, double minY, double maxY, @Nonnull DoubleFunction<String> labelFormatFunc, int historyLength, @Nonnull IntToLongFunction timestampFunc, @Nonnull IntToDoubleFunction valueFunc) {
        double lengthY = maxY - minY;
        long lengthX = maxX - minX;
        double rowAggLength = lengthY / (double)height;
        double colAggLength = (double)lengthX / (double)width;
        double[] values = new double[width];
        Arrays.fill(values, -1.0);
        int historyIndex = 0;
        for (int i = 0; i < width; ++i) {
            double total = 0.0;
            int count = 0;
            long nextAggTimestamp = maxX - (lengthX - (long)(colAggLength * (double)i));
            while (historyIndex < historyLength && timestampFunc.applyAsLong(historyIndex) < nextAggTimestamp) {
                total += valueFunc.applyAsDouble(historyIndex);
                ++count;
                ++historyIndex;
            }
            if (count != 0) {
                values[i] = total / (double)count;
                continue;
            }
            if (i <= 0) continue;
            values[i] = values[i - 1];
        }
        double last = -1.0;
        for (int i = values.length - 1; i >= 0; --i) {
            if (values[i] != -1.0) {
                last = values[i];
                continue;
            }
            if (last == -1.0) continue;
            values[i] = last;
        }
        int yLabelWidth = 0;
        String[] labels = new String[height];
        for (int row = 0; row < height; ++row) {
            String label;
            double rowMaxValue = minY + lengthY - rowAggLength * (double)row;
            labels[row] = label = labelFormatFunc.apply(rowMaxValue);
            int length = label.length();
            if (length <= yLabelWidth) continue;
            yLabelWidth = length;
        }
        String bar = " ".repeat(yLabelWidth) + " " + "#".repeat(width + 2);
        sb.append(bar).append('\n');
        for (int row = 0; row < height; ++row) {
            sb.append(" ".repeat(Math.max(0, yLabelWidth - labels[row].length()))).append(labels[row]).append(" #");
            double rowMinValue = minY + lengthY - rowAggLength * (double)(row + 1);
            for (int col = 0; col < width; ++col) {
                double colRowValue = values[col] - rowMinValue;
                if (colRowValue <= 0.0 || colRowValue > rowAggLength) {
                    sb.append(' ');
                    continue;
                }
                double valuePercent = colRowValue / rowAggLength;
                int charIndex = (int)Math.round(valuePercent * (double)(GRAPH_CHARS.length - 1));
                sb.append(GRAPH_CHARS[Math.max(0, charIndex)]);
            }
            sb.append("#\n");
        }
        sb.append(bar).append('\n');
        sb.append('\n');
    }

    public static enum MatchType {
        INDEX_DIFFERENCE,
        EQUALS,
        CASE_INSENSITIVE;

    }
}

