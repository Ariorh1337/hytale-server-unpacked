/*
 * Decompiled with CFR 0.152.
 */
package com.google.common.flogger.backend;

import com.google.common.flogger.backend.Platform;
import com.google.common.flogger.backend.system.DefaultPlatform;
import java.lang.reflect.InvocationTargetException;

public final class PlatformProvider {
    private PlatformProvider() {
    }

    public static Platform getPlatform() {
        try {
            return (Platform)DefaultPlatform.class.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
        }
        catch (IllegalAccessException | InstantiationException | NoClassDefFoundError | NoSuchMethodException | InvocationTargetException throwable) {
            return null;
        }
    }
}

