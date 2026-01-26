/*
 * Decompiled with CFR 0.152.
 */
package org.bson;

class BSON {
    static final byte B_GENERAL = 0;
    static final byte B_BINARY = 2;
    private static final int FLAG_GLOBAL = 256;
    private static final int[] FLAG_LOOKUP = new int[65535];

    BSON() {
    }

    static int regexFlags(String s) {
        int flags = 0;
        if (s == null) {
            return flags;
        }
        for (char f : s.toLowerCase().toCharArray()) {
            flags |= BSON.regexFlag(f);
        }
        return flags;
    }

    private static int regexFlag(char c) {
        int flag = FLAG_LOOKUP[c];
        if (flag == 0) {
            throw new IllegalArgumentException(String.format("Unrecognized flag [%c]", Character.valueOf(c)));
        }
        return flag;
    }

    static String regexFlags(int flags) {
        int processedFlags = flags;
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < FLAG_LOOKUP.length; ++i) {
            if ((processedFlags & FLAG_LOOKUP[i]) <= 0) continue;
            buf.append((char)i);
            processedFlags -= FLAG_LOOKUP[i];
        }
        if (processedFlags > 0) {
            throw new IllegalArgumentException("Some flags could not be recognized.");
        }
        return buf.toString();
    }

    static {
        BSON.FLAG_LOOKUP[103] = 256;
        BSON.FLAG_LOOKUP[105] = 2;
        BSON.FLAG_LOOKUP[109] = 8;
        BSON.FLAG_LOOKUP[115] = 32;
        BSON.FLAG_LOOKUP[99] = 128;
        BSON.FLAG_LOOKUP[120] = 4;
        BSON.FLAG_LOOKUP[100] = 1;
        BSON.FLAG_LOOKUP[116] = 16;
        BSON.FLAG_LOOKUP[117] = 64;
    }
}

