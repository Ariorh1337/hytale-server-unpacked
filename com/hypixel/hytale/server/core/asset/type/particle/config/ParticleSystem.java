/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.asset.type.particle.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.schema.metadata.ui.UIDefaultCollapsedState;
import com.hypixel.hytale.codec.schema.metadata.ui.UITypeIcon;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.server.core.asset.type.particle.config.ParticleSpawnerGroup;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import java.lang.ref.SoftReference;
import java.util.Arrays;
import javax.annotation.Nonnull;

public class ParticleSystem
implements JsonAssetWithMap<String, DefaultAssetMap<String, ParticleSystem>>,
NetworkSerializable<com.hypixel.hytale.protocol.ParticleSystem> {
    public static final AssetBuilderCodec<String, ParticleSystem> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(ParticleSystem.class, ParticleSystem::new, Codec.STRING, (particleSystem, s) -> {
        particleSystem.id = s;
    }, particleSystem -> particleSystem.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).metadata(new UITypeIcon("ParticleSystem.png"))).appendInherited(new KeyedCodec<T[]>("Spawners", new ArrayCodec<ParticleSpawnerGroup>(ParticleSpawnerGroup.CODEC, ParticleSpawnerGroup[]::new)), (particleSystem, o) -> {
        particleSystem.spawners = o;
    }, particleSystem -> particleSystem.spawners, (particleSystem, parent) -> {
        particleSystem.spawners = parent.spawners;
    }).metadata(UIDefaultCollapsedState.UNCOLLAPSED).addValidator(Validators.nonEmptyArray()).add()).appendInherited(new KeyedCodec<Float>("LifeSpan", Codec.FLOAT), (particleSystem, f) -> {
        particleSystem.lifeSpan = f.floatValue();
    }, particleSystem -> Float.valueOf(particleSystem.lifeSpan), (particleSystem, parent) -> {
        particleSystem.lifeSpan = parent.lifeSpan;
    }).add()).appendInherited(new KeyedCodec<Float>("CullDistance", Codec.FLOAT), (particleSystem, f) -> {
        particleSystem.cullDistance = f.floatValue();
    }, particleSystem -> Float.valueOf(particleSystem.cullDistance), (particleSystem, parent) -> {
        particleSystem.cullDistance = parent.cullDistance;
    }).add()).appendInherited(new KeyedCodec<Float>("BoundingRadius", Codec.FLOAT), (particleSystem, f) -> {
        particleSystem.boundingRadius = f.floatValue();
    }, particleSystem -> Float.valueOf(particleSystem.boundingRadius), (particleSystem, parent) -> {
        particleSystem.boundingRadius = parent.boundingRadius;
    }).add()).appendInherited(new KeyedCodec<Boolean>("IsImportant", Codec.BOOLEAN), (particleSystem, b) -> {
        particleSystem.isImportant = b;
    }, particleSystem -> particleSystem.isImportant, (particleSystem, parent) -> {
        particleSystem.isImportant = parent.isImportant;
    }).add()).build();
    public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(ParticleSystem::getAssetStore));
    private static AssetStore<String, ParticleSystem, DefaultAssetMap<String, ParticleSystem>> ASSET_STORE;
    protected AssetExtraInfo.Data data;
    protected String id;
    protected float lifeSpan;
    protected ParticleSpawnerGroup[] spawners;
    protected float cullDistance;
    protected float boundingRadius;
    protected boolean isImportant;
    private SoftReference<com.hypixel.hytale.protocol.ParticleSystem> cachedPacket;

    public static AssetStore<String, ParticleSystem, DefaultAssetMap<String, ParticleSystem>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(ParticleSystem.class);
        }
        return ASSET_STORE;
    }

    public static DefaultAssetMap<String, ParticleSystem> getAssetMap() {
        return ParticleSystem.getAssetStore().getAssetMap();
    }

    public ParticleSystem(String id, float lifeSpan, ParticleSpawnerGroup[] spawners, float cullDistance, float boundingRadius, boolean isImportant) {
        this.id = id;
        this.lifeSpan = lifeSpan;
        this.spawners = spawners;
        this.cullDistance = cullDistance;
        this.boundingRadius = boundingRadius;
        this.isImportant = isImportant;
    }

    protected ParticleSystem() {
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ParticleSystem toPacket() {
        com.hypixel.hytale.protocol.ParticleSystem cached;
        com.hypixel.hytale.protocol.ParticleSystem particleSystem = cached = this.cachedPacket == null ? null : this.cachedPacket.get();
        if (cached != null) {
            return cached;
        }
        com.hypixel.hytale.protocol.ParticleSystem packet = new com.hypixel.hytale.protocol.ParticleSystem();
        packet.id = this.id;
        packet.lifeSpan = this.lifeSpan;
        if (this.spawners != null && this.spawners.length > 0) {
            packet.spawners = ArrayUtil.copyAndMutate(this.spawners, ParticleSpawnerGroup::toPacket, com.hypixel.hytale.protocol.ParticleSpawnerGroup[]::new);
        }
        packet.cullDistance = this.cullDistance;
        packet.boundingRadius = this.boundingRadius;
        packet.isImportant = this.isImportant;
        this.cachedPacket = new SoftReference<com.hypixel.hytale.protocol.ParticleSystem>(packet);
        return packet;
    }

    @Override
    public String getId() {
        return this.id;
    }

    public float getLifeSpan() {
        return this.lifeSpan;
    }

    public ParticleSpawnerGroup[] getSpawners() {
        return this.spawners;
    }

    public float getCullDistance() {
        return this.cullDistance;
    }

    public float getBoundingRadius() {
        return this.boundingRadius;
    }

    public boolean isImportant() {
        return this.isImportant;
    }

    @Nonnull
    public String toString() {
        return "ParticleSystem{id='" + this.id + "', lifeSpan=" + this.lifeSpan + ", spawners=" + Arrays.toString(this.spawners) + ", cullDistance=" + this.cullDistance + ", boundingRadius=" + this.boundingRadius + ", isImportant=" + this.isImportant + "}";
    }
}

