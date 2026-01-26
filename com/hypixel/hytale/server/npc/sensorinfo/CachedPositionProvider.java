/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.sensorinfo;

import com.hypixel.hytale.server.npc.sensorinfo.PositionProvider;

public class CachedPositionProvider
extends PositionProvider {
    private boolean fromCache;

    public void setIsFromCache(boolean status) {
        this.fromCache = status;
    }

    public boolean isFromCache() {
        return this.fromCache;
    }
}

