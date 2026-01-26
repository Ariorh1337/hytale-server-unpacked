/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.blockphysics;

import com.hypixel.hytale.builtin.blockphysics.WorldValidationUtil;
import com.hypixel.hytale.common.util.ExceptionUtil;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.data.unknown.UnknownComponents;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.event.IEventDispatcher;
import com.hypixel.hytale.server.core.HytaleServer;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferUtil;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
import com.hypixel.hytale.server.core.universe.world.ValidationOption;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.FillerBlockUtil;
import com.hypixel.hytale.server.core.util.io.FileUtil;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabBufferValidator {
    private static final FillerBlockUtil.FillerFetcher<IPrefabBuffer, Void> FILLER_FETCHER = new FillerBlockUtil.FillerFetcher<IPrefabBuffer, Void>(){

        @Override
        public int getBlock(IPrefabBuffer iPrefabBuffer, Void unused, int x, int y, int z) {
            return iPrefabBuffer.getBlockId(x, y, z);
        }

        @Override
        public int getFiller(IPrefabBuffer iPrefabBuffer, Void unused, int x, int y, int z) {
            return iPrefabBuffer.getFiller(x, y, z);
        }

        @Override
        public int getRotationIndex(IPrefabBuffer iPrefabBuffer, Void unused, int x, int y, int z) {
            return iPrefabBuffer.getRotationIndex(x, y, z);
        }
    };

    @Nonnull
    public static List<String> validateAllPrefabs(@Nonnull List<ValidationOption> list) {
        EnumSet<ValidationOption> options = !list.isEmpty() ? EnumSet.copyOf(list) : EnumSet.of(ValidationOption.BLOCK_STATES, ValidationOption.ENTITIES, ValidationOption.BLOCKS, ValidationOption.BLOCK_FILLER);
        List<String> out = PrefabBufferValidator.validatePrefabsInPath(PrefabStore.get().getWorldGenPrefabsPath(), options);
        out.addAll(PrefabBufferValidator.validatePrefabsInPath(PrefabStore.get().getAssetPrefabsPath(), options));
        out.addAll(PrefabBufferValidator.validatePrefabsInPath(PrefabStore.get().getServerPrefabsPath(), options));
        return out;
    }

    @Nonnull
    public static List<String> validatePrefabsInPath(@Nonnull Path dataFolder, @Nonnull Set<ValidationOption> options) {
        List<String> list;
        block9: {
            if (!Files.exists(dataFolder, new LinkOption[0])) {
                return new ArrayList<String>();
            }
            Stream<Path> stream = Files.walk(dataFolder, FileUtil.DEFAULT_WALK_TREE_OPTIONS_ARRAY);
            try {
                list = stream.map(path -> {
                    if (!Files.isRegularFile(path, new LinkOption[0]) || !path.toString().endsWith(".prefab.json")) {
                        return null;
                    }
                    IPrefabBuffer prefab = PrefabBufferUtil.getCached(path);
                    try {
                        String results = PrefabBufferValidator.validate(prefab, options);
                        String string = results != null ? String.valueOf(path) + "\n" + results : null;
                        prefab.release();
                        return string;
                    }
                    catch (Throwable throwable) {
                        try {
                            prefab.release();
                            throw throwable;
                        }
                        catch (Throwable e) {
                            return String.valueOf(path) + "\n\t" + ExceptionUtil.combineMessages(e, "\n\t");
                        }
                    }
                }).filter(Objects::nonNull).collect(Collectors.toList());
                if (stream == null) break block9;
            }
            catch (Throwable throwable) {
                try {
                    if (stream != null) {
                        try {
                            stream.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                catch (IOException e) {
                    throw SneakyThrow.sneakyThrow(e);
                }
            }
            stream.close();
        }
        return list;
    }

    @Nullable
    public static String validate(@Nonnull IPrefabBuffer prefab, @Nonnull Set<ValidationOption> options) {
        ComponentType<EntityStore, UnknownComponents<EntityStore>> unknownComponentType = EntityStore.REGISTRY.getUnknownComponentType();
        StringBuilder sb = new StringBuilder();
        int offsetX = prefab.getAnchorX();
        int offsetY = prefab.getAnchorY();
        int offsetZ = prefab.getAnchorZ();
        IPrefabBuffer.RawBlockConsumer<Void> legacyValidator = WorldValidationUtil.blockValidator(offsetX, offsetY, offsetZ, sb, options);
        prefab.forEachRaw(IPrefabBuffer.iterateAllColumns(), (x, y, z, mask, blockId, chance, holder, supportValue, rotation, filler, o) -> {
            legacyValidator.accept(x, y, z, mask, blockId, chance, holder, supportValue, rotation, filler, (Void)o);
            IEventDispatcher<ValidateBlockEvent, ValidateBlockEvent> dispatch = HytaleServer.get().getEventBus().dispatchFor(ValidateBlockEvent.class);
            if (dispatch.hasListener()) {
                dispatch.dispatch(new ValidateBlockEvent(x, y, z, blockId, supportValue, rotation, filler, holder, sb));
            }
            if (options.contains((Object)ValidationOption.BLOCK_FILLER)) {
                FillerBlockUtil.ValidationResult fillerResult = FillerBlockUtil.validateBlock(x, y, z, blockId, rotation, filler, prefab, null, FILLER_FETCHER);
                switch (fillerResult) {
                    case OK: {
                        break;
                    }
                    case INVALID_BLOCK: {
                        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
                        sb.append("\tBlock ").append(blockType != null ? blockType.getId() : "<missing>").append(" at ").append(x).append(", ").append(y).append(", ").append(z).append(" is not valid filler").append('\n');
                        break;
                    }
                    case INVALID_FILLER: {
                        BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
                        sb.append("\tBlock ").append(blockType != null ? blockType.getId() : "<missing>").append(" at ").append(x).append(", ").append(y).append(", ").append(z).append(" has invalid/missing filler blocks").append('\n');
                    }
                }
            }
        }, (x, y, z, fluidId, level, unused) -> {}, (x, z, holders, o) -> {
            if (holders == null) {
                return;
            }
            if (options.contains((Object)ValidationOption.ENTITIES)) {
                for (Holder entityHolder : holders) {
                    UnknownComponents unknownComponents = (UnknownComponents)entityHolder.getComponent(unknownComponentType);
                    if (unknownComponents == null || unknownComponents.getUnknownComponents().isEmpty()) continue;
                    sb.append("\tUnknown Entity Components: ").append(unknownComponents.getUnknownComponents()).append("\n");
                }
            }
        }, (Void)null);
        return !sb.isEmpty() ? sb.toString() : null;
    }

    public record ValidateBlockEvent(int x, int y, int z, int blockId, int support, int rotation, int filler, @Nullable Holder<ChunkStore> holder, StringBuilder reason) implements IEvent<Void>
    {
    }
}

