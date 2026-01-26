/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.prefab.selection.mask.BlockPattern;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockShape;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlocksUtil;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.CustomTemplateConnectedBlockPattern;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.CustomTemplateConnectedBlockRuleSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;

public class CustomConnectedBlockTemplateAsset
implements JsonAssetWithMap<String, DefaultAssetMap<String, CustomConnectedBlockTemplateAsset>> {
    public static final AssetBuilderCodec<String, CustomConnectedBlockTemplateAsset> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(CustomConnectedBlockTemplateAsset.class, CustomConnectedBlockTemplateAsset::new, Codec.STRING, (builder, id) -> {
        builder.id = id;
    }, builder -> builder.id, (builder, data) -> {
        builder.data = data;
    }, builder -> builder.data).append(new KeyedCodec<Boolean>("DontUpdateAfterInitialPlacement", Codec.BOOLEAN, false), (o, dontUpdateAfterInitialPlacement) -> {
        o.dontUpdateAfterInitialPlacement = dontUpdateAfterInitialPlacement;
    }, o -> o.dontUpdateAfterInitialPlacement).documentation("Default to false. When true, will not update the connected block after initial placement. Neighboring block updates won't affect this block when true.").add()).append(new KeyedCodec<Boolean>("ConnectsToOtherMaterials", Codec.BOOLEAN, false), (o, connectsToOtherMaterials) -> {
        o.connectsToOtherMaterials = connectsToOtherMaterials;
    }, o -> o.connectsToOtherMaterials).documentation("Defaults to true. If true, the material will connect to other materials of different block type sets, if false, the material will only connect to its own block types within the material").add()).append(new KeyedCodec<String>("DefaultShape", Codec.STRING, false), (o, defaultShapeName) -> {
        o.defaultShapeName = defaultShapeName;
    }, o -> o.defaultShapeName).add()).append(new KeyedCodec("Shapes", new MapCodec<ConnectedBlockShape, HashMap>(ConnectedBlockShape.CODEC, HashMap::new), true), (o, connectedBlockShapes) -> {
        o.connectedBlockShapes = connectedBlockShapes;
    }, o -> o.connectedBlockShapes).add()).build();
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(CustomConnectedBlockTemplateAsset::getAssetStore));
    private static AssetStore<String, CustomConnectedBlockTemplateAsset, DefaultAssetMap<String, CustomConnectedBlockTemplateAsset>> ASSET_STORE;
    private String id;
    private AssetExtraInfo.Data data;
    protected boolean connectsToOtherMaterials = true;
    private boolean dontUpdateAfterInitialPlacement;
    private String defaultShapeName;
    protected Map<String, ConnectedBlockShape> connectedBlockShapes;

    public static AssetStore<String, CustomConnectedBlockTemplateAsset, DefaultAssetMap<String, CustomConnectedBlockTemplateAsset>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(CustomConnectedBlockTemplateAsset.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, CustomConnectedBlockTemplateAsset> getAssetMap() {
        return CustomConnectedBlockTemplateAsset.getAssetStore().getAssetMap();
    }

    @Nonnull
    public Optional<ConnectedBlocksUtil.ConnectedBlockResult> getConnectedBlockType(World world, Vector3i coordinate, CustomTemplateConnectedBlockRuleSet ruleSet, BlockType blockType, int rotation, Vector3i placementNormal, boolean useDefaultShapeIfNoMatch, boolean isPlacement) {
        for (Map.Entry<String, ConnectedBlockShape> entry : this.connectedBlockShapes.entrySet()) {
            CustomTemplateConnectedBlockPattern[] patterns;
            ConnectedBlockShape connectedBlockShape = entry.getValue();
            if (connectedBlockShape == null || (patterns = connectedBlockShape.getPatternsToMatchAnyOf()) == null) continue;
            for (CustomTemplateConnectedBlockPattern connectedBlockPattern : patterns) {
                Optional<ConnectedBlocksUtil.ConnectedBlockResult> blockRotationIfMatchedOptional = connectedBlockPattern.getConnectedBlockTypeKey(entry.getKey(), world, coordinate, ruleSet, blockType, rotation, placementNormal, isPlacement);
                if (blockRotationIfMatchedOptional.isEmpty()) continue;
                return blockRotationIfMatchedOptional;
            }
        }
        if (useDefaultShapeIfNoMatch) {
            BlockPattern defaultShapeBlockPattern = ruleSet.getShapeNameToBlockPatternMap().get(this.defaultShapeName);
            if (defaultShapeBlockPattern == null) {
                return Optional.empty();
            }
            BlockPattern.BlockEntry defaultBlock = defaultShapeBlockPattern.nextBlockTypeKey(ThreadLocalRandom.current());
            return Optional.of(new ConnectedBlocksUtil.ConnectedBlockResult(defaultBlock.blockTypeKey(), rotation));
        }
        return Optional.empty();
    }

    public boolean isDontUpdateAfterInitialPlacement() {
        return this.dontUpdateAfterInitialPlacement;
    }

    @Override
    public String getId() {
        return this.id;
    }
}

