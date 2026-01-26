/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.codec.util;

import it.unimi.dsi.fastutil.chars.CharArrayList;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import javax.annotation.Nullable;

public class Documentation {
    public static String stripMarkdown(@Nullable String markdown) {
        if (markdown == null) {
            return null;
        }
        StringBuilder output = new StringBuilder();
        IntArrayList counts = new IntArrayList();
        CharArrayList expectedChars = new CharArrayList();
        block3: for (int i = 0; i < markdown.length(); ++i) {
            char c = markdown.charAt(i);
            switch (c) {
                case '*': 
                case '_': {
                    int targetCount;
                    boolean isEnding;
                    int start = i;
                    boolean bl = isEnding = start >= 1 && !Character.isWhitespace(markdown.charAt(start - 1));
                    if (isEnding && (expectedChars.isEmpty() || expectedChars.getChar(expectedChars.size() - 1) != c)) {
                        output.append(markdown.charAt(i));
                        continue block3;
                    }
                    int n = targetCount = !counts.isEmpty() && isEnding ? counts.getInt(counts.size() - 1) : -1;
                    while (i < markdown.length() && markdown.charAt(i) == c && i - start != targetCount) {
                        ++i;
                    }
                    int matchingCount = i - start;
                    if (!counts.isEmpty() && counts.getInt(counts.size() - 1) == matchingCount) {
                        if (!isEnding) {
                            output.append(String.valueOf(c).repeat(matchingCount));
                            continue block3;
                        }
                        counts.removeInt(counts.size() - 1);
                        expectedChars.removeChar(expectedChars.size() - 1);
                    } else {
                        if (i < markdown.length() && Character.isWhitespace(markdown.charAt(i))) {
                            output.append(String.valueOf(c).repeat(matchingCount));
                            output.append(markdown.charAt(i));
                            continue block3;
                        }
                        counts.add(matchingCount);
                        expectedChars.add(c);
                    }
                    --i;
                    continue block3;
                }
                default: {
                    output.append(c);
                }
            }
        }
        if (!counts.isEmpty()) {
            throw new IllegalArgumentException("Unbalanced markdown formatting");
        }
        return output.toString();
    }
}

