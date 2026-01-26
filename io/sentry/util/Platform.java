/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package io.sentry.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

@ApiStatus.Internal
public final class Platform {
    static boolean isAndroid;
    static boolean isJavaNinePlus;

    public static boolean isAndroid() {
        return isAndroid;
    }

    public static boolean isJvm() {
        return !isAndroid;
    }

    public static boolean isJavaNinePlus() {
        return isJavaNinePlus;
    }

    static {
        try {
            isAndroid = "The Android Project".equals(System.getProperty("java.vendor"));
        }
        catch (Throwable e) {
            isAndroid = false;
        }
        try {
            double javaVersion;
            @Nullable String javaStringVersion = System.getProperty("java.specification.version");
            isJavaNinePlus = javaStringVersion != null ? (javaVersion = Double.valueOf(javaStringVersion).doubleValue()) >= 9.0 : false;
        }
        catch (Throwable e) {
            isJavaNinePlus = false;
        }
    }
}

