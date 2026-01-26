/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.codecs.set.SetCodec;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.buildertool.config.BlockTypeListAsset;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockFaceTags;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ConnectedBlockPatternRule {
    public static final BuilderCodec<ConnectedBlockPatternRule> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(ConnectedBlockPatternRule.class, ConnectedBlockPatternRule::new).append(new KeyedCodec<Vector3i>("Position", Vector3i.CODEC, false), (o, relativePosition) -> {
        o.relativePosition = relativePosition;
    }, o -> o.relativePosition).add()).append(new KeyedCodec<IncludeOrExclude>("IncludeOrExclude", new EnumCodec<IncludeOrExclude>(IncludeOrExclude.class), true), (o, allowOrExclude) -> {
        o.includeOrExclude = allowOrExclude;
    }, o -> o.includeOrExclude).add()).append(new KeyedCodec<T[]>("PlacementNormals", new ArrayCodec<AdjacentSide>(new EnumCodec<AdjacentSide>(AdjacentSide.class), AdjacentSide[]::new), false), (o, placementNormals) -> {
        o.placementNormals = placementNormals;
    }, o -> o.placementNormals).add()).documentation("Queries the face the block was placed against")).append(new KeyedCodec<ConnectedBlockFaceTags>("FaceTags", ConnectedBlockFaceTags.CODEC, false), (o, faceTags) -> {
        o.faceTags = faceTags;
    }, o -> o.faceTags).add()).append(new KeyedCodec("Shapes", new SetCodec<BlockPattern.BlockEntry, HashSet>(BlockPattern.BlockEntry.CODEC, HashSet::new, true)), (o, blockTypesAllowed) -> {
        o.shapeBlockTypeKeys = blockTypesAllowed;
    }, o -> o.shapeBlockTypeKeys).add()).append(new KeyedCodec<T[]>("BlockTypes", new ArrayCodec<String>(Codec.STRING, String[]::new)), (o, blockTypesAllowed) -> {
        if (blockTypesAllowed == null) {
            return;
        }
        Collections.addAll(o.blockTypes, blockTypesAllowed);
    }, o -> o.blockTypes != null ? (String[])o.blockTypes.toArray(String[]::new) : null).add()).append(new KeyedCodec<T[]>("BlockTypeLists", Codec.STRING_ARRAY), (o, blockTypeListAssetsAllowed) -> {
        if (blockTypeListAssetsAllowed == null) {
            return;
        }
        o.blockTypeListAssets = new BlockTypeListAsset[((String[])blockTypeListAssetsAllowed).length];
        for (int i = 0; i < ((String[])blockTypeListAssetsAllowed).length; ++i) {
            o.blockTypeListAssets[i] = BlockTypeListAsset.getAssetMap().getAsset(blockTypeListAssetsAllowed[i]);
            if (o.blockTypeListAssets[i] != null) continue;
            System.out.println("BlockTypeListAsset with name: " + blockTypeListAssetsAllowed[i] + " does not exist");
        }
    }, o -> {
        if (o.blockTypeListAssets == null) {
            return null;
        }
        String[] assetIds = new String[o.blockTypeListAssets.length];
        for (int i = 0; i < o.blockTypeListAssets.length; ++i) {
            assetIds[i] = o.blockTypeListAssets[i].getId();
        }
        return assetIds;
    }).add()).build();
    private IncludeOrExclude includeOrExclude;
    private Vector3i relativePosition = Vector3i.ZERO;
    private final HashSet<String> blockTypes = new HashSet();
    @Nullable
    private BlockTypeListAsset[] blockTypeListAssets;
    private Set<BlockPattern.BlockEntry> shapeBlockTypeKeys = Collections.emptySet();
    private ConnectedBlockFaceTags faceTags = ConnectedBlockFaceTags.EMPTY;
    private AdjacentSide[] placementNormals;

    public Vector3i getRelativePosition() {
        return this.relativePosition;
    }

    @Nonnull
    public HashSet<String> getBlockTypes() {
        return this.blockTypes;
    }

    @Nonnull
    public Set<BlockPattern.BlockEntry> getShapeBlockTypeKeys() {
        return this.shapeBlockTypeKeys;
    }

    public ConnectedBlockFaceTags getFaceTags() {
        return this.faceTags;
    }

    @Nullable
    public BlockTypeListAsset[] getBlockTypeListAssets() {
        return this.blockTypeListAssets;
    }

    public AdjacentSide[] getPlacementNormals() {
        return this.placementNormals;
    }

    public boolean isInclude() {
        return this.includeOrExclude == IncludeOrExclude.INCLUDE;
    }

    public static enum AdjacentSide {
        Up(Vector3i.UP),
        Down(Vector3i.DOWN),
        North(Vector3i.NORTH),
        East(Vector3i.EAST),
        South(Vector3i.SOUTH),
        West(Vector3i.WEST);

        public final Vector3i relativePosition;

        private AdjacentSide(Vector3i side) {
            this.relativePosition = side;
        }
    }

    public static enum IncludeOrExclude {
        INCLUDE,
        EXCLUDE;

    }
}

