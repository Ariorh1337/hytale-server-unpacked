/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.common.util;

import javax.annotation.Nonnull;

public class SystemUtil {
    public static final SystemType TYPE = SystemUtil.getSystemType();

    @Nonnull
    private static SystemType getSystemType() {
        String osName = System.getProperty("os.name");
        if (osName.startsWith("Windows")) {
            return SystemType.WINDOWS;
        }
        if (osName.startsWith("Mac OS X")) {
            return SystemType.MACOS;
        }
        if (osName.startsWith("Linux")) {
            return SystemType.LINUX;
        }
        if (osName.startsWith("LINUX")) {
            return SystemType.LINUX;
        }
        return SystemType.OTHER;
    }

    public static boolean getEnvBoolean(@Nonnull String name) {
        String value = System.getenv(name);
        if (value == null) {
            return false;
        }
        return !"false".equalsIgnoreCase(value) && !"0".equals(value) && !"no".equalsIgnoreCase(value);
    }

    public static enum SystemType {
        WINDOWS,
        MACOS,
        LINUX,
        OTHER;

    }
}

