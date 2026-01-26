/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.worldgen.loader.cave;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.common.map.WeightedMap;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.procedurallib.json.DoubleRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.FloatRangeJsonLoader;
import com.hypixel.hytale.procedurallib.json.JsonLoader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.procedurallib.supplier.DoubleRange;
import com.hypixel.hytale.procedurallib.supplier.IDoubleRange;
import com.hypixel.hytale.procedurallib.supplier.IFloatRange;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.CaveNodeType;
import com.hypixel.hytale.server.worldgen.cave.CaveYawMode;
import com.hypixel.hytale.server.worldgen.loader.cave.CaveNodeTypeStorage;
import java.nio.file.Path;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CaveNodeChildEntryJsonLoader
extends JsonLoader<SeedStringResource, CaveNodeType.CaveNodeChildEntry> {
    protected final CaveNodeTypeStorage storage;

    public CaveNodeChildEntryJsonLoader(@Nonnull SeedString<SeedStringResource> seed, Path dataFolder, JsonElement json, CaveNodeTypeStorage storage) {
        super(seed.append(".CaveNodeChildEntry"), dataFolder, json);
        this.storage = storage;
    }

    @Override
    @Nonnull
    public CaveNodeType.CaveNodeChildEntry load() {
        return new CaveNodeType.CaveNodeChildEntry(this.loadNodes(), this.loadAnchor(), this.loadOffset(), this.loadRotations(), this.loadChildrenLimit(), this.loadRepeat(), this.loadPitchModifier(), this.loadYawModifier(), this.loadChance(), this.loadYawMode());
    }

    @Nonnull
    protected IWeightedMap<CaveNodeType> loadNodes() {
        WeightedMap.Builder<CaveNodeType> builder = WeightedMap.builder(CaveNodeType.EMPTY_ARRAY);
        JsonElement nodeElement = this.get("Node");
        if (nodeElement.isJsonArray()) {
            JsonArray weightsArray;
            JsonArray nodeArray = nodeElement.getAsJsonArray();
            if (this.has("Weights")) {
                JsonElement weightsElement = this.get("Weights");
                if (!weightsElement.isJsonArray()) {
                    throw new IllegalArgumentException("'Weights' must be an array if set");
                }
                weightsArray = weightsElement.getAsJsonArray();
                if (weightsArray.size() != nodeArray.size()) {
                    throw new IllegalArgumentException("Weight array size is different from node name array.");
                }
            } else {
                weightsArray = null;
            }
            for (int i = 0; i < nodeArray.size(); ++i) {
                JsonElement nodeEntryElement = this.getOrLoad(nodeArray.get(i));
                CaveNodeType caveNodeType = this.loadCaveNodeType(i, nodeEntryElement);
                double weight = weightsArray != null ? weightsArray.get(i).getAsDouble() : 1.0;
                builder.put(caveNodeType, weight);
            }
        } else if (nodeElement.isJsonObject() || nodeElement.isJsonPrimitive()) {
            CaveNodeType caveNodeType = this.loadCaveNodeType(0, nodeElement);
            builder.put(caveNodeType, 1.0);
        }
        if (builder.size() <= 0) {
            throw new IllegalArgumentException("There are no valid nodes in this child entry!");
        }
        return builder.build();
    }

    @Nonnull
    protected CaveNodeType loadCaveNodeType(int index, @Nonnull JsonElement element) {
        if (element.isJsonObject()) {
            String caveNodeTypeName = ((SeedStringResource)this.seed.get()).getUniqueName("ChildCaveType#");
            return this.storage.loadCaveNodeType(caveNodeTypeName, element.getAsJsonObject());
        }
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            String caveNodeTypeName = element.getAsString();
            return this.storage.getOrLoadCaveNodeType(caveNodeTypeName);
        }
        throw CaveNodeChildEntryJsonLoader.error("Invalid cave node type entry: %d", index);
    }

    @Nonnull
    protected Vector3d loadAnchor() {
        Vector3d anchor = Vector3d.ZERO;
        if (this.has("Anchor")) {
            anchor = this.loadVector(anchor.clone(), this.get("Anchor"));
        }
        return anchor;
    }

    @Nonnull
    protected Vector3d loadOffset() {
        Vector3d offset = Vector3d.ZERO;
        if (this.has("Offset")) {
            offset = this.loadVector(offset.clone(), this.get("Offset"));
        }
        return offset;
    }

    @Nonnull
    protected PrefabRotation[] loadRotations() {
        PrefabRotation[] rotations = new PrefabRotation[]{PrefabRotation.ROTATION_0};
        if (this.has("Rotation")) {
            JsonElement rotationElement = this.get("Rotation");
            if (rotationElement.isJsonPrimitive()) {
                rotations = new PrefabRotation[]{PrefabRotation.valueOfExtended(rotationElement.getAsString())};
            } else if (rotationElement.isJsonArray()) {
                JsonArray rotationArray = rotationElement.getAsJsonArray();
                rotations = new PrefabRotation[rotationArray.size()];
                for (int i = 0; i < rotations.length; ++i) {
                    rotations[i] = PrefabRotation.valueOfExtended(rotationArray.get(i).getAsString());
                }
            }
        }
        return rotations;
    }

    @Nullable
    protected IDoubleRange loadChildrenLimit() {
        if (this.has("ChildrenLimit")) {
            return new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("ChildrenLimit"), 0.0).load();
        }
        return null;
    }

    @Nonnull
    protected IDoubleRange loadRepeat() {
        IDoubleRange range = DoubleRange.ONE;
        if (this.has("Repeat")) {
            range = new DoubleRangeJsonLoader(this.seed, this.dataFolder, this.get("Repeat"), 0.0).load();
        }
        return range;
    }

    @Nonnull
    protected CaveNodeType.CaveNodeChildEntry.OrientationModifier loadYawModifier() {
        IFloatRange yawAdd = this.loadYawAdd();
        if (yawAdd != null) {
            return (current, random) -> current + yawAdd.getValue(random);
        }
        IFloatRange yawSet = this.loadYawSet();
        if (yawSet != null) {
            return (current, random) -> yawSet.getValue(random);
        }
        return (current, random) -> current;
    }

    @Nonnull
    protected CaveNodeType.CaveNodeChildEntry.OrientationModifier loadPitchModifier() {
        IFloatRange pitchAdd = this.loadPitchAdd();
        if (pitchAdd != null) {
            return (current, random) -> current + pitchAdd.getValue(random);
        }
        IFloatRange pitchSet = this.loadPitchSet();
        if (pitchSet != null) {
            return (current, random) -> pitchSet.getValue(random);
        }
        return (current, random) -> current;
    }

    @Nullable
    protected IFloatRange loadYawAdd() {
        IFloatRange yawAdd = null;
        if (this.has("YawAdd")) {
            yawAdd = new FloatRangeJsonLoader(this.seed, this.dataFolder, this.get("YawAdd"), 0.0f, deg -> deg * ((float)Math.PI / 180)).load();
        }
        return yawAdd;
    }

    @Nullable
    protected IFloatRange loadPitchAdd() {
        IFloatRange pitchAdd = null;
        if (this.has("PitchAdd")) {
            pitchAdd = new FloatRangeJsonLoader(this.seed, this.dataFolder, this.get("PitchAdd"), 0.0f, deg -> deg * ((float)Math.PI / 180)).load();
        }
        return pitchAdd;
    }

    @Nullable
    protected IFloatRange loadYawSet() {
        IFloatRange yawSet = null;
        if (this.has("YawSet")) {
            yawSet = new FloatRangeJsonLoader(this.seed, this.dataFolder, this.get("YawSet"), 0.0f, deg -> deg * ((float)Math.PI / 180)).load();
        }
        return yawSet;
    }

    @Nullable
    protected IFloatRange loadPitchSet() {
        IFloatRange pitchSet = null;
        if (this.has("PitchSet")) {
            pitchSet = new FloatRangeJsonLoader(this.seed, this.dataFolder, this.get("PitchSet"), 0.0f, deg -> deg * ((float)Math.PI / 180)).load();
        }
        return pitchSet;
    }

    protected double loadChance() {
        double chance = 1.0;
        if (this.has("Chance")) {
            chance = this.get("Chance").getAsDouble();
        }
        return chance;
    }

    @Nonnull
    protected CaveYawMode loadYawMode() {
        CaveYawMode combiner = CaveYawMode.NODE;
        if (this.has("YawMode")) {
            combiner = CaveYawMode.valueOf(this.get("YawMode").getAsString());
        }
        return combiner;
    }

    @Nonnull
    protected Vector3d loadVector(@Nonnull Vector3d vector, @Nonnull JsonElement jsonElement) {
        JsonArray array = jsonElement.getAsJsonArray();
        vector.x = array.get(0).getAsDouble();
        vector.y = array.get(1).getAsDouble();
        vector.z = array.get(2).getAsDouble();
        return vector;
    }

    public static interface Constants {
        public static final String KEY_NODE = "Node";
        public static final String KEY_SEED = "Seed";
        public static final String KEY_WEIGHTS = "Weights";
        public static final String KEY_ANCHOR = "Anchor";
        public static final String KEY_OFFSET = "Offset";
        public static final String KEY_ROTATION = "Rotation";
        public static final String KEY_CHILDREN_LIMIT = "ChildrenLimit";
        public static final String KEY_REPEAT = "Repeat";
        public static final String KEY_PITCH_ADD = "PitchAdd";
        public static final String KEY_PITCH_SET = "PitchSet";
        public static final String KEY_YAW_ADD = "YawAdd";
        public static final String KEY_YAW_SET = "YawSet";
        public static final String KEY_CHANCE = "Chance";
        public static final String KEY_YAW_MODE = "YawMode";
        public static final String ERROR_WEIGHTS_ARRAY = "'Weights' must be an array if set";
        public static final String ERROR_ENTRY_WEIGHT_SIZE = "Weight array size is different from node name array.";
        public static final String ERROR_NO_NODES = "There are no valid nodes in this child entry!";
    }
}

