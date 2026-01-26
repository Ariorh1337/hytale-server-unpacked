/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.util;

import java.util.UUID;
import javax.annotation.Nonnull;

public class UUIDUtil {
    public static final UUID EMPTY_UUID = new UUID(0L, 0L);

    @Nonnull
    public static UUID generateVersion3UUID() {
        UUID out = UUID.randomUUID();
        if (out.version() != 3) {
            long msb = out.getMostSignificantBits();
            msb &= 0xFFFFFFFFFFFFBFFFL;
            out = new UUID(msb |= 0x3000L, out.getLeastSignificantBits());
        }
        return out;
    }

    public static boolean isEmptyOrNull(UUID uuid) {
        return uuid == null || uuid.equals(EMPTY_UUID);
    }
}

