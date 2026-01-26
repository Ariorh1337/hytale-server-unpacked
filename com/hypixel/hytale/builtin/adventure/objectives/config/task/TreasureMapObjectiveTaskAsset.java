/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.adventure.objectives.config.task;

import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.builtin.adventure.objectives.config.task.ObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.taskcondition.TaskConditionAsset;
import com.hypixel.hytale.builtin.adventure.objectives.config.worldlocationproviders.WorldLocationProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TreasureMapObjectiveTaskAsset
extends ObjectiveTaskAsset {
    public static final BuilderCodec<TreasureMapObjectiveTaskAsset> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(TreasureMapObjectiveTaskAsset.class, TreasureMapObjectiveTaskAsset::new, BASE_CODEC).append(new KeyedCodec<T[]>("Chests", new ArrayCodec<ChestConfig>(ChestConfig.CODEC, ChestConfig[]::new)), (treasureMapObjectiveTaskAsset, chestConfigs) -> {
        treasureMapObjectiveTaskAsset.chestConfigs = chestConfigs;
    }, treasureMapObjectiveTaskAsset -> treasureMapObjectiveTaskAsset.chestConfigs).addValidator(Validators.nonEmptyArray()).add()).build();
    protected ChestConfig[] chestConfigs;

    public TreasureMapObjectiveTaskAsset(String descriptionId, TaskConditionAsset[] taskConditions, Vector3i[] mapMarkers, ChestConfig[] chestConfigs) {
        super(descriptionId, taskConditions, mapMarkers);
        this.chestConfigs = chestConfigs;
    }

    protected TreasureMapObjectiveTaskAsset() {
    }

    @Override
    @Nonnull
    public ObjectiveTaskAsset.TaskScope getTaskScope() {
        return ObjectiveTaskAsset.TaskScope.PLAYER;
    }

    public ChestConfig[] getChestConfigs() {
        return this.chestConfigs;
    }

    @Override
    protected boolean matchesAsset0(ObjectiveTaskAsset task) {
        if (!(task instanceof TreasureMapObjectiveTaskAsset)) {
            return false;
        }
        TreasureMapObjectiveTaskAsset treasureMapObjectiveTaskAsset = (TreasureMapObjectiveTaskAsset)task;
        return Arrays.equals(treasureMapObjectiveTaskAsset.chestConfigs, this.chestConfigs);
    }

    @Override
    @Nonnull
    public String toString() {
        return "TreasureMapObjectiveTaskAsset{chestConfigs=" + Arrays.toString(this.chestConfigs) + "} " + super.toString();
    }

    public static class ChestConfig {
        public static final BuilderCodec<ChestConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ChestConfig.class, ChestConfig::new).append(new KeyedCodec<Float>("MinRadius", Codec.FLOAT), (chestConfig, aFloat) -> {
            chestConfig.minRadius = aFloat.floatValue();
        }, chestConfig -> Float.valueOf(chestConfig.minRadius)).addValidator(Validators.greaterThan(Float.valueOf(0.0f))).add()).append(new KeyedCodec<Float>("MaxRadius", Codec.FLOAT), (chestConfig, aFloat) -> {
            chestConfig.maxRadius = aFloat.floatValue();
        }, chestConfig -> Float.valueOf(chestConfig.maxRadius)).addValidator(Validators.greaterThan(Float.valueOf(1.0f))).add()).append(new KeyedCodec("DropList", new ContainedAssetCodec(ItemDropList.class, ItemDropList.CODEC)), (chestConfig, s) -> {
            chestConfig.droplistId = s;
        }, chestConfig -> chestConfig.droplistId).addValidator(Validators.nonNull()).addValidator(ItemDropList.VALIDATOR_CACHE.getValidator()).add()).append(new KeyedCodec<WorldLocationProvider>("WorldLocationCondition", WorldLocationProvider.CODEC), (chestConfig, worldLocationCondition) -> {
            chestConfig.worldLocationProvider = worldLocationCondition;
        }, chestConfig -> chestConfig.worldLocationProvider).add()).append(new KeyedCodec<String>("ChestBlockTypeKey", Codec.STRING), (chestConfig, blockTypeKey) -> {
            chestConfig.chestBlockTypeKey = blockTypeKey;
        }, chestConfig -> chestConfig.chestBlockTypeKey).addValidator(Validators.nonNull()).addValidator(BlockType.VALIDATOR_CACHE.getValidator()).add()).afterDecode(chestConfig -> {
            if (chestConfig.minRadius >= chestConfig.maxRadius) {
                throw new IllegalArgumentException("ChestConfig.MinRadius (" + chestConfig.minRadius + ") needs to be greater than ChestConfig.MaxRadius (" + chestConfig.maxRadius + ")");
            }
        })).build();
        protected float minRadius = 10.0f;
        protected float maxRadius = 20.0f;
        protected String droplistId;
        protected WorldLocationProvider worldLocationProvider;
        protected String chestBlockTypeKey;

        public float getMinRadius() {
            return this.minRadius;
        }

        public float getMaxRadius() {
            return this.maxRadius;
        }

        public String getDroplistId() {
            return this.droplistId;
        }

        public WorldLocationProvider getWorldLocationProvider() {
            return this.worldLocationProvider;
        }

        public String getChestBlockTypeKey() {
            return this.chestBlockTypeKey;
        }

        public boolean equals(@Nullable Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            ChestConfig that = (ChestConfig)o;
            if (Float.compare(that.minRadius, this.minRadius) != 0) {
                return false;
            }
            if (Float.compare(that.maxRadius, this.maxRadius) != 0) {
                return false;
            }
            if (this.droplistId != null ? !this.droplistId.equals(that.droplistId) : that.droplistId != null) {
                return false;
            }
            if (this.worldLocationProvider != null ? !this.worldLocationProvider.equals(that.worldLocationProvider) : that.worldLocationProvider != null) {
                return false;
            }
            return this.chestBlockTypeKey != null ? this.chestBlockTypeKey.equals(that.chestBlockTypeKey) : that.chestBlockTypeKey == null;
        }

        public int hashCode() {
            int result = this.minRadius != 0.0f ? Float.floatToIntBits(this.minRadius) : 0;
            result = 31 * result + (this.maxRadius != 0.0f ? Float.floatToIntBits(this.maxRadius) : 0);
            result = 31 * result + (this.droplistId != null ? this.droplistId.hashCode() : 0);
            result = 31 * result + (this.worldLocationProvider != null ? this.worldLocationProvider.hashCode() : 0);
            result = 31 * result + (this.chestBlockTypeKey != null ? this.chestBlockTypeKey.hashCode() : 0);
            return result;
        }

        @Nonnull
        public String toString() {
            return "ChestConfig{minRadius=" + this.minRadius + ", maxRadius=" + this.maxRadius + ", droplistId='" + this.droplistId + "', worldLocationCondition=" + String.valueOf(this.worldLocationProvider) + ", chestBlockTypeKey=" + this.chestBlockTypeKey + "}";
        }
    }
}

