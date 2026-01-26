/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.selection.buffer;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockMigration;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.prefab.config.SelectionPrefabSerializer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferDeserializer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBuffer;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.PrefabBufferBlockEntry;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonValue;

public class BsonPrefabBufferDeserializer
implements PrefabBufferDeserializer<BsonDocument> {
    public static final BsonPrefabBufferDeserializer INSTANCE = new BsonPrefabBufferDeserializer();
    public static final BsonInt32 LEGACY_BLOCK_ID_VERSION = new BsonInt32(8);
    private static final BsonInt32 DEFAULT_SUPPORT_VALUE = new BsonInt32(0);
    private static final BsonInt32 DEFAULT_FILLER_VALUE = new BsonInt32(0);
    private static final BsonInt32 DEFAULT_ROTATION_VALUE = new BsonInt32(0);

    @Override
    @Nonnull
    public PrefabBuffer deserialize(Path path, @Nonnull BsonDocument document) {
        BsonValue fluidsValue;
        Int2ObjectMap<PrefabBufferBlockEntry> column;
        int columnIndex;
        int entityVersion;
        int version;
        BsonValue versionValue = document.get("version");
        int n = version = versionValue != null ? versionValue.asInt32().getValue() : -1;
        if (version > 8) {
            throw new IllegalArgumentException("Prefab version is too new: " + version + " by expected 8");
        }
        int worldVersion = version < 4 ? SelectionPrefabSerializer.readWorldVersion(document) : 0;
        BsonValue entityVersionValue = document.get("entityVersion");
        int n2 = entityVersion = entityVersionValue != null ? entityVersionValue.asInt32().getValue() : 0;
        if (version < 1) {
            throw new IllegalArgumentException("Prefab version " + version + " is no longer supported. Please re-save the prefab.");
        }
        Vector3i anchor = new Vector3i();
        anchor.x = document.getInt32("anchorX").getValue();
        anchor.y = document.getInt32("anchorY").getValue();
        anchor.z = document.getInt32("anchorZ").getValue();
        int blockIdVersion = document.getInt32("blockIdVersion", LEGACY_BLOCK_ID_VERSION).getValue();
        Function<String, String> blockMigration = null;
        Map<Integer, BlockMigration> blockMigrationMap = BlockMigration.getAssetMap().getAssetMap();
        int v = blockIdVersion;
        BlockMigration migration = blockMigrationMap.get(v);
        while (migration != null) {
            blockMigration = blockMigration == null ? migration::getMigration : blockMigration.andThen(migration::getMigration);
            migration = blockMigrationMap.get(++v);
        }
        Int2ObjectOpenHashMap columnMap = new Int2ObjectOpenHashMap();
        PrefabBuffer.Builder builder = PrefabBuffer.newBuilder();
        builder.setAnchor(anchor);
        BsonValue blocksValue = document.get("blocks");
        if (blocksValue != null) {
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            BsonArray blocksArray = blocksValue.asArray();
            for (BsonValue blockValue : blocksArray) {
                PrefabBufferBlockEntry existing;
                BsonDocument blockDocument = blockValue.asDocument();
                int realX = blockDocument.getInt32("x").getValue();
                int realY = blockDocument.getInt32("y").getValue();
                int realZ = blockDocument.getInt32("z").getValue();
                int x = realX - anchor.x;
                int y = realY - anchor.y;
                int z = realZ - anchor.z;
                if (Short.MIN_VALUE > x || x > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Violation X: Short.MIN_VALUE < " + x + " < Short.MAX_VALUE");
                }
                if (Short.MIN_VALUE > y || y > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Violation Y: Short.MIN_VALUE < " + y + " < Short.MAX_VALUE");
                }
                if (Short.MIN_VALUE > z || z > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Violation Z: Short.MIN_VALUE < " + z + " < Short.MAX_VALUE");
                }
                PrefabBufferBlockEntry blockEntry = builder.newBlockEntry(y);
                try {
                    BsonPrefabBufferDeserializer.deserializeBlockType(blockEntry, blockDocument, assetMap, blockMigration);
                }
                catch (Throwable t) {
                    throw new IllegalStateException("Failed to load block type for " + String.valueOf(path) + " at " + realX + ", " + realY + ", " + realZ, t);
                }
                BsonPrefabBufferDeserializer.deserializeState(blockEntry, blockDocument, version, worldVersion);
                blockEntry.supportValue = (byte)blockDocument.getInt32("support", DEFAULT_SUPPORT_VALUE).getValue();
                blockEntry.filler = blockDocument.getInt32("filler", DEFAULT_FILLER_VALUE).getValue();
                blockEntry.rotation = blockDocument.getInt32("rotation", DEFAULT_ROTATION_VALUE).getValue();
                columnIndex = MathUtil.packInt(x, z);
                column = (Int2ObjectMap)columnMap.get(columnIndex);
                if (column == null) {
                    column = new Int2ObjectOpenHashMap();
                    columnMap.put(columnIndex, column);
                }
                if ((existing = column.putIfAbsent(y, blockEntry)) == null) continue;
                throw new IllegalStateException("Block is already present in column. Given: " + realX + ", " + realY + ", " + realZ + ", " + blockEntry.blockTypeKey + " - Existing: " + existing.y + ", " + existing.blockTypeKey);
            }
        }
        if ((fluidsValue = document.get("fluids")) != null) {
            IndexedLookupTableAssetMap<String, Fluid> assetMap = Fluid.getAssetMap();
            BsonArray fluidsArray = fluidsValue.asArray();
            for (BsonValue fluidValue : fluidsArray) {
                BsonDocument fluidDocument = fluidValue.asDocument();
                int realX = fluidDocument.getInt32("x").getValue();
                int realY = fluidDocument.getInt32("y").getValue();
                int realZ = fluidDocument.getInt32("z").getValue();
                int x = realX - anchor.x;
                int y = realY - anchor.y;
                int z = realZ - anchor.z;
                if (Short.MIN_VALUE > x || x > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Violation X: Short.MIN_VALUE < " + x + " < Short.MAX_VALUE");
                }
                if (Short.MIN_VALUE > y || y > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Violation Y: Short.MIN_VALUE < " + y + " < Short.MAX_VALUE");
                }
                if (Short.MIN_VALUE > z || z > Short.MAX_VALUE) {
                    throw new IllegalArgumentException("Violation Z: Short.MIN_VALUE < " + z + " < Short.MAX_VALUE");
                }
                columnIndex = MathUtil.packInt(x, z);
                column = (Int2ObjectOpenHashMap<PrefabBufferBlockEntry>)columnMap.get(columnIndex);
                if (column == null) {
                    column = new Int2ObjectOpenHashMap<PrefabBufferBlockEntry>();
                    columnMap.put(columnIndex, column);
                }
                PrefabBufferBlockEntry entry2 = column.computeIfAbsent(y, builder::newBlockEntry);
                String fluidName = fluidDocument.getString("name").getValue();
                entry2.fluidId = Fluid.getFluidIdOrUnknown(fluidName, "Unknown fluid '%s'", fluidName);
                entry2.fluidLevel = (byte)fluidDocument.getInt32("level").getValue();
            }
        }
        Int2ObjectOpenHashMap<List<Holder<EntityStore>>> entityMap = BsonPrefabBufferDeserializer.deserializeEntityHolders(document, anchor, version, entityVersion);
        columnMap.int2ObjectEntrySet().fastForEach(entry -> {
            int columnIndex = entry.getIntKey();
            int x = MathUtil.unpackLeft(columnIndex);
            int z = MathUtil.unpackRight(columnIndex);
            Int2ObjectMap columnBlockMap = (Int2ObjectMap)entry.getValue();
            PrefabBufferBlockEntry[] entries = (PrefabBufferBlockEntry[])columnBlockMap.values().toArray(PrefabBufferBlockEntry[]::new);
            Arrays.sort(entries, Comparator.comparingInt(o -> o.y));
            List entityColumn = (List)entityMap.remove(columnIndex);
            Holder[] entityArray = entityColumn != null && !entityColumn.isEmpty() ? (Holder[])entityColumn.toArray(Holder[]::new) : null;
            builder.addColumn(x, z, entries, entityArray);
        });
        entityMap.int2ObjectEntrySet().fastForEach(entry -> {
            Holder[] entityArray;
            int columnIndex = entry.getIntKey();
            int x = MathUtil.unpackLeft(columnIndex);
            int z = MathUtil.unpackRight(columnIndex);
            List entityColumn = (List)entry.getValue();
            Holder[] holderArray = entityArray = !entityColumn.isEmpty() ? (Holder[])entityColumn.toArray(Holder[]::new) : null;
            if (entityArray != null) {
                builder.addColumn(x, z, PrefabBufferBlockEntry.EMPTY_ARRAY, entityArray);
            }
        });
        return builder.build();
    }

    private static void deserializeBlockType(@Nonnull PrefabBufferBlockEntry blockEntry, @Nonnull BsonDocument blockDocument, @Nonnull BlockTypeAssetMap<String, BlockType> assetMap, @Nullable Function<String, String> blockMigration) {
        String blockType = blockDocument.getString("name").getValue();
        int idx = blockType.indexOf(37);
        String blockTypeStr = idx != -1 ? blockType.substring(idx + 1) : blockType;
        blockEntry.blockTypeKey = blockTypeStr;
        if (blockMigration != null) {
            blockEntry.blockTypeKey = blockMigration.apply(blockEntry.blockTypeKey);
        }
        blockEntry.blockId = BlockType.getBlockIdOrUnknown(assetMap, blockEntry.blockTypeKey, "Failed to find block. Given %s", blockTypeStr);
        if (idx != -1) {
            String chanceString = blockType.substring(0, idx);
            float chancePercent = Float.parseFloat(chanceString);
            if (chancePercent < 0.0f) {
                throw new IllegalArgumentException("Chance is smaller than 0%. Given: " + chancePercent);
            }
            if (chancePercent > 100.0f) {
                throw new IllegalArgumentException("Chance is larger than 100%. Given: " + chancePercent);
            }
            blockEntry.chance = chancePercent / 100.0f;
        }
    }

    private static void deserializeState(@Nonnull PrefabBufferBlockEntry blockEntry, @Nonnull BsonDocument blockDocument, int version, int worldVersion) {
        if (version <= 2) {
            BsonValue stateValue = blockDocument.get("state");
            if (stateValue != null) {
                blockEntry.state = SelectionPrefabSerializer.legacyStateDecode(stateValue.asDocument());
            }
        } else {
            BsonValue stateValue = blockDocument.get("components");
            if (stateValue != null) {
                blockEntry.state = version < 4 ? ChunkStore.REGISTRY.deserialize(stateValue.asDocument(), worldVersion) : ChunkStore.REGISTRY.deserialize(stateValue.asDocument());
            }
        }
    }

    @Nonnull
    private static Int2ObjectOpenHashMap<List<Holder<EntityStore>>> deserializeEntityHolders(@Nonnull BsonDocument document, @Nonnull Vector3i anchor, int version, int entityVersion) {
        BsonValue entitiesValue = document.get("entities");
        Int2ObjectOpenHashMap<List<Holder<EntityStore>>> entityMap = new Int2ObjectOpenHashMap<List<Holder<EntityStore>>>();
        if (entitiesValue == null) {
            return entityMap;
        }
        BsonArray entitiesArray = entitiesValue.asArray();
        int size = entitiesArray.size();
        for (int i = 0; i < size; ++i) {
            BsonDocument entityDocument = entitiesArray.get(i).asDocument();
            try {
                Holder<EntityStore> entityHolder = version <= 1 ? SelectionPrefabSerializer.legacyEntityDecode(entityDocument, entityVersion) : EntityStore.REGISTRY.deserialize(entityDocument);
                TransformComponent transformComponent = entityHolder.getComponent(TransformComponent.getComponentType());
                assert (transformComponent != null);
                Vector3d position = transformComponent.getPosition();
                position.add(-anchor.x, -anchor.y, -anchor.z);
                int x = MathUtil.floor(position.getX()) & 0xFFFF;
                int z = MathUtil.floor(position.getZ()) & 0xFFFF;
                int columnIndex = MathUtil.packInt(x, z);
                List<Holder<EntityStore>> entityColumn = entityMap.get(columnIndex);
                if (entityColumn == null) {
                    entityColumn = new ObjectArrayList<Holder<EntityStore>>();
                    entityMap.put(columnIndex, entityColumn);
                }
                entityColumn.add(entityHolder);
                continue;
            }
            catch (Exception e) {
                throw new IllegalStateException("Failed to load entity wrapper #" + i + ": " + String.valueOf(entityDocument), e);
            }
        }
        return entityMap;
    }
}

