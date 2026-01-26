/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.npc.asset.builder.validators.asset;

import com.hypixel.hytale.server.flock.config.FlockAsset;
import com.hypixel.hytale.server.npc.asset.builder.validators.AssetValidator;
import java.util.EnumSet;
import javax.annotation.Nonnull;

public class FlockAssetExistsValidator
extends AssetValidator {
    private static final FlockAssetExistsValidator DEFAULT_INSTANCE = new FlockAssetExistsValidator();

    private FlockAssetExistsValidator() {
    }

    private FlockAssetExistsValidator(EnumSet<AssetValidator.Config> config) {
        super(config);
    }

    @Override
    @Nonnull
    public String getDomain() {
        return "FlockAsset";
    }

    @Override
    public boolean test(String flockAsset) {
        return FlockAsset.getAssetMap().getAsset(flockAsset) != null;
    }

    @Override
    @Nonnull
    public String errorMessage(String flockAsset, String attribute) {
        return "The flock asset with the name \"" + flockAsset + "\" does not exist in attribute \"" + attribute + "\"";
    }

    @Override
    @Nonnull
    public String getAssetName() {
        return FlockAsset.class.getSimpleName();
    }

    public static FlockAssetExistsValidator required() {
        return DEFAULT_INSTANCE;
    }

    @Nonnull
    public static FlockAssetExistsValidator withConfig(EnumSet<AssetValidator.Config> config) {
        return new FlockAssetExistsValidator(config);
    }
}

