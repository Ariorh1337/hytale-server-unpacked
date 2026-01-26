/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.config.modifiers;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.Range;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.GrowthModifierAsset;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkLightData;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;

public class LightLevelGrowthModifierAsset
extends GrowthModifierAsset {
    public static final BuilderCodec<LightLevelGrowthModifierAsset> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(LightLevelGrowthModifierAsset.class, LightLevelGrowthModifierAsset::new, ABSTRACT_CODEC).addField(new KeyedCodec<ArtificialLight>("ArtificialLight", ArtificialLight.CODEC), (lightLevel, artificialLight) -> {
        lightLevel.artificialLight = artificialLight;
    }, lightLevel -> lightLevel.artificialLight)).addField(new KeyedCodec<Rangef>("Sunlight", ProtocolCodecs.RANGEF), (lightLevel, sunLight) -> {
        lightLevel.sunlight = sunLight;
    }, lightLevel -> lightLevel.sunlight)).addField(new KeyedCodec<Boolean>("RequireBoth", Codec.BOOLEAN), (lightLevel, requireBoth) -> {
        lightLevel.requireBoth = requireBoth;
    }, lightLevel -> lightLevel.requireBoth)).build();
    protected ArtificialLight artificialLight;
    protected Rangef sunlight;
    protected boolean requireBoth;

    public ArtificialLight getArtificialLight() {
        return this.artificialLight;
    }

    public Rangef getSunlight() {
        return this.sunlight;
    }

    public boolean isRequireBoth() {
        return this.requireBoth;
    }

    protected boolean checkArtificialLight(byte red, byte green, byte blue) {
        ArtificialLight artificialLight = this.artificialLight;
        Range redRange = artificialLight.getRed();
        Range greenRange = artificialLight.getGreen();
        Range blueRange = artificialLight.getBlue();
        return LightLevelGrowthModifierAsset.isInRange(redRange, red) && LightLevelGrowthModifierAsset.isInRange(greenRange, green) && LightLevelGrowthModifierAsset.isInRange(blueRange, blue);
    }

    protected boolean checkSunLight(WorldTimeResource worldTimeResource, byte sky) {
        Rangef range = this.sunlight;
        double sunlightFactor = worldTimeResource.getSunlightFactor();
        double daylight = sunlightFactor * (double)sky;
        return (double)range.min <= daylight && daylight <= (double)range.max;
    }

    protected static boolean isInRange(@Nonnull Range range, int value) {
        return range.min <= value && value <= range.max;
    }

    @Override
    public double getCurrentGrowthMultiplier(CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> sectionRef, Ref<ChunkStore> blockRef, int x, int y, int z, boolean initialTick) {
        BlockSection blockSection = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
        short lightRaw = blockSection.getGlobalLight().getLightRaw(x, y, z);
        byte redLight = ChunkLightData.getLightValue(lightRaw, 0);
        byte greenLight = ChunkLightData.getLightValue(lightRaw, 1);
        byte blueLight = ChunkLightData.getLightValue(lightRaw, 2);
        byte skyLight = ChunkLightData.getLightValue(lightRaw, 3);
        WorldTimeResource worldTimeResource = commandBuffer.getExternalData().getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
        boolean active = false;
        boolean onlySunlight = false;
        if (this.requireBoth) {
            active = this.checkArtificialLight(redLight, greenLight, blueLight) && this.checkSunLight(worldTimeResource, skyLight);
        } else if (this.checkArtificialLight(redLight, greenLight, blueLight)) {
            active = true;
        } else if (this.checkSunLight(worldTimeResource, skyLight)) {
            active = true;
            onlySunlight = true;
        }
        if (active) {
            if (onlySunlight && initialTick) {
                return super.getCurrentGrowthMultiplier(commandBuffer, sectionRef, blockRef, x, y, z, initialTick) * (double)0.6f;
            }
            return super.getCurrentGrowthMultiplier(commandBuffer, sectionRef, blockRef, x, y, z, initialTick);
        }
        return 1.0;
    }

    @Override
    @Nonnull
    public String toString() {
        return "LightLevelGrowthModifierAsset{artificialLight=" + String.valueOf(this.artificialLight) + ", sunLight=" + String.valueOf(this.sunlight) + ", requireBoth=" + this.requireBoth + "} " + super.toString();
    }

    public static class ArtificialLight {
        public static final BuilderCodec<ArtificialLight> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ArtificialLight.class, ArtificialLight::new).addField(new KeyedCodec<Range>("Red", ProtocolCodecs.RANGE), (light, red) -> {
            light.red = red;
        }, light -> light.red)).addField(new KeyedCodec<Range>("Green", ProtocolCodecs.RANGE), (light, green) -> {
            light.green = green;
        }, light -> light.green)).addField(new KeyedCodec<Range>("Blue", ProtocolCodecs.RANGE), (light, blue) -> {
            light.blue = blue;
        }, light -> light.blue)).build();
        protected Range red;
        protected Range green;
        protected Range blue;

        public Range getRed() {
            return this.red;
        }

        public Range getGreen() {
            return this.green;
        }

        public Range getBlue() {
            return this.blue;
        }

        @Nonnull
        public String toString() {
            return "ArtificialLightLevel{red=" + String.valueOf(this.red) + ", green=" + String.valueOf(this.green) + ", blue=" + String.valueOf(this.blue) + "}";
        }
    }
}

