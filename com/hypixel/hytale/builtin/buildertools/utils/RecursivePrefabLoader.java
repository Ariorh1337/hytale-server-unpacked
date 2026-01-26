/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.buildertools.utils;

import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.modules.prefabspawner.PrefabSpawnerState;
import com.hypixel.hytale.server.core.prefab.PrefabLoadException;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.core.prefab.PrefabWeights;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabLoader;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.world.meta.BlockStateModule;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class RecursivePrefabLoader<T>
implements BiFunction<String, Random, T> {
    private static final int MAX_RECURSION_DEPTH = 10;
    protected final Path rootPrefabsDir;
    protected final Function<String, T> prefabsLoader;
    protected final Set<Path> visitedFiles = new HashSet<Path>();
    @Nullable
    protected final ComponentType<ChunkStore, PrefabSpawnerState> prefabComponentType = BlockStateModule.get().getComponentType(PrefabSpawnerState.class);
    private int depthTracker = 0;

    public RecursivePrefabLoader(Path rootPrefabsDir, Function<String, T> prefabsLoader) {
        this.rootPrefabsDir = rootPrefabsDir;
        this.prefabsLoader = prefabsLoader;
    }

    @Override
    @Nonnull
    public T apply(@Nonnull String name, @Nonnull Random random) {
        return this.load(name, random);
    }

    @Nonnull
    public T load(@Nonnull String name, @Nonnull Random random) {
        return this.load(0, 0, 0, name, PrefabRotation.ROTATION_0, PrefabWeights.NONE, random);
    }

    @Nonnull
    protected T load(int x, int y, int z, @Nonnull String name, PrefabRotation rotation, @Nonnull PrefabWeights weights, @Nonnull Random random) {
        if (this.depthTracker >= 10) {
            throw new PrefabLoadException(PrefabLoadException.Type.NOT_FOUND, "Prefab nesting limit exceeded!");
        }
        try {
            ++this.depthTracker;
            DistinctCollector<Path> prefabs = new DistinctCollector<Path>();
            PrefabLoader.resolvePrefabs(this.rootPrefabsDir, RecursivePrefabLoader.stripSuffix(name), prefabs);
            if (prefabs.isEmpty()) {
                throw new PrefabLoadException(PrefabLoadException.Type.NOT_FOUND, "Could not locate prefab: " + name);
            }
            if (prefabs.size() == 1) {
                T t = this.loadSinglePrefab(x, y, z, (Path)prefabs.getFirst(), rotation, random);
                return t;
            }
            if (weights.size() > 0) {
                T t = this.loadWeightedPrefab(x, y, z, name, prefabs, rotation, weights, random);
                return t;
            }
            T t = this.loadRandomPrefab(x, y, z, prefabs, rotation, random);
            return t;
        }
        catch (IOException e) {
            throw new PrefabLoadException(PrefabLoadException.Type.ERROR, (Throwable)e);
        }
        finally {
            --this.depthTracker;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected T loadSinglePrefab(int x, int y, int z, @Nonnull Path file, PrefabRotation rotation, Random random) {
        if (!this.visitedFiles.add(file)) {
            throw new PrefabLoadException(PrefabLoadException.Type.ERROR, "Cyclic prefab dependency detected: " + String.valueOf(file));
        }
        try {
            String path = this.rootPrefabsDir.relativize(file).toString();
            T t = this.loadPrefab(x, y, z, RecursivePrefabLoader.appendSuffix(path), rotation, random);
            return t;
        }
        finally {
            this.visitedFiles.remove(file);
        }
    }

    protected T loadWeightedPrefab(int x, int y, int z, @Nonnull String name, @Nonnull List<Path> files, PrefabRotation rotation, @Nonnull PrefabWeights weights, @Nonnull Random random) {
        Path[] prefabs = (Path[])files.toArray(Path[]::new);
        Path prefab = weights.get(prefabs, path -> PrefabLoader.resolveRelativeJsonPath(name, path, this.rootPrefabsDir), random);
        if (prefab != null) {
            return this.loadSinglePrefab(x, y, z, prefab, rotation, random);
        }
        throw new PrefabLoadException(PrefabLoadException.Type.ERROR, String.format("Unable to pick weighted prefab! Files: %s, Weights: %s", files, weights));
    }

    protected T loadRandomPrefab(int x, int y, int z, @Nonnull List<Path> files, PrefabRotation rotation, @Nonnull Random random) {
        Path file = files.get(random.nextInt(files.size()));
        return this.loadSinglePrefab(x, y, z, file, rotation, random);
    }

    protected abstract T loadPrefab(int var1, int var2, int var3, String var4, PrefabRotation var5, Random var6);

    @Nonnull
    private static String stripSuffix(@Nonnull String path) {
        return path.replace(".prefab.json", "");
    }

    @Nonnull
    private static String appendSuffix(@Nonnull String path) {
        if (path.endsWith(".prefab.json")) {
            return path;
        }
        return path + ".prefab.json";
    }

    protected static class DistinctCollector<T>
    extends ArrayList<T>
    implements Consumer<T> {
        protected DistinctCollector() {
        }

        @Override
        public void accept(T t) {
            if (this.contains(t)) {
                return;
            }
            this.add(t);
        }
    }

    public static class BlockSelectionLoader
    extends RecursivePrefabLoader<BlockSelection> {
        public BlockSelectionLoader(Path rootPrefabsDir, @Nonnull Function<String, BlockSelection> prefabsLoader) {
            super(rootPrefabsDir, prefabsLoader.andThen(BlockSelection::cloneSelection));
        }

        @Override
        @Nonnull
        protected BlockSelection loadPrefab(int x, int y, int z, String file, @Nonnull PrefabRotation rotation, @Nonnull Random random) {
            BlockSelection prefab = (BlockSelection)this.prefabsLoader.apply(file);
            prefab.setPosition(x, y, z);
            ObjectArrayList children = new ObjectArrayList();
            prefab.forEachBlock((dx, dy, dz, block) -> {
                Holder<ChunkStore> state = block.holder();
                if (state == null) {
                    return;
                }
                PrefabSpawnerState spawner = (PrefabSpawnerState)state.getComponent(this.prefabComponentType);
                if (spawner == null) {
                    return;
                }
                BlockType blockType = BlockType.getAssetMap().getAsset(block.blockId());
                int childX = x + rotation.getX(dx, dz);
                int childY = y + dy;
                int childZ = z + rotation.getZ(dx, dz);
                String childPath = spawner.getPrefabPath();
                PrefabWeights childWeights = spawner.getPrefabWeights();
                PrefabRotation childRotation = rotation.add(BlockSelectionLoader.getRotation(blockType));
                BlockSelection child = (BlockSelection)this.load(childX, childY, childZ, childPath, childRotation, childWeights, random);
                children.add(child);
            });
            for (int i = 0; i < children.size(); ++i) {
                prefab.add((BlockSelection)children.get(i));
            }
            return prefab;
        }

        @Nonnull
        private static PrefabRotation getRotation(@Nonnull BlockType blockType) {
            Rotation rotation = blockType.getRotationYawPlacementOffset();
            if (rotation == null) {
                return PrefabRotation.ROTATION_0;
            }
            return PrefabRotation.fromRotation(rotation);
        }
    }
}

