/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.zone;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ZoneDiscoveryConfig(boolean display, String zone, @Nullable String soundEventId, @Nullable String icon, boolean major, float duration, float fadeInDuration, float fadeOutDuration) {
    @Nonnull
    public static final ZoneDiscoveryConfig DEFAULT = new ZoneDiscoveryConfig(false, "Void", null, null, true, 4.0f, 1.5f, 1.5f);

    @Nonnull
    public static ZoneDiscoveryConfig of(@Nullable Boolean display, @Nullable String zone, @Nullable String soundEventId, @Nullable String icon, @Nullable Boolean major, @Nullable Float duration, @Nullable Float fadeInDuration, @Nullable Float fadeOutDuration) {
        return new ZoneDiscoveryConfig(display != null ? display : false, zone != null ? zone : "Void", soundEventId, icon, major != null ? major : true, duration != null ? duration.floatValue() : 4.0f, fadeInDuration != null ? fadeInDuration.floatValue() : 1.5f, fadeOutDuration != null ? fadeOutDuration.floatValue() : 1.5f);
    }
}

