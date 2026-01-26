/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.modules.projectile.config;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.map.EnumMapCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Direction;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.asset.type.soundevent.validator.SoundEventValidators;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import com.hypixel.hytale.server.core.io.NetworkSerializable;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.RootInteraction;
import com.hypixel.hytale.server.core.modules.projectile.config.BallisticData;
import com.hypixel.hytale.server.core.modules.projectile.config.PhysicsConfig;
import com.hypixel.hytale.server.core.modules.projectile.config.StandardPhysicsConfig;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ProjectileConfig
implements JsonAssetWithMap<String, DefaultAssetMap<String, ProjectileConfig>>,
NetworkSerializable<com.hypixel.hytale.protocol.ProjectileConfig>,
BallisticData {
    @Nonnull
    public static final AssetBuilderCodec<String, ProjectileConfig> CODEC = ((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)((AssetBuilderCodec.Builder)AssetBuilderCodec.builder(ProjectileConfig.class, ProjectileConfig::new, Codec.STRING, (config, s) -> {
        config.id = s;
    }, config -> config.id, (asset, data) -> {
        asset.data = data;
    }, asset -> asset.data).appendInherited(new KeyedCodec<PhysicsConfig>("Physics", PhysicsConfig.CODEC), (o, i) -> {
        o.physicsConfig = i;
    }, o -> o.physicsConfig, (o, p) -> {
        o.physicsConfig = p.physicsConfig;
    }).add()).appendInherited(new KeyedCodec<String>("Model", Codec.STRING), (o, i) -> {
        o.model = i;
    }, o -> o.model, (o, p) -> {
        o.model = p.model;
    }).addValidator(Validators.nonNull()).addValidator(ModelAsset.VALIDATOR_CACHE.getValidator()).add()).appendInherited(new KeyedCodec<Double>("LaunchForce", Codec.DOUBLE), (o, i) -> {
        o.launchForce = i;
    }, o -> o.launchForce, (o, p) -> {
        o.launchForce = p.launchForce;
    }).add()).appendInherited(new KeyedCodec<Vector3f>("SpawnOffset", ProtocolCodecs.VECTOR3F), (o, i) -> {
        o.spawnOffset = i;
    }, o -> o.spawnOffset, (o, p) -> {
        o.spawnOffset = p.spawnOffset;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<Direction>("SpawnRotationOffset", ProtocolCodecs.DIRECTION), (o, i) -> {
        o.spawnRotationOffset = i;
        o.spawnRotationOffset.yaw *= (float)Math.PI / 180;
        o.spawnRotationOffset.pitch *= (float)Math.PI / 180;
        o.spawnRotationOffset.roll *= (float)Math.PI / 180;
    }, o -> o.spawnRotationOffset, (o, p) -> {
        o.spawnRotationOffset = p.spawnRotationOffset;
    }).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec("Interactions", new EnumMapCodec(InteractionType.class, RootInteraction.CHILD_ASSET_CODEC)), (o, i) -> {
        o.interactions = i;
    }, o -> o.interactions, (o, p) -> {
        o.interactions = p.interactions;
    }).addValidatorLate(() -> RootInteraction.VALIDATOR_CACHE.getMapValueValidator().late()).addValidator(Validators.nonNull()).add()).appendInherited(new KeyedCodec<String>("LaunchLocalSoundEventId", Codec.STRING), (o, i) -> {
        o.launchLocalSoundEventId = i;
    }, o -> o.launchLocalSoundEventId, (o, p) -> {
        o.launchLocalSoundEventId = p.launchLocalSoundEventId;
    }).addValidator(SoundEventValidators.ONESHOT).documentation("The sound event played to the throwing player when the projectile is spawned/launched").add()).appendInherited(new KeyedCodec<String>("LaunchWorldSoundEventId", Codec.STRING), (o, i) -> {
        o.launchWorldSoundEventId = i;
    }, o -> o.launchWorldSoundEventId, (o, p) -> {
        o.launchWorldSoundEventId = p.launchWorldSoundEventId;
    }).addValidator(SoundEventValidators.MONO).addValidator(SoundEventValidators.ONESHOT).documentation("The positioned sound event played to surrounding players when the projectile is spawned/launched").add()).appendInherited(new KeyedCodec<String>("ProjectileSoundEventId", Codec.STRING), (o, i) -> {
        o.projectileSoundEventId = i;
    }, o -> o.projectileSoundEventId, (o, p) -> {
        o.projectileSoundEventId = p.projectileSoundEventId;
    }).addValidator(SoundEventValidators.LOOPING).addValidator(SoundEventValidators.MONO).documentation("The looping sound event to attach to the projectile.").add()).afterDecode(ProjectileConfig::processConfig)).build();
    @Nullable
    private static AssetStore<String, ProjectileConfig, DefaultAssetMap<String, ProjectileConfig>> ASSET_STORE;
    @Nonnull
    public static final ValidatorCache<String> VALIDATOR_CACHE;
    @Nullable
    protected AssetExtraInfo.Data data;
    @Nullable
    protected String id;
    @Nonnull
    protected PhysicsConfig physicsConfig = StandardPhysicsConfig.DEFAULT;
    protected String model;
    protected Model generatedModel;
    protected double launchForce = 1.0;
    @Nonnull
    protected Vector3f spawnOffset = new Vector3f(0.0f, 0.0f, 0.0f);
    @Nonnull
    protected Direction spawnRotationOffset = new Direction(0.0f, 0.0f, 0.0f);
    @Nonnull
    protected Map<InteractionType, String> interactions = Collections.emptyMap();
    protected String launchLocalSoundEventId;
    protected String launchWorldSoundEventId;
    protected String projectileSoundEventId;
    protected int launchLocalSoundEventIndex = 0;
    protected int launchWorldSoundEventIndex = 0;
    protected int projectileSoundEventIndex = 0;

    @Nonnull
    public static AssetStore<String, ProjectileConfig, DefaultAssetMap<String, ProjectileConfig>> getAssetStore() {
        if (ASSET_STORE == null) {
            ASSET_STORE = AssetRegistry.getAssetStore(ProjectileConfig.class);
        }
        return ASSET_STORE;
    }

    @Nonnull
    public static DefaultAssetMap<String, ProjectileConfig> getAssetMap() {
        return ProjectileConfig.getAssetStore().getAssetMap();
    }

    @Override
    @Nullable
    public String getId() {
        return this.id;
    }

    protected void processConfig() {
        if (this.launchWorldSoundEventId != null) {
            this.launchWorldSoundEventIndex = this.launchLocalSoundEventIndex = SoundEvent.getAssetMap().getIndex(this.launchWorldSoundEventId);
        }
        if (this.launchLocalSoundEventId != null) {
            this.launchLocalSoundEventIndex = SoundEvent.getAssetMap().getIndex(this.launchLocalSoundEventId);
        }
        if (this.projectileSoundEventId != null) {
            this.projectileSoundEventIndex = SoundEvent.getAssetMap().getIndex(this.projectileSoundEventId);
        }
    }

    @Nonnull
    public PhysicsConfig getPhysicsConfig() {
        return this.physicsConfig;
    }

    @Nonnull
    public Model getModel() {
        if (this.generatedModel != null) {
            return this.generatedModel;
        }
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(this.model);
        this.generatedModel = Model.createUnitScaleModel(modelAsset);
        return this.generatedModel;
    }

    public double getLaunchForce() {
        return this.launchForce;
    }

    @Override
    public double getMuzzleVelocity() {
        return this.launchForce;
    }

    @Override
    public double getGravity() {
        return this.physicsConfig.getGravity();
    }

    @Override
    public double getVerticalCenterShot() {
        return this.spawnOffset.y;
    }

    @Override
    public double getDepthShot() {
        return this.spawnOffset.z;
    }

    @Override
    public boolean isPitchAdjustShot() {
        return true;
    }

    public Map<InteractionType, String> getInteractions() {
        return this.interactions;
    }

    public int getLaunchWorldSoundEventIndex() {
        return this.launchWorldSoundEventIndex;
    }

    public int getProjectileSoundEventIndex() {
        return this.projectileSoundEventIndex;
    }

    @Nonnull
    public Vector3f getSpawnOffset() {
        return this.spawnOffset;
    }

    @Nonnull
    public Direction getSpawnRotationOffset() {
        return this.spawnRotationOffset;
    }

    @Nonnull
    public Vector3d getCalculatedOffset(float pitch, float yaw) {
        Vector3d offset = new Vector3d(this.spawnOffset.x, this.spawnOffset.y, this.spawnOffset.z);
        offset.rotateX(pitch);
        offset.rotateY(yaw);
        return offset;
    }

    @Override
    @Nonnull
    public com.hypixel.hytale.protocol.ProjectileConfig toPacket() {
        com.hypixel.hytale.protocol.ProjectileConfig config = new com.hypixel.hytale.protocol.ProjectileConfig();
        config.physicsConfig = (com.hypixel.hytale.protocol.PhysicsConfig)this.physicsConfig.toPacket();
        config.model = this.getModel().toPacket();
        config.launchForce = this.launchForce;
        config.spawnOffset = this.spawnOffset;
        config.rotationOffset = this.spawnRotationOffset;
        config.launchLocalSoundEventIndex = this.launchLocalSoundEventIndex;
        config.projectileSoundEventIndex = this.projectileSoundEventIndex;
        config.interactions = new EnumMap<InteractionType, Integer>(InteractionType.class);
        for (Map.Entry<InteractionType, String> e : this.interactions.entrySet()) {
            config.interactions.put(e.getKey(), RootInteraction.getRootInteractionIdOrUnknown(e.getValue()));
        }
        return config;
    }

    static {
        VALIDATOR_CACHE = new ValidatorCache(new AssetKeyValidator(ProjectileConfig::getAssetStore));
    }
}

