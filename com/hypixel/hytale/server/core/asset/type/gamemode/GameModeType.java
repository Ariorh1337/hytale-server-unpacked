/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.gamemode;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class GameModeType
implements JsonAssetWithMap<String, DefaultAssetMap<String, GameModeType>> {
    @Nonnull
    public static final AssetBuilderCodec<String, GameModeType> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(GameModeType.class, GameModeType::new, Codec.STRING, (gmType, s) -> {
        gmType.id = s;
    }, gmType -> gmType.id, (gmType, data) -> {
        gmType.data = data;
    }, gmType -> gmType.data).append(new KeyedCodec<T[]>("PermissionGroups", Codec.STRING_ARRAY), (gmType, o) -> {
        gmType.permissionGroups = o;
    }, gmType -> gmType.permissionGroups).add()).append(new KeyedCodec("InteractionsOnEnter", RootInteraction.CHILD_ASSET_CODEC), (gmType, interactions) -> {
        gmType.interactionsOnEnter = interactions;
    }, gmType -> gmType.interactionsOnEnter).addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getValidator().late()).add()).build();
    private static AssetStore<String, GameModeType, DefaultAssetMap<String, GameModeType>> ASSET_STORE;
    @Nonnull
    public static final ValidatorCache<String> VALIDATOR_CACHE;
    protected AssetExtraInfo.Data data;
    protected String id;
    private String[] permissionGroups;
    private String interactionsOnEnter;

    @Nonnull
    public static AssetStore<String, GameModeType, DefaultAssetMap<String, GameModeType>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(GameModeType.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, GameModeType> getAssetMap() {
        return GameModeType.getAssetStore().getAssetMap();
    }

    @Nonnull
    public static GameModeType fromGameMode(@Nonnull GameMode gameMode) {
        GameModeType type = GameModeType.getAssetStore().getAssetMap().getAsset(gameMode.name());
        return type == null ? new GameModeType() : type;
    }

    protected GameModeType() {
    }

    @Nullable
    public String getInteractionsOnEnter() {
        return this.interactionsOnEnter;
    }

    @Nonnull
    public String[] getPermissionGroups() {
        if (this.permissionGroups == null) {
            return ArrayUtil.EMPTY_STRING_ARRAY;
        }
        return this.permissionGroups;
    }

    @Override
    public String getId() {
        return this.id;
    }

    static {
        VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(GameModeType::getAssetStore));
    }
}

