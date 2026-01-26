/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.camera;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.IndexedLookupTableAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.protocol.packets.camera.CameraShakeEffect;
import javax.annotation.Nonnull;

public abstract class CameraEffect
implements JsonAssetWithMap<String, IndexedLookupTableAssetMap<String, CameraEffect>> {
    @Nonnull
    public static final AssetCodecMapCodec<String, CameraEffect> CODEC = new AssetCodecMapCodec<String, CameraEffect>(Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (t, data) -> {
        t.data = data;
    }, t -> t.data);
    @Nonnull
    public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec(CameraEffect.class, CODEC);
    @Nonnull
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(CameraEffect::getAssetStore));
    private static AssetStore<String, CameraEffect, IndexedLookupTableAssetMap<String, CameraEffect>> ASSET_STORE;
    protected String id;
    protected AssetExtraInfo.Data data;

    @Nonnull
    public static AssetStore<String, CameraEffect, IndexedLookupTableAssetMap<String, CameraEffect>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(CameraEffect.class);
        }
        return ASSET_STORE;
    }

    @Nonnull
    public static IndexedLookupTableAssetMap<String, CameraEffect> getAssetMap() {
        return CameraEffect.getAssetStore().getAssetMap();
    }

    @Override
    public String getId() {
        return this.id;
    }

    public abstract CameraShakeEffect createCameraShakePacket();

    public abstract CameraShakeEffect createCameraShakePacket(float var1);

    public static class MissingCameraEffect
    extends CameraEffect {
        public MissingCameraEffect(@Nonnull String id) {
        }

        @Override
        @Nonnull
        public CameraShakeEffect createCameraShakePacket() {
            return new CameraShakeEffect();
        }

        @Override
        @Nonnull
        public CameraShakeEffect createCameraShakePacket(float intensityContext) {
            return new CameraShakeEffect();
        }

        @Nonnull
        public String toString() {
            return "MissingShakeEffect{}";
        }
    }
}

