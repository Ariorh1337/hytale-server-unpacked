/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.worldmap;

import com.hypixel.hytale.math.shape.Box2D;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.protocol.packets.worldmap.UpdateWorldMapSettings;
import javax.annotation.Nonnull;

public class WorldMapSettings {
    public static final WorldMapSettings DISABLED = new WorldMapSettings();
    private Box2D worldMapArea;
    private float imageScale = 0.5f;
    private float viewRadiusMultiplier = 1.0f;
    private int viewRadiusMin = 1;
    private int viewRadiusMax = 512;
    @Nonnull
    private UpdateWorldMapSettings settingsPacket;

    public WorldMapSettings() {
        this.settingsPacket = new UpdateWorldMapSettings();
        this.settingsPacket.enabled = false;
    }

    public WorldMapSettings(Box2D worldMapArea, float imageScale, float viewRadiusMultiplier, int viewRadiusMin, int viewRadiusMax, @Nonnull UpdateWorldMapSettings settingsPacket) {
        this.worldMapArea = worldMapArea;
        this.imageScale = imageScale;
        this.viewRadiusMultiplier = viewRadiusMultiplier;
        this.viewRadiusMin = viewRadiusMin;
        this.viewRadiusMax = viewRadiusMax;
        this.settingsPacket = settingsPacket;
    }

    public Box2D getWorldMapArea() {
        return this.worldMapArea;
    }

    public float getImageScale() {
        return this.imageScale;
    }

    @Nonnull
    public UpdateWorldMapSettings getSettingsPacket() {
        return this.settingsPacket;
    }

    public int getViewRadius(int viewRadius) {
        return MathUtil.clamp(Math.round((float)viewRadius * this.viewRadiusMultiplier), this.viewRadiusMin, this.viewRadiusMax);
    }

    @Nonnull
    public String toString() {
        return "WorldMapSettings{worldMapArea=" + String.valueOf(this.worldMapArea) + ", imageScale=" + this.imageScale + ", viewRadiusMultiplier=" + this.viewRadiusMultiplier + ", viewRadiusMin=" + this.viewRadiusMin + ", viewRadiusMax=" + this.viewRadiusMax + ", settingsPacket=" + String.valueOf(this.settingsPacket) + "}";
    }
}

