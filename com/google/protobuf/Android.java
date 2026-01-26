/*
 * Decompiled with CFR 0.152.
 */
package com.google.protobuf;

final class Android {
    private static boolean ASSUME_ANDROID;
    static boolean assumeLiteRuntime;
    private static final Class<?> MEMORY_CLASS;
    private static final boolean IS_ROBOLECTRIC;

    private Android() {
    }

    static boolean isOnAndroidDevice() {
        return ASSUME_ANDROID || MEMORY_CLASS != null && !IS_ROBOLECTRIC;
    }

    static Class<?> getMemoryClass() {
        return MEMORY_CLASS;
    }

    private static <T> Class<T> getClassForName(String name) {
        try {
            return Class.forName(name);
        }
        catch (Throwable e) {
            return null;
        }
    }

    static {
        assumeLiteRuntime = false;
        MEMORY_CLASS = Android.getClassForName("libcore.io.Memory");
        IS_ROBOLECTRIC = !ASSUME_ANDROID && Android.getClassForName("org.robolectric.Robolectric") != null;
    }
}

