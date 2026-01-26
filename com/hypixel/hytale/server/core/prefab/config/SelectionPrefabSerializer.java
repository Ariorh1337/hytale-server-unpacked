/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.prefab.config;

import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.codec.DirectDecodeCodec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.codec.lookup.ACodecMapCodec;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.data.unknown.TempUnknownComponent;
import com.hypixel.hytale.component.data.unknown.UnknownComponents;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockMigration;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.fluid.Fluid;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.prefab.selection.buffer.BsonPrefabBufferDeserializer;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.meta.BlockState;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonArray;
import org.bson.BsonDocument;
import org.bson.BsonInt32;
import org.bson.BsonString;
import org.bson.BsonValue;

public class SelectionPrefabSerializer {
    public static final int VERSION = 8;
    private static final Comparator<BsonDocument> COMPARE_BLOCK_POSITION = Comparator.comparingInt(doc -> doc.getInt32("x").getValue()).thenComparingInt(doc -> doc.getInt32("z").getValue()).thenComparingInt(doc -> doc.getInt32("y").getValue());
    private static final BsonInt32 DEFAULT_SUPPORT_VALUE = new BsonInt32(0);
    private static final BsonInt32 DEFAULT_FILLER_VALUE = new BsonInt32(0);
    private static final BsonInt32 DEFAULT_ROTATION_VALUE = new BsonInt32(0);

    private SelectionPrefabSerializer() {
    }

