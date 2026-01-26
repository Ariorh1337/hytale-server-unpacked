/*
 * Decompiled with CFR 0.152.
 */
package org.jline.reader.impl;

import org.jline.reader.LineReader;
import org.jline.utils.Levenshtein;

public class ReaderUtils {
    private ReaderUtils() {
    }

    public static boolean isSet(LineReader reader, LineReader.Option option) {
        return reader != null && reader.isSet(option);
    }

    public static String getString(LineReader reader, String name, String def) {
        Object v = reader != null ? reader.getVariable(name) : null;
        return v != null ? v.toString() : def;
    }

    public static boolean getBoolean(LineReader reader, String name, boolean def) {
        Object v;
        Object object = v = reader != null ? reader.getVariable(name) : null;
        if (v instanceof Boolean) {
            return (Boolean)v;
        }
        if (v != null) {
            String s = v.toString();
            return s.isEmpty() || s.equalsIgnoreCase("on") || s.equalsIgnoreCase("1") || s.equalsIgnoreCase("true");
        }
        return def;
    }

    public static int getInt(LineReader reader, String name, int def) {
        Object v;
        int nb = def;
        Object object = v = reader != null ? reader.getVariable(name) : null;
        if (v instanceof Number) {
            return ((Number)v).intValue();
        }
        if (v != null) {
            nb = 0;
            try {
                nb = Integer.parseInt(v.toString());
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return nb;
    }

    public static long getLong(LineReader reader, String name, long def) {
        Object v;
        long nb = def;
        Object object = v = reader != null ? reader.getVariable(name) : null;
        if (v instanceof Number) {
            return ((Number)v).longValue();
        }
        if (v != null) {
            nb = 0L;
            try {
                nb = Long.parseLong(v.toString());
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return nb;
    }

    public static int distance(String word, String cand) {
        if (word.length() < cand.length()) {
            int d1 = Levenshtein.distance(word, cand.substring(0, Math.min(cand.length(), word.length())));
            int d2 = Levenshtein.distance(word, cand);
            return Math.min(d1, d2);
        }
        return Levenshtein.distance(word, cand);
    }
}

