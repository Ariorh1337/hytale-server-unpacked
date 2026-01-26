/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.blocktype.config;

import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.AllowEmptyObject;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockBreakingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.HarvestingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.PhysicsDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.SoftBlockDropType;
import com.hypixel.hytale.server.core.asset.type.item.config.Item;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;

public class BlockGathering
implements NetworkSerializable<com.hypixel.hytale.protocol.BlockGathering> {
    public static final BuilderCodec<BlockGathering> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockGathering.class, BlockGathering::new).append(new KeyedCodec<BlockBreakingDropType>("Breaking", BlockBreakingDropType.CODEC), (blockGathering, o) -> {
        blockGathering.breaking = o;
    }, blockGathering -> blockGathering.breaking).metadata(AllowEmptyObject.INSTANCE).add()).append(new KeyedCodec<HarvestingDropType>("Harvest", HarvestingDropType.CODEC), (blockGathering, o) -> {
        blockGathering.harvest = o;
    }, blockGathering -> blockGathering.harvest).metadata(AllowEmptyObject.INSTANCE).add()).append(new KeyedCodec<SoftBlockDropType>("Soft", SoftBlockDropType.CODEC), (blockGathering, o) -> {
        blockGathering.soft = o;
    }, blockGathering -> blockGathering.soft).metadata(AllowEmptyObject.INSTANCE).add()).append(new KeyedCodec<PhysicsDropType>("Physics", PhysicsDropType.CODEC), (blockGathering, o) -> {
        blockGathering.physics = o;
    }, blockGathering -> blockGathering.physics).metadata(AllowEmptyObject.INSTANCE).add()).append(new KeyedCodec<T[]>("Tools", new ArrayCodec<BlockToolData>(BlockToolData.CODEC, BlockToolData[]::new)), (blockGathering, o) -> {
        blockGathering.toolDataRaw = o;
    }, blockGathering -> blockGathering.toolDataRaw).metadata(AllowEmptyObject.INSTANCE).add()).appendInherited(new KeyedCodec<Boolean>("UseDefaultDropWhenPlaced", Codec.BOOLEAN), (o, v) -> {
        o.useDefaultDropWhenPlaced = v;
    }, o -> o.useDefaultDropWhenPlaced, (o, p) -> {
        o.useDefaultDropWhenPlaced = p.useDefaultDropWhenPlaced;
    }).documentation("If this is set then player placed blocks will use the default drop behaviour instead of using the droplists.").add()).afterDecode(g -> {
        if (g.toolDataRaw != null) {
            g.toolData = new Object2ObjectOpenHashMap<String, BlockToolData>();
            for (BlockToolData t : g.toolDataRaw) {
                g.toolData.put(t.getTypeId(), t);
            }
        }
    })).build();
    protected BlockBreakingDropType breaking;
    protected HarvestingDropType harvest;
    protected SoftBlockDropType soft;
    protected PhysicsDropType physics;
    protected BlockToolData[] toolDataRaw;
    @Nonnull
    protected Map<String, BlockToolData> toolData = Collections.emptyMap();
    protected boolean useDefaultDropWhenPlaced = false;

    protected BlockGathering() {
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.BlockGathering toPacket() {
        com.hypixel.hytale.protocol.BlockGathering packet = new com.hypixel.hytale.protocol.BlockGathering();
        if (this.breaking != null) {
            packet.breaking = this.breaking.toPacket();
        }
        if (this.harvest != null) {
            packet.harvest = this.harvest.toPacket();
        }
        if (this.soft != null) {
            packet.soft = this.soft.toPacket();
        }
        return packet;
    }

    public BlockBreakingDropType getBreaking() {
        return this.breaking;
    }

    public HarvestingDropType getHarvest() {
        return this.harvest;
    }

    public SoftBlockDropType getSoft() {
        return this.soft;
    }

    public boolean isHarvestable() {
        return this.harvest != null;
    }

    public boolean isSoft() {
        return this.soft != null;
    }

    public PhysicsDropType getPhysics() {
        return this.physics;
    }

    public boolean shouldUseDefaultDropWhenPlaced() {
        return this.useDefaultDropWhenPlaced;
    }

    @Nonnull
    public String toString() {
        return "BlockGathering{breaking=" + String.valueOf(this.breaking) + ", harvest=" + String.valueOf(this.harvest) + ", harvest=" + String.valueOf(this.soft) + "}";
    }

    @Nonnull
    public Map<String, BlockToolData> getToolData() {
        return this.toolData;
    }

    public static class BlockToolData {
        public static final BuilderCodec<BlockToolData> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(BlockToolData.class, BlockToolData::new).append(new KeyedCodec<String>("Type", Codec.STRING), (toolData, o) -> {
            toolData.typeId = o;
        }, toolData -> toolData.typeId).metadata(AllowEmptyObject.INSTANCE).add()).append(new KeyedCodec<String>("State", Codec.STRING), (toolData, o) -> {
            toolData.stateId = o;
        }, toolData -> toolData.stateId).metadata(AllowEmptyObject.INSTANCE).add()).append(new KeyedCodec<String>("ItemId", Codec.STRING), (toolData, s) -> {
            toolData.itemId = s;
        }, toolData -> toolData.itemId).addValidatorLate(() -> Item.VALIDATOR_CACHE.getValidator().late()).add()).append(new KeyedCodec("DropList", new ContainedAssetCodec(ItemDropList.class, ItemDropList.CODEC)), (toolData, s) -> {
            toolData.dropListId = s;
        }, toolData -> toolData.dropListId).addValidatorLate(() -> ItemDropList.VALIDATOR_CACHE.getValidator().late()).add()).build();
        protected String typeId;
        protected String stateId;
        protected String itemId;
        protected String dropListId;

        public String getTypeId() {
            return this.typeId;
        }

        public String getStateId() {
            return this.stateId;
        }

        public String getItemId() {
            return this.itemId;
        }

        public String getDropListId() {
            return this.dropListId;
        }
    }
}