    @Nonnull
    public static BlockSelection deserialize(@Nonnull BsonDocument doc) {
        BsonValue entitiesValues;
        BsonValue fluidsValue;
        int version;
        BsonValue versionValue = doc.get("version");
        int n = version = versionValue != null ? versionValue.asInt32().getValue() : -1;
        if (version <= 0) {
            throw new IllegalArgumentException("Prefab version is too old: " + version);
        }
        if (version > 8) {
            throw new IllegalArgumentException("Prefab version is too new: " + version + " by expected 8");
        }
        int worldVersion = version < 4 ? SelectionPrefabSerializer.readWorldVersion(doc) : 0;
        BsonValue entityVersionValue = doc.get("entityVersion");
        int entityVersion = entityVersionValue != null ? entityVersionValue.asInt32().getValue() : 0;
        int anchorX = doc.getInt32("anchorX").getValue();
        int anchorY = doc.getInt32("anchorY").getValue();
        int anchorZ = doc.getInt32("anchorZ").getValue();
        BlockSelection selection = new BlockSelection();
        selection.setAnchor(anchorX, anchorY, anchorZ);
        int blockIdVersion = doc.getInt32("blockIdVersion", BsonPrefabBufferDeserializer.LEGACY_BLOCK_ID_VERSION).getValue();
        Function<String, String> blockMigration = null;
        Map<Integer, BlockMigration> blockMigrationMap = BlockMigration.getAssetMap().getAssetMap();
        int v = blockIdVersion;
        BlockMigration migration = blockMigrationMap.get(v);
        while (migration != null) {
            blockMigration = blockMigration == null ? migration::getMigration : blockMigration.andThen(migration::getMigration);
            migration = blockMigrationMap.get(++v);
        }
        BsonValue blocksValue = doc.get("blocks");
        if (blocksValue != null) {
            BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
            BsonArray bsonArray = blocksValue.asArray();
            for (int i = 0; i < bsonArray.size(); ++i) {
                int endOfName;
                Fluid.ConversionResult result;
                BsonDocument innerObj = bsonArray.get(i).asDocument();
                int x = innerObj.getInt32("x").getValue();
                int y = innerObj.getInt32("y").getValue();
                int z = innerObj.getInt32("z").getValue();
                String blockTypeStr = innerObj.getString("name").getValue();
                boolean legacyStripName = false;
                if (version <= 4 && (result = Fluid.convertBlockToFluid(blockTypeStr)) != null) {
                    legacyStripName = true;
                    selection.addFluidAtLocalPos(x, y, z, result.fluidId, result.fluidLevel);
                    if (result.blockTypeStr == null) continue;
                }
                int support = 0;
                if (version >= 6) {
                    support = innerObj.getInt32("support", DEFAULT_SUPPORT_VALUE).getValue();
                } else if (blockTypeStr.contains("|Deco")) {
                    legacyStripName = true;
                    support = 15;
                } else if (blockTypeStr.contains("|Support=")) {
                    legacyStripName = true;
                    int start = blockTypeStr.indexOf("|Support=") + "|Support=".length();
                    int end = blockTypeStr.indexOf(124, start);
                    if (end == -1) {
                        end = blockTypeStr.length();
                    }
                    support = Integer.parseInt(blockTypeStr, start, end, 10);
                } else {
                    support = 0;
                }
                int filler = 0;
                if (version >= 7) {
                    filler = innerObj.getInt32("filler", DEFAULT_FILLER_VALUE).getValue();
                } else if (blockTypeStr.contains("|Filler=")) {
                    legacyStripName = true;
                    int start = blockTypeStr.indexOf("|Filler=") + "|Filler=".length();
                    int firstComma = blockTypeStr.indexOf(44, start);
                    if (firstComma == -1) {
                        throw new IllegalArgumentException("Invalid filler metadata! Missing comma");
                    }
                    int secondComma = blockTypeStr.indexOf(44, firstComma + 1);
                    if (secondComma == -1) {
                        throw new IllegalArgumentException("Invalid filler metadata! Missing second comma");
                    }
                    int end = blockTypeStr.indexOf(124, start);
                    if (end == -1) {
                        end = blockTypeStr.length();
                    }
                    int fillerX = Integer.parseInt(blockTypeStr, start, firstComma, 10);
                    int fillerY = Integer.parseInt(blockTypeStr, firstComma + 1, secondComma, 10);
                    int fillerZ = Integer.parseInt(blockTypeStr, secondComma + 1, end, 10);
                    filler = FillerBlockUtil.pack(fillerX, fillerY, fillerZ);
                } else {
                    filler = 0;
                }
                int rotation = 0;
                if (version >= 8) {
                    rotation = innerObj.getInt32("rotation", DEFAULT_ROTATION_VALUE).getValue();
                } else {
                    int end;
                    int start;
                    Rotation yaw = Rotation.None;
                    Rotation pitch = Rotation.None;
                    Rotation roll = Rotation.None;
                    if (blockTypeStr.contains("|Yaw=")) {
                        legacyStripName = true;
                        start = blockTypeStr.indexOf("|Yaw=") + "|Yaw=".length();
                        end = blockTypeStr.indexOf(124, start);
                        if (end == -1) {
                            end = blockTypeStr.length();
                        }
                        yaw = Rotation.ofDegrees(Integer.parseInt(blockTypeStr, start, end, 10));
                    }
                    if (blockTypeStr.contains("|Pitch=")) {
                        legacyStripName = true;
                        start = blockTypeStr.indexOf("|Pitch=") + "|Pitch=".length();
                        end = blockTypeStr.indexOf(124, start);
                        if (end == -1) {
                            end = blockTypeStr.length();
                        }
                        pitch = Rotation.ofDegrees(Integer.parseInt(blockTypeStr, start, end, 10));
                    }
                    if (blockTypeStr.contains("|Roll=")) {
                        legacyStripName = true;
                        start = blockTypeStr.indexOf("|Roll=") + "|Roll=".length();
                        end = blockTypeStr.indexOf(124, start);
                        if (end == -1) {
                            end = blockTypeStr.length();
                        }
                        pitch = Rotation.ofDegrees(Integer.parseInt(blockTypeStr, start, end, 10));
                    }
                    rotation = RotationTuple.index(yaw, pitch, roll);
                }
                if (legacyStripName && (endOfName = blockTypeStr.indexOf(124)) != -1) {
                    blockTypeStr = blockTypeStr.substring(0, endOfName);
                }
                String blockTypeKey = blockTypeStr;
                if (blockMigration != null) {
                    blockTypeKey = blockMigration.apply(blockTypeKey);
                }
                int blockId = BlockType.getBlockIdOrUnknown(assetMap, blockTypeKey, "Failed to find block '%s' in unknown legacy prefab!", blockTypeStr);
                Holder<ChunkStore> wrapper = null;
                if (version <= 2) {
                    stateValue = innerObj.get("state");
                    if (stateValue != null) {
                        wrapper = SelectionPrefabSerializer.legacyStateDecode(stateValue.asDocument());
                    }
                } else {
                    stateValue = innerObj.get("components");
                    if (stateValue != null) {
                        wrapper = version < 4 ? ChunkStore.REGISTRY.deserialize(stateValue.asDocument(), worldVersion) : ChunkStore.REGISTRY.deserialize(stateValue.asDocument());
                    }
                }
                selection.addBlockAtLocalPos(x, y, z, blockId, rotation, filler, support, wrapper);
            }
        }
        if ((fluidsValue = doc.get("fluids")) != null) {
            IndexedLookupTableAssetMap<String, Fluid> assetMap = Fluid.getAssetMap();
            BsonArray bsonArray = fluidsValue.asArray();
            for (int i = 0; i < bsonArray.size(); ++i) {
                BsonDocument innerObj = bsonArray.get(i).asDocument();
                int x = innerObj.getInt32("x").getValue();
                int y = innerObj.getInt32("y").getValue();
                int z = innerObj.getInt32("z").getValue();
                String fluidName = innerObj.getString("name").getValue();
                int fluidId = Fluid.getFluidIdOrUnknown(assetMap, fluidName, "Failed to find fluid '%s' in unknown legacy prefab!", fluidName);
                byte fluidLevel = (byte)innerObj.getInt32("level").getValue();
                selection.addFluidAtLocalPos(x, y, z, fluidId, fluidLevel);
            }
        }
        if ((entitiesValues = doc.get("entities")) != null) {
            BsonArray entities = entitiesValues.asArray();
            for (int i = 0; i < entities.size(); ++i) {
                BsonDocument bsonDocument = entities.get(i).asDocument();
                if (version <= 1) {
                    try {
                        selection.addEntityHolderRaw(SelectionPrefabSerializer.legacyEntityDecode(bsonDocument, entityVersion));
                    }
                    catch (Throwable t) {
                        ((HytaleLogger.Api)HytaleLogger.getLogger().at(Level.WARNING).withCause(t)).log("Exception when loading entity state %s", bsonDocument);
                    }
                    continue;
                }
                selection.addEntityHolderRaw(EntityStore.REGISTRY.deserialize(bsonDocument));
            }
        }
        return selection;
    }

