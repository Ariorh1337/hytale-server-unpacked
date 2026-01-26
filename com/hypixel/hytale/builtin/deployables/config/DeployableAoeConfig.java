/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.builtin.deployables.config;

import com.hypixel.hytale.builtin.deployables.component.DeployableComponent;
import com.hypixel.hytale.builtin.deployables.config.DeployableConfig;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.EnumCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.Vector3f;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageCause;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class DeployableAoeConfig
extends DeployableConfig {
    public static final BuilderCodec<DeployableAoeConfig> CODEC = ((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)((BuilderCodec.Builder)BuilderCodec.builder(DeployableAoeConfig.class, DeployableAoeConfig::new, DeployableConfig.BASE_CODEC).append(new KeyedCodec<Shape>("Shape", new EnumCodec<Shape>(Shape.class)), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.shape = s;
    }, DeployableAoeConfig2 -> DeployableAoeConfig2.shape).documentation("The shape of the detection area").add()).append(new KeyedCodec<Float>("StartRadius", Codec.FLOAT), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.startRadius = s.floatValue();
    }, DeployableAoeConfig2 -> Float.valueOf(DeployableAoeConfig2.startRadius)).documentation("The initial detection radius").add()).append(new KeyedCodec<Float>("EndRadius", Codec.FLOAT), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.endRadius = s.floatValue();
    }, DeployableAoeConfig2 -> Float.valueOf(DeployableAoeConfig2.endRadius)).documentation("If set, the detection radius will expand to this size over the RadiusChangeTime (RadiusChangeTime must be set)").add()).append(new KeyedCodec<Float>("Height", Codec.FLOAT), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.height = s.floatValue();
    }, DeployableAoeConfig2 -> Float.valueOf(DeployableAoeConfig2.height)).documentation("The height of the Shape, if using a cylinder shape").add()).append(new KeyedCodec<Float>("RadiusChangeTime", Codec.FLOAT), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.radiusChangeTime = s.floatValue();
    }, DeployableAoeConfig2 -> Float.valueOf(DeployableAoeConfig2.radiusChangeTime)).documentation("The time (starting at spawn) it takes to change from StartRadius to EndRadius").add()).append(new KeyedCodec<Float>("DamageInterval", Codec.FLOAT), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.damageInterval = s.floatValue();
    }, DeployableAoeConfig2 -> Float.valueOf(DeployableAoeConfig2.damageInterval)).documentation("The interval between damage being applied to targets in seconds").add()).append(new KeyedCodec<Float>("DamageAmount", Codec.FLOAT), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.damageAmount = s.floatValue();
    }, DeployableAoeConfig2 -> Float.valueOf(DeployableAoeConfig2.damageAmount)).documentation("The amount of damage to apply to targets per interval").add()).append(new KeyedCodec<String>("DamageCause", DamageCause.CHILD_ASSET_CODEC), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.damageCause = s;
    }, DeployableAoeConfig2 -> DeployableAoeConfig2.damageCause).documentation("The amount of damage to apply to targets per interval").add()).append(new KeyedCodec<T[]>("ApplyEffects", new ArrayCodec<String>(EntityEffect.CHILD_ASSET_CODEC, String[]::new)), (DeployableAoeConfig2, s) -> {
        DeployableAoeConfig2.effectsToApply = s;
    }, DeployableAoeConfig2 -> DeployableAoeConfig2.effectsToApply).add()).appendInherited(new KeyedCodec<Boolean>("AttackOwner", Codec.BOOLEAN), (o, i) -> {
        o.attackOwner = i;
    }, o -> o.attackOwner, (i, o) -> {
        i.attackOwner = o.attackOwner;
    }).documentation("Whether or not the owner is affected by the attack & effect of this deployable").add()).appendInherited(new KeyedCodec<Boolean>("AttackTeam", Codec.BOOLEAN), (o, i) -> {
        o.attackTeam = i;
    }, o -> o.attackTeam, (i, o) -> {
        i.attackTeam = o.attackTeam;
    }).documentation("Whether or not the team is affected by the attack & effect of this deployable").add()).appendInherited(new KeyedCodec<Boolean>("AttackEnemies", Codec.BOOLEAN), (o, i) -> {
        o.attackEnemies = i;
    }, o -> o.attackEnemies, (i, o) -> {
        i.attackEnemies = o.attackEnemies;
    }).documentation("Whether or not this deployable interacts with non-team entities").add()).build();
    protected float startRadius = 1.0f;
    protected float endRadius = -1.0f;
    protected float radiusChangeTime = -1.0f;
    protected float damageInterval = 1.0f;
    protected float damageAmount = 1.0f;
    protected String damageCause = "Physical";
    protected String[] effectsToApply;
    protected boolean attackOwner;
    protected boolean attackTeam;
    protected boolean attackEnemies = true;
    protected Shape shape = Shape.Sphere;
    protected float height = 1.0f;
    protected DamageCause processedDamageCause;

    protected DeployableAoeConfig() {
    }

    @Override
    public void tick(@Nonnull DeployableComponent deployableComponent, float dt, int index, @Nonnull ArchetypeChunk<EntityStore> archetypeChunk, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
        Vector3d position = archetypeChunk.getComponent(index, TransformComponent.getComponentType()).getPosition();
        World world = store.getExternalData().getWorld();
        Ref<EntityStore> entityRef = archetypeChunk.getReferenceTo(index);
        float radius = this.getRadius(store, deployableComponent.getSpawnInstant());
        this.handleDebugGraphics(world, deployableComponent.getDebugColor(), position, radius * 2.0f);
        switch (deployableComponent.getFlag(DeployableComponent.DeployableFlag.STATE)) {
            case 0: {
                deployableComponent.setFlag(DeployableComponent.DeployableFlag.STATE, 1);
                break;
            }
            case 1: {
                deployableComponent.setFlag(DeployableComponent.DeployableFlag.STATE, 2);
                DeployableAoeConfig.playAnimation(store, entityRef, this, "Grow");
                break;
            }
            case 2: {
                if (!(radius >= this.endRadius)) break;
                deployableComponent.setFlag(DeployableComponent.DeployableFlag.STATE, 3);
                DeployableAoeConfig.playAnimation(store, entityRef, this, "Looping");
            }
        }
        Ref<EntityStore> deployableRef = archetypeChunk.getReferenceTo(index);
        if (deployableComponent.incrementTimeSinceLastAttack(dt) > this.damageInterval) {
            deployableComponent.setTimeSinceLastAttack(0.0f);
            this.handleDetection(store, commandBuffer, deployableRef, deployableComponent, position, radius, DamageCause.PHYSICAL);
        }
        super.tick(deployableComponent, dt, index, archetypeChunk, store, commandBuffer);
    }

    protected void handleDetection(final Store<EntityStore> store, final CommandBuffer<EntityStore> commandBuffer, final Ref<EntityStore> deployableRef, DeployableComponent deployableComponent, Vector3d position, float radius, final DamageCause damageCause) {
        var attackConsumer = new Consumer<Ref<EntityStore>>(){
            final /* synthetic */ DeployableAoeConfig this$0;
            {
                this.this$0 = this$0;
            }

            @Override
            public void accept(Ref<EntityStore> entityStoreRef) {
                if (entityStoreRef == deployableRef) {
                    return;
                }
                this.this$0.attackTarget(entityStoreRef, deployableRef, damageCause, commandBuffer);
                this.this$0.applyEffectToTarget(store, entityStoreRef);
            }
        };
        switch (this.shape.ordinal()) {
            case 0: {
                List<Ref<EntityStore>> targetRefs = TargetUtil.getAllEntitiesInSphere(position, radius, store);
                for (Ref<EntityStore> targetRef : targetRefs) {
                    attackConsumer.accept(targetRef);
                }
                break;
            }
            case 1: {
                List<Ref<EntityStore>> targetRefs = TargetUtil.getAllEntitiesInCylinder(position, radius, this.height, store);
                for (Ref<EntityStore> targetRef : targetRefs) {
                    attackConsumer.accept(targetRef);
                }
                break;
            }
        }
    }

    protected void handleDebugGraphics(World world, Vector3f color, Vector3d position, float scale) {
        if (!this.getDebugVisuals()) {
            return;
        }
    }

    protected void attackTarget(Ref<EntityStore> targetRef, Ref<EntityStore> ownerRef, DamageCause damageCause, CommandBuffer<EntityStore> commandBuffer) {
        if (this.damageAmount <= 0.0f) {
            return;
        }
        Damage damageEntry = new Damage((Damage.Source)new Damage.EntitySource(ownerRef), damageCause, this.damageAmount);
        if (targetRef.equals(ownerRef)) {
            damageEntry.setSource(Damage.NULL_SOURCE);
        }
        DamageSystems.executeDamage(targetRef, commandBuffer, damageEntry);
    }

    protected void applyEffectToTarget(Store<EntityStore> store, Ref<EntityStore> targetRef) {
        if (this.effectsToApply == null) {
            return;
        }
        EffectControllerComponent effectController = store.getComponent(targetRef, EffectControllerComponent.getComponentType());
        if (effectController == null) {
            return;
        }
        for (String effect : this.effectsToApply) {
            EntityEffect effectAsset;
            if (effect == null || (effectAsset = (EntityEffect)EntityEffect.getAssetMap().getAsset(effect)) == null) continue;
            effectController.addEffect(targetRef, effectAsset, store);
        }
    }

    protected boolean canAttackEntity(Ref<EntityStore> target, DeployableComponent deployable) {
        boolean isOwner = target.equals(deployable.getOwner());
        return !isOwner || this.attackOwner;
    }

    protected float getRadius(Store<EntityStore> store, Instant startInstant) {
        if (this.radiusChangeTime <= 0.0f || this.endRadius < 0.0f) {
            return this.startRadius;
        }
        float radiusDiff = this.endRadius - this.startRadius;
        float increment = radiusDiff / this.radiusChangeTime;
        Instant now = store.getResource(TimeResource.getResourceType()).getNow();
        float timeDiff = (float)Duration.between(startInstant, now).toMillis() / 1000.0f;
        if (timeDiff > this.radiusChangeTime) {
            return this.endRadius;
        }
        float nowIncrement = increment * timeDiff;
        return this.startRadius + nowIncrement;
    }

    protected DamageCause getDamageCause() {
        if (this.processedDamageCause == null) {
            this.processedDamageCause = (DamageCause)DamageCause.getAssetMap().getAsset(this.damageCause);
            if (this.processedDamageCause == null) {
                this.processedDamageCause = DamageCause.PHYSICAL;
            }
        }
        return this.processedDamageCause;
    }

    @Override
    public String toString() {
        return "DeployableAoeConfig{}" + super.toString();
    }

    public static enum Shape {
        Sphere,
        Cylinder;

    }
}

