/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

public final class WildcardMatch {
    private WildcardMatch() {
    }

    public static boolean test(String text, String pattern) {
        return WildcardMatch.test(text, pattern, false);
    }

    public static boolean test(String text, String pattern, boolean ignoreCase) {
        if (ignoreCase) {
            text = text.toLowerCase();
            pattern = pattern.toLowerCase();
        }
        if (text.equals(pattern)) {
            return true;
        }
        int t = 0;
        int p = 0;
        int starIdx = -1;
        int match = 0;
        while (t < text.length()) {
            if (p < pattern.length() && (pattern.charAt(p) == '?' || pattern.charAt(p) == text.charAt(t))) {
                ++t;
                ++p;
                continue;
            }
            if (p < pattern.length() && pattern.charAt(p) == '*') {
                starIdx = p++;
                match = t;
                continue;
            }
            if (starIdx != -1) {
                p = starIdx + 1;
                t = ++match;
                continue;
            }
            return false;
        }
        while (p < pattern.length() && pattern.charAt(p) == '*') {
            ++p;
        }
        return p == pattern.length();
    }
}