    @Nonnull
    public static BsonDocument serialize(@Nonnull BlockSelection prefab) {
        Objects.requireNonNull(prefab, "null prefab");
        BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
        IndexedLookupTableAssetMap<String, Fluid> fluidMap = Fluid.getAssetMap();
        BsonDocument out = new BsonDocument();
        out.put("version", new BsonInt32(8));
        out.put("blockIdVersion", new BsonInt32(BlockMigration.getAssetMap().getAssetCount()));
        out.put("anchorX", new BsonInt32(prefab.getAnchorX()));
        out.put("anchorY", new BsonInt32(prefab.getAnchorY()));
        out.put("anchorZ", new BsonInt32(prefab.getAnchorZ()));
        BsonArray contentOut = new BsonArray();
        prefab.forEachBlock((x, y, z, block) -> {
            BsonDocument innerObj = new BsonDocument();
            innerObj.put("x", new BsonInt32(x));
            innerObj.put("y", new BsonInt32(y));
            innerObj.put("z", new BsonInt32(z));
            innerObj.put("name", new BsonString(((BlockType)assetMap.getAsset(block.blockId())).getId().toString()));
            if (block.holder() != null) {
                innerObj.put("components", ChunkStore.REGISTRY.serialize(block.holder()));
            }
            if (block.supportValue() != 0) {
                innerObj.put("support", new BsonInt32(block.supportValue()));
            }
            if (block.filler() != 0) {
                innerObj.put("filler", new BsonInt32(block.filler()));
            }
            if (block.rotation() != 0) {
                innerObj.put("rotation", new BsonInt32(block.rotation()));
            }
            contentOut.add(innerObj);
        });
        contentOut.sort((a, b) -> {
            BsonDocument aDoc = a.asDocument();
            BsonDocument bDoc = b.asDocument();
            return COMPARE_BLOCK_POSITION.compare(aDoc, bDoc);
        });
        out.put("blocks", contentOut);
        BsonArray fluidContentOut = new BsonArray();
        prefab.forEachFluid((x, y, z, fluid, level) -> {
            BsonDocument innerObj = new BsonDocument();
            innerObj.put("x", new BsonInt32(x));
            innerObj.put("y", new BsonInt32(y));
            innerObj.put("z", new BsonInt32(z));
            innerObj.put("name", new BsonString(((Fluid)fluidMap.getAsset(fluid)).getId()));
            innerObj.put("level", new BsonInt32(level));
            fluidContentOut.add(innerObj);
        });
        fluidContentOut.sort((a, b) -> {
            BsonDocument aDoc = a.asDocument();
            BsonDocument bDoc = b.asDocument();
            return COMPARE_BLOCK_POSITION.compare(aDoc, bDoc);
        });
        if (!fluidContentOut.isEmpty()) {
            out.put("fluids", fluidContentOut);
        }
        ArrayList entityList = new ArrayList();
        prefab.forEachEntity(holder -> entityList.add(EntityStore.REGISTRY.serialize((Holder<EntityStore>)holder)));
        if (!entityList.isEmpty()) {
            BsonArray entities = new BsonArray();
            entityList.forEach(entities::add);
            out.put("entities", entities);
        }
        return out;
    }

