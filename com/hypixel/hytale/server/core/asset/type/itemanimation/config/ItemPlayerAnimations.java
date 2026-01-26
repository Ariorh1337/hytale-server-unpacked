/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.itemanimation.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.map.MapCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.MapUtil;
import com.hypixel.hytale.protocol.ItemAnimation;
import com.hypixel.hytale.protocol.WiggleWeights;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemPullbackConfig;
import com.hypixel.hytale.server.core.asset.type.model.config.camera.CameraSettings;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class ItemPlayerAnimations
implements JsonAssetWithMap<String, DefaultAssetMap<String, ItemPlayerAnimations>>,
NetworkSerializable<com.hypixel.hytale.protocol.ItemPlayerAnimations> {
    public static final String DEFAULT_ID = "Default";
    public static final BuilderCodec<WiggleWeights> WIGGLE_WEIGHTS_CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(WiggleWeights.class, WiggleWeights::new).addField(new KeyedCodec<Double>("X", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.x = d.floatValue();
    }, wiggleWeight -> wiggleWeight.x)).addField(new KeyedCodec<Double>("XDeceleration", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.xDeceleration = d.floatValue();
    }, wiggleWeight -> wiggleWeight.xDeceleration)).addField(new KeyedCodec<Double>("Y", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.y = d.floatValue();
    }, wiggleWeight -> wiggleWeight.y)).addField(new KeyedCodec<Double>("YDeceleration", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.yDeceleration = d.floatValue();
    }, wiggleWeight -> wiggleWeight.yDeceleration)).addField(new KeyedCodec<Double>("Z", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.z = d.floatValue();
    }, wiggleWeight -> wiggleWeight.z)).addField(new KeyedCodec<Double>("ZDeceleration", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.zDeceleration = d.floatValue();
    }, wiggleWeight -> wiggleWeight.zDeceleration)).addField(new KeyedCodec<Double>("Roll", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.roll = d.floatValue();
    }, wiggleWeight -> wiggleWeight.roll)).addField(new KeyedCodec<Double>("RollDeceleration", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.rollDeceleration = d.floatValue();
    }, wiggleWeight -> wiggleWeight.rollDeceleration)).addField(new KeyedCodec<Double>("Pitch", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.pitch = d.floatValue();
    }, wiggleWeight -> wiggleWeight.pitch)).addField(new KeyedCodec<Double>("PitchDeceleration", Codec.DOUBLE), (wiggleWeight, d) -> {
        wiggleWeight.pitchDeceleration = d.floatValue();
    }, wiggleWeight -> wiggleWeight.pitchDeceleration)).build();
    public static final AssetBuilderCodec<String, ItemPlayerAnimations> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(ItemPlayerAnimations.class, ItemPlayerAnimations::new, Codec.STRING, (t, k) -> {
        t.id = k;
    }, t -> t.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).appendInherited(new KeyedCodec("Animations", new MapCodec<ItemAnimation, HashMap>(ProtocolCodecs.ITEM_ANIMATION_CODEC, HashMap::new)), (itemPlayerAnimations, map) -> {
        itemPlayerAnimations.animations = MapUtil.combineUnmodifiable(itemPlayerAnimations.animations, map);
    }, itemPlayerAnimations -> itemPlayerAnimations.animations, (itemPlayerAnimations, parent) -> {
        itemPlayerAnimations.animations = parent.animations;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<WiggleWeights>("WiggleWeights", WIGGLE_WEIGHTS_CODEC), (itemPlayerAnimations, map) -> {
        itemPlayerAnimations.wiggleWeights = map;
    }, itemPlayerAnimations -> itemPlayerAnimations.wiggleWeights, (itemPlayerAnimations, parent) -> {
        itemPlayerAnimations.wiggleWeights = parent.wiggleWeights;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<CameraSettings>("Camera", CameraSettings.CODEC), (itemPlayerAnimations, o) -> {
        itemPlayerAnimations.camera = o;
    }, itemPlayerAnimations -> itemPlayerAnimations.camera, (itemPlayerAnimations, parent) -> {
        itemPlayerAnimations.camera = parent.camera;
    }).add()).appendInherited(new KeyedCodec<ItemPullbackConfig>("PullbackConfig", ItemPullbackConfig.CODEC), (itemPlayerAnimations, s) -> {
        itemPlayerAnimations.pullbackConfig = s;
    }, itemPlayerAnimations -> itemPlayerAnimations.pullbackConfig, (itemPlayerAnimations, parent) -> {
        itemPlayerAnimations.pullbackConfig = parent.pullbackConfig;
    }).documentation("Overrides the offset of first person arms when close to obstacles").add()).appendInherited(new KeyedCodec<Boolean>("UseFirstPersonOverrides", Codec.BOOLEAN), (itemPlayerAnimations, s) -> {
        itemPlayerAnimations.useFirstPersonOverrides = s;
    }, itemPlayerAnimations -> itemPlayerAnimations.useFirstPersonOverrides, (itemPlayerAnimations, parent) -> {
        itemPlayerAnimations.useFirstPersonOverrides = parent.useFirstPersonOverrides;
    }).documentation("Determines whether or not to use FirstPersonOverride animations within ItemAnimations").add()).build();
    public static final Codec<String> CHILD_CODEC = new ContainedAssetCodec(ItemPlayerAnimations.class, CODEC);
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(ItemPlayerAnimations::getAssetStore));
    private static AssetStore<String, ItemPlayerAnimations, DefaultAssetMap<String, ItemPlayerAnimations>> ASSET_STORE;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected Map<String, ItemAnimation> animations = Collections.emptyMap();
    protected WiggleWeights wiggleWeights;
    protected CameraSettings camera;
    protected ItemPullbackConfig pullbackConfig;
    protected boolean useFirstPersonOverrides;
    private SoftReference<com.hypixel.hytale.protocol.ItemPlayerAnimations> cachedPacket;

    public static AssetStore<String, ItemPlayerAnimations, DefaultAssetMap<String, ItemPlayerAnimations>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(ItemPlayerAnimations.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, ItemPlayerAnimations> getAssetMap() {
        return ItemPlayerAnimations.getAssetStore().getAssetMap();
    }

    public ItemPlayerAnimations(String id, Map<String, ItemAnimation> animations, WiggleWeights wiggleWeights, CameraSettings camera, ItemPullbackConfig pullbackConfig, boolean useFirstPersonOverrides) {
        this.id = id;
        this.animations = animations;
        this.wiggleWeights = wiggleWeights;
        this.camera = camera;
        this.pullbackConfig = pullbackConfig;
        this.useFirstPersonOverrides = useFirstPersonOverrides;
    }

    protected ItemPlayerAnimations() {
    }

    @Override
    public String getId() {
        return this.id;
    }

    public Map<String, ItemAnimation> getAnimations() {
        return this.animations;
    }

    public WiggleWeights getWiggleWeights() {
        return this.wiggleWeights;
    }

    public CameraSettings getCamera() {
        return this.camera;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ItemPlayerAnimations toPacket() {
        com.hypixel.hytale.protocol.ItemPlayerAnimations cached;
        com.hypixel.hytale.protocol.ItemPlayerAnimations itemPlayerAnimations = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.ItemPlayerAnimations packet = new com.hypixel.hytale.protocol.ItemPlayerAnimations();
        packet.id = this.id;
        packet.animations = this.animations;
        packet.wiggleWeights = this.wiggleWeights;
        if (this.camera != null) {
            packet.camera = this.camera.toPacket();
        }
        if (this.pullbackConfig != null) {
            packet.pullbackConfig = this.pullbackConfig.toPacket();
        }
        packet.useFirstPersonOverride = this.useFirstPersonOverrides;
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.ItemPlayerAnimations>(packet);
        return packet;
    }

    @Nonnull
    public String toString() {
        return "ItemPlayerAnimations{id='" + this.id + "', animations=" + String.valueOf(this.animations) + ", wiggleWeights=" + String.valueOf(this.wiggleWeights) + ", camera=" + String.valueOf(this.camera) + ", pullbackConfig=" + String.valueOf(this.pullbackConfig) + ", useFirstPersonOverrides=" + this.useFirstPersonOverrides + "}";
    }
}

