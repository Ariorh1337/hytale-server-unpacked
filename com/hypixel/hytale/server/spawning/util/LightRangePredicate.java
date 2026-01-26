/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.spawning.util;

import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.assets.spawns.LightType;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LightRangePredicate {
    private byte lightValueMin;
    private byte lightValueMax;
    private byte skyLightValueMin;
    private byte skyLightValueMax;
    private byte sunlightValueMin;
    private byte sunlightValueMax;
    private byte redLightValueMin;
    private byte redLightValueMax;
    private byte greenLightValueMin;
    private byte greenLightValueMax;
    private byte blueLightValueMin;
    private byte blueLightValueMax;
    private boolean testLightValue;
    private boolean testSkyLightValue;
    private boolean testSunlightValue;
    private boolean testRedLightValue;
    private boolean testGreenLightValue;
    private boolean testBlueLightValue;

    public static int lightToPrecentage(byte light) {
        return MathUtil.fastRound((float)light * 100.0f / 15.0f);
    }

    public void setLightRange(@Nonnull LightType type, double[] lightRange) {
        switch (type) {
            case Light: {
                this.setLightRange(lightRange);
                break;
            }
            case SkyLight: {
                this.setSkyLightRange(lightRange);
                break;
            }
            case Sunlight: {
                this.setSunlightRange(lightRange);
                break;
            }
            case RedLight: {
                this.setRedLightRange(lightRange);
                break;
            }
            case GreenLight: {
                this.setGreenLightRange(lightRange);
                break;
            }
            case BlueLight: {
                this.setBlueLightRange(lightRange);
            }
        }
    }

    public void setLightRange(@Nullable double[] lightRange) {
        boolean bl = this.testLightValue = lightRange != null;
        if (this.testLightValue) {
            this.lightValueMin = this.lightPercentageToAbsolute(lightRange[0]);
            this.lightValueMax = this.lightPercentageToAbsolute(lightRange[1]);
            this.testLightValue = this.isPartialRange(this.lightValueMin, this.lightValueMax);
        }
    }

    public void setSkyLightRange(@Nullable double[] lightRange) {
        boolean bl = this.testSkyLightValue = lightRange != null;
        if (this.testSkyLightValue) {
            this.skyLightValueMin = this.lightPercentageToAbsolute(lightRange[0]);
            this.skyLightValueMax = this.lightPercentageToAbsolute(lightRange[1]);
            this.testSkyLightValue = this.isPartialRange(this.skyLightValueMin, this.skyLightValueMax);
        }
    }

    public void setSunlightRange(@Nullable double[] lightRange) {
        boolean bl = this.testSunlightValue = lightRange != null;
        if (this.testSunlightValue) {
            this.sunlightValueMin = this.lightPercentageToAbsolute(lightRange[0]);
            this.sunlightValueMax = this.lightPercentageToAbsolute(lightRange[1]);
            this.testSunlightValue = this.isPartialRange(this.sunlightValueMin, this.sunlightValueMax);
        }
    }

    public void setRedLightRange(@Nullable double[] lightRange) {
        boolean bl = this.testRedLightValue = lightRange != null;
        if (this.testRedLightValue) {
            this.redLightValueMin = this.lightPercentageToAbsolute(lightRange[0]);
            this.redLightValueMax = this.lightPercentageToAbsolute(lightRange[1]);
            this.testRedLightValue = this.isPartialRange(this.redLightValueMin, this.redLightValueMax);
        }
    }

    public void setGreenLightRange(@Nullable double[] lightRange) {
        boolean bl = this.testGreenLightValue = lightRange != null;
        if (this.testGreenLightValue) {
            this.greenLightValueMin = this.lightPercentageToAbsolute(lightRange[0]);
            this.greenLightValueMax = this.lightPercentageToAbsolute(lightRange[1]);
            this.testGreenLightValue = this.isPartialRange(this.greenLightValueMin, this.greenLightValueMax);
        }
    }

    public void setBlueLightRange(@Nullable double[] lightRange) {
        boolean bl = this.testBlueLightValue = lightRange != null;
        if (this.testBlueLightValue) {
            this.blueLightValueMin = this.lightPercentageToAbsolute(lightRange[0]);
            this.blueLightValueMax = this.lightPercentageToAbsolute(lightRange[1]);
            this.testBlueLightValue = this.isPartialRange(this.blueLightValueMin, this.blueLightValueMax);
        }
    }

    public boolean isTestLightValue() {
        return this.testLightValue;
    }

    public boolean isTestSkyLightValue() {
        return this.testSkyLightValue;
    }

    public boolean isTestSunlightValue() {
        return this.testSunlightValue;
    }

    public boolean isTestRedLightValue() {
        return this.testRedLightValue;
    }

    public boolean isTestGreenLightValue() {
        return this.testGreenLightValue;
    }

    public boolean isTestBlueLightValue() {
        return this.testBlueLightValue;
    }

    public boolean test(@Nonnull World world, @Nonnull Vector3d position, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        int x = MathUtil.floor(position.getX());
        int y = MathUtil.floor(position.getY());
        int z = MathUtil.floor(position.getZ());
        WorldTimeResource worldTimeResource = componentAccessor.getResource(WorldTimeResource.getResourceType());
        WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(x, z));
        return chunk != null && this.test(chunk.getBlockChunk(), x, y, z, worldTimeResource.getSunlightFactor());
    }

    public boolean test(@Nullable BlockChunk blockChunk, int x, int y, int z, double sunlightFactor) {
        byte lightValue;
        byte maxLight;
        if (blockChunk == null) {
            return false;
        }
        if (this.testLightValue && !this.testLight(maxLight = LightRangePredicate.calculateLightValue(blockChunk, x, y, z, sunlightFactor))) {
            return false;
        }
        if (this.testSkyLightValue && !this.testSkyLight(lightValue = blockChunk.getSkyLight(x, y, z))) {
            return false;
        }
        if (this.testSunlightValue && !this.testSunlight(lightValue = (byte)((double)blockChunk.getSkyLight(x, y, z) * sunlightFactor))) {
            return false;
        }
        if (this.testRedLightValue && !this.testRedLight(lightValue = blockChunk.getRedBlockLight(x, y, z))) {
            return false;
        }
        if (this.testGreenLightValue && !this.testGreenLight(lightValue = blockChunk.getGreenBlockLight(x, y, z))) {
            return false;
        }
        return !this.testBlueLightValue || this.testBlueLight(lightValue = blockChunk.getBlueBlockLight(x, y, z));
    }

    public boolean testLight(byte lightValue) {
        return this.test(lightValue, this.lightValueMin, this.lightValueMax);
    }

    public boolean testSkyLight(byte lightValue) {
        return this.test(lightValue, this.skyLightValueMin, this.skyLightValueMax);
    }

    public boolean testSunlight(byte lightValue) {
        return this.test(lightValue, this.sunlightValueMin, this.sunlightValueMax);
    }

    public boolean testRedLight(byte lightValue) {
        return this.test(lightValue, this.redLightValueMin, this.redLightValueMax);
    }

    public boolean testGreenLight(byte lightValue) {
        return this.test(lightValue, this.greenLightValueMin, this.greenLightValueMax);
    }

    public boolean testBlueLight(byte lightValue) {
        return this.test(lightValue, this.blueLightValueMin, this.blueLightValueMax);
    }

    public static byte calculateLightValue(@Nonnull BlockChunk blockChunk, int x, int y, int z, double sunlightFactor) {
        byte lightValue = blockChunk.getBlockLightIntensity(x, y, z);
        byte skyLightValue = (byte)((double)blockChunk.getSkyLight(x, y, z) * sunlightFactor);
        return (byte)Math.max(skyLightValue, lightValue);
    }

    private boolean test(byte lightValue, byte min, byte max) {
        return lightValue >= min && lightValue <= max;
    }

    private byte lightPercentageToAbsolute(double light) {
        return (byte)MathUtil.fastRound(light * 0.15);
    }

    private boolean isPartialRange(byte min, byte max) {
        return min > 0 || max < 15;
    }
}

