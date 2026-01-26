/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.buffer;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.block.BlockUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockMigration;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.prefab.config.SelectionPrefabSerializer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferCodec;
import com.hypixel.hytale.server.core.prefab.selection.buffer.UpdateBinaryPrefabException;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBufferBlockEntry;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.BsonUtil;
import com.hypixel.hytale.server.core.util.io.ByteBufUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class BinaryPrefabBufferCodec
implements PrefabBufferCodec<ByteBuf> {
    public static final BinaryPrefabBufferCodec INSTANCE = new BinaryPrefabBufferCodec();
    public static final int VERSION = 21;
    private static final int MASK_CHANCE = 1;
    private static final int MASK_COMPONENTS = 2;
    private static final int MASK_FLUID = 4;
    private static final int MASK_SUPPORT_VALUE = 8;
    private static final int MASK_FILLER = 16;
    private static final int MASK_ROTATION = 32;

    @Override
    @Nonnull
    public PrefabBuffer deserialize(Path path, @Nonnull ByteBuf buffer) {
        int worldVersion;
        int version = buffer.readUnsignedShort();
        if (version == 18553) {
            throw new UpdateBinaryPrefabException("Old prefab format!");
        }
        if (21 < version) {
            throw new IllegalStateException("Prefab version is newer than supported. Given: " + version);
        }
        int n = worldVersion = version < 17 ? buffer.readUnsignedShort() : 0;
        if (version == 11) {
            buffer.readUnsignedShort();
        }
        int entityVersion = version >= 14 && version < 17 ? buffer.readUnsignedShort() : 0;
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        int blockIdVersion = 8;
        if (version >= 13) {
            blockIdVersion = buffer.readShort();
        }
        Vector3i anchor = Vector3i.ZERO;
        if (version >= 16) {
            long packedAnchor = buffer.readLong();
            anchor = new Vector3i(BlockUtil.unpackX(packedAnchor), BlockUtil.unpackY(packedAnchor), BlockUtil.unpackZ(packedAnchor));
        }
        Function<String, String> blockMigration = null;
        Map<Integer, BlockMigration> blockMigrationMap = BlockMigration.getAssetMap().getAssetMap();
        int v = blockIdVersion;
        BlockMigration migration = blockMigrationMap.get(v);
        while (migration != null) {
            blockMigration = blockMigration == null ? migration::getMigration : blockMigration.andThen(migration::getMigration);
            migration = blockMigrationMap.get(++v);
        }
        int blockNameCount = buffer.readInt();
        Int2ObjectOpenHashMap<BlockIdEntry> blockIdMapping = new Int2ObjectOpenHashMap<BlockIdEntry>(blockNameCount);
        for (int i = 0; i < blockNameCount; ++i) {
            try {
                int readId = buffer.readInt();
                BlockIdEntry block = this.deserializeBlock(buffer, assetMap, blockMigration);
                blockIdMapping.put(readId, block);
                continue;
            }
            catch (Exception e) {
                throw new IllegalStateException("Failed to deserialize block name #" + i, e);
            }
        }
        IndexedLookupTableAssetMap<String, Fluid> fluidMap = Fluid.getAssetMap();
        int fluidNameCount = version >= 18 ? buffer.readInt() : 0;
        Int2ObjectOpenHashMap<FluidIdEntry> fluidIdMapping = new Int2ObjectOpenHashMap<FluidIdEntry>(fluidNameCount);
        for (int i = 0; i < fluidNameCount; ++i) {
            try {
                int readId = buffer.readInt();
                FluidIdEntry fluid = this.deserializeFluid(buffer, fluidMap);
                fluidIdMapping.put(readId, fluid);
                continue;
            }
            catch (Exception e) {
                throw new IllegalStateException("Failed to deserialize block name #" + i, e);
            }
        }
        PrefabBuffer.Builder builder = PrefabBuffer.newBuilder();
        builder.setAnchor(anchor);
        int columnCount = buffer.readInt();
        for (int i = 0; i < columnCount; ++i) {
            int columnIndex = buffer.readInt();
            int blocks = buffer.readInt();
            PrefabBufferBlockEntry[] blockEntries = new PrefabBufferBlockEntry[blocks];
            for (int j = 0; j < blocks; ++j) {
                short y = buffer.readShort();
                int readId = buffer.readInt();
                BlockIdEntry block = (BlockIdEntry)blockIdMapping.get(readId);
                short mask = buffer.readUnsignedByte();
                boolean hasChance = (mask & 1) == 1;
                boolean hasState = (mask & 2) == 2;
                boolean hasFluid = (mask & 4) == 4;
                boolean hasSupportValue = (mask & 8) == 8;
                boolean hasFiller = (mask & 0x10) == 16;
                boolean hasRotation = (mask & 0x20) == 32;
                float chance = hasChance ? buffer.readFloat() : 1.0f;
                Holder<ChunkStore> holder = null;
                if (hasState) {
                    BsonDocument doc = BsonUtil.readFromBinaryStream(buffer);
                    holder = version < 15 ? SelectionPrefabSerializer.legacyStateDecode(doc) : (version < 17 ? ChunkStore.REGISTRY.deserialize(doc, worldVersion) : ChunkStore.REGISTRY.deserialize(doc));
                }
                byte supportValue = 0;
                if (hasSupportValue) {
                    supportValue = (byte)(buffer.readByte() & 0xF);
                }
                int filler = 0;
                if (hasFiller) {
                    filler = buffer.readUnsignedShort();
                }
                short rotation = 0;
                if (hasRotation) {
                    rotation = buffer.readUnsignedByte();
                }
                int fluidId = 0;
                byte fluidLevel = 0;
                if (hasFluid) {
                    int id = buffer.readInt();
                    fluidId = ((FluidIdEntry)fluidIdMapping.get((int)id)).id;
                    fluidLevel = buffer.readByte();
                }
                blockEntries[j] = new PrefabBufferBlockEntry(y, block.id, block.key, chance, holder, fluidId, fluidLevel, supportValue, rotation, filler);
            }
            int entityCount = buffer.readUnsignedShort();
            Holder[] entityHolders = null;
            if (entityCount > 0) {
                entityHolders = new Holder[entityCount];
                for (int j = 0; j < entityCount; ++j) {
                    try {
                        if (version >= 12 && version < 14) {
                            entityVersion = buffer.readUnsignedShort();
                        }
                        BsonDocument entityDocument = BsonUtil.readFromBinaryStream(buffer);
                        Holder<EntityStore> entityHolder = version < 14 ? SelectionPrefabSerializer.legacyEntityDecode(entityDocument, entityVersion) : (version < 17 ? EntityStore.REGISTRY.deserialize(entityDocument, entityVersion) : EntityStore.REGISTRY.deserialize(entityDocument));
                        entityHolders[j] = entityHolder;
                        continue;
                    }
                    catch (Exception e) {
                        throw new IllegalStateException("Failed to deserialize entity wrapper #" + i, e);
                    }
                }
            }
            int x = MathUtil.unpackLeft(columnIndex);
            int z = MathUtil.unpackRight(columnIndex);
            builder.addColumn(x, z, blockEntries, entityHolders);
        }
        return builder.build();
    }

    @Nonnull
    private BlockIdEntry deserializeBlock(@Nonnull ByteBuf buffer, @Nonnull BlockTypeAssetMap<String, BlockType> assetMap, @Nullable Function<String, String> blockMigration) {
        String blockTypeString;
        String blockTypeKey = blockTypeString = ByteBufUtil.readUTF(buffer);
        if (blockMigration != null) {
            blockTypeKey = blockMigration.apply(blockTypeKey);
        }
        int blockId = BlockType.getBlockIdOrUnknown(assetMap, blockTypeKey, "Failed to find block '%s'", blockTypeString);
        return new BlockIdEntry(blockId, blockTypeKey);
    }

    @Nonnull
    private FluidIdEntry deserializeFluid(@Nonnull ByteBuf buffer, @Nonnull IndexedLookupTableAssetMap<String, Fluid> assetMap) {
        String fluidName = ByteBufUtil.readUTF(buffer);
        int fluidId = Fluid.getFluidIdOrUnknown(assetMap, fluidName, "Failed to find fluid '%s'", fluidName);
        return new FluidIdEntry(fluidId, fluidName);
    }

    @Override
    @Nonnull
    public ByteBuf serialize(@Nonnull PrefabBuffer prefabBuffer) {
        PrefabBuffer.PrefabBufferAccessor access = prefabBuffer.newAccess();
        Int2ObjectOpenHashMap blockNameMapping = new Int2ObjectOpenHashMap();
        Int2ObjectOpenHashMap fluidNameMapping = new Int2ObjectOpenHashMap();
        int[] counts = new int[3];
        access.forEachRaw((x, z, blocks, o) -> {
            counts[0] = counts[0] + 1;
            counts[1] = counts[1] + blocks;
            return true;
        }, (x, y, z, mask, blockId, chance, holder, support, rotation, filler, o) -> {
            if (blockNameMapping.containsKey(blockId)) {
                return;
            }
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            BlockType blockType = assetMap.getAsset(blockId);
            if (blockType == null) {
                blockType = BlockType.UNKNOWN;
            }
            blockNameMapping.put(blockId, blockType.getId().toString());
        }, (x, y, z, fluidId, level, o) -> {
            if (fluidNameMapping.containsKey(fluidId)) {
                return;
            }
            IndexedLookupTableAssetMap<String, Fluid> assetMap = Fluid.getAssetMap();
            Fluid fluidType = assetMap.getAsset(fluidId);
            if (fluidType == null) {
                fluidType = Fluid.UNKNOWN;
            }
            fluidNameMapping.put(fluidId, fluidType.getId());
        }, (x, z, entityHolders, o) -> {
            if (entityHolders != null) {
                counts[2] = counts[2] + entityHolders.length;
            }
        }, null);
        ByteBuf buffer = Unpooled.buffer(4 + blockNameMapping.size() * 261 + counts[0] * 8 + counts[1] * 13 + counts[2] * 2048);
        buffer.writeShort(21);
        buffer.writeShort(BlockMigration.getAssetMap().getAssetCount());
        buffer.writeLong(BlockUtil.pack(prefabBuffer.getAnchorX(), prefabBuffer.getAnchorY(), prefabBuffer.getAnchorZ()));
        buffer.writeInt(blockNameMapping.size());
        blockNameMapping.int2ObjectEntrySet().fastForEach(entry -> {
            buffer.writeInt(entry.getIntKey());
            ByteBufUtil.writeUTF(buffer, (String)entry.getValue());
        });
        buffer.writeInt(fluidNameMapping.size());
        fluidNameMapping.int2ObjectEntrySet().fastForEach(entry -> {
            buffer.writeInt(entry.getIntKey());
            ByteBufUtil.writeUTF(buffer, (String)entry.getValue());
        });
        buffer.writeInt(access.getColumnCount());
        access.forEachRaw((x, z, blocks, o) -> {
            buffer.writeInt(MathUtil.packInt(x, z));
            buffer.writeInt(blocks);
            return true;
        }, (x, y, z, entryMask, blockId, chance, holder, supportValue, rotation, filler, o) -> {
            buffer.writeShort((short)y);
            buffer.writeInt(blockId);
            boolean hasChance = chance < 1.0f;
            boolean hasComponents = holder != null;
            int mask = 0;
            if (hasChance) {
                mask |= 1;
            }
            if (hasComponents) {
                mask |= 2;
            }
            if ((entryMask & 0xC0) != 0) {
                mask |= 4;
            }
            if (supportValue != 0) {
                mask |= 8;
            }
            if (filler != 0) {
                mask |= 0x10;
            }
            if (rotation != 0) {
                mask |= 0x20;
            }
            buffer.writeByte(mask);
            if (hasChance) {
                buffer.writeFloat(chance);
            }
            if (hasComponents) {
                try {
                    BsonUtil.writeToBinaryStream(buffer, ChunkStore.REGISTRY.serialize(holder));
                }
                catch (Throwable t) {
                    throw new IllegalStateException(String.format("Exception while writing %d, %d, %d state!", x, y, z), t);
                }
            }
            if (supportValue != 0) {
                buffer.writeByte(supportValue);
            }
            if (filler != 0) {
                buffer.writeShort(filler);
            }
            if (rotation != 0) {
                buffer.writeByte(rotation);
            }
        }, (x, y, z, fluidId, level, o) -> {
            buffer.writeInt(fluidId);
            buffer.writeByte(level);
        }, (x, z, entityHolders, o) -> {
            int entities = entityHolders != null ? entityHolders.length : 0;
            buffer.writeShort(entities);
            for (int i = 0; i < entities; ++i) {
                Holder entityHolder = entityHolders[i];
                try {
                    BsonDocument document = EntityStore.REGISTRY.serialize(entityHolder);
                    BsonUtil.writeToBinaryStream(buffer, document);
                    continue;
                }
                catch (Exception e) {
                    throw new IllegalStateException(String.format("Failed to write EntityWrapper at %d, %d #%d", x, z, i), e);
                }
            }
        }, null);
        return buffer;
    }

    private static class BlockIdEntry {
        public int id;
        public String key;

        public BlockIdEntry(int id, String key) {
            this.id = id;
            this.key = key;
        }
    }

    private static class FluidIdEntry {
        public int id;
        public String key;

        public FluidIdEntry(int id, String key) {
            this.id = id;
            this.key = key;
        }
    }
}

