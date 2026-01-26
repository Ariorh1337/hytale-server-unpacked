/*
 * Decompiled with CFR 0.152.
 */
package com.hypixel.hytale.server.core.entity.effect;

import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.protocol.EffectOp;
import com.hypixel.hytale.protocol.EntityEffectUpdate;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.EntityEffect;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.OverlapBehavior;
import com.hypixel.hytale.server.core.asset.type.entityeffect.config.RemovalBehavior;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.effect.ActiveEntityEffect;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.livingentity.LivingEntityEffectSystem;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntArraySet;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Iterator;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class EffectControllerComponent
implements Component<EntityStore> {
    @Nonnull
    public static final BuilderCodec<EffectControllerComponent> CODEC = ((BuilderCodec.Builder)BuilderCodec.builder(EffectControllerComponent.class, EffectControllerComponent::new).append(new KeyedCodec<T[]>("ActiveEntityEffects", new ArrayCodec<ActiveEntityEffect>(ActiveEntityEffect.CODEC, ActiveEntityEffect[]::new)), EffectControllerComponent::addActiveEntityEffects, EffectControllerComponent::getAllActiveEntityEffects).add()).build();
    @Nonnull
    protected final Int2ObjectMap<ActiveEntityEffect> activeEffects = new Int2ObjectOpenHashMap<ActiveEntityEffect>();
    @Nullable
    protected int[] cachedActiveEffectIndexes;
    @Nonnull
    protected ObjectList<EntityEffectUpdate> changes = new ObjectArrayList<EntityEffectUpdate>();
    protected boolean isNetworkOutdated;
    @Nullable
    protected Model originalModel = null;
    protected int activeModelChangeEntityEffectIndex;
    protected boolean isInvulnerable;

    @Nonnull
    public static ComponentType<EntityStore, EffectControllerComponent> getComponentType() {
        return EntityModule.get().getEffectControllerComponentType();
    }

    public EffectControllerComponent() {
    }

    public EffectControllerComponent(@Nonnull EffectControllerComponent effectControllerComponent) {
        this.originalModel = effectControllerComponent.originalModel;
        this.activeModelChangeEntityEffectIndex = effectControllerComponent.activeModelChangeEntityEffectIndex;
        this.changes.addAll(effectControllerComponent.changes);
        ActiveEntityEffect[] activeEntityEffects = effectControllerComponent.getAllActiveEntityEffects();
        if (activeEntityEffects != null) {
            effectControllerComponent.addActiveEntityEffects(activeEntityEffects);
        }
    }

    public boolean isInvulnerable() {
        return this.isInvulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.isInvulnerable = invulnerable;
    }

    public boolean addEffect(@Nonnull Ref<EntityStore> ownerRef, @Nonnull EntityEffect entityEffect, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        int entityEffectIndex = EntityEffect.getAssetMap().getIndex(entityEffect.getId());
        if (entityEffectIndex == Integer.MIN_VALUE) {
            return false;
        }
        return this.addEffect(ownerRef, entityEffectIndex, entityEffect, componentAccessor);
    }

    public boolean addEffect(@Nonnull Ref<EntityStore> ownerRef, int entityEffectIndex, @Nonnull EntityEffect entityEffect, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        boolean infinite = entityEffect.isInfinite();
        float duration = entityEffect.getDuration();
        OverlapBehavior overlapBehavior = entityEffect.getOverlapBehavior();
        if (infinite) {
            return this.addInfiniteEffect(ownerRef, entityEffectIndex, entityEffect, componentAccessor);
        }
        return this.addEffect(ownerRef, entityEffectIndex, entityEffect, duration, overlapBehavior, componentAccessor);
    }

    public boolean addEffect(@Nonnull Ref<EntityStore> ownerRef, @Nonnull EntityEffect entityEffect, float duration, @Nonnull OverlapBehavior overlapBehavior, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        int entityEffectIndex = EntityEffect.getAssetMap().getIndex(entityEffect.getId());
        if (entityEffectIndex == Integer.MIN_VALUE) {
            return false;
        }
        return this.addEffect(ownerRef, entityEffectIndex, entityEffect, duration, overlapBehavior, componentAccessor);
    }

    public boolean addEffect(@Nonnull Ref<EntityStore> ownerRef, int entityEffectIndex, @Nonnull EntityEffect entityEffect, float duration, @Nonnull OverlapBehavior overlapBehavior, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (!LivingEntityEffectSystem.canApplyEffect(ownerRef, entityEffect, componentAccessor)) {
            return false;
        }
        ActiveEntityEffect currentActiveEntityEffectEntry = (ActiveEntityEffect)this.activeEffects.get(entityEffectIndex);
        if (currentActiveEntityEffectEntry != null) {
            if (currentActiveEntityEffectEntry.isInfinite()) {
                return true;
            }
            switch (overlapBehavior) {
                case EXTEND: {
                    currentActiveEntityEffectEntry.remainingDuration += duration;
                    this.addChange(new EntityEffectUpdate(EffectOp.Add, entityEffectIndex, currentActiveEntityEffectEntry.remainingDuration, false, currentActiveEntityEffectEntry.debuff, currentActiveEntityEffectEntry.statusEffectIcon));
                    return true;
                }
                case IGNORE: {
                    return true;
                }
            }
        }
        ActiveEntityEffect activeEntityEffectEntry = new ActiveEntityEffect(entityEffect.getId(), entityEffectIndex, duration, entityEffect.isDebuff(), entityEffect.getStatusEffectIcon(), entityEffect.isInvulnerable());
        this.activeEffects.put(entityEffectIndex, activeEntityEffectEntry);
        Entity ownerEntity = EntityUtils.getEntity(ownerRef, componentAccessor);
        if (ownerEntity instanceof LivingEntity) {
            LivingEntity ownerLivingEntity = (LivingEntity)ownerEntity;
            ownerLivingEntity.getStatModifiersManager().setRecalculate(true);
        }
        this.setModelChange(ownerRef, entityEffect, entityEffectIndex, componentAccessor);
        this.addChange(new EntityEffectUpdate(EffectOp.Add, entityEffectIndex, activeEntityEffectEntry.remainingDuration, false, activeEntityEffectEntry.debuff, activeEntityEffectEntry.statusEffectIcon));
        this.invalidateCache();
        return true;
    }

    public boolean addInfiniteEffect(@Nonnull Ref<EntityStore> ownerRef, int entityEffectIndex, @Nonnull EntityEffect entityEffect, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (!LivingEntityEffectSystem.canApplyEffect(ownerRef, entityEffect, componentAccessor)) {
            return false;
        }
        ActiveEntityEffect currentActiveEntityEffectEntry = (ActiveEntityEffect)this.activeEffects.get(entityEffectIndex);
        if (currentActiveEntityEffectEntry == null) {
            currentActiveEntityEffectEntry = new ActiveEntityEffect(entityEffect.getId(), entityEffectIndex, true, entityEffect.isInvulnerable());
            this.activeEffects.put(entityEffectIndex, currentActiveEntityEffectEntry);
            Entity ownerEntity = EntityUtils.getEntity(ownerRef, componentAccessor);
            if (ownerEntity instanceof LivingEntity) {
                LivingEntity ownerLivingEntity = (LivingEntity)ownerEntity;
                ownerLivingEntity.getStatModifiersManager().setRecalculate(true);
            }
            this.invalidateCache();
        } else if (!currentActiveEntityEffectEntry.isInfinite()) {
            currentActiveEntityEffectEntry.infinite = true;
        }
        this.setModelChange(ownerRef, entityEffect, entityEffectIndex, componentAccessor);
        this.addChange(new EntityEffectUpdate(EffectOp.Add, entityEffectIndex, currentActiveEntityEffectEntry.remainingDuration, true, currentActiveEntityEffectEntry.debuff, currentActiveEntityEffectEntry.statusEffectIcon));
        return true;
    }

    public void setModelChange(@Nonnull Ref<EntityStore> ownerRef, @Nonnull EntityEffect entityEffect, int entityEffectIndex, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (this.originalModel != null) {
            return;
        }
        if (entityEffect.getModelChange() == null) {
            return;
        }
        ModelComponent modelComponent = componentAccessor.getComponent(ownerRef, ModelComponent.getComponentType());
        assert (modelComponent != null);
        this.originalModel = modelComponent.getModel();
        this.activeModelChangeEntityEffectIndex = entityEffectIndex;
        ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(entityEffect.getModelChange());
        Model scaledModel = Model.createRandomScaleModel(modelAsset);
        componentAccessor.putComponent(ownerRef, ModelComponent.getComponentType(), new ModelComponent(scaledModel));
    }

    public void tryResetModelChange(@Nonnull Ref<EntityStore> ownerRef, int activeEffectIndex, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        if (this.originalModel != null && this.activeModelChangeEntityEffectIndex == activeEffectIndex) {
            componentAccessor.putComponent(ownerRef, ModelComponent.getComponentType(), new ModelComponent(this.originalModel));
            PlayerSkinComponent playerSkinComponent = componentAccessor.getComponent(ownerRef, PlayerSkinComponent.getComponentType());
            if (playerSkinComponent != null) {
                playerSkinComponent.setNetworkOutdated();
            }
            this.originalModel = null;
        }
    }

    public void addActiveEntityEffects(@Nonnull ActiveEntityEffect[] activeEntityEffects) {
        if (activeEntityEffects.length == 0) {
            return;
        }
        for (ActiveEntityEffect activeEntityEffect : activeEntityEffects) {
            int entityEffectIndex = EntityEffect.getAssetMap().getIndex(activeEntityEffect.entityEffectId);
            if (entityEffectIndex == Integer.MIN_VALUE) continue;
            activeEntityEffect.entityEffectIndex = entityEffectIndex;
            this.activeEffects.put(entityEffectIndex, activeEntityEffect);
            this.addChange(new EntityEffectUpdate(EffectOp.Add, entityEffectIndex, activeEntityEffect.remainingDuration, activeEntityEffect.infinite, activeEntityEffect.debuff, activeEntityEffect.statusEffectIcon));
        }
        this.invalidateCache();
    }

    public void removeEffect(@Nonnull Ref<EntityStore> ownerRef, int entityEffectIndex, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        EntityEffect entityEffect = EntityEffect.getAssetMap().getAsset(entityEffectIndex);
        if (entityEffect == null) {
            throw new IllegalArgumentException(String.format("Unknown EntityEffect with index \"%s\"", entityEffectIndex));
        }
        this.removeEffect(ownerRef, entityEffectIndex, entityEffect.getRemovalBehavior(), componentAccessor);
    }

    public void removeEffect(@Nonnull Ref<EntityStore> ownerRef, int entityEffectIndex, @Nonnull RemovalBehavior removalBehavior, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        ActiveEntityEffect activeEffectEntry = (ActiveEntityEffect)this.activeEffects.get(entityEffectIndex);
        if (activeEffectEntry == null) {
            return;
        }
        this.tryResetModelChange(ownerRef, activeEffectEntry.getEntityEffectIndex(), componentAccessor);
        switch (removalBehavior) {
            case COMPLETE: {
                this.activeEffects.remove(entityEffectIndex);
                Entity ownerEntity = EntityUtils.getEntity(ownerRef, componentAccessor);
                if (ownerEntity instanceof LivingEntity) {
                    LivingEntity ownerLivingEntity = (LivingEntity)ownerEntity;
                    ownerLivingEntity.getStatModifiersManager().setRecalculate(true);
                }
                this.addChange(new EntityEffectUpdate(EffectOp.Remove, entityEffectIndex, 0.0f, false, false, ""));
                this.invalidateCache();
                return;
            }
            case INFINITE: {
                activeEffectEntry.infinite = false;
                break;
            }
            case DURATION: {
                activeEffectEntry.remainingDuration = 0.0f;
            }
        }
        Entity ownerEntity = EntityUtils.getEntity(ownerRef, componentAccessor);
        if (ownerEntity instanceof LivingEntity) {
            LivingEntity ownerLivingEntity = (LivingEntity)ownerEntity;
            ownerLivingEntity.getStatModifiersManager().setRecalculate(true);
        }
        this.addChange(new EntityEffectUpdate(EffectOp.Remove, entityEffectIndex, activeEffectEntry.remainingDuration, activeEffectEntry.infinite, activeEffectEntry.debuff, activeEffectEntry.statusEffectIcon));
    }

    private void addChange(@Nonnull EntityEffectUpdate update) {
        this.isNetworkOutdated = true;
        this.changes.add(update);
    }

    public void clearEffects(@Nonnull Ref<EntityStore> ownerRef, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
        IntArraySet keys = new IntArraySet(this.activeEffects.keySet());
        IntIterator intIterator = keys.iterator();
        while (intIterator.hasNext()) {
            int effect = (Integer)intIterator.next();
            this.removeEffect(ownerRef, effect, componentAccessor);
        }
        this.invalidateCache();
        if (this.originalModel != null) {
            componentAccessor.putComponent(ownerRef, ModelComponent.getComponentType(), new ModelComponent(this.originalModel));
            this.originalModel = null;
        }
    }

    public void invalidateCache() {
        this.cachedActiveEffectIndexes = null;
    }

    @Nonnull
    public Int2ObjectMap<ActiveEntityEffect> getActiveEffects() {
        return this.activeEffects;
    }

    public int[] getActiveEffectIndexes() {
        if (this.cachedActiveEffectIndexes == null) {
            this.cachedActiveEffectIndexes = this.activeEffects.isEmpty() ? ArrayUtil.EMPTY_INT_ARRAY : this.activeEffects.keySet().toIntArray();
        }
        return this.cachedActiveEffectIndexes;
    }

    public boolean consumeNetworkOutdated() {
        boolean temp = this.isNetworkOutdated;
        this.isNetworkOutdated = false;
        return temp;
    }

    @Nonnull
    public EntityEffectUpdate[] consumeChanges() {
        return (EntityEffectUpdate[])this.changes.toArray(EntityEffectUpdate[]::new);
    }

    public void clearChanges() {
        this.changes.clear();
    }

    @Nonnull
    public EntityEffectUpdate[] createInitUpdates() {
        EntityEffectUpdate[] changeArray = new EntityEffectUpdate[this.activeEffects.size()];
        int index = 0;
        ObjectIterator<Int2ObjectMap.Entry<ActiveEntityEffect>> iterator = Int2ObjectMaps.fastIterator(this.activeEffects);
        while (iterator.hasNext()) {
            Int2ObjectMap.Entry entry = (Int2ObjectMap.Entry)iterator.next();
            ActiveEntityEffect activeEntityEffectEntry = (ActiveEntityEffect)entry.getValue();
            changeArray[index++] = new EntityEffectUpdate(EffectOp.Add, entry.getIntKey(), activeEntityEffectEntry.remainingDuration, activeEntityEffectEntry.infinite, activeEntityEffectEntry.debuff, activeEntityEffectEntry.statusEffectIcon);
        }
        return changeArray;
    }

    @Nullable
    public ActiveEntityEffect[] getAllActiveEntityEffects() {
        if (this.activeEffects.isEmpty()) {
            return null;
        }
        ActiveEntityEffect[] activeEntityEffects = new ActiveEntityEffect[this.activeEffects.size()];
        int index = 0;
        Iterator iterator = this.activeEffects.values().iterator();
        while (iterator.hasNext()) {
            ActiveEntityEffect entityEffect;
            activeEntityEffects[index] = entityEffect = (ActiveEntityEffect)iterator.next();
            ++index;
        }
        return activeEntityEffects;
    }

    @Nonnull
    public String toString() {
        return "EntityEffectController{, activeEffects=" + String.valueOf(this.activeEffects) + "}";
    }

    @Nonnull
    public EffectControllerComponent clone() {
        return new EffectControllerComponent(this);
    }
}

