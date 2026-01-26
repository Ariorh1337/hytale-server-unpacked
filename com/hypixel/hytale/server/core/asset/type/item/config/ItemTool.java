/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.item.config;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.server.core.asset.type.blockset.config.BlockSet;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemToolSpec;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.soundevent.validator.SoundEventValidators;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ItemTool
implements NetworkSerializable<com.hypixel.hytale.protocol.ItemTool> {
    public static final BuilderCodec<ItemTool> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ItemTool.class, ItemTool::new).addField(new KeyedCodec<T[]>("Specs", new ArrayCodec(ItemToolSpec.CODEC, ItemToolSpec[]::new)), (itemTool, s) -> {
        itemTool.specs = s;
    }, itemTool -> itemTool.specs)).addField(new KeyedCodec<Double>("Speed", Codec.DOUBLE), (itemTool, d) -> {
        itemTool.speed = d.floatValue();
    }, itemTool -> itemTool.speed)).addField(new KeyedCodec<T[]>("DurabilityLossBlockTypes", new ArrayCodec<DurabilityLossBlockTypes>(DurabilityLossBlockTypes.CODEC, DurabilityLossBlockTypes[]::new)), (item, s) -> {
        item.durabilityLossBlockTypes = s;
    }, item -> item.durabilityLossBlockTypes)).appendInherited(new KeyedCodec<String>("HitSoundLayer", Codec.STRING), (item, s) -> {
        item.hitSoundLayerId = s;
    }, item -> item.hitSoundLayerId, (item, parent) -> {
        item.hitSoundLayerId = parent.hitSoundLayerId;
    }).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.MONO).documentation("Sound to play in addition to the block breaking sound when hitting a block this tool is designed to break.").add()).appendInherited(new KeyedCodec<String>("IncorrectMaterialSoundLayer", Codec.STRING), (item, s) -> {
        item.incorrectMaterialSoundLayerId = s;
    }, item -> item.incorrectMaterialSoundLayerId, (item, parent) -> {
        item.incorrectMaterialSoundLayerId = parent.incorrectMaterialSoundLayerId;
    }).addValidator(SoundEvent.VALIDATOR_CACHE.getValidator()).addValidator(SoundEventValidators.MONO).documentation("Sound to play in addition to the block breaking sound when hitting a block this tool cannot break.").add()).afterDecode(ItemTool::processConfig)).build();
    protected ItemToolSpec[] specs;
    protected float speed;
    protected DurabilityLossBlockTypes[] durabilityLossBlockTypes;
    @Nullable
    protected String hitSoundLayerId = null;
    protected transient int hitSoundLayerIndex = 0;
    @Nullable
    protected String incorrectMaterialSoundLayerId = null;
    protected transient int incorrectMaterialSoundLayerIndex = 0;

    public ItemTool(ItemToolSpec[] specs, float speed, DurabilityLossBlockTypes[] durabilityLossBlockTypes) {
        this.specs = specs;
        this.speed = speed;
        this.durabilityLossBlockTypes = durabilityLossBlockTypes;
    }

    protected ItemTool() {
    }

    protected void processConfig() {
        if (this.hitSoundLayerId != null) {
            this.hitSoundLayerIndex = SoundEvent.getAssetMap().getIndex(this.hitSoundLayerId);
        }
        if (this.incorrectMaterialSoundLayerId != null) {
            this.incorrectMaterialSoundLayerIndex = SoundEvent.getAssetMap().getIndex(this.incorrectMaterialSoundLayerId);
        }
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ItemTool toPacket() {
        com.hypixel.hytale.protocol.ItemTool packet = new com.hypixel.hytale.protocol.ItemTool();
        if (this.specs != null && this.specs.length > 0) {
            packet.specs = ArrayUtil.copyAndMutate(this.specs, ItemToolSpec::toPacket, com.hypixel.hytale.protocol.ItemToolSpec[]::new);
        }
        packet.speed = this.speed;
        return packet;
    }

    public ItemToolSpec[] getSpecs() {
        return this.specs;
    }

    public float getSpeed() {
        return this.speed;
    }

    public DurabilityLossBlockTypes[] getDurabilityLossBlockTypes() {
        return this.durabilityLossBlockTypes;
    }

    public int getHitSoundLayerIndex() {
        return this.hitSoundLayerIndex;
    }

    public int getIncorrectMaterialSoundLayerIndex() {
        return this.incorrectMaterialSoundLayerIndex;
    }

    @Nonnull
    public String toString() {
        return "ItemTool{specs=" + Arrays.toString(this.specs) + ", speed=" + this.speed + ", durabilityLossBlockTypes=" + Arrays.toString(this.durabilityLossBlockTypes) + ", hitSoundLayerId='" + this.hitSoundLayerId + "', hitSoundLayerIndex=" + this.hitSoundLayerIndex + ", incorrectMaterialSoundLayerId='" + this.incorrectMaterialSoundLayerId + "', incorrectMaterialSoundLayerIndex=" + this.incorrectMaterialSoundLayerIndex + "}";
    }

    public static class DurabilityLossBlockTypes {
        public static final BuilderCodec<DurabilityLossBlockTypes> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DurabilityLossBlockTypes.class, DurabilityLossBlockTypes::new).addField(new KeyedCodec<T[]>("BlockTypes", new ArrayCodec<String>(Codec.STRING, String[]::new)), (item, s) -> {
            item.blockTypes = s;
        }, item -> item.blockTypes)).addField(new KeyedCodec<T[]>("BlockSets", Codec.STRING_ARRAY), (item, s) -> {
            item.blockSets = s;
        }, item -> item.blockSets)).addField(new KeyedCodec<Double>("DurabilityLossOnHit", Codec.DOUBLE), (item, s) -> {
            item.durabilityLossOnHit = s;
        }, item -> item.durabilityLossOnHit)).afterDecode(item -> {
            if (item.blockSets != null) {
                item.blockSetIndexes = new int[item.blockSets.length];
                for (int i = 0; i < item.blockSets.length; ++i) {
                    String blockSet = item.blockSets[i];
                    int index = BlockSet.getAssetMap().getIndex(blockSet);
                    if (index == Integer.MIN_VALUE) {
                        throw new IllegalArgumentException("Unknown key! " + blockSet);
                    }
                    item.blockSetIndexes[i] = index;
                }
            }
        })).build();
        protected String[] blockTypes;
        protected String[] blockSets;
        protected double durabilityLossOnHit;
        protected int[] blockTypeIndexes;
        protected int[] blockSetIndexes;

        protected DurabilityLossBlockTypes() {
        }

        public DurabilityLossBlockTypes(String[] blockTypes, String[] blockSets, double durabilityLossOnHit) {
            this.blockTypes = blockTypes;
            this.blockSets = blockSets;
            this.durabilityLossOnHit = durabilityLossOnHit;
        }

        public String[] getBlockTypes() {
            return this.blockTypes;
        }

        public String[] getBlockSets() {
            return this.blockSets;
        }

        public double getDurabilityLossOnHit() {
            return this.durabilityLossOnHit;
        }

        public int[] getBlockTypeIndexes() {
            if (this.blockTypes != null && this.blockTypeIndexes == null) {
                int[] blockTypeIndexes = new int[this.blockTypes.length];
                for (int i = 0; i < this.blockTypes.length; ++i) {
                    String key = this.blockTypes[i];
                    int index = BlockType.getAssetMap().getIndex(key);
                    if (index == Integer.MIN_VALUE) {
                        throw new IllegalArgumentException("Unknown key! " + key);
                    }
                    blockTypeIndexes[i] = index;
                }
                this.blockTypeIndexes = blockTypeIndexes;
            }
            return this.blockTypeIndexes;
        }

        public int[] getBlockSetIndexes() {
            return this.blockSetIndexes;
        }

        @Nonnull
        public String toString() {
            return "DurabilityLossBlockTypes{blockTypes=" + Arrays.toString(this.blockTypes) + ", blockSets=" + Arrays.toString(this.blockSets) + ", durabilityLossOnHit=" + this.durabilityLossOnHit + "}";
        }
    }
}