    public static int readWorldVersion(@Nonnull BsonDocument document) {
        int worldVersion = document.containsKey("worldVersion") ? document.getInt32("worldVersion").getValue() : (document.containsKey("worldver") ? document.getInt32("worldver").getValue() : 5);
        if (worldVersion == 18553) {
            throw new IllegalArgumentException("WorldChunk version old format! Update!");
        }
        if (worldVersion > 23) {
            throw new IllegalArgumentException("WorldChunk version is newer than we understand! Version: " + worldVersion + ", Latest Version: 23");
        }
        return worldVersion;
    }

    @Nullable
    public static Holder<EntityStore> legacyEntityDecode(@Nonnull BsonDocument document, int version) {
        String entityTypeStr = document.getString("EntityType").getValue();
        Class<? extends Entity> entityType = EntityModule.get().getClass(entityTypeStr);
        if (entityType == null) {
            UnknownComponents unknownComponents = new UnknownComponents();
            unknownComponents.addComponent(entityTypeStr, new TempUnknownComponent(document));
            return EntityStore.REGISTRY.newHolder(Archetype.of(EntityStore.REGISTRY.getUnknownComponentType()), new Component[]{unknownComponents});
        }
        Function<World, ? extends Entity> constructor = EntityModule.get().getConstructor(entityType);
        if (constructor == null) {
            return null;
        }
        DirectDecodeCodec<? extends Entity> codec = EntityModule.get().getCodec(entityType);
        Objects.requireNonNull(codec, "Unable to create entity because there is no associated codec");
        Entity entity = constructor.apply(null);
        codec.decode(document, entity, new ExtraInfo(version));
        return entity.toHolder();
    }

    @Nonnull
    public static Holder<ChunkStore> legacyStateDecode(@Nonnull BsonDocument document) {
        ExtraInfo extraInto = ExtraInfo.THREAD_LOCAL.get();
        String type = BlockState.TYPE_STRUCTURE.getNow(document, extraInto);
        Class blockStateClass = BlockState.CODEC.getClassFor(type);
        if (blockStateClass != null) {
            try {
                BlockState t = (BlockState)BlockState.CODEC.decode(document, extraInto);
                Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
                ComponentType componentType = BlockStateModule.get().getComponentType(blockStateClass);
                if (componentType == null) {
                    throw new IllegalArgumentException("Unable to find component type for: " + String.valueOf(blockStateClass));
                }
                holder.addComponent(componentType, t);
                return holder;
            }
            catch (ACodecMapCodec.UnknownIdException t) {
                // empty catch block
            }
        }
        Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
        UnknownComponents unknownComponents = new UnknownComponents();
        unknownComponents.addComponent(type, new TempUnknownComponent(document));
        holder.addComponent(ChunkStore.REGISTRY.getUnknownComponentType(), unknownComponents);
        return holder;
    }
}

