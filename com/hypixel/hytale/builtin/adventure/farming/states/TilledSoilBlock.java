/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.farming.states;

import com.hypixel.hytale.builtin.adventure.farming.FarmingPlugin;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.time.Instant;
import javax.annotation.Nullable;

public class TilledSoilBlock
implements Component<ChunkStore> {
    public static int VERSION = 1;
    public static final BuilderCodec<TilledSoilBlock> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(TilledSoilBlock.class, TilledSoilBlock::new).versioned()).codecVersion(VERSION)).append(new KeyedCodec<Boolean>("Planted", Codec.BOOLEAN), (state, planted) -> {
        state.planted = planted;
    }, state -> state.planted ? Boolean.TRUE : null).add()).append(new KeyedCodec("ModifierTimes", new MapCodec(Codec.INSTANT, Object2ObjectOpenHashMap::new, false)), (state, times) -> {
        if (times == null) {
            return;
        }
        state.wateredUntil = (Instant)times.get("WateredUntil");
    }, state -> null).setVersionRange(0, 0).add()).append(new KeyedCodec<T[]>("Flags", Codec.STRING_ARRAY), (state, flags) -> {
        if (flags == null) {
            return;
        }
        state.fertilized = ArrayUtil.contains(flags, "Fertilized");
        state.externalWater = ArrayUtil.contains(flags, "ExternalWater");
    }, state -> null).setVersionRange(0, 0).add()).append(new KeyedCodec<Boolean>("Fertilized", Codec.BOOLEAN), (state, v) -> {
        state.fertilized = v;
    }, state -> state.fertilized ? Boolean.TRUE : null).setVersionRange(1, VERSION).add()).append(new KeyedCodec<Boolean>("ExternalWater", Codec.BOOLEAN), (state, v) -> {
        state.externalWater = v;
    }, state -> state.externalWater ? Boolean.TRUE : null).setVersionRange(1, VERSION).add()).append(new KeyedCodec("WateredUntil", Codec.INSTANT), (state, v) -> {
        state.wateredUntil = v;
    }, state -> state.wateredUntil).setVersionRange(1, VERSION).add()).append(new KeyedCodec("DecayTime", Codec.INSTANT), (state, v) -> {
        state.decayTime = v;
    }, state -> state.decayTime).add()).build();
    protected boolean planted;
    protected boolean fertilized;
    protected boolean externalWater;
    @Nullable
    protected Instant wateredUntil;
    @Nullable
    protected Instant decayTime;

    public static ComponentType<ChunkStore, TilledSoilBlock> getComponentType() {
        return FarmingPlugin.get().getTiledSoilBlockComponentType();
    }

    public TilledSoilBlock() {
    }

    public TilledSoilBlock(boolean planted, boolean fertilized, boolean externalWater, Instant wateredUntil, Instant decayTime) {
        this.planted = planted;
        this.fertilized = fertilized;
        this.externalWater = externalWater;
        this.wateredUntil = wateredUntil;
        this.decayTime = decayTime;
    }

    public boolean isPlanted() {
        return this.planted;
    }

    public void setPlanted(boolean planted) {
        this.planted = planted;
    }

    public void setWateredUntil(@Nullable Instant wateredUntil) {
        this.wateredUntil = wateredUntil;
    }

    @Nullable
    public Instant getWateredUntil() {
        return this.wateredUntil;
    }

    public boolean isFertilized() {
        return this.fertilized;
    }

    public void setFertilized(boolean fertilized) {
        this.fertilized = fertilized;
    }

    public boolean hasExternalWater() {
        return this.externalWater;
    }

    public void setExternalWater(boolean externalWater) {
        this.externalWater = externalWater;
    }

    @Nullable
    public Instant getDecayTime() {
        return this.decayTime;
    }

    public void setDecayTime(@Nullable Instant decayTime) {
        this.decayTime = decayTime;
    }

    public String computeBlockType(Instant gameTime, BlockType type) {
        boolean watered;
        boolean bl = watered = this.hasExternalWater() || this.wateredUntil != null && this.wateredUntil.isAfter(gameTime);
        if (this.fertilized && watered) {
            return type.getBlockKeyForState("Fertilized_Watered");
        }
        if (this.fertilized) {
            return type.getBlockKeyForState("Fertilized");
        }
        if (watered) {
            return type.getBlockKeyForState("Watered");
        }
        return type.getBlockKeyForState("default");
    }

    public String toString() {
        return "TilledSoilBlock{planted=" + this.planted + ", fertilized=" + this.fertilized + ", externalWater=" + this.externalWater + ", wateredUntil=" + String.valueOf(this.wateredUntil) + ", decayTime=" + String.valueOf(this.decayTime) + "}";
    }

    @Override
    @Nullable
    public Component<ChunkStore> clone() {
        return new TilledSoilBlock(this.planted, this.fertilized, this.externalWater, this.wateredUntil, this.decayTime);
    }
}

