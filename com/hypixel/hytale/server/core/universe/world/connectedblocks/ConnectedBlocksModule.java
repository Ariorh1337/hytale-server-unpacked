/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.universe.world.connectedblocks;

import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.codec.AssetCodec;
import com.hypixel.hytale.assetstore.event.LoadedAssetsEvent;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.ConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.CustomConnectedBlockPattern;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.CustomConnectedBlockTemplateAsset;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.CustomTemplateConnectedBlockPattern;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.CustomTemplateConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin.RoofConnectedBlockRuleSet;
import com.hypixel.hytale.server.core.universe.world.connectedblocks.builtin.StairConnectedBlockRuleSet;
import javax.annotation.Nonnull;

public class ConnectedBlocksModule
extends JavaPlugin {
    public static final PluginManifest MANIFEST = PluginManifest.corePlugin(ConnectedBlocksModule.class).depends(EntityModule.class).depends(InteractionModule.class).build();
    private static ConnectedBlocksModule instance;

    public static ConnectedBlocksModule get() {
        return instance;
    }

    public ConnectedBlocksModule(@Nonnull JavaPluginInit init) {
        super(init);
        instance = this;
    }

    @Override
    protected void setup() {
        AssetRegistry.register(((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(CustomConnectedBlockTemplateAsset.class, new DefaultAssetMap()).setPath("Item/CustomConnectedBlockTemplates")).setKeyFunction(CustomConnectedBlockTemplateAsset::getId)).setCodec((AssetCodec)CustomConnectedBlockTemplateAsset.CODEC)).build());
        this.getEventRegistry().register(LoadedAssetsEvent.class, BlockType.class, ConnectedBlocksModule::onBlockTypesChanged);
        CustomTemplateConnectedBlockPattern.CODEC.register("Custom", (Class<CustomTemplateConnectedBlockPattern>)CustomConnectedBlockPattern.class, (Codec<CustomTemplateConnectedBlockPattern>)CustomConnectedBlockPattern.CODEC);
        ConnectedBlockRuleSet.CODEC.register("CustomTemplate", (Class<ConnectedBlockRuleSet>)CustomTemplateConnectedBlockRuleSet.class, (Codec<ConnectedBlockRuleSet>)CustomTemplateConnectedBlockRuleSet.CODEC);
        ConnectedBlockRuleSet.CODEC.register("Stair", (Class<ConnectedBlockRuleSet>)StairConnectedBlockRuleSet.class, (Codec<ConnectedBlockRuleSet>)StairConnectedBlockRuleSet.CODEC);
        ConnectedBlockRuleSet.CODEC.register("Roof", (Class<ConnectedBlockRuleSet>)RoofConnectedBlockRuleSet.class, (Codec<ConnectedBlockRuleSet>)RoofConnectedBlockRuleSet.CODEC);
    }

    private static void onBlockTypesChanged(@Nonnull LoadedAssetsEvent<String, BlockType, BlockTypeAssetMap<String, BlockType>> event) {
        for (BlockType blockType : event.getLoadedAssets().values()) {
            ConnectedBlockRuleSet ruleSet = blockType.getConnectedBlockRuleSet();
            if (ruleSet == null) continue;
            ruleSet.updateCachedBlockTypes(blockType, event.getAssetMap());
        }
    }
}

